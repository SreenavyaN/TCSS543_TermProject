package com.company;

import java.math.BigInteger;

public class InputData {
    public final BigInteger p,d,n;
    public final int N;
    public final BigInteger[] a = new BigInteger[2];

    public InputData(String b, String e, String c, String d, String n, String ax, String ay, String N) {
        BigInteger t = new BigInteger(b);
        this.p = t.pow(Integer.valueOf(e)).subtract(new BigInteger(c));
        this.d = new BigInteger(d);
        this.n = new BigInteger(n);
        this.N = Integer.parseInt(N);
        this.a[0] = new BigInteger(ax);
        this.a[1] = new BigInteger(ay);
    }

    public InputData(String[] arr) {
        this(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7]);
    }

    public String toString() {
        return String.format("p: %s, d: %s, n: %s, a: (%s,%s), N: %d", p.toString(), d.toString(), n.toString(), a[0].toString(), a[1].toString(), N);
    }
}
