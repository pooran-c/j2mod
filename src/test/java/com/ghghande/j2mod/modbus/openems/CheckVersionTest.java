package com.ghghande.j2mod.modbus.openems;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ghgande.j2mod.modbus.openems.App;

public class CheckVersionTest {

	@Test
	public void test() {

		String path = "C:\\ATLUPDATE\\feneconR0.0.bin";
		String expected = "0.0";
		assertEquals(expected, App.getFileVersion(path));

	}

}
