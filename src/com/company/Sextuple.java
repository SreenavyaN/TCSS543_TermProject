package com.company;

import java.math.BigInteger;

import static com.company.Utils.printPoint;

public class Sextuple {
    public final BigInteger alpha_k;
    public final BigInteger beta_k;
    public final BigInteger[] z_k;
    public final BigInteger alpha_2k;
    public final BigInteger beta_2k;
    public final BigInteger[] z_2k;

    public Sextuple(BigInteger a, BigInteger b, BigInteger[] z, BigInteger a2, BigInteger b2, BigInteger[] z2) {
        this.alpha_k = a;
        this.beta_k = b;
        this.z_k = z;
        this.alpha_2k = a2;
        this.beta_2k = b2;
        this.z_2k = z2;
    }

    public Sextuple(Triple left, Triple right) {
        this.alpha_k = left.alpha;
        this.beta_k = left.beta;
        this.z_k = left.z;
        this.alpha_2k = right.alpha;
        this.beta_2k = right.beta;
        this.z_2k = right.z;
    }

    public Triple getLeft() {
        return new Triple(alpha_k, beta_k, z_k);
    }

    public Triple getRight() {
        return new Triple(alpha_2k, beta_2k, z_2k);
    }

    public String toString() {
        return String.format("(%s, %s, %s, %s, %s, %s)", alpha_k, beta_k, printPoint(z_k), alpha_2k, beta_2k, printPoint(z_2k));
    }

    public Boolean check_zk_z2k(){
        return z_k[0].equals(z_2k[0]) && z_k[1].equals(z_2k[1]);
    }
}

