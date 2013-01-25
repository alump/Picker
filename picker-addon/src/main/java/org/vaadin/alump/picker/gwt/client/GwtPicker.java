package org.vaadin.alump.picker.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

public class GwtPicker extends Widget implements HasValueChangeHandlers<Integer> {
	
	public final static String CLASSNAME = "alump-picker";
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
	
	public GwtPicker() {
		setElement(Document.get().createDivElement());
		
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
	
	public void setButtonsVisible(boolean visible) {
		if (visible) {
			if (upButtonElement == null) {
				upButtonElement = Document.get().createDivElement();
				upButtonElement.addClassName(CLASSNAME_BUTTON);
				upButtonElement.addClassName(CLASSNAME_UP);
				upButtonElement.setInnerHTML("&nbsp;");
				getElement().appendChild(upButtonElement);
			}
			if (downButtonElement == null) {
				downButtonElement = Document.get().createDivElement();
				downButtonElement.addClassName(CLASSNAME_BUTTON);
				downButtonElement.addClassName(CLASSNAME_DOWN);
				downButtonElement.setInnerHTML("&nbsp;");
				getElement().appendChild(downButtonElement);
			}
			addStyleName(CLASSNAME_BUTTONS);
		} else {
			if (upButtonElement != null) {
				upButtonElement.removeFromParent();
				upButtonElement = null;
			}
			if (downButtonElement != null) {
				downButtonElement.removeFromParent();
				downButtonElement = null;
			}
			removeStyleName(CLASSNAME_BUTTONS);
		}
	}
	
	public void setValueAmount (int amount) {
		if (amount < 0) {
			amount = 0;
		}
		
		while (valueElements.size() > amount) {
			valueElements.remove(valueElements.size() - 1);
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
			//TODO
		}
	}
	
	public void setValue(int index) {
		try {
			Element element = valueElements.get(index);
			int top = element.getOffsetTop();
			int height = element.getClientHeight();
			innerScrollElement.getStyle().setTop(-(int)Math.round(top - height * 0.5), Unit.PX);
			for (int i = 0; i < valueElements.size(); ++i) {
				if (i == index) {
					element.addClassName(CLASSNAME_CURRENT);
				} else {
					element.removeClassName(CLASSNAME_CURRENT);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			//TODO
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
