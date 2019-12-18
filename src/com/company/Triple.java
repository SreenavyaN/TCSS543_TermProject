package com.company;

import java.math.BigInteger;

import static com.company.Utils.printPoint;

public class Triple {
    public final BigInteger alpha;
    public final BigInteger beta;
    public final BigInteger[] z;

    public Triple(BigInteger alpha, BigInteger beta, BigInteger[] z) {
        this.alpha = alpha;
        this.beta = beta;
        this.z = z;
    }

    public String toString() {
        return String.format("(%s, %s, %s)", alpha, beta, printPoint(z));
    }
}
