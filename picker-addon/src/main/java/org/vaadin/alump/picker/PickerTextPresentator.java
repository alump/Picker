package org.vaadin.alump.picker;

/**
 * Simple generator that uses toString function to generate plain text
 * presentation for the values.
 */
public class PickerTextPresentator implements PickerValuePresentator {

    public static String nullString = "null";

    @Override
    public String generate(Object value) {
        if (value == null) {
            return nullString;
        } else {
            return value.toString();
        }
    }

    /**
     * Define string given when value object is null
     * 
     * @param value
     *            Value given for null objects
     */
    public void setNullString(String value) {
        nullString = value;
    }

    @Override
    public boolean isHtml() {
        return false;
    }

}
