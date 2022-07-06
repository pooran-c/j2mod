package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.AdditionalRegister;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

public class FC42WriteTaskRequest extends ModbusRequest {

	private static int LENGTH_OF_MSG = 130;
	private static int ADDRESS = 1;

	private int msgLength;
	private Register[] register;
	private int frameNumber;

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

	public FC42WriteTaskRequest( Register[] reg) {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_42);

		setMsgLength(LENGTH_OF_MSG);
		setRegister(reg);
		setFrameNumber(1);
		setDataLength(131);

	}

	@Override
	public ModbusResponse getResponse() {
		return updateResponseWithHeader(new FC40WriteTaskResponse());
	}

	@Override
	public ModbusResponse createResponse(AbstractModbusListener listener) {
//		Register[] reg;
//
//		// 1. get process image
//		ProcessImage procimg = listener.getProcessImage(getUnitID());
//
//		// 2. get register
//		try {
//			reg = procimg.getRegister(ADDRESS); // address is always 1
//
//			// 3. set Register
//			
//			reg.setValue(register.toBytes());
//		} catch (IllegalAddressException iaex) {
//			return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
//		}
		//return updateResponseWithHeader(new FC42WriteTaskResponse(reg.getValue()));
		return updateResponseWithHeader(new FC42WriteTaskResponse(1,1));
	}

	@Override
	public void writeData(DataOutput dout) throws IOException {
		dout.write(getMsgLength());
		for (Register r : register) {
			dout.write(r.toBytes());
		}

	}

	@Override
	public void readData(DataInput din) throws IOException {
		// register = new AdditionalRegister(din.readByte(), din.readByte());
	}

	@Override
	public byte[] getMessage() {
		byte[] result = new byte[register.length];

		for (int i = 0; i < register.length; i++) {
			if (i == 0 || i == 1) {
				result[i] = (byte) (register[i].getValue());
			} else {
				result[i] = (byte) (register[i].getValue() >> (int) Math.pow(2, i));
			}

		}

//		result[0] = (byte) (register.getValue());
//		result[1] = (byte) (register.getValue() >> 8);
//		result[2] = (byte) (register.getValue() >> 16);
//		result[3] = (byte) (register.getValue() >> 24);

		return result;
	}
}
