package openems;

import java.io.IOException;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class ReadSingleRegister {

	public static String PORTNAME = "COM3";
	public static int REFERENCE = 10000;
//	public static int DATA = 572042494; // 254
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
		transport.setTimeout(50);

		ReadMultipleRegistersResponse response = getReadRegisterRTU(transport, REFERENCE);
		System.out.println(response.getHexMessage());

		ReadMultipleRegistersResponse response1 = getReadRegisterRTU(transport, REFERENCE + 1);
		System.out.println(response1.getHexMessage());

	}

	private static ReadMultipleRegistersResponse getReadRegisterRTU(ModbusRTUTransport transport, int ref) {

		ReadMultipleRegistersRequest readMultipleRegistersRequest = new ReadMultipleRegistersRequest(ref, 1);
		readMultipleRegistersRequest.setUnitID(UNITID);

		ModbusRequest req = readMultipleRegistersRequest;
		req.setUnitID(UNITID);

		ModbusTransaction trans = transport.createTransaction();
		trans.setRequest(req);

		// Execute the transaction
		try {
			trans.execute();
		} catch (ModbusException e) {
			e.printStackTrace();
		}

		ReadMultipleRegistersResponse readMultipleRegistersResponse = (ReadMultipleRegistersResponse) trans
				.getResponse();

		return readMultipleRegistersResponse;
	}

}
