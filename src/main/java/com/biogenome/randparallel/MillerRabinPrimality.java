package com.biogenome.randparallel;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

/**
 * Miller-Rabin Probabilistic Primality Test.
 * <p>
 * Employs randomized Fermat witness testing to verify large prime moduli used in
 * Rabin-Karp polynomial rolling hashing, genomic bloom filters, and cryptographic
 * patient identifier de-identification.
 * <br/>
 * <b>Error Bound:</b> If the algorithm declares a number "Probably Prime" across <code>k</code> rounds,
 * the error probability of it being composite is strictly &le; 4<sup>-k</sup>.
 * </p>
 *
 * <b>Complexity Analysis:</b>
 * <ul>
 *   <li><b>Time Complexity:</b> O(k &times; log&sup3; n) bit operations using modular exponentiation</li>
 *   <li><b>Space Complexity:</b> O(log n) bit storage</li>
 * </ul>
 *
 * @author BioGenome Team
 * @version 2.0.0
 */
public final class MillerRabinPrimality {

    public static final class RoundDetail {
        private final int roundNumber;
        private final String baseTested;
        private final boolean passed;
        private final String outcome;

        public RoundDetail(int roundNumber, String baseTested, boolean passed, String outcome) {
            this.roundNumber = roundNumber;
            this.baseTested = baseTested;
            this.passed = passed;
            this.outcome = outcome;
        }

        public int getRoundNumber() { return roundNumber; }
        public String getBaseTested() { return baseTested; }
        public boolean isPassed() { return passed; }
        public String getOutcome() { return outcome; }
    }

    public static final class PrimalityResult {
        private final String numberTested;
        private final boolean isPrime;
        private final int rounds;
        private final int s;
        private final String d;
        private final List<RoundDetail> roundDetails;
        private final String witness; // Composite witness if composite
        private final double maxErrorProbability;
        private final long executionTimeNs;

        public PrimalityResult(String numberTested, boolean isPrime, int rounds, int s, String d,
                               List<RoundDetail> roundDetails, String witness,
                               double maxErrorProbability, long executionTimeNs) {
            this.numberTested = numberTested;
            this.isPrime = isPrime;
            this.rounds = rounds;
            this.s = s;
            this.d = d;
            this.roundDetails = roundDetails;
            this.witness = witness;
            this.maxErrorProbability = maxErrorProbability;
            this.executionTimeNs = executionTimeNs;
        }

        public String getNumberTested() { return numberTested; }
        public boolean isPrime() { return isPrime; }
        public int getRounds() { return rounds; }
        public int getS() { return s; }
        public String getD() { return d; }
        public List<RoundDetail> getRoundDetails() { return roundDetails; }
        public String getWitness() { return witness; }
        public double getMaxErrorProbability() { return maxErrorProbability; }
        public double getExecutionTimeMs() { return executionTimeNs / 1_000_000.0; }
        public String getComplexity() { return "O(k * log^3 n) Randomized Modular Exponentiation"; }
    }

    private static final SecureRandom secureRandom = new SecureRandom();

    public static PrimalityResult testPrimality(String numberStr, int rounds) {
        long start = System.nanoTime();
        BigInteger n;
        try {
            n = new BigInteger(numberStr.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid integer format: " + numberStr);
        }

        if (rounds <= 0) rounds = 10;

        // Base cases
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return new PrimalityResult(numberStr, false, rounds, 0, "0",
                    Collections.emptyList(), "n <= 1", 0.0, System.nanoTime() - start);
        }
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) {
            return new PrimalityResult(numberStr, true, rounds, 0, "1",
                    Collections.emptyList(), null, 0.0, System.nanoTime() - start);
        }
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return new PrimalityResult(numberStr, false, rounds, 1, n.divide(BigInteger.TWO).toString(),
                    Collections.emptyList(), "Divisible by 2", 0.0, System.nanoTime() - start);
        }

        // Factor n - 1 = 2^s * d where d is odd
        BigInteger d = n.subtract(BigInteger.ONE);
        int s = 0;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            s++;
        }

        List<RoundDetail> roundDetails = new ArrayList<>();
        String compositeWitness = null;
        boolean probablyPrime = true;

        for (int r = 1; r <= rounds; r++) {
            // Pick a uniformly random base a in [2, n - 2]
            BigInteger a = getRandomBase(n);
            BigInteger x = a.modPow(d, n);

            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) {
                roundDetails.add(new RoundDetail(r, a.toString(), true, "x == 1 or x == n-1 passed"));
                continue;
            }

            boolean compositeInRound = true;
            for (int j = 1; j < s; j++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    compositeInRound = false;
                    roundDetails.add(new RoundDetail(r, a.toString(), true, "x^(2^" + j + ") == n-1 passed"));
                    break;
                }
            }

            if (compositeInRound) {
                compositeWitness = a.toString();
                probablyPrime = false;
                roundDetails.add(new RoundDetail(r, a.toString(), false, "Composite witness found (a = " + a + ")"));
                break;
            }
        }

        double errorProb = probablyPrime ? Math.pow(0.25, rounds) : 0.0;
        long elapsedNs = System.nanoTime() - start;
        return new PrimalityResult(numberStr, probablyPrime, rounds, s, d.toString(),
                roundDetails, compositeWitness, errorProb, elapsedNs);
    }

    private static BigInteger getRandomBase(BigInteger n) {
        BigInteger max = n.subtract(BigInteger.valueOf(2));
        BigInteger min = BigInteger.TWO;
        BigInteger range = max.subtract(min).add(BigInteger.ONE);
        if (range.compareTo(BigInteger.ONE) <= 0) {
            return BigInteger.TWO;
        }
        BigInteger a;
        do {
            a = new BigInteger(range.bitLength(), secureRandom).add(min);
        } while (a.compareTo(max) > 0 || a.compareTo(min) < 0);
        return a;
    }
}
