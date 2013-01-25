package org.vaadin.alump.picker;

/**
 * Generator used to generate presentation of values of Picker
 */
public interface PickerValuePresentator {
	
	/**
	 * Is content returned from generate safe HTML
	 * @return true if content is HTML, false if text.
	 */
	public boolean isHtml();
	
	/**
	 * Generate presentation for value
	 * @param value Value presented
	 * @return Presentation of value
	 */
	public String generate (Object value);
}
