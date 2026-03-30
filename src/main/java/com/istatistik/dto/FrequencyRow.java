package com.istatistik.dto;

/**
 * Frekans tablosunun bir satırını temsil eder.
 * Akademik adımlara göre: Alt Limit, Üst Limit, Alt Sınır, Üst Sınır, Orta Nokta (yk), Frekans (f), Eklemeli Frekans.
 */
public class FrequencyRow {

    private String classInterval;       // Sınıf aralığı metin (örn: "10 - 20")
    private double altLimit;            // Sınıf Alt Limiti
    private double ustLimit;            // Sınıf Üst Limiti
    private double altSinir;            // Sınıf Alt Sınırı (boundary)
    private double ustSinir;            // Sınıf Üst Sınırı (boundary)
    private double ortaNokta;           // Sınıf Orta Noktası (yk)
    private int frequency;              // Frekans (fi)
    private double relativeFrequency;   // Bağıl frekans (fi/n)
    private int cumulativeFrequency;    // Eklemeli (Kümülatif) Frekans
    private double cumulativeRelativeFrequency; // Kümülatif bağıl frekans

    // Eski alanlar (uyumluluk)
    private double lowerBound;
    private double upperBound;

    public FrequencyRow() {}

    public FrequencyRow(String classInterval, double altLimit, double ustLimit,
                        double altSinir, double ustSinir, double ortaNokta,
                        int frequency, double relativeFrequency,
                        int cumulativeFrequency, double cumulativeRelativeFrequency) {
        this.classInterval = classInterval;
        this.altLimit = altLimit;
        this.ustLimit = ustLimit;
        this.altSinir = altSinir;
        this.ustSinir = ustSinir;
        this.ortaNokta = ortaNokta;
        this.frequency = frequency;
        this.relativeFrequency = relativeFrequency;
        this.cumulativeFrequency = cumulativeFrequency;
        this.cumulativeRelativeFrequency = cumulativeRelativeFrequency;
        // Eski alanlarla uyumluluk
        this.lowerBound = altSinir;
        this.upperBound = ustSinir;
    }

    // Getters & Setters

    public String getClassInterval() { return classInterval; }
    public void setClassInterval(String classInterval) { this.classInterval = classInterval; }

    public double getAltLimit() { return altLimit; }
    public void setAltLimit(double altLimit) { this.altLimit = altLimit; }

    public double getUstLimit() { return ustLimit; }
    public void setUstLimit(double ustLimit) { this.ustLimit = ustLimit; }

    public double getAltSinir() { return altSinir; }
    public void setAltSinir(double altSinir) { this.altSinir = altSinir; }

    public double getUstSinir() { return ustSinir; }
    public void setUstSinir(double ustSinir) { this.ustSinir = ustSinir; }

    public double getOrtaNokta() { return ortaNokta; }
    public void setOrtaNokta(double ortaNokta) { this.ortaNokta = ortaNokta; }

    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }

    public double getRelativeFrequency() { return relativeFrequency; }
    public void setRelativeFrequency(double relativeFrequency) { this.relativeFrequency = relativeFrequency; }

    public int getCumulativeFrequency() { return cumulativeFrequency; }
    public void setCumulativeFrequency(int cumulativeFrequency) { this.cumulativeFrequency = cumulativeFrequency; }

    public double getCumulativeRelativeFrequency() { return cumulativeRelativeFrequency; }
    public void setCumulativeRelativeFrequency(double cumulativeRelativeFrequency) { this.cumulativeRelativeFrequency = cumulativeRelativeFrequency; }

    public double getLowerBound() { return lowerBound; }
    public void setLowerBound(double lowerBound) { this.lowerBound = lowerBound; }

    public double getUpperBound() { return upperBound; }
    public void setUpperBound(double upperBound) { this.upperBound = upperBound; }
}
