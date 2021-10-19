package me.astri.idleBot.GameBot.Entities;

import java.util.regex.Pattern;

public class Number {//TODO try to avoid using Math.pow
    private double value;
    private long powerOfTen;

    public Number(double value, long powerOfTen) {
        this.value = value;
        this.powerOfTen = powerOfTen;
        toScientificNotation();
    }
    public Number() {
        this(0,0);
    }
    public Number(Number number) {
        this(number.value,number.powerOfTen);
    }

    public Number(double value) {
        this.value = value;
        toScientificNotation();
    }
    public Number(String value) {
        this(Double.parseDouble(value));
    }
    public Number(long value) {
        this(((double) value));
    }
    public Number(int value) {
        this(((double) value));
    }

    public Number add(Number n1) {
        this.value += n1.value/Math.pow(10,this.powerOfTen-n1.powerOfTen);
        toScientificNotation();
        return this;
    }

    public Number subtract(Number n1) {
        this.value -= n1.value/Math.pow(10,this.powerOfTen-n1.powerOfTen);
        toScientificNotation();
        return this;
    }

    public Number multiply(Number n1) {
        this.value *= n1.value;
        this.powerOfTen += n1.powerOfTen;
        toScientificNotation();
        return this;
    }

    public Number divide(Number n1) {
        this.value /= n1.value;
        this.powerOfTen -= n1.powerOfTen;
        toScientificNotation();
        return this;
    }

    public Number pow(Number n1) {
        this.powerOfTen *= n1.value * Math.pow(10,n1.powerOfTen);
        this.value = Math.pow(Math.pow(value,n1.value),Math.pow(10,n1.powerOfTen));
        toScientificNotation();
        return this;
    }

    public static Number add(Number n1,Number n2) {
        return new Number(n1).add(n2);
    }

    public static Number subtract(Number n1,Number n2) {
        return new Number(n1).subtract(n2);
    }

    public static Number multiply(Number n1,Number n2) {
        return new Number(n1).multiply(n2);
    }

    public static Number divide(Number n1,Number n2) {
        return new Number(n1).divide(n2);
    }

    public static Number pow(Number n1,Number n2) {
        return new Number(n1).pow(n2);
    }


    private static final String[] bigUnits = {"","K","M","B","T","Qa","Qi","Sx","Sp","Oc","No","De","Ud","Dd","Td","Qt",
            "Qd","Sd","St","Od","Nd","Vg"};
    public String getUnitNotation() {
        String unit = bigUnits[(int) (this.powerOfTen/3)];
        String tweaked_value = Double.toString(this.value * Math.pow(10,powerOfTen%3));
        return tweaked_value + unit;
    }

    private static final Pattern pattern = Pattern.compile("(,.)?0+$");
    public String getScientificNotation(int digits) {
        return pattern.matcher(String.format("%."+(digits-1)+ "f",this.value)).replaceAll("$1") +"e"+ this.powerOfTen;
    }

    public String getScientificNotation() {
        return getScientificNotation(3);
    }

    private void toScientificNotation() {
        if(this.value == 0) {
            this.powerOfTen = 0;
            return;
        }

        while(Math.abs(this.value) >= 10.) {
            this.value/=10.;
            this.powerOfTen++;
        }

        while(Math.abs(this.value) < 1.) {
            this.value*=10.;
            this.powerOfTen--;
        }
    }
}
