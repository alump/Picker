package org.vaadin.alump.picker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.vaadin.alump.picker.gwt.client.connect.PickerServerRpc;
import org.vaadin.alump.picker.gwt.client.shared.PickerState;

import com.vaadin.ui.AbstractComponent;

/**
 * Picker widget is similar to iOS's resolver selector for values (used in day
 * picker). This widget allows to have similar experience on all devices. It
 * also offers way to present custom content (not just text).
 */
@SuppressWarnings("serial")
public class Picker extends AbstractComponent {

    protected PickerValuePresentator itemGenerator;
    protected List<Object> values = new ArrayList<Object>();
    protected Set<ValueChangeListener> valueChangeListeners = new HashSet<ValueChangeListener>();

    /**
     * Listener for value changes. This interface will be replaced with generic
     * Vaadin value change interfaces when implementation of those have been
     * rewritten for Vaadin 7.
     */
    public interface ValueChangeListener {
        /**
         * Current value has been changed
         * 
         * @param value
         *            New value or null if no value selected
         * @param index
         *            Index of value selected
         */
        public void onValueChanged(Object value, Integer index);
    }

    protected PickerServerRpc serverRpc = new PickerServerRpc() {
        @Override
        public void onValueChanged(Integer value) {
            if (getState().currentValue != value) {
                getState().currentValue = value;
                notifyValueChange();
            }
        }
    };

    /**
     * Create new Picker with default generator using toString to generate plain
     * text presentation of value.
     */
    public Picker() {
        this(new PickerTextPresentator());
    }

    /**
     * Create picker with caption and default value presentation generator
     * 
     * @param caption
     *            Caption for Picker
     */
    public Picker(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Create new Picker with custom item presentation generator
     * 
     * @param generator
     *            Generator for value presentation
     */
    public Picker(PickerValuePresentator generator) {
        itemGenerator = generator;
        registerRpc(serverRpc);
    }

    /**
     * Create new Picker with caption
     * 
     * @param caption
     *            Caption for Picker
     * @param generator
     *            Generator for value presentation
     */
    public Picker(String caption, PickerValuePresentator generator) {
        this(generator);
        setCaption(caption);
    }

    /**
     * Add value to options list
     * 
     * @param object
     *            Value added
     */
    public void addValue(Object object) {
        values.add(object);
        markAsDirty();
    }

    /**
     * Remove value from options list
     * 
     * @param object
     *            Value removed
     */
    public void removeValue(Object object) {
        int index = values.indexOf(object);

        if (index < 0) {
            return;
        }

        if (index >= getState().currentValue) {
            getState().currentValue -= 1;
        }

        values.remove(object);
        markAsDirty();
    }

    /**
     * Get all possible values of Picker
     * 
     * @return List of values
     */
    public List<Object> getPossibleValues() {
        return new LinkedList<Object>(values);
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
     * 
     * @param visible
     *            true to show, false to hide
     */
    public void setButtonsVisible(boolean visible) {
        getState().showButtons = visible;
    }

    /**
     * Are buttons visible
     */
    public boolean isButtonsVisible() {
        return getState().showButtons;
    }

    /**
     * Get current value of Picker
     * 
     * @return Current value
     */
    public Object getValue() {
        try {
            return values.get(getState().currentValue);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get index of currently active value.
     * 
     * @return Index of value or null if no value selected
     */
    public Integer getValueIndex() {
        return getState().currentValue;
    }

    /**
     * Set currently active value
     * 
     * @param value
     *            Value set as current value. Will be added to picker if not in
     *            possible values yet.
     */
    public void setValue(Object value) {
        if (!hasValue(value)) {
            addValue(value);
        }

        setValueWithIndex(values.indexOf(value));
    }

    /**
     * Use index to tell value to be selected. Use this if you happen to use
     * multiple equal objects in picker for some reason.
     * 
     * @param index
     *            Index of selected value
     */
    public void setValueWithIndex(int index) {
        if (index >= 0 && index < values.size()) {
            getState().currentValue = index;
        } else {
            throw new IndexOutOfBoundsException("Invalid value index: " + index);
        }
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        getState().values.clear();

        getState().valuesInHTML = itemGenerator.isHtml();

        for (Object value : values) {
            getState().values.add(itemGenerator.generate(value));
        }

        if (getState().currentValue == null || getState().currentValue < 0) {
            if (values.isEmpty()) {
                getState().currentValue = null;
            } else {
                getState().currentValue = 0;
            }
        }
    }

    /**
     * Check if Picker has given value as possible value
     * 
     * @param value
     *            Value searched
     * @return true if value is in possible values list
     */
    public boolean hasValue(Object value) {
        return values.contains(value);
    }

    /**
     * Notify listeners
     */
    protected void notifyValueChange() {
        Object value = this.getValue();
        Integer index = this.getValueIndex();

        for (ValueChangeListener listener : valueChangeListeners) {
            listener.onValueChanged(value, index);
        }
    }

    /**
     * Add new listener for value change events
     * 
     * @param listener
     *            Listener added
     */
    public void addValueChangeListener(ValueChangeListener listener) {
        valueChangeListeners.add(listener);
    }

    /**
     * Remove listener for value change events
     * 
     * @param listener
     *            Listener removed
     */
    public void removeValueChangeListener(ValueChangeListener listener) {
        valueChangeListeners.remove(listener);
    }

}