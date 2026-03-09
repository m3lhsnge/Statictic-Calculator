package com.istatistik.dto;

// Frekans tablosunun bir satırını temsil eder
public class FrequencyRow {

    private String classInterval; // Sınıf aralığı (örn: "10 - 20")
    private double lowerBound; // Alt sınır
    private double upperBound; // Üst sınır
    private int frequency; // Frekans (f)
    private double relativeFrequency; // Bağıl frekans (f/n)
    private int cumulativeFrequency; // Kümülatif frekans
    private double cumulativeRelativeFrequency; // Kümülatif bağıl frekans

    public FrequencyRow() {
    }

    public FrequencyRow(String classInterval, double lowerBound, double upperBound,
            int frequency, double relativeFrequency,
            int cumulativeFrequency, double cumulativeRelativeFrequency) {
        this.classInterval = classInterval;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.frequency = frequency;
        this.relativeFrequency = relativeFrequency;
        this.cumulativeFrequency = cumulativeFrequency;
        this.cumulativeRelativeFrequency = cumulativeRelativeFrequency;
    }

    public String getClassInterval() {
        return classInterval;
    }

    public void setClassInterval(String classInterval) {
        this.classInterval = classInterval;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public double getRelativeFrequency() {
        return relativeFrequency;
    }

    public void setRelativeFrequency(double relativeFrequency) {
        this.relativeFrequency = relativeFrequency;
    }

    public int getCumulativeFrequency() {
        return cumulativeFrequency;
    }

    public void setCumulativeFrequency(int cumulativeFrequency) {
        this.cumulativeFrequency = cumulativeFrequency;
    }

    public double getCumulativeRelativeFrequency() {
        return cumulativeRelativeFrequency;
    }

    public void setCumulativeRelativeFrequency(double cumulativeRelativeFrequency) {
        this.cumulativeRelativeFrequency = cumulativeRelativeFrequency;
    }
}
