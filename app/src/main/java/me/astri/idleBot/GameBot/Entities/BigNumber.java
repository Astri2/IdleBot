package me.astri.idleBot.GameBot.entities;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@SuppressWarnings("unused")
public class BigNumber {//TODO try to avoid using Math.pow
    private double value;
    private long powerOfTen;

    public BigNumber(double value, long powerOfTen) {
        this.value = value;
        this.powerOfTen = powerOfTen;
        toScientificNotation();
    }
    public BigNumber() {
        this(0,0);
    }
    public BigNumber(BigNumber bigNumber) {
        this(bigNumber.value, bigNumber.powerOfTen);
    }

    public BigNumber(double value) {
        this.value = value;
        toScientificNotation();
    }
    public BigNumber(long value) {
        this((double) value);
    }
    public BigNumber(int value) {
        this((double) value);
    }
    public BigNumber(String value) {
        this(Double.parseDouble(value));
    }

    public BigNumber add(@NotNull BigNumber n1) {
        if(this.powerOfTen - n1.powerOfTen > 20) { //this >> n1 so we return this
            return this;
        }
        else if(n1.powerOfTen - this.powerOfTen > 20) { //this << n1 so we return n1
            this.powerOfTen = n1.powerOfTen;
            this.value = n1.value;
            return this;
        }
        else { //perform the calculation
            this.value += n1.value/Math.pow(10,this.powerOfTen-n1.powerOfTen);
            toScientificNotation();
            return this;
        }
    }

    public BigNumber subtract(@NotNull BigNumber n1) {
        n1.value*=-1;
        this.add(n1);
        return this;
    }

    public BigNumber multiply(@NotNull BigNumber n1) {
        this.value *= n1.value;
        this.powerOfTen += n1.powerOfTen;
        toScientificNotation();
        return this;
    }

    public BigNumber divide(@NotNull BigNumber n1) {
        this.value /= n1.value;
        this.powerOfTen -= n1.powerOfTen;
        toScientificNotation();
        return this;
    }

    public BigNumber pow(@NotNull BigNumber n1) {
        this.powerOfTen *= n1.value * Math.pow(10,n1.powerOfTen);
        this.value = Math.pow(value,n1.value*Math.pow(10,n1.powerOfTen));
        toScientificNotation();
        return this;
    }

    public BigNumber negate() {
        this.value *= -1;
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
        String unit = bigUnits[(int) (this.powerOfTen/3)];
        String tweaked_value = this.getRoundVal(2,this.value * Math.pow(10,powerOfTen%3));
        return tweaked_value + unit;
    }

    public String getScientificNotation() {
        return "%se%d".formatted(this.getRoundVal(2, this.value),this.powerOfTen);
    }

    @Override
    public String toString() {
        return this.getScientificNotation();
    }

    public String getNotation(boolean scientific) {
        if(scientific && this.powerOfTen >= 3 ) return getScientificNotation();
        else return getUnitNotation();
    }

    private void toScientificNotation() {
        try {
            if (Double.isInfinite(this.value)) {
                throw new NumberFormatException("Number is infinite!");
            }

            if (this.value == 0) {
                this.powerOfTen = 0;
                return;
            }

            while (Math.abs(this.value) >= 10.) {
                this.value /= 10.;
                this.powerOfTen++;
            }

            while (Math.abs(this.value) < 1.) {
                this.value *= 10.;
                this.powerOfTen--;
            }
        } catch (NumberFormatException e) {e.printStackTrace();}
    }

    public double toDouble() {
        return this.value*Math.pow(10,this.powerOfTen);
    }

    public int compareTo(BigNumber nb) {
        if(nb.powerOfTen > this.powerOfTen) return -1;
        if(nb.powerOfTen < this.powerOfTen) return 1;
        return Double.compare(this.value, nb.value); // -1, 0 or 1 | < = >
    }
}