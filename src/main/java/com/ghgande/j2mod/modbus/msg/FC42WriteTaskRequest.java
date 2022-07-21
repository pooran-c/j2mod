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
	private Register register;
	private int msgLength;
	private byte[] frameNumber = new byte[2];

	public Register getRegister() {
		return register;
	}

	public void setRegister(Register register) {
		this.register = register;
	}

	public Register getResRegister() {
		return resRegister;
	}

	public void setResRegister(Register resRegister) {
		this.resRegister = resRegister;
	}

	private Register resRegister;

	public byte[] getFrameNumber() {
		return frameNumber;
	}

	public void setFrameNumber(byte[] frameNum) {
		this.frameNumber = frameNum;
	}

	public int getMsgLength() {
		return msgLength;
	}

	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}

	public FC42WriteTaskRequest() {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_42);
		setMsgLength(LENGTH_OF_MSG);
		setDataLength(131);
	}

	public FC42WriteTaskRequest(byte[] framenum) {
		super();
		setFunctionCode(Modbus.FUNCTION_CODE_42);
		setMsgLength(LENGTH_OF_MSG);
		setFrameNumber(framenum);
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

		dout.write(getMsgLength());
		dout.write(getFrameNumber());
		// for (Register r : register) {
		dout.write(register.toBytes());
		// }

	}

	@Override
	public void readData(DataInput din) throws IOException {
		resRegister.setValue(din.readByte()); // new AdditionalRegister(din.readByte(), din.readByte());
	}

	@Override
	public byte[] getMessage() {

		return register.toBytes();
//		byte[] result = new byte[2];
//		
////		for (int i = 0; i < register.) {
////			
////		}
//
//		result[0] = (byte) (register.getValue());
//		result[1] = (byte) (register.getValue() >> 8);
//		//result[2] = (byte) (register.getValue() >> 16);
//		//result[3] = (byte) (register.getValue() >> 24);
//
//		return result;
	}

}
