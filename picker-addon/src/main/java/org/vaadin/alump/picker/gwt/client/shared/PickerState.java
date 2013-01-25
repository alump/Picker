package org.vaadin.alump.picker.gwt.client.shared;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.AbstractComponentState;

@SuppressWarnings("serial")
public class PickerState extends AbstractComponentState {
	public boolean valuesInHTML = false;
	public List<String> values = new ArrayList<String>();
	public Integer currentValue = null;
	public boolean showButtons = false;
}
