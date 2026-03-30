package com.istatistik.dto;

import java.util.List;

// Kullanıcıdan gelen istek verisi
public class DataRequest {

    private List<Double> data;       // Veri seti (sayılar) — Basit Seri, Frekans için
    private int sampleSize;          // Örneklem büyüklüğü (küçük n) — örneklem parametreleri için
    private Integer classCount;      // Sınıf sayısı (opsiyonel, boş bırakılırsa Sturges kuralı kullanılır)
    private Integer strataCount;     // Tabaka sayısı (opsiyonel, boş bırakılırsa √N hesaplanır)
    private Integer dataSampleSize;  // Veri seti örneklem büyüklüğü (küçük n) — veri seti için

    // BRO, Sistematik, Tabakalı için yeni alanlar
    private Double minValue;         // Min değer (double)
    private Double maxValue;         // Max değer (double)
    private Integer populationSize;  // Büyük N (popülasyon/veri sayısı)

    public DataRequest() {
    }

    public DataRequest(List<Double> data, int sampleSize, Integer classCount) {
        this.data = data;
        this.sampleSize = sampleSize;
        this.classCount = classCount;
    }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    public Integer getClassCount() {
        return classCount;
    }

    public void setClassCount(Integer classCount) {
        this.classCount = classCount;
    }

    public Integer getStrataCount() {
        return strataCount;
    }

    public void setStrataCount(Integer strataCount) {
        this.strataCount = strataCount;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(Integer populationSize) {
        this.populationSize = populationSize;
    }

    public Integer getDataSampleSize() {
        return dataSampleSize;
    }

    public void setDataSampleSize(Integer dataSampleSize) {
        this.dataSampleSize = dataSampleSize;
    }
}
