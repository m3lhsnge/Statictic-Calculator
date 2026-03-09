package com.istatistik.dto;

import java.util.List;

// Kullanıcıdan gelen istek verisi
public class DataRequest {

    private List<Double> data;       // Veri seti (sayılar)
    private int sampleSize;          // Örneklem büyüklüğü (n)
    private Integer classCount;      // Sınıf sayısı (opsiyonel, boş bırakılırsa Sturges kuralı kullanılır)
    private Integer strataCount;     // Tabaka sayısı (opsiyonel, boş bırakılırsa √N hesaplanır)

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
}
