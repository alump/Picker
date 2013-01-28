package org.vaadin.alump.picker.gwt.client.connect;

import org.vaadin.alump.picker.gwt.client.GwtPicker;
import org.vaadin.alump.picker.gwt.client.shared.PickerState;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.vaadin.client.VConsole;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
@Connect(org.vaadin.alump.picker.Picker.class)
public class PickerConnector extends AbstractComponentConnector
	implements ValueChangeHandler<Integer> {
	
    protected final PickerServerRpc serverRpc = RpcProxy.create(
            PickerServerRpc.class, this);

    @Override
    public void init() {
        super.init();
        
        getWidget().addValueChangeHandler(this);
    }

    @Override
    public GwtPicker getWidget() {
        return (GwtPicker) super.getWidget();
    }
    
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
    	super.onStateChanged(stateChangeEvent);
    	
    	getWidget().setButtonsVisible(getState().showButtons);
    	
    	int numberOfValues = getState().values.size();
    	getWidget().setValueAmount(numberOfValues);
    	    	
    	for (int i = 0; i < numberOfValues; ++i) {
    		getWidget().setValuePresentation(i, getState().values.get(i),
    				getState().valuesInHTML);
    	}
    	
    	if (getState().currentValue != null) {
    		getWidget().setValue(getState().currentValue);
    	} else {
    		VConsole.error("Current value missing");
    	}
    }
    
    @Override
    public PickerState getState() {
        return (PickerState) super.getState();
    }

	@Override
	public void onValueChange(ValueChangeEvent<Integer> event) {
		serverRpc.onValueChanged(event.getValue());
	}
}
