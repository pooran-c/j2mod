package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

public class FC41WriteTaskRequest extends ModbusRequest {

	private static int LENGTH_OF_MSG = 0;
	private Register register;

	public Register getRegister() {
		return register;
	}

	public void setRegister(Register reg) {
		register = reg;
	}

	public FC41WriteTaskRequest() {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_41);
		setDataLength(1);
	}

	public FC41WriteTaskRequest(int ref) {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_41);
		setDataLength(1);

	}

	@Override
	public byte[] getMessage() {
		byte[] result = new byte[LENGTH_OF_MSG];
		return result;
	}

	@Override
	public ModbusResponse getResponse() {
		return updateResponseWithHeader(new FC41WriteTaskResponse());
	}

	@Override
	public ModbusResponse createResponse(AbstractModbusListener listener) {
		Register reg;

		// 1. get process image
		ProcessImage procimg = listener.getProcessImage(getUnitID());

		// 2. get register
		try {
			reg = procimg.getRegister(1); // address is always 1

			// 3. set Register
			reg.setValue(register.toBytes());
		} catch (IllegalAddressException iaex) {
			return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
		}
		return updateResponseWithHeader(new FC41WriteTaskResponse(reg.getValue()));
	}

	@Override
	public void writeData(DataOutput dout) throws IOException {
		dout.write(LENGTH_OF_MSG);

	}

	@Override
	public void readData(DataInput din) throws IOException {
		register = new SimpleRegister(din.readByte(), din.readByte());

	}

}
