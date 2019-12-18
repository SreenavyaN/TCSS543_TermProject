package com.company;

import java.io.*;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * This is to implement the Pollard's Rho Algorithm
 * @author Sreenavya Nrusimhadevara
 * UW ID :- sreevpk@uw.edu
 * University of Washington, Tacoma
 * TCSS 543 A Advanced Algorithms,Fall 2019
 **/

public class Main {
    public static final BigInteger THREE = BigInteger.valueOf(3);
    public static final BigInteger[] UNIT = new BigInteger[]{BigInteger.ZERO, BigInteger.ONE};
    private static List<InputData> inputDataList;
    private static Map<Long, Triple> kToTripleMap; //Memoization

    public static void main(String[] args) {
        Instant start = Instant.now();
        long k, sum;
        int skip = 0;
        readInput("src\\com\\company\\testInputs.csv");
        for (InputData inputData : inputDataList) {
            System.out.println("Input data is: " + inputData.toString());
            sum = 0;
            for (int i = 0; i < inputData.N; i++) {
                try {
                    k = check(inputData.a, inputData.d, inputData.p, inputData.n);
                    sum = sum + k;
                }
                catch (Exception e) {
                    System.out.println("Rare case exception."+e.getMessage()+"Skip this iteration");
                    skip++;
                }
            }
            System.out.println("Number of exceptions occured : " + skip);
            long avg = sum / (inputData.N - skip); //calculating the average after ignoring the 1/n probability cases
            System.out.println("Average of k values is : " + avg);
        }
        Instant finish = Instant.now();
        System.out.println("Time taken to complete is : " + Duration.between(start,finish).toMillis());
    }

    public static void readInput(String filePath) {
        File file = new File(filePath);
        inputDataList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            while ((str = br.readLine()) != null) {
                if (str.startsWith("#")) continue;
                inputDataList.add(new InputData(str.split(",")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BigInteger[] mul(BigInteger[] a1, BigInteger[] a2, BigInteger d, BigInteger p) {
        //System.out.println("a1: " + printPoint(a1) + " a2: " + printPoint(a2) + " d: " + d + " p: " + p);
        BigInteger[] a3 = new BigInteger[2];
        BigInteger prod_x = modmul(a1[0], a2[0], p); // x1 x2
        BigInteger prod_y = modmul(a1[1], a2[1], p); // y1 y2
        BigInteger den = modmul(d, prod_x, prod_y, p); // d x1 x2 y1 y2
        BigInteger numerator = modadd( modmul(a1[0], a2[1], p), modmul(a1[1], a2[0], p) , p); // x1 y2 + y1 x2
        BigInteger denominator = modadd(BigInteger.ONE, den, p); // 1 + d x1 x2 y1 y2
        if (denominator.equals(BigInteger.ZERO)) {
            throw new RuntimeException("Numerator " + numerator + " Denominator " + denominator);
        }
        a3[0] = modInvmul(numerator, denominator, p);
        numerator = modsub(prod_y, prod_x, p); // y1 y2 - x1 x2
        denominator = modsub(BigInteger.ONE, den, p); // 1 - d x1 x2 y1 y2
        if (denominator.equals(BigInteger.ZERO)) {
            throw new RuntimeException("Numerator " + numerator + " Denominator " + denominator);
        }
        a3[1] = modInvmul(numerator, denominator, p);
        return a3;
    }

    public static BigInteger modmul(BigInteger a, BigInteger b, BigInteger c, BigInteger p) {
        //System.out.println(a + "." + b + "." + c + " (mod " + p + ")");
        return a.multiply(b).multiply(c).mod(p);
    }
    public static BigInteger modmul(BigInteger a, BigInteger b, BigInteger p) {
        //System.out.println(a + "." + b + " (mod " + p + ")");
        return a.multiply(b).mod(p);
    }
    public static BigInteger modadd(BigInteger a, BigInteger b, BigInteger p) {
        //System.out.println(a + "+" + b + " (mod " + p + ")");
        return a.add(b).mod(p);
    }
    public static BigInteger modsub(BigInteger a, BigInteger b, BigInteger p) {
        //System.out.println(a + "-" + b + " (mod " + p + ")");
        return a.subtract(b).mod(p);
    }
    public static BigInteger modInvmul(BigInteger a, BigInteger b, BigInteger p) {
        //System.out.println(a + "/" + b + " (mod " + p + ")");
        return a.multiply(b.modInverse(p)).mod(p);
    }


    public static BigInteger[] exp(BigInteger[] a, BigInteger m, BigInteger d, BigInteger p) {
        BigInteger[] b = UNIT;
        for (int i = m.bitLength() - 1; i >= 0; i--) {
            b = mul(b, b, d, p);
            if (m.testBit(i)) {
                b = mul(b, a, d, p);
            }
        }
        return b;
    }

    public static BigInteger[] rho(BigInteger[] a, BigInteger[] b, BigInteger d, BigInteger p, BigInteger n) {
        kToTripleMap = new HashMap<>(n.sqrt().intValue());
        Long k = 0L;
        Triple t0 = new Triple(BigInteger.ZERO, BigInteger.ZERO, UNIT);
        kToTripleMap.put(k,t0);
        Sextuple tp = new Sextuple(t0, t0);
        do {
            tp = getNext(tp, a, b, d, p, k);
            k++;
        } while (!tp.check_zk_z2k());
        BigInteger v, w; // For calculating m = v/m
        v = modsub(tp.beta_2k, tp.beta_k, n);
        w = modsub(tp.alpha_k, tp.alpha_2k, n);
        if (w.equals(BigInteger.ZERO)) {
            throw new RuntimeException("rho method failed for the simple initialization of variables alpha0, beta0, and z0 ");
        }
//        kToTripleMap.clear();
        return new BigInteger[]{modInvmul(v, w, n), new BigInteger(String.valueOf(k))};
    }

    public static Sextuple getNext(Sextuple t, BigInteger[] a, BigInteger[] b, BigInteger d, BigInteger p, Long k) {
        // Compute alpha k+1 ,beta k+1 and z k+1 from alpha k ,beta k ,z k
        Triple nextLeft = increment(t.getLeft(), a, b, d, p, k);
        // Compute alpha 2k+1,beta 2k+1, z 2k+1 from alpha 2k,beta 2k,z 2k
        Triple nextRight = increment(t.getRight(), a, b, d, p, 2*k);
        return new Sextuple(nextLeft, increment(nextRight, a, b, d, p, 2*k+1));
    }

    public static Triple increment(Triple t, BigInteger[] a, BigInteger[] b, BigInteger d, BigInteger p, Long k) {
        Long k1 = k+1;
        if (kToTripleMap.containsKey(k1)) {
            return kToTripleMap.get(k1);
        }
        BigInteger alpha;
        BigInteger beta;
        BigInteger[] z;
        switch (t.z[0].mod(THREE).intValue()) {
            case 0:
                z = mul(b, t.z, d, p);
                alpha = t.alpha.add(BigInteger.ONE);
                beta = t.beta;
                break;
            case 1:
                z = mul(t.z, t.z, d, p);
                alpha = t.alpha.multiply(BigInteger.TWO);
                beta = t.beta.multiply(BigInteger.TWO);
                break;
            case 2:
                z = mul(a, t.z, d, p);
                alpha = t.alpha;
                beta = t.beta.add(BigInteger.ONE);
                break;
            default:
                throw new RuntimeException("Unexpected case : " + t.toString());
        }
        Triple tk1 = new Triple(alpha, beta, z);
        kToTripleMap.put(k1, tk1); //Adding k+1 triple to Map
        return tk1;
    }

    public static long check(BigInteger[] a, BigInteger d, BigInteger p, BigInteger n) {
        Random r = new Random();
        BigInteger m = BigInteger.valueOf(r.nextInt()).mod(n);
        //System.out.println("random generated m value is: " + m);
        BigInteger[] b = exp(a, m, d, p);
        //System.out.println("after exp value of b is: (" + b[0] + "," + b[1] + ")");
        BigInteger[] rhoresult = rho(a, b, d, p, n);
        //System.out.println("m' value from rho method is: " + rhoresult[0]);
        if (!rhoresult[0].equals(m)) {
            throw new RuntimeException("m: " + m + " and m': " + rhoresult[0] + " did not match!");
        }
        return rhoresult[1].longValue();
    }
}