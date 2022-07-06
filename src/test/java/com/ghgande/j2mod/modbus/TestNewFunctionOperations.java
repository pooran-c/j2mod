//package com.ghgande.j2mod.modbus;
//
//import com.ghgande.j2mod.modbus.procimg.Register;
//import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
//import com.ghgande.j2mod.modbus.utils.AbstractTestModbusSerialRTUMaster;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//@SuppressWarnings("ConstantConditions")
//public class TestNewFunctionOperations extends AbstractTestModbusSerialRTUMaster {
//	
//	@Test
//	public void testFC40() {
//		try {
//			int registerCount = 1;
//
//			Register[] writeRegisters = new Register[registerCount];
//
//			writeRegisters[0] = new SimpleRegister(250);
//
//			master.Fc40WriteTask(UNIT_ID, 1, writeRegisters);
//		} catch (Exception e) {
//			fail(String.format("Cannot write to registers - %s", e.getMessage()));
//		}
//	}
//
//}
