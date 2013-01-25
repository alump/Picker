package org.vaadin.alump.picker.demo;

import org.vaadin.alump.picker.Picker;
import org.vaadin.alump.picker.PickerValuePresentator;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("demo")
public class PickerDemoUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		setContent(layout);
	
		addPickerA (layout);
		addPickerB (layout);
	}
	
	protected void addPickerA (ComponentContainer container) {
		Picker picker = new Picker();
		for (int i = 1; i < 10; ++i) {
			picker.addValue("Value " + i);
		}
		container.addComponent(picker);
	}
	
	PickerValuePresentator presentatorB = new PickerValuePresentator() {
		@Override
		public boolean isHtml() {
			return true;
		}

		@Override
		public String generate(Object value) {
			Integer number = (Integer)value;
			StringBuilder sb = new StringBuilder();
			sb.append("<div class=\"demo-b-line\" style=\"width:");
			sb.append(number * 10);
			sb.append("px;\">&nbsp</div>");
			return sb.toString();
		}
	};
	
	protected void addPickerB (ComponentContainer container) {
		Picker picker = new Picker(presentatorB);
		for (int i = 1; i < 10; ++i) {
			picker.addValue(new Integer(i));
		}
		container.addComponent(picker);
	}
	
}