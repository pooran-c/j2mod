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

/**
 * Abstract class with synchronized register operations.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public abstract class SynchronizedAbstractAdditionalRegister implements Register {

	/**
	 * The word (<tt>byte[2]</tt>) holding the state of this register.
	 *
	 * Note that a superclass may set register to null to create a gap in a Modbus
	 * map.
	 */
	protected byte[] register = new byte[4];

	@Override
	public synchronized int getValue() {
		if (register == null) {
			throw new IllegalAddressException();
		}

		return ((register[0] & 0xff) | //
				(register[1] & 0xff) << 8 | //
				(register[2] & 0xff) << 16 | //
				(register[3] & 0xff) << 24);
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

		return (short) ((register[0] << 8) | (register[1] & 0xff));
	}

	@Override
	public synchronized byte[] toBytes() {
		byte[] dest = new byte[register.length];
		System.arraycopy(register, 0, dest, 0, dest.length);
		return dest;
	}

	@Override
	public synchronized void setValue(short s) {
		if (register == null) {
			throw new IllegalAddressException();
		}

		register[0] = (byte) (0xff & (s >> 8));
		register[1] = (byte) (0xff & s);
	}

	@Override
	public synchronized void setValue(byte[] bytes) {
		if (bytes.length < 2) {
			throw new IllegalArgumentException();
		} else {
			if (register == null) {
				throw new IllegalAddressException();
			}

			register[0] = bytes[0];
			register[1] = bytes[1];
			register[2] = bytes[2];
			register[3] = bytes[3];
		}
	}

	@Override
	public synchronized void setValue(int v) {
		if (register == null) {
			throw new IllegalAddressException();
		}

		register[0] = (byte) (0xff & (v >> 8));
		register[1] = (byte) (0xff & v);
	}

}