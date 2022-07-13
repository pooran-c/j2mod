package openems;

import java.io.IOException;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.FC41WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.FC41WriteTaskResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class DoFc41 {

	public static class Codes {

		public static final int CODE_41 = 65;
		public static final int CODE_40 = 64;
		public static final int CODE_43 = 43;
		public static final int CODE_44 = 44;

	}

	public static String PORTNAME = "COM3";
	public static int REFERENCE = 0;
	// public static int DATA = 513227;
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

		if (getFc41ResponseRTU(transport).getFunctionCode() == Codes.CODE_41) {
			System.out.println(" Success in fc 41 ");
		} else {
			System.out.println(" Failure in fc 41 ");
		}

	}

	private static FC41WriteTaskResponse getFc41ResponseRTU(ModbusRTUTransport transport) {

		FC41WriteTaskRequest fc41WriteTaskRequest = new FC41WriteTaskRequest();

		// fc41WriteTaskRequest.setReference(REFERENCE);
		fc41WriteTaskRequest.setUnitID(UNITID);

		ModbusRequest req = fc41WriteTaskRequest;
		req.setUnitID(UNITID);

		ModbusTransaction trans = transport.createTransaction();
		trans.setRequest(req);

		try {
			trans.execute();
		} catch (ModbusException e) {
			e.printStackTrace();
		}
		FC41WriteTaskResponse fC41WriteTaskResponse = (FC41WriteTaskResponse) trans.getResponse();
		return fC41WriteTaskResponse;
	}

}
