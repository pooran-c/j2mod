package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;

public class FC44WriteTaskResponse extends ModbusResponse {

	private static int LENGTH_OF_MSG = 1;
	private int msgLength;
	private int responseData;

	public FC44WriteTaskResponse() {
		super();
		setMsgLength(LENGTH_OF_MSG);
		setDataLength(3);
		setFunctionCode(Modbus.FUNCTION_CODE_44);
	}
	
	public FC44WriteTaskResponse(int value) {
		super();
		setMsgLength(LENGTH_OF_MSG);
		setDataLength(3);
		setFunctionCode(Modbus.FUNCTION_CODE_44);
	}

	public int getResponseData() {
		return responseData;
	}

	public void setResponseData(int responseData) {
		this.responseData = responseData;
	}

	public int getMsgLength() {
		return msgLength;
	}

	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}

	

	@Override
	public void writeData(DataOutput dout) throws IOException {
		dout.write(getMsgLength());
	}

	@Override
	public void readData(DataInput din) throws IOException {
		setResponseData(din.readUnsignedByte());
		setMsgLength(LENGTH_OF_MSG);
		setDataLength(3);
	}

	@Override
	public byte[] getMessage() {

		byte[] result = new byte[2];
		result[0] = (byte) ((responseData) & 0xff);
		result[1] = (byte) ((responseData));

		return result;
	}

}
