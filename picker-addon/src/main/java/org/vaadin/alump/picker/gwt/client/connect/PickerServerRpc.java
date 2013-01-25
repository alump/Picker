package org.vaadin.alump.picker.gwt.client.connect;

import com.vaadin.shared.communication.ServerRpc;

/**
 * Server RPC interface for Picker
 */
public interface PickerServerRpc extends ServerRpc {
	
	/**
	 * Currently active value has been changed by user
	 * @param value Index of value active now
	 */
	public void onValueChanged(Integer value);
}
