package org.vaadin.alump.picker.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.MouseWheelVelocity;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;

public class GwtPicker extends Widget implements HasValueChangeHandlers<Integer> {
	
	public final static String CLASSNAME = "alump-picker";
	public final static String CLASSNAME_STEPPING = "alump-picker-stepping";
	public final static String CLASSNAME_BUTTONS = CLASSNAME + "-buttons";
	public final static String CLASSNAME_BUTTON = CLASSNAME + "-button";
	public final static String CLASSNAME_UP = CLASSNAME + "-up";
	public final static String CLASSNAME_DOWN = CLASSNAME + "-down";
	public final static String CLASSNAME_LINE = CLASSNAME + "-line";
	public final static String CLASSNAME_CURRENT = CLASSNAME + "-current";
	public final static String CLASSNAME_OUTER = CLASSNAME + "-outer";
	public final static String CLASSNAME_INNER = CLASSNAME + "-inner";
	public final static String CLASSNAME_OVERLAY = CLASSNAME + "-overlay";
	
	protected Element upButtonElement;
	protected Element downButtonElement;
	protected Element outerScrollElement;
	protected Element innerScrollElement;
	protected Element overlayElement;
	
	protected List<Element> valueElements = new ArrayList<Element>();
	protected Integer currentValue = null;
	
	public GwtPicker() {
		setElement(Document.get().createDivElement());
		
		upButtonElement = Document.get().createDivElement();
		upButtonElement.addClassName(CLASSNAME_BUTTON);
		upButtonElement.addClassName(CLASSNAME_UP);
		upButtonElement.setInnerHTML("⇧");
		getElement().appendChild(upButtonElement);
		
		downButtonElement = Document.get().createDivElement();
		downButtonElement.addClassName(CLASSNAME_BUTTON);
		downButtonElement.addClassName(CLASSNAME_DOWN);
		downButtonElement.setInnerHTML("⇩");
		getElement().appendChild(downButtonElement);
		
		outerScrollElement = Document.get().createDivElement();
		outerScrollElement.addClassName(CLASSNAME_OUTER);
		getElement().appendChild(outerScrollElement);
		
		innerScrollElement = Document.get().createDivElement();
		innerScrollElement.addClassName(CLASSNAME_INNER);
		outerScrollElement.appendChild(innerScrollElement);
		
		overlayElement = Document.get().createDivElement();
		overlayElement.addClassName(CLASSNAME_OVERLAY);
		getElement().appendChild(overlayElement);
		
		setStylePrimaryName(CLASSNAME);
	}
	
	public void onAttach() {
		super.onAttach();
		
		//addHandler(mouseWheelHandler, MouseWheelEvent.getType());
		addHandler(clickHandler, ClickEvent.getType());
	}
	
	/**
	 * Handler for mouse wheel events
	 *
	protected MouseWheelHandler mouseWheelHandler = new MouseWheelHandler() {

		@Override
		public void onMouseWheel(MouseWheelEvent event) {
			event.preventDefault();
			event.stopPropagation();
			
			int steps = event.getDeltaY();
			
			VConsole.log("onMouseWheel: " + steps);
		
			if (steps < 0) {
				GwtPicker.this.setPrevValue(-steps);	
			} else if (steps > 0) {
				GwtPicker.this.setNextValue(steps);
			}
			
		}
		
	};
	*/
	
	/**
	 * Handler for click events
	 */
	protected ClickHandler clickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			Element element = Element.as(event.getNativeEvent().getEventTarget());
			if (upButtonElement.isOrHasChild(element)) {
				event.preventDefault();
				event.stopPropagation();
				setPrevValue();
			} else if (downButtonElement.isOrHasChild(element)) {
				event.preventDefault();
				event.stopPropagation();
				setNextValue();
			} else if (overlayElement != null && overlayElement.isOrHasChild(element)) {
				findValueElement (event.getClientX(), event.getClientY());
			}
		}
		
	};
	
	protected void findValueElement (int clientX, int clientY) {
		for (Element value : valueElements) {
			int valY = value.getAbsoluteTop();
			if (clientY > valY) {
				int valB = valY + value.getClientHeight();
				if (clientY < valB) {
					setValue(valueElements.indexOf(value), true);
					break;
				}
			}
		}
	}
	
	public void setButtonsVisible(boolean visible) {
		if (visible) {
			addStyleName(CLASSNAME_BUTTONS);
		} else {
			removeStyleName(CLASSNAME_BUTTONS);
		}
	}
	
	public void setValueAmount (int amount) {
		if (amount < 0) {
			amount = 0;
		}
		
		while (valueElements.size() > amount) {
			Element element = valueElements.get(valueElements.size() - 1);
			valueElements.remove(element);
			element.removeFromParent();
		}
		
		while (valueElements.size() < amount) {
			Element element = createValueElement();
			valueElements.add(element);
			innerScrollElement.appendChild(element);
		}
	}
	
	protected Element createValueElement() {
		Element element = Document.get().createDivElement();
		element.addClassName(CLASSNAME_LINE);
		return element;
	}
	
	public void setValuePresentation (int index, String presentation,
			boolean isHTML) {
		
		try {
			Element element = valueElements.get(index);
			if (presentation == null || presentation.isEmpty()) {
				element.setInnerHTML("&nbsp;");
			} else {
				if (isHTML) {
					element.setInnerHTML(presentation);
				} else {
					element.setInnerText(presentation);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			VConsole.error("Failed to get value presentation");
		}
	}
	
	public void setPrevValue() {
		setPrevValue(1);
	}
	
	public void setPrevValue(int steps) {
		if (currentValue != null) {
			int newIndex = currentValue - steps;
			if (newIndex < 0) {
				newIndex = 0;
			}
			setValue(newIndex, true);
		}
	}
	
	public void setNextValue() {
		setNextValue(1);
	}
	
	public void setNextValue(int steps) {
		if (currentValue != null) {
			int newIndex = currentValue + steps;
			if (newIndex >= valueElements.size()) {
				newIndex = valueElements.size() - 1;
			}
			setValue(newIndex, true);
		}
	}
	
	public void setValue(int index) {
		setValue(index, false);
	}
	
	/**
	 * Change value of picker
	 * @param index New index to be selected
	 * @param user Is event caused by user (if true value change will be fired)
	 */
    protected void setValue(int index, boolean user) {
		try {
			Element element = valueElements.get(index);
			
			double elementTop = element.getOffsetTop();
			double elementHeight = element.getClientHeight();
			double scrollerHeight = outerScrollElement.getClientHeight();
			
			int scrollTop = (int)Math.round(scrollerHeight / 2
					- elementHeight / 2 - elementTop);
			
			innerScrollElement.addClassName(CLASSNAME_STEPPING);
			innerScrollElement.getStyle().setTop(scrollTop, Unit.PX);
			for (int i = 0; i < valueElements.size(); ++i) {
				if (i == index) {
					valueElements.get(i).addClassName(CLASSNAME_CURRENT);
				} else {
					valueElements.get(i).removeClassName(CLASSNAME_CURRENT);
				}
			}
			if (currentValue == null || index != currentValue) {
				currentValue = index;
				if (user) {
					ValueChangeEvent.fire(this, currentValue);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			VConsole.error("Failed to set value");
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Integer> handler) {
        HandlerRegistration registration = this.addHandler(handler,
                ValueChangeEvent.getType());
        return registration;
	}

}
