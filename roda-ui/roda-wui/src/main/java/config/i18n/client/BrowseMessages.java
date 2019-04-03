/**
 * 
 */
package config.i18n.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Luis Faria
 * 
 */
public interface BrowseMessages extends Messages {

	// Tree
	@DefaultMessage("See {0}-{1}")
	public String previousItems(int from, int to);

	@DefaultMessage("See {0}-{1} (total {2})")
	public String nextItems(int from, int to, int total);

	// Item Popup
	@DefaultMessage("To lock wait {0} sec.")
	public String waitToLock(int sec);

	@DefaultMessage("Click here to close")
	public String close();

	// Browse
	@DefaultMessage("{0} fonds")
	public String totalFondsNumber(int count);

	@DefaultMessage("There is no such element in the repository identified by{0}.")
	public String noSuchRODAObject(String pid);

	// Edit
	@DefaultMessage("Unable to save the changes. Details: {0}")
	public String editSaveError(String message);

	@DefaultMessage("Unable to move the element because the levels of description are not appropriate. Details: {0}")
	public String moveIllegalOperation(String message);

	@DefaultMessage("Unable to move the element because it or the destination were not found in the repository. Details: {0}")
	public String moveNoSuchObject(String message);

	// Representations Panel
	@DefaultMessage("Disseminations of {0} - ''{1}''")
	public String representationsTitle(String id, String title);

	@DefaultMessage("{0} does not have associated representations")
	public String noRepresentationsTitle(String id);

	@DefaultMessage("Download representation with format {0}, {1} files, {2} bytes uncompressed")
	public String representationDownloadTooltip(String format,
			int numberOfFiles, long sizeOfFiles);

	// Preservation Metadata Panel
	@DefaultMessage("{0} (original)")
	public String preservationRepOriginal(String format);

	@DefaultMessage("{0} (normalized)")
	public String preservationRepNormalized(String format);

	@DefaultMessage("{0}")
	public String preservationRepAlternative(String format);

	@DefaultMessage("{0} files, {1} bytes")
	public String preservationRepTooltip(int numberOfFiles, long sizeOfFiles);

}
