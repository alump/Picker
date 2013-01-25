package org.vaadin.alump.picker;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.alump.picker.gwt.client.connect.PickerServerRpc;
import org.vaadin.alump.picker.gwt.client.shared.PickerState;

import com.vaadin.ui.AbstractComponent;

/**
 * Picker widget is similar to HTML select element presentation of iOS device.
 * This widget allows to have similar experience on all devices. It also offers
 * way to present custom content (not just text).
 */
@SuppressWarnings("serial")
public class Picker extends AbstractComponent {
	
	protected PickerValuePresentator itemGenerator;
	protected List<Object> values = new ArrayList<Object>();
	
	protected PickerServerRpc serverRpc = new PickerServerRpc() {
		@Override
		public void onValueChanged(Integer value) {
			getState().currentValue = value;
		}
	};
	
	/**
	 * Create new Picker with default item generator.
	 */
	public Picker() {
		this(new PickerTextPresentator());
	}
	
	/**
	 * Create new Picker with custom item presentation generator
	 * @param generator
	 */
	public Picker(PickerValuePresentator generator) {
		itemGenerator = generator;
	}
	
	/**
	 * Add value to options list
	 * @param object Value added
	 */
	public void addValue(Object object) {
		values.add(object);
		markAsDirty();
	}
	
	/**
	 * Remove value from options list
	 * @param object Value removed
	 */
	public void removeValue(Object object) {
		if (values.remove(object)) {
			markAsDirty();
		}
	}
	
	@Override
	public PickerState getState() {
		return (PickerState) super.getState();
	}
	
	/**
	 * Update value presentation from generator
	 */
	public void updateValues() {
		markAsDirty();
	}
	
	/**
	 * Show or hide browse buttons
	 * @param visible true to show, false to hide
	 */
	public void setButtonsVisible(boolean visible) {
		getState().showButtons = visible;
	}
	
	/**
	 * Get current value of Picker
	 * @return Current value
	 */
	public Object getValue() {
		try {
			return values.get(getState().currentValue);
		} catch (Exception e) {
			return null;
		}
	}
	
    @Override
    public void beforeClientResponse(boolean initial) {
    	getState().values.clear();
    	
    	getState().valuesInHTML = itemGenerator.isHtml();
    	
    	for (Object value : values) {
    		getState().values.add(itemGenerator.generate(value));
    	}
    }
	
	
}