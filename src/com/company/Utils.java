package com.company;

import java.math.BigInteger;

public class Utils {
    public static String printPoint(BigInteger[] p) {
        return String.format("( %s , %s )", p[0].toString(), p[1].toString());
    }
}
