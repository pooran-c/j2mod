package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;

public class FC43WriteTaskResponse extends ModbusResponse {

	// Message fields.
	private int reference;
	private int registerValue;

	public int getRegisterValue() {
		return registerValue;
	}

	public void setRegisterValue(int value) {
		registerValue = value;
	}

	public int getReference() {
		return reference;
	}

	public void setReference(int ref) {
		reference = ref;
	}

	public FC43WriteTaskResponse() {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_43);
		setDataLength(4);
	}

	public FC43WriteTaskResponse(int reference, int value) {
		super();
		setReference(reference);
		setRegisterValue(value);
		setDataLength(4);
		setFunctionCode(Modbus.FUNCTION_CODE_43);
	}

	@Override
	public byte[] getMessage() {
		byte[] result = new byte[4];

		result[0] = (byte) ((reference >> 8) & 0xff);
		result[1] = (byte) (reference & 0xff);
		result[2] = (byte) ((registerValue >> 8) & 0xff);
		result[3] = (byte) (registerValue & 0xff);

		return result;
	}

	@Override
	public void writeData(DataOutput dout) throws IOException {
		dout.write(getMessage());

	}

	@Override
	public void readData(DataInput din) throws IOException {
		setReference(din.readUnsignedShort());
		setRegisterValue(din.readUnsignedShort());
		setDataLength(1);
	}

}
