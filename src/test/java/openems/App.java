package openems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.FC40WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.FC40WriteTaskResponse;
import com.ghgande.j2mod.modbus.msg.FC41WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.FC41WriteTaskResponse;
import com.ghgande.j2mod.modbus.msg.FC42WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.FC42WriteTaskResponse;
import com.ghgande.j2mod.modbus.msg.FC43WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.FC43WriteTaskResponse;
import com.ghgande.j2mod.modbus.msg.FC44WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.FC44WriteTaskResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class App {

	// Initial state
	public static StateMachine stateMachine = StateMachine.FC40;

	// Necessary Codes
	public static class Codes {

		public static final int CODE_40 = 64;
		public static final int CODE_41 = 65;
		public static final int CODE_42 = 66;
		public static final int CODE_43 = 67;
		public static final int CODE_44 = 68;
		public static final int UNITD_ID = 1;
		private final static int SIZEOFPAYLOAD = 128;

	}

	// State Machine
	public enum StateMachine {
		FC40, //
		FC41, //
		FC42, //
		FC43, //
		FC44, //
		FINISHED
	}

	public static void main(String[] args) throws InterruptedException {

		if (args.length < 3) {
			printUsage();
			killProcess();
		}

		String PORTNAME = args[0];
		String BAUDRATE = args[1];
		String SizeOfUpdateFile = args[2];
		String PATH = args[3];

		SerialParameters parms = getSerialParam(PORTNAME, BAUDRATE);
		SerialConnection serialConnection = new SerialConnection(parms);
		ModbusRTUTransport transport1 = new ModbusRTUTransport();
		ModbusRTUTransport transport2 = new ModbusRTUTransport();
		ModbusRTUTransport transport3 = new ModbusRTUTransport();
		ModbusRTUTransport transport4 = new ModbusRTUTransport();
		ModbusRTUTransport transport5 = new ModbusRTUTransport();
		try {
			transport1.setCommPort(serialConnection);
			transport2.setCommPort(serialConnection);
			transport3.setCommPort(serialConnection);
			transport4.setCommPort(serialConnection);
			transport5.setCommPort(serialConnection);
		} catch (IOException e) {
			killProcess();
			e.printStackTrace();
		}
		transport1.setEcho(false);
		transport2.setEcho(false);
		transport3.setEcho(false);
		transport4.setEcho(false);
		transport5.setEcho(false);

		boolean isFinished;
		do {
			isFinished = false;
			switch (stateMachine) {
			case FC40:

				FC40WriteTaskResponse fc40WriteTaskResponse = getFc40ResponseRTU(transport1, //
						Integer.parseInt(SizeOfUpdateFile));

//				if (fc40WriteTaskResponse.getFunctionCode() == Codes.CODE_40) {
//					System.out.println("-----------------------------------------");
//					System.out.println("Step 1 finished, Chech the response below");
//					System.out.println(fc40WriteTaskResponse.getHexMessage());
//
//					isFinished = changeState(StateMachine.FC41);
//					break;
//				}
				isFinished = changeState(StateMachine.FC41);
				Thread.sleep(500);
				break;
			case FC41:

				FC41WriteTaskResponse fc41WriteTaskResponse = getFc41ResponseRTU(transport2);

//				if (fc41WriteTaskResponse.getFunctionCode() == Codes.CODE_41) {
//					System.out.println("-----------------------------------------");
//					System.out.println("Step 2 finished, Chech the response below");
//					System.out.println(fc41WriteTaskResponse.getHexMessage());
//					isFinished = changeState(StateMachine.FC42);
//					break;
//				}

				isFinished = changeState(StateMachine.FC42);
				Thread.sleep(3000);
				break;
			case FC42:
				boolean isSuccesful = false;
				byte[] allBytes = null;
				try {
					allBytes = Files.readAllBytes(Paths.get(PATH));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				isSuccesful = getFc42ResponseRTU(transport3, allBytes);

				if (isSuccesful) {
					isFinished = changeState(StateMachine.FC43);
				} else {
					isFinished = changeState(StateMachine.FC42);
				}
				Thread.sleep(500);
				break;
			case FC43:

				FC43WriteTaskResponse fc43WriteTaskResponse = getFc43ResponseRTU(transport4);

				if (fc43WriteTaskResponse.getFunctionCode() == Codes.CODE_43) {
					System.out.println("-----------------------------------------");
					System.out.println("Step 4 finished, Chech the response below");
					System.out.println(fc43WriteTaskResponse.getHexMessage());
					isFinished = changeState(StateMachine.FC44);
					break;
				}

				isFinished = changeState(StateMachine.FC43);
				break;
			case FC44:

				FC44WriteTaskResponse fc44WriteTaskResponse = getFc44ResponseRTU(transport5);

				// Logic to chekc if this is finished
				if (fc44WriteTaskResponse.getFunctionCode() == Codes.CODE_44) {
					System.out.println("-----------------------------------------");
					System.out.println("Step 5 finished, Chech the response below");
					System.out.println(fc44WriteTaskResponse.getHexMessage());
					isFinished = changeState(StateMachine.FINISHED);
					break;
				}
				isFinished = changeState(StateMachine.FC44);
				break;
			case FINISHED:
				System.out.println("Update Finished");
				break;

			}

		} while (isFinished);

	}

	/**
	 * Get the FC 40 response
	 * 
	 * @param transport           The ModbusRTUTransport
	 * @param sizeOfTheUpdateFile the the payload is the "size of the update file"
	 * @return {@code FC40WriteTaskResponse}
	 */
	private static FC40WriteTaskResponse getFc40ResponseRTU(ModbusRTUTransport transport, int sizeOfTheUpdateFile) {

		byte[] sizeOfTheUpdateFileToByte = Utils.hexStringToByteArray(sizeOfTheUpdateFile);

		FC40WriteTaskRequest fc40WriteTaskRequest = new FC40WriteTaskRequest();
		fc40WriteTaskRequest.setRegister(new SimpleRegister(sizeOfTheUpdateFileToByte));
		fc40WriteTaskRequest.setUnitID(Codes.UNITD_ID);

		return (FC40WriteTaskResponse) executeProcess(transport, fc40WriteTaskRequest);

	}

	/**
	 * Get the FC 41 response
	 * 
	 * @param transport The ModbusRTUTransport
	 * @return {@code FC41WriteTaskResponse}
	 */
	private static FC41WriteTaskResponse getFc41ResponseRTU(ModbusRTUTransport transport) {

		FC41WriteTaskRequest fc41WriteTaskRequest = new FC41WriteTaskRequest();
		fc41WriteTaskRequest.setUnitID(Codes.UNITD_ID);

		return (FC41WriteTaskResponse) executeProcess(transport, fc41WriteTaskRequest);

	}

	/**
	 * Make the series of FC42 requests
	 * 
	 * @param transport The ModbusRTUTransport
	 * @param allBytes  The payload, the update file is converted into bytes.
	 * @return boolean true if all the request is complete, false if not all the
	 *         request is complete.
	 */
	private static boolean getFc42ResponseRTU(ModbusRTUTransport transport, byte[] allBytes) {

		byte[][] payLoad = Utils.getData(allBytes, Codes.SIZEOFPAYLOAD);

		int counter = 1;
		for (byte[] load : payLoad) {

			FC42WriteTaskRequest fc42WriteTaskRequest = new FC42WriteTaskRequest(counter);
			fc42WriteTaskRequest.setRegister(new SimpleRegister(load));

			counter++;

			FC42WriteTaskResponse fc42response = (FC42WriteTaskResponse) executeProcess(transport, //
					fc42WriteTaskRequest);
			System.out.println("SendDataPacket " + counter + " success! -> " + fc42response.getHexMessage());

		}

		System.out.println("Send Data Finish CMD!");
		return true;

	}

	/**
	 * Get the FC 43 response
	 * 
	 * @param transport The ModbusRTUTransport
	 * @return {@code FC43WriteTaskResponse}
	 */
	private static FC43WriteTaskResponse getFc43ResponseRTU(ModbusRTUTransport transport) {

		FC43WriteTaskRequest fc43WriteTaskRequest = new FC43WriteTaskRequest();
		fc43WriteTaskRequest.setUnitID(Codes.UNITD_ID);

		return (FC43WriteTaskResponse) executeProcess(transport, fc43WriteTaskRequest);
	}

	/**
	 * Get the FC 44 response
	 * 
	 * @param transport The ModbusRTUTransport
	 * @return {@code FC44WriteTaskResponse}
	 */
	private static FC44WriteTaskResponse getFc44ResponseRTU(ModbusRTUTransport transport) {

		FC44WriteTaskRequest fc44WriteTaskRequest = new FC44WriteTaskRequest();
		fc44WriteTaskRequest.setUnitID(Codes.UNITD_ID);

		return (FC44WriteTaskResponse) executeProcess(transport, fc44WriteTaskRequest);
	}

	/**
	 * Helper method to execute the modbusRTUtransport execute
	 * 
	 * @param transport the ModbusRTUTransport
	 * @param req       the ModbusRequest
	 * @return ModbusResponse
	 */
	private static ModbusResponse executeProcess(ModbusRTUTransport transport, ModbusRequest req) {
		req.setUnitID(Codes.UNITD_ID);

		ModbusTransaction trans = transport.createTransaction();
		trans.setRequest(req);

		try {
			trans.execute();
		} catch (ModbusException e) {
			e.printStackTrace();
		}

		return trans.getResponse();

	}

	/**
	 * Get the SerialParameter
	 * 
	 * @param portName Portname to be connected to.
	 * @param baudRate Baudrate of the connection.
	 * @return SerialParamter params
	 */
	private static SerialParameters getSerialParam(String portName, String baudRate) {

		SerialParameters params = new SerialParameters();

		params.setPortName(portName);
		params.setBaudRate(Integer.parseInt(baudRate));
		params.setOpenDelay(1000);
		params.setDatabits(8);
		params.setParity(AbstractSerialConnection.NO_PARITY);
		params.setStopbits(1);
		params.setFlowControlIn(AbstractSerialConnection.FLOW_CONTROL_DISABLED);
		params.setEcho(false);

		return params;
	}

	/**
	 * Simple method to change states of statemachine.
	 * 
	 * @param nextState
	 * @return boolean true if state changed and false if state is not changed
	 */
	private static boolean changeState(StateMachine nextState) {
		if (stateMachine != nextState) {
			stateMachine = nextState;
			return true;
		}
		return false;
	}

	/**
	 * java -jar ATLBATTERYUPDATE.jar port_name baud_rate size_of_the_updatefile
	 * path_of_the_update_file
	 */
	private static void printUsage() {
		System.out.printf(
				"\nUsage:\n  java -jar ATLBATTERYUPDATE.jar COM2 19200 123456 \"C:\\ATLUPDATE\\feneconR0.0.bin\"");
	}

	/**
	 * Kill the update process
	 */
	private static void killProcess() {
		System.out.println("Killing the process");
		System.exit(1);
	}
}
