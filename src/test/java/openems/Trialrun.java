package openems;

import java.io.IOException;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleInputRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class Trialrun {

	public static void main(String args[]) {

		SerialParameters parms = new SerialParameters();
		parms.setPortName("COM4");
		parms.setBaudRate(19200);
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

		WriteSingleRegisterResponse respo = getXX(transport);

		System.out.println("respo.getDataLength == >  " + respo.getDataLength());

		System.out.println("hex code == >" + respo.getHexMessage());

		System.out.println("function code == >" + respo.getFunctionCode());

		byte[] res = respo.getMessage();
		for (byte b : res) {
			System.out.print(b + " ");
		}

	}

	private static WriteSingleRegisterResponse getXX(ModbusRTUTransport transport) {

		WriteSingleRegisterRequest ww = new WriteSingleRegisterRequest();

		SimpleInputRegister simpleInputRegister = new SimpleInputRegister(511);

		ww.setRegister(simpleInputRegister);
		ww.setReference(0);
		ww.setUnitID(2);

		ModbusRequest req = ww;
		req.setUnitID(2);
		ModbusTransaction trans = transport.createTransaction();
		trans.setRequest(req);

		try {
			trans.execute();
		} catch (ModbusException e) {
			e.printStackTrace();
		}
		WriteSingleRegisterResponse RegistersResponse = (WriteSingleRegisterResponse) trans.getResponse();
		return RegistersResponse;
	}

}
