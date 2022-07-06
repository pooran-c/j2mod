package openems;

import java.io.IOException;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.FC40WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.FC40WriteTaskResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.AdditionalRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class dofc40 {

	public static class Codes {

		public static final int CODE_41 = 65;
		public static final int CODE_40 = 64;
		public static final int CODE_43 = 43;
		public static final int CODE_44 = 44;

	}

	public static String PORTNAME = "COM3";
	public static int REFERENCE = 1;
	public static int DATA = 513227; // 254
	public static int UNITID = 1;
	public static int BAUDRATE = 19200;

	public static void main(String args[]) {

		SerialParameters parms = new SerialParameters();
		parms.setPortName(PORTNAME);
		parms.setBaudRate(BAUDRATE);
		parms.setOpenDelay(1000);
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

		if (getFc40ResponseRTU(transport, DATA).getFunctionCode() == Codes.CODE_40) {
			System.out.println(" Success in fc 40 ");
		} else {
			System.out.println(" Failure in fc 40 ");
		}

	}

	private static FC40WriteTaskResponse getFc40ResponseRTU(ModbusRTUTransport transport, int sizeOfTheUpdateFile) {

		FC40WriteTaskRequest fc40WriteTaskRequest = new FC40WriteTaskRequest();
		
		

		byte[] s = new byte[] { (byte) 0x00, (byte) 0x08, (byte) 0xAC, (byte) 0xFE };
		//writeRegisters = new AdditionalRegister(s);

		// Set the data to be written into register
		fc40WriteTaskRequest.setRegister(new AdditionalRegister(s));

		// Set the reference
		// fc40WriteTaskRequest.setReference(REFERENCE);

		// Set the Unit id
		fc40WriteTaskRequest.setUnitID(UNITID);

		ModbusRequest req = fc40WriteTaskRequest;
		req.setUnitID(UNITID);

		ModbusTransaction trans = transport.createTransaction();
		trans.setRequest(req);

		// Execute the transaction
		try {
			trans.execute();
		} catch (ModbusException e) {
			e.printStackTrace();
		}
		FC40WriteTaskResponse fC40WriteTaskResponse = (FC40WriteTaskResponse) trans.getResponse();
		return fC40WriteTaskResponse;
	}
}
