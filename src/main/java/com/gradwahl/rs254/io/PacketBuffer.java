package com.gradwahl.rs254.io;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.CRC32;

public final class PacketBuffer {
    public final byte[] data;
    public int pos;
    public IsaacCipher random;

    public PacketBuffer(int size) {
        this.data = new byte[size];
    }

    public PacketBuffer(byte[] data) {
        this.data = data;
    }

    public int g1() { return data[pos++] & 0xff; }

    public int g4() {
        return ((data[pos++] & 0xff) << 24)
            | ((data[pos++] & 0xff) << 16)
            | ((data[pos++] & 0xff) << 8)
            | (data[pos++] & 0xff);
    }

    public long g8() {
        return ((long) g4() << 32) | (g4() & 0xffffffffL);
    }

    public void p1(int value) { data[pos++] = (byte) value; }

    public void p2(int value) {
        data[pos++] = (byte) (value >>> 8);
        data[pos++] = (byte) value;
    }

    public void p4(int value) {
        data[pos++] = (byte) (value >>> 24);
        data[pos++] = (byte) (value >>> 16);
        data[pos++] = (byte) (value >>> 8);
        data[pos++] = (byte) value;
    }

    public void pjstr(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.ISO_8859_1);
        pdata(bytes, 0, bytes.length);
        p1(10);
    }

    public void pdata(byte[] src, int off, int len) {
        System.arraycopy(src, off, data, pos, len);
        pos += len;
    }

    public void pIsaac(int opcode) {
        if (random == null) throw new IllegalStateException("ISAAC is not initialised");
        p1((opcode + random.nextInt()) & 0xff);
    }

    public byte[] bytes() {
        return Arrays.copyOf(data, pos);
    }

    public void rsaenc(BigInteger modulus, BigInteger exponent) {
        byte[] raw = Arrays.copyOf(data, pos);
        BigInteger rawInt = new BigInteger(1, raw);
        byte[] encrypted = rawInt.modPow(exponent, modulus).toByteArray();
        if (encrypted.length > 0 && encrypted[0] == 0) {
            encrypted = Arrays.copyOfRange(encrypted, 1, encrypted.length);
        }
        pos = 0;
        p1(encrypted.length);
        pdata(encrypted, 0, encrypted.length);
    }

    public static int crc32(byte[] src, int off, int len) {
        CRC32 crc = new CRC32();
        crc.update(src, off, len);
        return (int) crc.getValue();
    }
}
