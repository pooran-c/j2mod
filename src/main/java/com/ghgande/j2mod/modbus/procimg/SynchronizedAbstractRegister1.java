package com.ghgande.j2mod.modbus.procimg;

public class SynchronizedAbstractRegister1 implements Register {

	 /**
     * The word (<tt>byte[2]</tt>) holding the state of this register.
     *
     * Note that a superclass may set register to null to create a
     * gap in a Modbus map.
     */
    protected byte[] register = new byte[2];

    @Override
    public synchronized int getValue() {
        if (register == null) {
            throw new IllegalAddressException();
        }

        return ((register[0] & 0xff) << 8 | (register[1] & 0xff));
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

        return (short)((register[0] << 8) | (register[1] & 0xff));
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

        register[0] = (byte)(0xff & (s >> 8));
        register[1] = (byte)(0xff & s);
    }

    @Override
    public synchronized void setValue(byte[] bytes) {
        if (bytes.length < 2) {
            throw new IllegalArgumentException();
        }
        else {
            if (register == null) {
                throw new IllegalAddressException();
            }

            register[0] = bytes[0];
            register[1] = bytes[1];
        }
    }

    @Override
    public synchronized void setValue(int v) {
        if (register == null) {
            throw new IllegalAddressException();
        }

        register[0] = (byte)(0xff & (v >> 8));
        register[1] = (byte)(0xff & v);
    }

}
