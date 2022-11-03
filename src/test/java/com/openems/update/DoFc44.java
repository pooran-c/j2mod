package com.openems.update;

import java.io.IOException;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.FC44WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.FC44WriteTaskResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class DoFc44 {

	public static class Codes {

		public static final int CODE_41 = 41;
		public static final int CODE_40 = 40;
		public static final int CODE_43 = 43;
		public static final int CODE_44 = 68;

	}

	public static String PORTNAME = "COM3";
	public static int REFERENCE = 1;
	public static int DATA = 513227;
	public static int UNITID = 1;
	public static int BAUDRATE = 19200;

	public static void main(String args[]) {

		SerialParameters parms = new SerialParameters();
		parms.setPortName(PORTNAME);
		parms.setBaudRate(BAUDRATE);
		parms.setDatabits(8);
		parms.setParity(AbstractSerialConnection.NO_PARITY);
		parms.setStopbits(1);
		parms.setFlowControlIn(AbstractSerialConnection.FLOW_CONTROL_DISABLED);
		parms.setEcho(false);

		SerialConnection serialConnection = new SerialConnection(parms);
		ModbusRTUTransport transport = new ModbusRTUTransport();

		try {
			transport.setCommPort(serialConnection);
		} catch (IOException e) {
			e.printStackTrace();
		}
		transport.setEcho(false);
		transport.setTimeout(500);
		FC44WriteTaskResponse res = getFc44ResponseRTU(transport);
		if (res.getFunctionCode() == Codes.CODE_44) {
			System.out.println("Response data " + res.getResponseData());
			System.out.println("Response status " + res.getResponseStatus());
			System.out.println(" Success in fc 44 ");
		} else {
			System.out.println(" Failure in fc 44 ");
		}

	}

	private static FC44WriteTaskResponse getFc44ResponseRTU(ModbusRTUTransport transport) {

		FC44WriteTaskRequest fc44WriteTaskRequest = new FC44WriteTaskRequest();
		fc44WriteTaskRequest.setUnitID(UNITID);

		ModbusRequest req = fc44WriteTaskRequest;
		req.setUnitID(UNITID);

		ModbusTransaction trans = transport.createTransaction();
		trans.setRequest(req);

		try {
			trans.execute();
		} catch (ModbusException e) {
			e.printStackTrace();
		}
		FC44WriteTaskResponse fC44WriteTaskResponse = (FC44WriteTaskResponse) trans.getResponse();
		
		fC44WriteTaskResponse.getResponseData();
		return fC44WriteTaskResponse;

	}

}
