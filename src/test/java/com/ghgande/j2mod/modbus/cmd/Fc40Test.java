package com.ghgande.j2mod.modbus.cmd;

import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.util.SerialParameters;

public class Fc40Test {
	
	 public static void main(String[] args) {
		 
	        ModbusRequest req;
	        ModbusTransaction trans;
	        AbstractModbusTransport transport = null;
	        
	        int ref = 0;
	        int value = 0;
	        int repeat = 1;
	        int unit = 0;
	        
	        SerialParameters parms = new SerialParameters();
            parms.setPortName("COM2");
            parms.setBaudRate(19200);
            parms.setDatabits(8);
            parms.setParity(AbstractSerialConnection.NO_PARITY);
            parms.setStopbits(1);
            parms.setFlowControlIn(AbstractSerialConnection.FLOW_CONTROL_DISABLED);
            parms.setEcho(false);
		 
		 
	 }

}
