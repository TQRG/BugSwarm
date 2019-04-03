package pt.gov.dgarq.roda.core.plugins;

import java.util.List;
import java.util.Map;

import pt.gov.dgarq.roda.core.common.InvalidParameterException;
import pt.gov.dgarq.roda.core.data.PluginParameter;
import pt.gov.dgarq.roda.core.data.Report;

/**
 * This interface should be implemented by any class that want to be a RODA
 * plugin.
 * 
 * @author Rui Castro
 */
public interface Plugin {

	/**
	 * Initializes this {@link Plugin}. This method is called by the
	 * {@link PluginManager} before any other methods in the plugin.
	 * 
	 * @throws PluginException
	 */
	public void init() throws PluginException;;

	/**
	 * Stops all {@link Plugin} activity. This is the last method to be called
	 * by {@link PluginManager} on the {@link Plugin}.
	 */
	public void shutdown();

	/**
	 * Returns the name of this {@link Plugin}.
	 * 
	 * @return a {@link String} with the name of this {@link Plugin}.
	 */
	public String getName();

	/**
	 * Returns the version of this {@link Plugin}.
	 * 
	 * @return a <code>float</code> with the version number for this
	 *         {@link Plugin}.
	 */
	public float getVersion();

	/**
	 * Returns description of this {@link Plugin}.
	 * 
	 * @return a {@link String} with the description of this {@link Plugin}.
	 */
	public String getDescription();

	/**
	 * Returns the {@link List} of {@link PluginParameter}s necessary to run
	 * this {@link Plugin}.
	 * 
	 * @return a {@link List} of {@link PluginParameter} with the parameters.
	 */
	public List<PluginParameter> getParameters();

	/**
	 * Gets the parameter values inside a {@link Map} with attribute names and
	 * values.
	 * 
	 * @return a {@link Map} with the parameters name and value.
	 */
	public Map<String, String> getParameterValues();

	/**
	 * Sets the parameters returned by a previous call to
	 * {@link Plugin#getParameters()}.
	 * 
	 * @param parameters
	 *            a {@link List} of parameters.
	 * 
	 * @throws InvalidParameterException
	 */
	public void setParameterValues(Map<String, String> parameters)
			throws InvalidParameterException;

	/**
	 * Executes the {@link Plugin}.
	 * 
	 * @return a {@link Report} of the actions performed.
	 * 
	 * @throws PluginException
	 */
	public Report execute() throws PluginException;
}
