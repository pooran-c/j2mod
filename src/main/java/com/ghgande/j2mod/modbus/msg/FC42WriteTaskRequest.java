package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;

public class FC42WriteTaskRequest extends ModbusRequest {

	private static int LENGTH_OF_MSG = 130;
	private static int ADDRESS = 1;

	private int msgLength;
	private Register[] register;
	private int frameNumber;

	public Register getResRegister() {
		return resRegister;
	}

	public void setResRegister(Register resRegister) {
		this.resRegister = resRegister;
	}

	private Register resRegister;

	public int getFrameNumber() {
		return frameNumber;
	}

	public void setFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
	}

	public int getMsgLength() {
		return msgLength;
	}

	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}

	public Register[] getRegister() {
		return register;
	}

	public void setRegister(Register[] reg) {
		register = reg;
	}

	public FC42WriteTaskRequest() {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_42);
		setMsgLength(LENGTH_OF_MSG);

		setDataLength(131);
	}

	public FC42WriteTaskRequest(Register[] reg, int framenum) {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_42);

		setMsgLength(LENGTH_OF_MSG);
		setFrameNumber(framenum);
		setRegister(reg);
		setDataLength(131);

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
			reg.setValue(resRegister.toBytes());
		} catch (IllegalAddressException iaex) {
			return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
		}
		return updateResponseWithHeader(new FC42WriteTaskResponse(reg.getValue()));
	}

	@Override
	public void writeData(DataOutput dout) throws IOException {
		dout.write(getFrameNumber());
		dout.write(getMsgLength());

		for (Register r : register) {
			dout.write(r.toBytes());
		}

	}

	@Override
	public void readData(DataInput din) throws IOException {
		resRegister.setValue(din.readByte()); // new AdditionalRegister(din.readByte(), din.readByte());
	}

	@Override
	public byte[] getMessage() {
		byte[] result = new byte[register.length * 2];

//		result[1] = (byte) (register[0].getValue()>>8);
		int count = 0;
		for (int i = 0; i < register.length - 1; i++) {

			result[count] = (byte) (register[i].getValue() >> 8);
			result[count + 1] = (byte) (register[i].getValue());
			count = count + 2;
		}

//		result[0] = (byte) (register[0].getValue());
//		result[1] = (byte) (register[0].getValue() >> 8);
////		result[2] = (byte) (register[0].getValue() >> 16);
////		result[3] = (byte) (register[0].getValue() >> 24);

		return result;
	}
}
