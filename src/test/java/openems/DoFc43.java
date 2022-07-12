
package openems;

import java.io.IOException;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.FC43WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.FC43WriteTaskResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class DoFc43 {

	public static class Codes {

		public static final int CODE_41 = 65;
		public static final int CODE_40 = 64;
		public static final int CODE_43 = 43;
		public static final int CODE_44 = 44;

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

		if (getFc43ResponseRTU(transport).getFunctionCode() == Codes.CODE_43) {
			System.out.println(" Success in fc 43 ");
		} else {
			System.out.println(" Failure in fc 43 ");
		}

	}

	private static FC43WriteTaskResponse getFc43ResponseRTU(ModbusRTUTransport transport) {
		FC43WriteTaskRequest fc43WriteTaskRequest = new FC43WriteTaskRequest();

		
		fc43WriteTaskRequest.setUnitID(UNITID);

		ModbusRequest req = fc43WriteTaskRequest;
		req.setUnitID(UNITID);

		ModbusTransaction trans = transport.createTransaction();
		trans.setRequest(req);

		try {
			trans.execute();
		} catch (ModbusException e) {
			e.printStackTrace();
		}
		FC43WriteTaskResponse fC43WriteTaskResponse = (FC43WriteTaskResponse) trans.getResponse();
		return fC43WriteTaskResponse;

	}

}
