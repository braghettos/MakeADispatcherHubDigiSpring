package com.makeacoffee.dh.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBee64BitAddress;
import com.digi.xbee.api.models.XBeeMessage;

@RestController
public class DispatcherHubController {

	/* Constants */
    // TODO Replace with the port where your sender module is connected to.
    private static final String PORT = "/dev/ttyAMA0";
    // TODO Replace with the baud rate of your sender module.
    private static final int BAUD_RATE = 9600;
	
    private final Logger logger = LoggerFactory.getLogger(DispatcherHubController.class);
    
    private final XBeeDevice myLocalXBeeDevice = new XBeeDevice(PORT, BAUD_RATE);
    
    @RequestMapping("/ed/{deviceId}/{eventId}/sync")
    public String fireEventSync(@PathVariable String deviceId, @PathVariable String eventId) {
    	
    	logger.info("Device to contact: " + deviceId + " for action: " + eventId);
    	
    	// Instantiate a remote XBee device object.
    	RemoteXBeeDevice myRemoteXBeeDevice = new RemoteXBeeDevice(myLocalXBeeDevice, new XBee64BitAddress(deviceId));
    	
    	XBeeMessage xbeeMessage = null;
    	
    	// Send data using the remote object.
    	try {
    		
    		if(!myLocalXBeeDevice.isOpen())
				myLocalXBeeDevice.open();
    		
			myLocalXBeeDevice.sendData(myRemoteXBeeDevice, eventId.getBytes());
					
			// Read data sent by the remote XBee device.
			xbeeMessage = myLocalXBeeDevice.readDataFrom(myRemoteXBeeDevice);

			logger.info("message received from " + myRemoteXBeeDevice.get64BitAddress() + ":" + xbeeMessage.getDataString());
			
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
    	return xbeeMessage.getDataString();
    }
    
    
    @RequestMapping("/ed/{deviceId}/{eventId}/async")
    public boolean fireEventAsync(@PathVariable String deviceId, @PathVariable String eventId) {
    	
    	logger.info("Device to contact: " + deviceId + " for action: " + eventId);
    	
    	// Instantiate a remote XBee device object.
    	RemoteXBeeDevice myRemoteXBeeDevice = new RemoteXBeeDevice(myLocalXBeeDevice, new XBee64BitAddress(deviceId));
    	
    	IDataReceiveListener waitForOK = null;
    	
    	boolean dataSent = false;
    	
    	// Send data using the remote object.
    	try {
    		
    		if(!myLocalXBeeDevice.isOpen())
				myLocalXBeeDevice.open();
    		
			myLocalXBeeDevice.sendDataAsync(myRemoteXBeeDevice, eventId.getBytes());
			
			dataSent = true;
			
			waitForOK = new IDataReceiveListener () {
				
				@Override
				public void dataReceived(XBeeMessage xbeeMessage) {

					logger.info("From" + xbeeMessage.getDevice().get64BitAddress() + ":" + xbeeMessage.getDataString());
					
					String URL = "http://make-a-cloud.appspot.com/gae/dh/" + xbeeMessage.getDevice().get64BitAddress()
							+ "/" + xbeeMessage.getDataString();
					
					logger.info("Calling cloud with " + URL);
					
					/*RestTemplate restTemplate = new RestTemplate();
					ResponseEntity<String> update_stato_response = restTemplate.exchange("http://make-a-cloud.appspot.com/gae/dh/{device_id}/{event_id_feedback}", HttpMethod.GET, new HttpEntity<Object>(new HttpHeaders()), String.class, address_device, payload_device);
					String update_stato = update_stato_response.getBody();
					
					logger.info("From cloud:" + update_stato);*/
					
					XBeeMessage xbeeMessageTmp = null;
			    	
			    	// Read data sent by the remote XBee device.
					xbeeMessageTmp = myLocalXBeeDevice.readDataFrom(myRemoteXBeeDevice);

					logger.info("message received from " + myRemoteXBeeDevice.get64BitAddress() + ":" + xbeeMessageTmp.getDataString());
					
					myLocalXBeeDevice.removeDataListener(this);
						
					logger.info("Removed MyDataReceiveListener from DispatcherHubController");
					
				}
			};
			
			myLocalXBeeDevice.addDataListener(waitForOK);
			
			logger.info("waiting for data...");
			
		} catch (XBeeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			dataSent = false;
		}
    	
    	return dataSent;
    }
    
	/*@RequestMapping("/ed/{deviceId}/status")
	public void status(@PathVariable String deviceId) {
				
		try {
			
			if(!myLocalXBeeDevice.isOpen())
				myLocalXBeeDevice.open();
			
			// Read cached parameters.
			myLocalXBeeDevice.readDeviceInfo();
			
			logger.info("Cached parameters");
			logger.info("----------------------");
			logger.info(" - 64-bit address:   " + myLocalXBeeDevice.get64BitAddress());
			logger.info(" - 16-bit address:   " + myLocalXBeeDevice.get16BitAddress());
			logger.info(" - Node Identifier:  " + myLocalXBeeDevice.getNodeID());
			logger.info(" - Firmware version: " + myLocalXBeeDevice.getFirmwareVersion());
			logger.info(" - Hardware version: " + myLocalXBeeDevice.getHardwareVersion());
			logger.info("");
			
			//myLocalXBeeDevice.close();
			
		} catch (XBeeException e) {
			e.printStackTrace();
			//myLocalXBeeDevice.close();
		}
	}*/
}
