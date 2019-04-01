package org.stagemonitor.core;

import java.util.Collections;
import java.util.List;

import com.codahale.metrics.MetricRegistry;
import org.stagemonitor.core.configuration.Configuration;
import org.stagemonitor.core.configuration.ConfigurationOptionProvider;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;

/**
 * Base class for stagemonitor Plugins.
 *
 * The {@link #initializePlugin(Metric2Registry, Configuration)} )} method serves as a initialisation callback
 * for plugins.
 */
public abstract class StagemonitorPlugin extends ConfigurationOptionProvider {

	/**
	 * Implementing classes have to initialize the plugin by registering their metrics the
	 * {@link Metric2Registry}
	 */
	public void initializePlugin(Metric2Registry metricRegistry, Configuration configuration) throws Exception {
	}

	/**
	 * Implementing classes have to initialize the plugin by registering their metrics the
	 * {@link com.codahale.metrics.MetricRegistry}
	 *
	 * @deprecated Use {@link #initializePlugin(Metric2Registry, Configuration)}
	 */
	@Deprecated
	public void initializePlugin(MetricRegistry metricRegistry, Configuration configuration) throws Exception {
	}

	public void onShutDown() {
	}

	/**
	 * StagemonitorPlugins can add additional tabs to the in browser widget.
	 * A tab plugin consists of a javascript file and a html file.
	 * <p/>
	 * The files should be placed under
	 * <code>src/main/resources/stagemonitor/static/some/sub/folder/myPlugin[.js|.html]</code>
	 * if you return the path <code>/stagemonitor/static/some/sub/folder/myPlugin</code>
	 * <p/>
	 * The FileServlet serves all files under /src/main/resources/stagemonitor/static and
	 * src/main/resources/stagemonitor/public/static
	 *
	 * @return the paths of the tab plugins
	 */
	public List<String> getPathsOfWidgetTabPlugins() {
		return Collections.emptyList();
	}

	/**
	 * StagemonitorPlugins can extend the metrics tab in the in browser widget.
	 * A widget metrics tab plugin consists of a javascript file and a html file.
	 * <p/>
	 * The files should be placed under
	 * <code>/src/main/resources/stagemonitor/static/some/sub/folder/{pluginId}[.js|.html]</code>
	 * <p/>
	 * The FileServlet serves all files under /src/main/resources/stagemonitor/static and
	 * /src/main/resources/stagemonitor/public/static
	 * <p/>
	 * For documentation and a example of how a plugin should look like, see
	 * <code>stagemonitor-ehcache/src/main/resources/stagemonitor/static/tabs/metrics</code> and
	 * <code>stagemonitor-jvm/src/main/resources/stagemonitor/static/tabs/metrics</code>
	 *
	 * @return the paths of the widget metric tab plugins
	 */
	public List<String> getPathsOfWidgetMetricTabPlugins() {
		return Collections.emptyList();
	}

}
