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

public class FC40WriteTaskRequest extends ModbusRequest {

	private static int LENGTH_OF_MSG = 4;
	private static int ADDRESS = 1;

	private int msgLength;
	private Register register;

	public int getMsgLength() {
		return msgLength;
	}

	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}

	public Register getRegister() {
		return register;
	}

	public void setRegister(Register reg) {
		register = reg;
	}

	public FC40WriteTaskRequest() {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_40);
		setMsgLength(LENGTH_OF_MSG);
		setDataLength(5);
	}

	public FC40WriteTaskRequest(int ref, Register reg) {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_40);
		setMsgLength(LENGTH_OF_MSG);
		setDataLength(5);

	}

	@Override
	public ModbusResponse getResponse() {
		return updateResponseWithHeader(new FC40WriteTaskResponse());
	}

	@Override
	public ModbusResponse createResponse(AbstractModbusListener listener) {
		Register reg;

		// 1. get process image
		ProcessImage procimg = listener.getProcessImage(getUnitID());

		// 2. get register
		try {
			reg = procimg.getRegister(ADDRESS); // address is always 1

			// 3. set Register
			reg.setValue(register.toBytes());
		} catch (IllegalAddressException iaex) {
			return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
		}
		return updateResponseWithHeader(new FC40WriteTaskResponse(reg.getValue()));
	}

	@Override
	public void writeData(DataOutput dout) throws IOException {
		dout.write(getMsgLength());
		dout.write(register.toBytes());
	}

	@Override
	public void readData(DataInput din) throws IOException {
		register = new SimpleRegister(din.readByte(), din.readByte());
	}

	@Override
	public byte[] getMessage() {
		byte[] result = new byte[LENGTH_OF_MSG];

		result[0] = (byte) (register.getValue());
		result[1] = (byte) (register.getValue() >> 8);
		result[2] = (byte) (register.getValue() >> 16);
		result[3] = (byte) (register.getValue() >> 24);

		return result;
	}
}
