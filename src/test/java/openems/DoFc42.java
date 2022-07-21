package openems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.FC42WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class DoFc42 {
	private final static int SIZEOFPAYLOAD = 128;

	public static class Codes {
		public static final int CODE_42 = 66;

	}

	public static String PORTNAME = "COM4";
	public static int REFERENCE = 1;
	public static int UNITID = 1;
	public static int BAUDRATE = 19200;
	public static String PATH = "C:\\ATLUPDATE\\feneconR0.0.bin";

	public static void main(String args[]) {

		byte[] allBytes = null;
		try {
			allBytes = Files.readAllBytes(Paths.get(PATH));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

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
//		transport.setTimeout(50);

		if (getFc42ResponseRTU(transport, allBytes)) {
			System.out.println(" Success in fc 42 ");
		} else {
			System.out.println(" Failure in fc 42 ");
		}

	}

	private static boolean getFc42ResponseRTU(ModbusRTUTransport transport, byte[] allBytes) {

		byte[][] newArray = getData(allBytes, SIZEOFPAYLOAD);

		boolean isSuccesful = false;
		ModbusRequest req = null;

		int counter = 1;
		for (byte[] b : newArray) {

			
			FC42WriteTaskRequest fc42WriteTaskRequest = new FC42WriteTaskRequest(Utils.hexStringToByteArray(counter, 4, 2));
			fc42WriteTaskRequest.setRegister(new SimpleRegister(b));

			counter++;
			req = fc42WriteTaskRequest;

			req.setUnitID(UNITID);

			ModbusTransaction trans = transport.createTransaction();
			trans.setRequest(req);
			try {
				trans.execute();
				isSuccesful = true;
			} catch (ModbusException e) {
				isSuccesful = false;
				e.printStackTrace();
			}

		}

		return isSuccesful;
	}

	private static byte[][] getData(byte[] actualArray, int dataSize) {

		int len = actualArray.length;

		int m = len / dataSize;

		int counter = 0;

		byte[][] newArray = new byte[m + 1][dataSize];

		for (int i = 0; i < len - dataSize + 1; i += dataSize)
			newArray[counter++] = Arrays.copyOfRange(actualArray, i, i + dataSize);

		if (len % dataSize != 0)
			newArray[counter] = Arrays.copyOfRange(actualArray, len - len % dataSize, len);

//		for (byte[] b : newArray) {
//			for (byte b1 : b) {
//				System.out.print(b1 + "| "  );
//			}
//			System.out.println();
//		}
		return newArray;

	}
}
