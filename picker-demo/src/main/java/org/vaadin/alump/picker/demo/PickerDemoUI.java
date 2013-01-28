package org.vaadin.alump.picker.demo;

import java.util.List;
import java.util.Random;

import org.vaadin.alump.picker.Picker;
import org.vaadin.alump.picker.PickerValuePresentator;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("demo")
public class PickerDemoUI extends UI {
	
	protected Picker pickerA;
	protected Picker pickerB;
	protected Picker pickerC;
	protected int extraCounter = 0;

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setWidth("100%");
		setContent(layout);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setWidth("100%");
		layout.addComponent(buttonLayout);
		
		CheckBox cbox = new CheckBox("Show buttons");
		cbox.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean value = (Boolean)event.getProperty().getValue();
				pickerA.setButtonsVisible(value);
				pickerB.setButtonsVisible(value);
				pickerC.setButtonsVisible(value);
			}
			
		});
		buttonLayout.addComponent(cbox);
		
		Button jump = new Button("Select random value");
		jump.setDescription("Select random value from each Picker");
		buttonLayout.addComponent(jump);
		jump.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				onJumpRandomValue();
			}
			
		});
		
		Button remove = new Button("Remove current value");
		remove.setDescription("Remove current value from Pickers A and B");
		buttonLayout.addComponent(remove);
		remove.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				onRemoveCurrent();
			}
			
		});
		
		Button add = new Button("Add value");
		add.setDescription("Add value to Picker A and B");
		buttonLayout.addComponent(add);
		add.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				onAddValue();
			}
			
		});
		
		addPickerA (layout);
		addPickerB (layout);
		addPickerC (layout);
		
		cbox.setValue(pickerA.isButtonsVisible());
	}
	
	protected void onRemoveCurrent() {
		if (!pickerA.getPossibleValues().isEmpty()) {
			pickerA.removeValue(pickerA.getValue());
		} else {
			new Notification("Empty picker").show(this.getPage());
		}
		if (!pickerB.getPossibleValues().isEmpty()) {
			pickerB.removeValue(pickerB.getValue());
		} else {
			new Notification("Empty picker").show(this.getPage());
		}
	}
	
	protected void onJumpRandomValue() {
		Random rand = new Random();
		List<Object> values = pickerA.getPossibleValues();
		if (values.isEmpty()) {
			new Notification("Empty picker A").show(this.getPage());
		} else {
			pickerA.setValueWithIndex(rand.nextInt(values.size()));
		}
		
		values = pickerB.getPossibleValues();
		if (values.isEmpty()) {
			new Notification("Empty picker B").show(this.getPage());
		} else {
			pickerB.setValueWithIndex(rand.nextInt(values.size()));
		}
		
		values = pickerC.getPossibleValues();
		if (values.isEmpty()) {
			new Notification("Empty picker C").show(this.getPage());
		} else {
			pickerC.setValueWithIndex(rand.nextInt(values.size()));
		}
	}
	
	protected void onAddValue() {
		pickerA.addValue("Extra value #" + (++extraCounter));
		pickerB.addValue(new Integer(13 + ++extraCounter));
	}
	
	protected void addPickerA (ComponentContainer container) {
		pickerA = new Picker("Simple demo - Picker A");
		for (int i = 1; i < 10; ++i) {
			pickerA.addValue("Value " + i);
		}
		container.addComponent(pickerA);
	}
	
	/**
	 * Demo generator
	 */
	PickerValuePresentator presentatorB = new PickerValuePresentator() {
		@Override
		public boolean isHtml() {
			return true;
		}

		@Override
		public String generate(Object value) {
			Integer number = (Integer)value;
			StringBuilder sb = new StringBuilder();
			if (number <= 9) {
				sb.append("<div class=\"demo-b-tree demo-b-part-");
				sb.append(number);
				sb.append("\" style=\"width:");
				sb.append(number * 10);
				sb.append("%;\">&nbsp</div>");
			} else {
				sb.append("<div class=\"demo-b-trunk demo-b-part-");
				sb.append(number);
				sb.append("\">&nbsp;</div>");	
			}
			return sb.toString();
		}
	};
	
	protected void addPickerB (ComponentContainer container) {
		pickerB = new Picker("Xmas tree (generated html lines) - Picker B", presentatorB);
		pickerB.setHeight("200px");
		for (int i = 1; i < 12; ++i) {
			pickerB.addValue(new Integer(i));
		}
		container.addComponent(pickerB);
	}
	
	protected void addPickerC (ComponentContainer container) {
		pickerC = new Picker("Value signals - Picker C");
		pickerC.setWidth("80%");
		for (int i = 1982; i < 2013; ++i) {
			pickerC.addValue(new Integer(i));
		}
		pickerC.setValue(new Integer(2000));
		container.addComponent(pickerC);
		pickerC.addValueChangeListener(new Picker.ValueChangeListener() {

			@Override
			public void onValueChanged(Object value, Integer index) {
				new Notification(value.toString() + " selected!").show(
						PickerDemoUI.this.getPage());
			}
			
		});
	}
	
}