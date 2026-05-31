package com.gradwahl.rs254.io;

public final class IsaacCipher {
    private int count;
    private final int[] rsl = new int[256];
    private final int[] mem = new int[256];
    private int a;
    private int b;
    private int c;

    public IsaacCipher(int[] seed) {
        System.arraycopy(seed, 0, rsl, 0, Math.min(seed.length, rsl.length));
        init();
    }

    public int nextInt() {
        if (count-- == 0) {
            isaac();
            count = 255;
        }
        return rsl[count];
    }

    private void mix(int[] x) {
        x[0] ^= x[1] << 11; x[3] += x[0]; x[1] += x[2];
        x[1] ^= x[2] >>> 2; x[4] += x[1]; x[2] += x[3];
        x[2] ^= x[3] << 8; x[5] += x[2]; x[3] += x[4];
        x[3] ^= x[4] >>> 16; x[6] += x[3]; x[4] += x[5];
        x[4] ^= x[5] << 10; x[7] += x[4]; x[5] += x[6];
        x[5] ^= x[6] >>> 4; x[0] += x[5]; x[6] += x[7];
        x[6] ^= x[7] << 8; x[1] += x[6]; x[7] += x[0];
        x[7] ^= x[0] >>> 9; x[2] += x[7]; x[0] += x[1];
    }

    private void init() {
        int[] x = new int[8];
        for (int i = 0; i < x.length; i++) x[i] = 0x9e3779b9;
        for (int i = 0; i < 4; i++) mix(x);

        for (int i = 0; i < 256; i += 8) {
            for (int j = 0; j < 8; j++) x[j] += rsl[i + j];
            mix(x);
            System.arraycopy(x, 0, mem, i, 8);
        }

        for (int i = 0; i < 256; i += 8) {
            for (int j = 0; j < 8; j++) x[j] += mem[i + j];
            mix(x);
            System.arraycopy(x, 0, mem, i, 8);
        }

        isaac();
        count = 256;
    }

    private void isaac() {
        b += ++c;
        for (int i = 0; i < 256; i++) {
            int x = mem[i];
            switch (i & 3) {
                case 0 -> a ^= a << 13;
                case 1 -> a ^= a >>> 6;
                case 2 -> a ^= a << 2;
                case 3 -> a ^= a >>> 16;
                default -> throw new IllegalStateException();
            }
            a += mem[(i + 128) & 0xff];
            int y = mem[i] = mem[(x >>> 2) & 0xff] + a + b;
            rsl[i] = b = mem[(y >>> 10) & 0xff] + x;
        }
    }
}
