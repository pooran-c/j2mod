package com.ghgande.j2mod.modbus.openems;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

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
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;


/**
 * usage : java -jar ATLBATTERYUPDATE.jar <Portname> <baudrate>
 * <PathOfTheUpdateFile> <ForceUpdate> <writeToLog> example : java -jar
 * batteryUpdateV2.5.5.jar COM2 19200 \"C:\\ATLUPDATE\\feneconR0.0.bin\"" );
 *
 */
public class App {

	// Initial state
	public static StateMachine stateMachine = StateMachine.FC40;

	// public static StateMachine stateMachine = StateMachine.FINISHED;
	public static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

	public static int MainUpdateStatus = 0;

	public static void main(String[] args) throws InterruptedException, IOException {

		if (args.length < 3) {
			printUsage(args);
			killProcess();
		}

		boolean writeToLogFile = false; // Boolean.parseBoolean(args[4]);

		if (writeToLogFile) {
			try {
				printLog("Writing into log file");
				PrintStream o = new PrintStream(new File(LocalDateTime.now() + "_log.txt"));
				System.setOut(o);
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}
		} else {
			PrintStream console = System.out;
			System.setOut(console);
		}

		String PORT_NAME = args[0];
		String BAUD_RATE = args[1];
		String PATH = args[2];
		int SIZE_OF_THE_UPDATE_FILE = (int) new File(PATH).length();

		boolean forceUpdate = Boolean.parseBoolean(args[3]);

		System.out.println();
		printLog("PortName : " + PORT_NAME + "| BaudRate : " + BAUD_RATE + "| Size of the UpdateFile : "
				+ SIZE_OF_THE_UPDATE_FILE + " kb" + " | forceUpdate : " + forceUpdate);

		SerialParameters params = getSerialParam(PORT_NAME, BAUD_RATE);

		SerialConnection serialConnection = new SerialConnection(params);

		if (!serialConnection.isOpen()) {
			try {
				serialConnection.open();
			} catch (Exception e) {
				printLog("Connection via [" + PORT_NAME + "] failed: " + e.getMessage());
				System.out.println();
				e.printStackTrace();
				killProcess();
			}
		}

		ModbusRTUTransport transport0 = new ModbusRTUTransport();
		ModbusRTUTransport transport1 = new ModbusRTUTransport();
		ModbusRTUTransport transport2 = new ModbusRTUTransport();
		ModbusRTUTransport transport3 = new ModbusRTUTransport();
		ModbusRTUTransport transport4 = new ModbusRTUTransport();
		ModbusRTUTransport transport5 = new ModbusRTUTransport();
		try {
			transport0.setCommPort(serialConnection);
			transport1.setCommPort(serialConnection);
			transport2.setCommPort(serialConnection);
			transport3.setCommPort(serialConnection);
			transport4.setCommPort(serialConnection);
			transport5.setCommPort(serialConnection);
		} catch (IOException e) {
			killProcess();
			e.printStackTrace();
		}
		transport0.setEcho(false);
		transport1.setEcho(false);
		transport2.setEcho(false);
		transport3.setEcho(false);
		transport4.setEcho(false);
		transport5.setEcho(false);

		// Get the version from the file name
		String fileVersion = getFileVersion(PATH);

		try {

			if (forceUpdate) {

				ReadMultipleRegistersResponse readMultipleRegistersResponse = getReadMultipleRegistersResponse(
						transport0);

				String bmsVersion = getBmsVersion(readMultipleRegistersResponse.getRegisters());

				float bv = Float.parseFloat(bmsVersion);
				float fv = Float.parseFloat(fileVersion);

				printLog("Version of the BMS is : " + bv);
				printLog("Version of the file is : " + fv);

				if (fv <= bv) {
					printLog("Same version , no need to update");
					killProcess();
				} else {
					printLog("Make the update");
				}
			}

			boolean isFinished;
			do {
				isFinished = false;
				switch (stateMachine) {
				case FC40:
					printLog("In Fc 40 state");

					FC40WriteTaskResponse fc40WriteTaskResponse = getFc40ResponseRTU(transport1, //
							SIZE_OF_THE_UPDATE_FILE);

					System.out.println(
							"fc40WriteTaskResponse.getFunctionCode() : " + fc40WriteTaskResponse.getFunctionCode());

					if (fc40WriteTaskResponse.getFunctionCode() == Constants.CODE_40 && //
							fc40WriteTaskResponse.getResponseData() == 1 /* one is success */) {

						printLog("Read Data Fc40 : " + fc40WriteTaskResponse.getHexMessage());
						printLog("File size OK!");
						// Move to second step
						transport1.close();
						isFinished = changeState(StateMachine.FC41);
						break;
					}

					isFinished = changeState(StateMachine.FC40);
					// Sleep after one request
					Thread.sleep(Constants.SLEEP_TIME);
					break;
				case FC41:
					printLog("In Fc 41 state");

					FC41WriteTaskResponse fc41WriteTaskResponse = getFc41ResponseRTU(transport2);

					if (fc41WriteTaskResponse.getFunctionCode() == Constants.CODE_41 && //
							fc41WriteTaskResponse.getResponseData() == 1 /* one is success */) {

						printLog("Read Data  Fc41 : " + fc41WriteTaskResponse.getHexMessage());
						printLog("Erase Flash OK!");
						transport2.close();
						isFinished = changeState(StateMachine.FC42);
						break;
					}

					isFinished = changeState(StateMachine.FC41);
					// Sleep after one request
					Thread.sleep(Constants.SLEEP_TIME);
					break;
				case FC42:
					printLog("In Fc 42 state");
					boolean isSuccesful = false;
					byte[] allBytes = null;
					try {
						allBytes = Files.readAllBytes(Paths.get(PATH));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					printLog("Start Send Data ...");
					// make fc42
					isSuccesful = getFc42ResponseRTU(transport3, allBytes);

					if (isSuccesful) {
						printLog("Send Data Finish CMD!");

						transport3.close();
						isFinished = changeState(StateMachine.FC43);
					} else {
						isFinished = changeState(StateMachine.FC42);
					}
					Thread.sleep(Constants.SLEEP_TIME);
					break;
				case FC43:
					printLog("In Fc 43 state");
					FC43WriteTaskResponse fc43WriteTaskResponse = getFc43ResponseRTU(transport4);

					printLog("respose code : " + fc43WriteTaskResponse.getResponseData());

					if (fc43WriteTaskResponse.getFunctionCode() == Constants.CODE_43
					// && fc43WriteTaskResponse.getResponseData() == 1 /*one is success*/
					) {

						// printLog("response data : " + fc43WriteTaskResponse.getResponseData());
						printLog("Read Data  Fc43 : " + fc43WriteTaskResponse.getHexMessage());
						printLog("Data Verified!");
						transport4.close();
						isFinished = changeState(StateMachine.FC44);
						break;
					}

					isFinished = changeState(StateMachine.FC43);

					// Sleep after one request
					Thread.sleep(Constants.SLEEP_TIME);
					break;
				case FC44:
					printLog("In Fc 44 state");
					printLog(" Query, slave upgrade progress...");

					while (MainUpdateStatus != Constants.HUNDRED_PERCENT_COMPLETE) {
						FC44WriteTaskResponse fc44WriteTaskResponse = getFc44ResponseRTU(transport5);

						printLog("Read Data  Fc44 : " + fc44WriteTaskResponse.getHexMessage());
						printLog(" Update is " + fc44WriteTaskResponse.getResponseData() + " % complete");

						int functionCode = fc44WriteTaskResponse.getFunctionCode();
						MainUpdateStatus = fc44WriteTaskResponse.getResponseStatus();

						if (functionCode == Constants.CODE_44 && //
								MainUpdateStatus == Constants.HUNDRED_PERCENT_COMPLETE) {
							transport5.close();
							isFinished = changeState(StateMachine.FINISHED);
							break;
						}

					}
					// Sleep after one request
					Thread.sleep(Constants.SLEEP_TIME);
					isFinished = changeState(StateMachine.FC44);
					break;

				case FINISHED:

					ReadMultipleRegistersResponse readMultipleRegistersResponse1 = getReadMultipleRegistersResponse(
							transport0);

					String NewVersion = getBmsVersion(readMultipleRegistersResponse1.getRegisters());

					printLog("Updated to " + NewVersion + " version");

					PrintStream console = System.out;
					System.setOut(console);
					printLog("Updated to " + NewVersion + " version");
					isFinished = false;
					break;

				}

			} while (isFinished);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			printLog("Closing all the transports");
			transport0.close();
			transport1.close();
			transport2.close();
			transport3.close();
			transport4.close();
			transport5.close();
			killProcess();

		}
		// Finishing the update
		killProcess();

	}

	/**
	 * Get BMS version
	 * 
	 * @param transport The ModbusRTUTransport
	 * @return {@code ReadMultipleRegistersResponse}
	 */
	private static ReadMultipleRegistersResponse getReadMultipleRegistersResponse(ModbusRTUTransport transport) {

		ReadMultipleRegistersRequest readMultipleRegistersRequest = new ReadMultipleRegistersRequest(10000, 1);
		readMultipleRegistersRequest.setUnitID(Constants.UNITD_ID);

		ModbusRequest req = readMultipleRegistersRequest;
		req.setUnitID(Constants.UNITD_ID);

		printLog("Requesting bms version!");
		printLog("WriteData for bms version: " + readMultipleRegistersRequest.getHexMessage());

		return (ReadMultipleRegistersResponse) executeProcess(transport, readMultipleRegistersRequest);
	}

	/**
	 * Get the FC 40 response
	 * 
	 * @param transport           The ModbusRTUTransport
	 * @param sizeOfTheUpdateFile the the payload is the "size of the update file"
	 * @return {@code FC40WriteTaskResponse}
	 */
	private static FC40WriteTaskResponse getFc40ResponseRTU(ModbusRTUTransport transport, int sizeOfTheUpdateFile) {

		byte[] sizeOfTheUpdateFileToByte = Utils.hexStringToByteArray(sizeOfTheUpdateFile, 8, 4);

		FC40WriteTaskRequest fc40WriteTaskRequest = new FC40WriteTaskRequest();
		fc40WriteTaskRequest.setRegister(new SimpleRegister(sizeOfTheUpdateFileToByte));
		fc40WriteTaskRequest.setUnitID(Constants.UNITD_ID);
		printLog("Send File Size!");
		printLog("WriteData FC40: " + fc40WriteTaskRequest.getHexMessage());

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
		fc41WriteTaskRequest.setUnitID(Constants.UNITD_ID);
		printLog("Erase Flash ...");
		printLog("WriteData FC41: " + fc41WriteTaskRequest.getHexMessage());

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

		byte[][] payLoad = Utils.getData(allBytes, Constants.SIZEOFPAYLOAD);
		int FrameCounter = 1;
		int payloadSize = payLoad.length;

		boolean isSendDataFinished = false;

		for (byte[] load : payLoad) {

			FC42WriteTaskRequest fc42WriteTaskRequest = new FC42WriteTaskRequest(
					Utils.hexStringToByteArray(FrameCounter, 4, 2));
			fc42WriteTaskRequest.setRegister(new SimpleRegister(load));
			fc42WriteTaskRequest.setUnitID(Constants.UNITD_ID);

			printLog(" Write Data Fc42 : " + fc42WriteTaskRequest.getHexMessage());

			FC42WriteTaskResponse fc42response = (FC42WriteTaskResponse) executeProcess(transport, //
					fc42WriteTaskRequest);

			if (fc42response.getFunctionCode() == Constants.CODE_42 && //
					fc42response.getResponseData() == 1) {

				printLog("Read Data  Fc42 : " + fc42response.getHexMessage());
				printLog("SendDataPacket " + FrameCounter + " success! "
						+ Math.floor((((float) FrameCounter / (float) payloadSize)) * 100) + " % complete");

				isSendDataFinished = true;
			} else {
				isSendDataFinished = false;
			}
			FrameCounter++;
			// Sleep after each request
			try {
				Thread.sleep(Constants.SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		return isSendDataFinished;

	}

	/**
	 * Get the FC 43 response
	 * 
	 * @param transport The ModbusRTUTransport
	 * @return {@code FC43WriteTaskResponse}
	 */
	private static FC43WriteTaskResponse getFc43ResponseRTU(ModbusRTUTransport transport) {

		FC43WriteTaskRequest fc43WriteTaskRequest = new FC43WriteTaskRequest();
		fc43WriteTaskRequest.setUnitID(Constants.UNITD_ID);
		printLog("Verify Data!");
		printLog("Write Data Fc43 : " + fc43WriteTaskRequest.getHexMessage());
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
		fc44WriteTaskRequest.setUnitID(Constants.UNITD_ID);
		printLog("Write Data Fc44 : " + fc44WriteTaskRequest.getHexMessage());

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
		req.setUnitID(Constants.UNITD_ID);

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
		params.setEncoding("rtu");
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
			printLog("Chaging State from " + stateMachine + " --> to --> " + nextState);
			stateMachine = nextState;
			return true;
		}
		return false;
	}

	/**
	 * 
	 * java -jar ATLBATTERYUPDATE.jar port_name baud_rate size_of_the_updatefile
	 * path_of_the_update_file
	 */

	/**
	 * Prints out the command, how to run the jar file
	 * 
	 * @param args list of arguments
	 * 
	 */
	private static void printUsage(String[] args) {
		// batteryUpdateV2.5.5
		System.out.printf(
				"\nUsage:\n  java -jar <JARNAME.jar> <PORTNAME> <BAUDRATE> <PATH of the UPDATE FILE> <boolean force update(true or false)>");
		System.out.println("But the command was, <JARNAME.jar> = Hint batteryUpdateV2.5.5 true");

		String params = "";
		if (args.length > 0) {
			for (String s : args) {
				params = " " + s;
			}
		}
		System.out.println("java -jar " + params);

	}

	/**
	 * Kill the update process
	 */
	private static void killProcess() {
		printLog("Killing the process");
		System.exit(1);
	}

	/**
	 * Get the version of the File from the PATH variable
	 * 
	 * @param path path of the update file
	 * @return String version
	 */
	public static String getFileVersion(String path) {
		String[] parts = path.substring(path.lastIndexOf("\\") + 1, path.length())//
				.split(Pattern.quote("."));

		String version = parts[0].substring(parts[0].length() - 1);
		String subVersion = parts[1];

		return version + "." + subVersion;
	}

	/**
	 * pre-processing to get the current bms version
	 * 
	 * @param reg
	 * @return String bms version
	 */
	private static String getBmsVersion(Register[] reg) {

		String bmsVersion = "";
		for (Register r : reg) {
			byte[] b = r.toBytes();
			for (int i = 0; i <= b.length - 1; i++) {
				if (i == b.length - 1) {
					bmsVersion = bmsVersion + b[i];
				} else {
					bmsVersion = bmsVersion + b[i] + '.';
				}

			}
		}

		return bmsVersion;
	}

	/**
	 * Print the log
	 */
	private static void printLog(String msg) {
		System.out.println("[" + DATE_TIME_FORMATTER.format(LocalDateTime.now()) + "] -->  " + msg);
	}

}
