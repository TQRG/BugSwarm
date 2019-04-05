package org.stagemonitor.core;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.configuration.source.ConfigurationSource;
import org.stagemonitor.core.configuration.ConfigurationLogger;
import org.stagemonitor.core.instrument.AgentAttacher;
import org.stagemonitor.core.metrics.health.ImmediateResult;
import org.stagemonitor.core.metrics.health.OverridableHealthCheckRegistry;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.core.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Stagemonitor {

	private static Logger logger = LoggerFactory.getLogger(Stagemonitor.class);
	private static ConfigurationRegistry configuration;
	private static boolean initialized;
	private static boolean started;
	private static boolean disabled;
	private static MeasurementSession measurementSession = new MeasurementSession(null, null, null);
	private static List<String> pathsOfWidgetMetricTabPlugins = Collections.emptyList();
	private static List<String> pathsOfWidgetTabPlugins = Collections.emptyList();
	private static Iterable<StagemonitorPlugin> plugins;
	private static List<Runnable> onShutdownActions = new CopyOnWriteArrayList<Runnable>();
	private static Metric2Registry metric2Registry = new Metric2Registry(SharedMetricRegistries.getOrCreate("stagemonitor"));
	private static HealthCheckRegistry healthCheckRegistry = new OverridableHealthCheckRegistry();

	private Stagemonitor() {
	}

	static {
		reloadPluginsAndConfiguration();
	}

	public static synchronized void init() {
		if (initialized) {
			return;
		}
		initialized = true;
		try {
			reset();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private static void startMonitoring(MeasurementSession measurementSession) {
		if (!getPlugin(CorePlugin.class).isStagemonitorActive()) {
			logger.info("stagemonitor is deactivated");
			disabled = true;
		}
		if (started || disabled) {
			return;
		}
		Stagemonitor.measurementSession = measurementSession;
		doStartMonitoring();
	}

	private static void doStartMonitoring() {
		if (started) {
			return;
		}
		if (measurementSession.isInitialized()) {
			logger.info("Measurement Session is initialized: " + measurementSession);
			try {
				start();
			} catch (RuntimeException e) {
				logger.warn("Error while trying to start monitoring. (this exception is ignored)", e);
			}
		} else {
			logger.debug("Measurement Session is not initialized: {}", measurementSession);
			logger.debug("make sure the properties 'stagemonitor.instanceName' and 'stagemonitor.applicationName' " +
					"are set and stagemonitor.properties is available in the classpath");
		}
	}

	private static void start() {
		initializePlugins();
		started = true;
		// don't register a shutdown hook for web applications as this causes a memory leak
		if (ClassUtils.isNotPresent("javax.servlet.Servlet")) {
			// in case the application does not directly call shutDown
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					shutDown();
				}
			}));
		}
	}

	private static void logStatus() {
		logger.info("# stagemonitor status");
		logger.info("System information: {}", getJvmAndOsVersionString());
		for (Map.Entry<String, HealthCheck.Result> entry : healthCheckRegistry.runHealthChecks().entrySet()) {
			String status = entry.getValue().isHealthy() ? "OK  " : "FAIL";
			String message = entry.getValue().getMessage() == null ? "" : "(" + entry.getValue().getMessage() + ")";
			final String checkName = entry.getKey();
			logger.info("{} - {} {}", status, checkName, message);
			final Throwable error = entry.getValue().getError();
			if (error != null) {
				logger.warn("Exception thrown while initializing plugin", error);
			}
		}
	}

	private static String getJvmAndOsVersionString() {
		return "Java " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ") " +
				System.getProperty("os.name") + " " + System.getProperty("os.version");
	}

	private static void initializePlugins() {
		final CorePlugin corePlugin = getPlugin(CorePlugin.class);
		final Collection<String> disabledPlugins = corePlugin.getDisabledPlugins();
		pathsOfWidgetMetricTabPlugins = new CopyOnWriteArrayList<String>();
		pathsOfWidgetTabPlugins = new CopyOnWriteArrayList<String>();

		initializePluginsInOrder(disabledPlugins, plugins);
	}

	static void initializePluginsInOrder(Collection<String> disabledPlugins, Iterable<StagemonitorPlugin> plugins) {
		Set<Class<? extends StagemonitorPlugin>> alreadyInitialized = new HashSet<Class<? extends StagemonitorPlugin>>();
		Set<StagemonitorPlugin> notYetInitialized = getPluginsToInit(disabledPlugins, plugins);
		while (!notYetInitialized.isEmpty()) {
			int countNotYetInitialized = notYetInitialized.size();
			// try to init plugins which are
			for (Iterator<StagemonitorPlugin> iterator = notYetInitialized.iterator(); iterator.hasNext(); ) {
				StagemonitorPlugin stagemonitorPlugin = iterator.next();
				{
					final List<Class<? extends StagemonitorPlugin>> dependencies = stagemonitorPlugin.dependsOn();
					if (dependencies.isEmpty() || alreadyInitialized.containsAll(dependencies)) {
						initializePlugin(stagemonitorPlugin);
						iterator.remove();
						alreadyInitialized.add(stagemonitorPlugin.getClass());
					}
				}
			}
			if (countNotYetInitialized == notYetInitialized.size()) {
				// no plugins could be initialized in this try. this probably means there is a cyclic dependency
				throw new IllegalStateException("Cyclic dependencies detected: " + notYetInitialized);
			}
		}
	}

	private static Set<StagemonitorPlugin> getPluginsToInit(Collection<String> disabledPlugins, Iterable<StagemonitorPlugin> plugins) {
		Set<StagemonitorPlugin> notYetInitialized = new HashSet<StagemonitorPlugin>();
		for (StagemonitorPlugin stagemonitorPlugin : plugins) {
			final String pluginName = stagemonitorPlugin.getClass().getSimpleName();
			if (disabledPlugins.contains(pluginName)) {
				logger.info("Not initializing disabled plugin {}", pluginName);
				healthCheckRegistry.register(pluginName, ImmediateResult.of(HealthCheck.Result.unhealthy("disabled via configuration")));
			} else {
				notYetInitialized.add(stagemonitorPlugin);
			}
		}
		return notYetInitialized;
	}

	private static void initializePlugin(final StagemonitorPlugin stagemonitorPlugin) {
		final String pluginName = stagemonitorPlugin.getClass().getSimpleName();
		try {
			stagemonitorPlugin.initializePlugin(new StagemonitorPlugin.InitArguments(metric2Registry, getConfiguration(), measurementSession, healthCheckRegistry));
			stagemonitorPlugin.initialized = true;
			for (Runnable onInitCallback : stagemonitorPlugin.onInitCallbacks) {
				onInitCallback.run();
			}
			stagemonitorPlugin.registerWidgetTabPlugins(new StagemonitorPlugin.WidgetTabPluginsRegistry(pathsOfWidgetTabPlugins));
			stagemonitorPlugin.registerWidgetMetricTabPlugins(new StagemonitorPlugin.WidgetMetricTabPluginsRegistry(pathsOfWidgetMetricTabPlugins));
			healthCheckRegistry.register(pluginName, ImmediateResult.of(HealthCheck.Result.healthy("version " + stagemonitorPlugin.getVersion())));
		} catch (final Exception e) {
			healthCheckRegistry.register(pluginName, ImmediateResult.of(HealthCheck.Result.unhealthy(e)));
			logger.warn("Error while initializing plugin " + pluginName + " (this exception is ignored)", e);
		}
	}

	/**
	 * Should be called when the server is shutting down.
	 * Calls the {@link StagemonitorPlugin#onShutDown()} method of all plugins
	 */
	public static synchronized void shutDown() {
		if (measurementSession.getEndTimestamp() != null) {
			// shutDown has already been called
			return;
		}
		logger.info("Shutting down stagemonitor");
		measurementSession.setEndTimestamp(System.currentTimeMillis());
		for (Runnable onShutdownAction : onShutdownActions) {
			try {
				onShutdownAction.run();
			} catch (RuntimeException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		for (StagemonitorPlugin plugin : plugins) {
			try {
				plugin.onShutDown();
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
		}
		configuration.close();
	}

	/**
	 * @deprecated use {@link #getMetric2Registry()}
	 */
	@Deprecated
	public static MetricRegistry getMetricRegistry() {
		return metric2Registry.getMetricRegistry();
	}

	public static Metric2Registry getMetric2Registry() {
		return metric2Registry;
	}

	public static HealthCheckRegistry getHealthCheckRegistry() {
		return healthCheckRegistry;
	}

	public static ConfigurationRegistry getConfiguration() {
		return configuration;
	}

	public static <T extends StagemonitorPlugin> T getPlugin(Class<T> plugin) {
		return configuration.getConfig(plugin);
	}

	/**
	 * @deprecated use {@link #getPlugin(Class)}
	 */
	@Deprecated
	public static <T extends StagemonitorPlugin> T getConfiguration(Class<T> plugin) {
		return getPlugin(plugin);
	}

	static void setConfiguration(ConfigurationRegistry configuration) {
		Stagemonitor.configuration = configuration;
	}

	public static MeasurementSession getMeasurementSession() {
		return measurementSession;
	}

	public static boolean isStarted() {
		return started;
	}

	static boolean isDisabled() {
		return disabled;
	}

	static void setLogger(Logger logger) {
		Stagemonitor.logger = logger;
	}

	/**
	 * @see StagemonitorPlugin#registerWidgetTabPlugins(StagemonitorPlugin.WidgetTabPluginsRegistry)
	 */
	public static List<String> getPathsOfWidgetTabPlugins() {
		return Collections.unmodifiableList(pathsOfWidgetTabPlugins);
	}

	/**
	 * @see org.stagemonitor.core.StagemonitorPlugin#registerWidgetMetricTabPlugins(StagemonitorPlugin.WidgetMetricTabPluginsRegistry)
	 */
	public static List<String> getPathsOfWidgetMetricTabPlugins() {
		return Collections.unmodifiableList(pathsOfWidgetMetricTabPlugins);
	}

	/**
	 * Should only be used outside of this class by the internal unit tests
	 */
	@Deprecated
	public static void reset() {
		reset(null);
	}

	/**
	 * Should only be used outside of this class by the internal unit tests
	 */
	@Deprecated
	public static void reset(MeasurementSession measurementSession) {
		started = false;
		disabled = false;
		if (configuration == null) {
			reloadPluginsAndConfiguration();
		}
		if (measurementSession == null) {
			CorePlugin corePlugin = getPlugin(CorePlugin.class);
			measurementSession = new MeasurementSession(corePlugin.getApplicationName(),
					corePlugin.getHostName(), corePlugin.getInstanceName());
		}
		onShutdownActions.add(AgentAttacher.performRuntimeAttachment());
		startMonitoring(measurementSession);
		healthCheckRegistry.register("Startup", new HealthCheck() {
			@Override
			protected Result check() throws Exception {
				if (started) {
					return Result.healthy();
				} else {
					return Result.unhealthy("stagemonitor is not started");
				}
			}
		});
		logStatus();
		new ConfigurationLogger().logConfiguration(configuration);
	}

	private static void reloadPluginsAndConfiguration() {
		List<ConfigurationSource> configurationSources = new ArrayList<ConfigurationSource>();
		for (StagemonitorConfigurationSourceInitializer initializer : ServiceLoader.load(StagemonitorConfigurationSourceInitializer.class, Stagemonitor.class.getClassLoader())) {
			initializer.modifyConfigurationSources(new StagemonitorConfigurationSourceInitializer.ModifyArguments(configurationSources));
		}
		configurationSources.remove(null);

		plugins = ServiceLoader.load(StagemonitorPlugin.class, Stagemonitor.class.getClassLoader());
		configuration = ConfigurationRegistry.builder()
				.optionProviders(plugins)
				.configSources(configurationSources)
				.build();

		try {
			for (StagemonitorConfigurationSourceInitializer initializer : ServiceLoader.load(StagemonitorConfigurationSourceInitializer.class, Stagemonitor.class.getClassLoader())) {
				initializer.onConfigurationInitialized(new StagemonitorConfigurationSourceInitializer.ConfigInitializedArguments(configuration));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("Stagemonitor will be deactivated!");
			disabled = true;
		}
	}
}
