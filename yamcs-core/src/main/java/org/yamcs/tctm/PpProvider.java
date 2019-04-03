package org.yamcs.tctm;

/**
 * Interface for components providing parameters aquired from external systems.
 * @author nm
 *
 * @deprecated this interface has been renamed to {@link PpDataLink} for clarity
 */
@Deprecated
public interface PpProvider extends PpDataLink {
	public void setPpListener(PpListener ppListener);
}
