package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;

public class FC43WriteTaskRequest extends ModbusRequest {

	private int reference;

	public int getReference() {
		return reference;
	}

	public void setReference(int ref) {
		reference = ref;
	}

	public FC43WriteTaskRequest() {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_43);

		setDataLength(1);
	}

	public FC43WriteTaskRequest(int ref) {
		super();

		setFunctionCode(Modbus.FUNCTION_CODE_43);
		setDataLength(1);

		reference = ref;
	}

	@Override
	public byte[] getMessage() {
		byte[] result = new byte[2];

		result[0] = (byte) ((reference >> 8) & 0xff);
		result[1] = (byte) (reference & 0xff);

		return result;
	}

	@Override
	public ModbusResponse getResponse() {
		return updateResponseWithHeader(new FC41WriteTaskResponse());
	}

	@Override
	public ModbusResponse createResponse(AbstractModbusListener listener) {
		Register reg;

		ProcessImage procimg = listener.getProcessImage(getUnitID());

		try {
			reg = procimg.getRegister(reference);
			if (reg.getValue() == 1) {
				System.out.println("Status id ok");
			} else {
				System.out.println("Status is not ok");
			}
		} catch (IllegalAddressException iaex) {
			return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
		}

		return updateResponseWithHeader(new FC43WriteTaskResponse(this.getReference(), //
				// response value
				reg.getValue()));

	}

	@Override
	public void writeData(DataOutput dout) throws IOException {
		dout.write(getMessage());

	}

	@Override
	public void readData(DataInput din) throws IOException {
		reference = din.readUnsignedShort();

	}

}
