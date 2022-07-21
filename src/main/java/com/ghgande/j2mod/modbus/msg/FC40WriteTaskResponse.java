package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;

public class FC40WriteTaskResponse extends ModbusResponse {

	private static int LENGTH_OF_MSG = 1;
	private int msgLength;
	private int responseData;

	public FC40WriteTaskResponse() {
		super();
		setMsgLength(LENGTH_OF_MSG);
		setDataLength(2);
		setFunctionCode(Modbus.FUNCTION_CODE_40);
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

	public FC40WriteTaskResponse(int value) {
		super();
		setMsgLength(LENGTH_OF_MSG);
		setDataLength(2);
		setFunctionCode(Modbus.FUNCTION_CODE_40);
	}

	@Override
	public void writeData(DataOutput dout) throws IOException {
		dout.write(getMsgLength());
	}

	@Override
	public void readData(DataInput din) throws IOException {
		setMsgLength(din.readByte());
		setResponseData(din.readByte());		
		setDataLength(4);
	}

	@Override
	public byte[] getMessage() {

		byte[] result = new byte[2];
//		result[0] = (byte) ((responseData) & 0xff);
//		result[1] = (byte) ((responseData));

        result[0] = (byte)((responseData >> 8) & 0xff);
        result[1] = (byte)(responseData & 0xff);
		
		
		return result;
	}

}
