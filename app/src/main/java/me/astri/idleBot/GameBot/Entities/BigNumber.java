package me.astri.idleBot.GameBot.entities;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@SuppressWarnings("unused")
public class BigNumber {//TODO try to avoid using Math.pow
    private double val;
    private long p10;

    public BigNumber(double val, long powOf10) {
        this.val = val;
        this.p10 = powOf10;
        toScientificNotation();
    }
    public BigNumber() {
        this(0,0);
    }
    public BigNumber(BigNumber bigNumber) {
        this(bigNumber.val, bigNumber.p10);
    }

    public BigNumber(double val) {
        this.val = val;
        toScientificNotation();
    }
    public BigNumber(long val) {
        this((double) val);
    }
    public BigNumber(int val) {
        this((double) val);
    }
    public BigNumber(String nb) {
        String[] args = nb.split("e");
        this.val = Double.parseDouble(args[0]);
        this.p10 = args.length != 1 ? Long.parseLong(args[1]) : 0;
        toScientificNotation();
    }

    public BigNumber add(@NotNull BigNumber n1) {
        if(this.p10 - n1.p10 > 20) { //this >> n1 so we return this
            return this;
        }
        else if(n1.p10 - this.p10 > 20) { //this << n1 so we return n1
            this.p10 = n1.p10;
            this.val = n1.val;
            return this;
        }
        else { //perform the calculation
            this.val += n1.val /Math.pow(10,this.p10 -n1.p10);
            toScientificNotation();
            return this;
        }
    }

    public BigNumber subtract(@NotNull BigNumber n1) {
        n1.val *=-1;
        this.add(n1);
        return this;
    }

    public BigNumber multiply(@NotNull BigNumber n1) {
        this.val *= n1.val;
        this.p10 += n1.p10;
        toScientificNotation();
        return this;
    }

    public BigNumber divide(@NotNull BigNumber n1) {
        this.val /= n1.val;
        this.p10 -= n1.p10;
        toScientificNotation();
        return this;
    }

    public BigNumber pow(@NotNull BigNumber n1) {
        this.p10 *= n1.val * Math.pow(10,n1.p10);
        this.val = Math.pow(val,n1.val *Math.pow(10,n1.p10));
        toScientificNotation();
        return this;
    }

    public BigNumber negate() {
        this.val *= -1;
        return this;
    }

    public static BigNumber add(@NotNull BigNumber n1, @NotNull BigNumber n2) {
        return new BigNumber(n1).add(n2);
    }

    public static BigNumber subtract(@NotNull BigNumber n1, @NotNull BigNumber n2) {
        return new BigNumber(n1).subtract(n2);
    }

    public static BigNumber multiply(@NotNull BigNumber n1, @NotNull BigNumber n2) {
        return new BigNumber(n1).multiply(n2);
    }

    public static BigNumber divide(@NotNull BigNumber n1, @NotNull BigNumber n2) {
        return new BigNumber(n1).divide(n2);
    }

    public static BigNumber pow(@NotNull BigNumber n1, @NotNull BigNumber n2) {
        return new BigNumber(n1).pow(n2);
    }

    public static BigNumber negate(@NotNull BigNumber n1) {
        return new BigNumber(n1).negate();
    }


    private String getRoundVal(int precision, double val) {
        DecimalFormat myFormatter = new DecimalFormat("###." + "#".repeat(precision));
        return myFormatter.format(val);
    }

    private static final String[] bigUnits = {"","K","M","B","T","Qa","Qi","Sx","Sp","Oc","No","De","Ud","Dd","Td","Qt",
            "Qd","Sd","St","Od","Nd","Vg"};
    public String getUnitNotation() {
        int unitRank = (int)(this.p10/3); //int division -> floor
        if(unitRank >= bigUnits.length) return getScientificNotation();

        String tweaked_value = this.getRoundVal(2,this.val * Math.pow(10, p10 % 3));
        return tweaked_value + bigUnits[unitRank];
    }

    public String getScientificNotation() {
        return "%se%d".formatted(this.getRoundVal(2, this.val),this.p10);
    }

    @Override
    public String toString() {
        return this.getScientificNotation();
    }

    public String getNotation(boolean scientific) {
        if(scientific && this.p10 >= 3 ) return getScientificNotation();
        else return getUnitNotation();
    }

    private void toScientificNotation() {
        try {
            if (Double.isInfinite(this.val)) {
                throw new NumberFormatException("Number is infinite!");
            }

            if (this.val == 0) {
                this.p10 = 0;
                return;
            }

            while (Math.abs(this.val) >= 10.) {
                this.val /= 10.;
                this.p10++;
            }

            while (Math.abs(this.val) < 1.) {
                this.val *= 10.;
                this.p10--;
            }
        } catch (NumberFormatException e) {e.printStackTrace();}
    }

    public double toDouble() {
        return this.val *Math.pow(10,this.p10);
    }

    public int compareTo(BigNumber nb) {
        if(nb.p10 > this.p10) return -1;
        if(nb.p10 < this.p10) return 1;
        return Double.compare(this.val, nb.val); // -1, 0 or 1 | < = >
    }
}