package openems;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.FC40WriteTaskRequest;
import com.ghgande.j2mod.modbus.msg.FC40WriteTaskResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

import openems.App.Codes;

public class FC40Handler {

	/**
	 * Get the FC 40 response
	 * 
	 * @param transport           The ModbusRTUTransport
	 * @param sizeOfTheUpdateFile the the payload is the "size of the update file"
	 * @return {@code FC40WriteTaskResponse}
	 */
	protected static FC40WriteTaskResponse getFc40ResponseRTU(ModbusRTUTransport transport, int sizeOfTheUpdateFile) {

		byte[] sizeOfTheUpdateFileToByte = Utils.hexStringToByteArray(sizeOfTheUpdateFile, 8, 4);

		FC40WriteTaskRequest fc40WriteTaskRequest = new FC40WriteTaskRequest();
		fc40WriteTaskRequest.setRegister(new SimpleRegister(sizeOfTheUpdateFileToByte));
		fc40WriteTaskRequest.setUnitID(Codes.UNITD_ID);

		System.out.println("Send File Size!");
		System.out.println("WriteData FC40: " + fc40WriteTaskRequest.getHexMessage());

		return (FC40WriteTaskResponse) executeProcess(transport, fc40WriteTaskRequest);

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

}
