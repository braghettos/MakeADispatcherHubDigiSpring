package com.makeacoffee.dh.models;

import com.digi.xbee.api.AbstractXBeeDevice;

public class MakeAXBeeMessage {

	// Variables.
	private final AbstractXBeeDevice remoteXBeeDevice;
	private final byte[] data;
	private boolean isBroadcast;
	
	public MakeAXBeeMessage(AbstractXBeeDevice remoteXBeeDevice, byte[] data) {
		this(remoteXBeeDevice, data, false);
	}
	
	public MakeAXBeeMessage(AbstractXBeeDevice remoteXBeeDevice, byte[] data, boolean isBroadcast) {
		if (remoteXBeeDevice == null)
			throw new NullPointerException("Remote XBee device cannot be null.");
		if (data == null)
			throw new NullPointerException("Data cannot be null.");
		
		this.remoteXBeeDevice = remoteXBeeDevice;
		this.data = data;
		this.isBroadcast = isBroadcast;
	}

	public boolean isBroadcast() {
		return isBroadcast;
	}

	public void setBroadcast(boolean isBroadcast) {
		this.isBroadcast = isBroadcast;
	}

	public AbstractXBeeDevice getRemoteXBeeDevice() {
		return remoteXBeeDevice;
	}

	public byte[] getData() {
		return data;
	}
	
	
}
