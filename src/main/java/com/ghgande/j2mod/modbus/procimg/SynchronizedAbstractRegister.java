/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ghgande.j2mod.modbus.procimg;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class with synchronized register operations.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public abstract class SynchronizedAbstractRegister implements Register {

	/**
	 * The word (<tt>byte[2]</tt>) holding the state of this register.
	 *
	 * Note that a superclass may set register to null to create a gap in a Modbus
	 * map.
	 */
	protected List<Byte> register = new ArrayList<Byte>();

	@Override
	public synchronized int getValue() {
		if (register == null) {
			throw new IllegalAddressException();
		}
		int result = 0;
		for (int i = 0; i < register.size(); i++) {
			result = result | ((register.get(i) & 0xff) << 8 * i);
		}
		return result;
	}

	@Override
	public int toUnsignedShort() {
		return getValue();
	}

	@Override
	public short toShort() {
		if (register == null) {
			throw new IllegalAddressException();
		}
		return (short) ((register.get(0) << 8) | (register.get(1) & 0xff));
	}

	@Override
	public synchronized byte[] toBytes() {
		byte[] dest = new byte[register.size()];

		for (int i = 0; i < register.size(); i++) {
			dest[i] = register.get(i);
		}
		return dest;
	}

	@Override
	public synchronized void setValue(short s) {
		if (register == null) {
			throw new IllegalAddressException();
		}

		register.set(0, (byte) (0xff & (s >> 8)));
		register.set(1, (byte) (0xff & s));
	}

	@Override
	public synchronized void setValue(byte[] bytes) {
		if (register == null) {
			throw new IllegalAddressException();
		}
		for (int i = 0; i < register.size(); i++) {
			register.set(i, bytes[i]);
		}
	}

	@Override
	public synchronized void setValue(int v) {
		if (register == null) {
			throw new IllegalAddressException();
		}
		for (int i = register.size() - 1; i >= 0; i--) {
			register.set(i, (byte) (0xff & (v >> 8 * i)));
		}
	}
}
