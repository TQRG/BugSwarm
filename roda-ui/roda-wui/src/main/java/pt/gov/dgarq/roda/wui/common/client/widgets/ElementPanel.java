package pt.gov.dgarq.roda.wui.common.client.widgets;

import com.google.gwt.user.client.ui.FocusPanel;

/**
 * Element Panel
 * 
 * @author Luis Faria
 * 
 * @param <T>
 *            the element type
 */
public abstract class ElementPanel<T> extends FocusPanel {

	private T element;

	private boolean selected;

	/**
	 * Create a new element panel
	 * 
	 * @param element
	 */
	public ElementPanel(T element) {
		this.element = element;
	}

	/**
	 * Is current panel selected
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set current panel selected;
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		if (selected) {
			addStyleDependentName("selected");
		} else {
			removeStyleDependentName("selected");
		}
	}

	/**
	 * Get element defined by this panel
	 * 
	 * @return
	 */
	public T get() {
		return element;
	}

	/**
	 * Set element defined by this panel
	 * 
	 * @param element
	 */
	public void set(T element) {
		if(this.element != element) {
			this.element = element;
			update(element);
		}
	}

	/**
	 * Update layout with element
	 * 
	 * @param element
	 */
	protected abstract void update(T element);
}
