package org.vaadin.alump.picker.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;

public class GwtPicker extends Widget implements
        HasValueChangeHandlers<Integer> {

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

    public final static String CLASSNAME_STEPPING = CLASSNAME + "-stepping";
    public final static String CLASSNAME_DRAGGING = CLASSNAME + "-dragging";

    protected final static int DRAG_TRESSHOLD_Y = 10;

    protected Element upButtonElement;
    protected Element downButtonElement;
    protected Element outerScrollElement;
    protected Element innerScrollElement;
    protected Element overlayElement;

    protected List<Element> valueElements = new ArrayList<Element>();
    protected Integer currentValue = null;
    protected PickerEventHandler eventHandler = new PickerEventHandler();

    protected TransitionMode currentTransitionMode = TransitionMode.NONE;

    protected int scrollTopValue = 0;

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
        eventHandler.setCaptureElement(overlayElement);

        setStylePrimaryName(CLASSNAME);
    }

    @Override
    public void onAttach() {
        super.onAttach();

        if (BrowserInfo.get().isTouchDevice()) {
            VConsole.log("Connect picker events in touch mode");
            addHandler(eventHandler, ClickEvent.getType());

            DOM.sinkEvents((com.google.gwt.user.client.Element) overlayElement,
                    Event.TOUCHEVENTS);
            DOM.setEventListener(
                    (com.google.gwt.user.client.Element) overlayElement,
                    eventHandler);
        } else {
            VConsole.log("Connect picker events in mouse mode");
            addHandler(eventHandler, ClickEvent.getType());

            // Buggy
            // DOM.sinkEvents((com.google.gwt.user.client.Element)
            // overlayElement,
            // Event.MOUSEEVENTS);
            // DOM.setEventListener(
            // (com.google.gwt.user.client.Element) overlayElement,
            // eventHandler);
        }

    }

    /**
     * Event handler class
     */
    protected class PickerEventHandler implements ClickHandler,
            MouseDownHandler, MouseMoveHandler, MouseUpHandler,
            TouchStartHandler, TouchMoveHandler, TouchEndHandler, EventListener {

        private boolean dragged = false;
        private int dragStartY = -1;
        private int dragStartIndex = -1;
        private Element captureElement = null;

        public void setCaptureElement(Element element) {
            captureElement = element;
        }

        @Override
        public void onClick(ClickEvent event) {
            Element element = Element.as(event.getNativeEvent()
                    .getEventTarget());

            if (upButtonElement.isOrHasChild(element)) {
                event.preventDefault();
                event.stopPropagation();
                setPrevValue();
            } else if (downButtonElement.isOrHasChild(element)) {
                event.preventDefault();
                event.stopPropagation();
                setNextValue();
            } else if (overlayElement != null
                    && overlayElement.isOrHasChild(element)) {
                VConsole.log("onClick");
                GwtPicker.this.setValueAtPosition(
                        Util.getTouchOrMouseClientX(event.getNativeEvent()),
                        Util.getTouchOrMouseClientY(event.getNativeEvent()),
                        true, null);
            }

            Event.releaseCapture(captureElement);
        }

        @Override
        public void onTouchStart(TouchStartEvent event) {
            onDragStart(event.getNativeEvent());
        }

        protected void onDragStart(NativeEvent event) {
            if (dragStartY >= 0) {
                return;
            }

            Element element = Element.as(event.getEventTarget());
            if (!overlayElement.isOrHasChild(element)) {
                return;
            }

            VConsole.log("onDragStart");

            dragStartY = Util.getTouchOrMouseClientY(event);
            dragStartIndex = GwtPicker.this.currentValue;
            setTransitionMode(TransitionMode.DRAGGING);

        }

        @Override
        public void onTouchMove(TouchMoveEvent event) {
            onDragMove(event.getNativeEvent());
        }

        protected void onDragMove(NativeEvent event) {
            if (dragStartY < 0) {
                return;
            }

            int dragDist = Math.round(dragStartY
                    - Util.getTouchOrMouseClientY(event));

            if (!dragged) {
                if (Math.abs(dragDist) < DRAG_TRESSHOLD_Y) {
                    dragged = true;
                    Event.setCapture(captureElement);
                    event.preventDefault();
                    event.stopPropagation();
                }
            } else {
                int top = GwtPicker.this.scrollTopValue - dragDist;
                setScrollTop(top, false);
                GwtPicker.this.setValueAtMiddle(false, false);
                event.preventDefault();
                event.stopPropagation();
            }

        }

        @Override
        public void onTouchEnd(TouchEndEvent event) {
            onDragEnd(event.getNativeEvent());
        }

        public void onDragEnd(NativeEvent event) {
            dragStartY = -1;

            if (!dragged) {
                return;
            }

            VConsole.log("onDragEnd");

            Event.releaseCapture(captureElement);
            dragged = false;
            event.preventDefault();
            event.stopPropagation();

            GwtPicker.this.setValue(currentValue, true,
                    dragStartIndex != currentValue);
        }

        @Override
        public void onBrowserEvent(Event event) {
            switch (event.getTypeInt()) {
            case Event.ONTOUCHSTART:
            case Event.ONMOUSEDOWN:
                onDragStart(event);
                break;
            case Event.ONTOUCHMOVE:
            case Event.ONMOUSEMOVE:
                onDragMove(event);
                break;
            case Event.ONTOUCHEND:
            case Event.ONMOUSEOUT:
                onDragEnd(event);
                break;
            }
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            onDragMove(event.getNativeEvent());
        }

        @Override
        public void onMouseMove(MouseMoveEvent event) {
            onDragMove(event.getNativeEvent());
        }

        @Override
        public void onMouseUp(MouseUpEvent event) {
            onDragEnd(event.getNativeEvent());
        }

    }

    /**
     * Select value at middle
     */
    protected void setValueAtMiddle(boolean move, Boolean fire) {

        int y = (int) Math.round(overlayElement.getAbsoluteTop()
                + overlayElement.getClientHeight() / 2.0);
        Integer index = findValue(y);
        if (index != null) {
            setValue(index, move, fire);
        }
    }

    protected void setValueAtPosition(int clientX, int clientY, boolean move,
            Boolean fire) {

        Integer index = findValue(clientY);
        if (index != null) {
            setValue(index, move, fire);
        }
    }

    /**
     * Find index of value at given client Y value
     * 
     * @param clientY
     * @return Index of value or null if out of bounds
     */
    protected Integer findValue(int clientY) {
        for (Element value : valueElements) {
            int valY = value.getAbsoluteTop();
            if (clientY > valY) {
                int valB = valY + value.getClientHeight();
                if (clientY < valB) {
                    return valueElements.indexOf(value);
                }
            }
        }

        return null;
    }

    /**
     * Show or hide browse buttons
     * 
     * @param visible
     *            true to show, false to hide
     */
    public void setButtonsVisible(boolean visible) {
        if (visible) {
            addStyleName(CLASSNAME_BUTTONS);
        } else {
            removeStyleName(CLASSNAME_BUTTONS);
        }
    }

    /**
     * How many values there are in Picker
     * 
     * @param amount
     *            Amount of values
     */
    public void setValueAmount(int amount) {
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

    /**
     * Define presentation for given value
     * 
     * @param index
     *            Index of value
     * @param presentation
     *            Presentation of value
     * @param isHTML
     *            Is content safe HTML (true), or plain text (false)
     */
    public void setValuePresentation(int index, String presentation,
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

    /**
     * Set previous value active
     */
    public void setPrevValue() {
        setPrevValue(1);
    }

    /**
     * Set previous value active
     * 
     * @param steps
     *            How many steps to previous
     */
    public void setPrevValue(int steps) {
        if (currentValue != null) {
            int newIndex = currentValue - steps;
            if (newIndex < 0) {
                newIndex = 0;
            }
            setValue(newIndex, true, true);
        }
    }

    /**
     * Set next value active
     */
    public void setNextValue() {
        setNextValue(1);
    }

    /**
     * Set next value active
     * 
     * @param steps
     *            How many steps to next
     */
    public void setNextValue(int steps) {
        if (currentValue != null) {
            int newIndex = currentValue + steps;
            if (newIndex >= valueElements.size()) {
                newIndex = valueElements.size() - 1;
            }
            setValue(newIndex, true, true);
        }
    }

    /**
     * Set value with given index active
     * 
     * @param index
     *            Index of value
     */
    public void setValue(int index) {
        setValue(index, true, false);
    }

    /**
     * Change value of picker
     * 
     * @param index
     *            New index to be selected
     * @param move
     *            If picker should scroll to given position
     * @param fire
     *            If value change should be fired. If null it will be fired if
     *            value has changed.
     */
    protected void setValue(int index, boolean move, Boolean fire) {
        try {

            boolean doFire = (currentValue == null || currentValue != index);
            if (fire != null) {
                doFire = fire;
            }
            currentValue = index;

            if (move) {
                Element element = valueElements.get(currentValue);
                double elementTop = element.getOffsetTop();
                double elementHeight = element.getClientHeight();
                double scrollerHeight = outerScrollElement.getClientHeight();
                int scrollTop = (int) Math.round(scrollerHeight / 2
                        - elementHeight / 2 - elementTop);
                setTransitionMode(TransitionMode.STEPPING);
                setScrollTop(scrollTop, true);
            }

            updateCurrentValueStyles();

            if (doFire) {
                ValueChangeEvent.fire(this, currentValue);
            }

        } catch (IndexOutOfBoundsException e) {
            VConsole.error("Failed to set value");
        }
    }

    /**
     * Set scroll top value to style
     * 
     * @param topValue
     *            New top value
     * @param updateValue
     *            If value should be stored to scrollTopValue
     */
    protected void setScrollTop(int topValue, boolean updateValue) {
        innerScrollElement.getStyle().setTop(topValue, Unit.PX);
        if (updateValue) {
            scrollTopValue = topValue;
        }
    }

    /**
     * Makes sure only current value has current value style name
     */
    protected void updateCurrentValueStyles() {
        if (currentValue == null) {
            for (Element element : valueElements) {
                element.removeClassName(CLASSNAME_CURRENT);
            }
        } else {
            for (int i = 0; i < valueElements.size(); ++i) {
                if (i == currentValue) {
                    valueElements.get(i).addClassName(CLASSNAME_CURRENT);
                } else {
                    valueElements.get(i).removeClassName(CLASSNAME_CURRENT);
                }
            }
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<Integer> handler) {
        HandlerRegistration registration = this.addHandler(handler,
                ValueChangeEvent.getType());
        return registration;
    }

    protected enum TransitionMode {
        NONE(null), STEPPING(CLASSNAME_STEPPING), DRAGGING(CLASSNAME_DRAGGING);

        private final String className;

        private TransitionMode(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        public boolean hasClassName() {
            return className != null;
        }
    }

    protected void setTransitionMode(TransitionMode mode) {
        if (currentTransitionMode != mode) {
            if (currentTransitionMode.hasClassName()) {
                innerScrollElement.removeClassName(currentTransitionMode
                        .getClassName());
            }
            currentTransitionMode = mode;
            if (currentTransitionMode.hasClassName()) {
                innerScrollElement.addClassName(currentTransitionMode
                        .getClassName());
            }
        }
    }
}
