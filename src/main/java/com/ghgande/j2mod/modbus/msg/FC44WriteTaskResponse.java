package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;

public class FC44WriteTaskResponse extends ModbusResponse {

	private static int LENGTH_OF_MSG = 2;
	private int msgLength;
	private int responseStatus;
	private int responseData;

	public int getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
	}

	public FC44WriteTaskResponse() {
		super();
		setMsgLength(LENGTH_OF_MSG);
		setDataLength(3);
		setFunctionCode(Modbus.FUNCTION_CODE_44);
	}

//	public FC44WriteTaskResponse(int value) {
//		super();
//		setMsgLength(LENGTH_OF_MSG);
//		setDataLength(3);
//		setFunctionCode(Modbus.FUNCTION_CODE_44);
//	}

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
		dout.write(getResponseStatus());
		dout.write(getResponseData());
	}

	@Override
	public void readData(DataInput din) throws IOException {

		setMsgLength(din.readByte());
		setResponseStatus(din.readByte());
		setResponseData(din.readByte());
		setDataLength(3);
	}

	@Override
	public byte[] getMessage() {

		byte[] result = new byte[4];

		result[0] = (byte) (0xff & (responseData >> 8));
		result[1] = (byte) (0xff & (responseData ));
		result[2] = (byte) (0xff & (responseStatus >> 8));
		result[3] = (byte) (0xff & (responseStatus ));

		return result;
	}

}
