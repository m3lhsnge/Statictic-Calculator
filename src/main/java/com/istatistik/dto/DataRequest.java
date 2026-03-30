package com.istatistik.dto;

import java.util.List;

// Kullanıcıdan gelen istek verisi
public class DataRequest {

    private List<Double> data;       // Veri seti (sayılar) — Basit Seri, Frekans için
    private int sampleSize;          // Örneklem büyüklüğü (küçük n) — örneklem parametreleri için
    private Integer classCount;      // Sınıf sayısı (opsiyonel, boş bırakılırsa Sturges kuralı kullanılır)
    private Integer strataCount;     // Tabaka sayısı (opsiyonel, boş bırakılırsa √N hesaplanır)
    private Integer dataSampleSize;  // Veri seti örneklem büyüklüğü (küçük n) — veri seti için

    // Ortak alanlar veya Basit Seri/Frekans için
    private Double minValue;         
    private Double maxValue;         

    // Basit Rastgele Örneklem
    private Integer basRastMin;
    private Integer basRastMax;
    private Integer basRastN;

    // Sistematik Örneklem
    private Double sisMin;
    private Double sisMax;
    private Integer sisBuyukN;
    private Integer sisKucukN;

    // Tabakalı Örneklem
    private Double tabMin;
    private Double tabMax;
    private Integer tabBuyukN;
    private Integer tabKucukN;
    private String tabOranlar;
    
    // Yalnızca eski uyumluluk için, artık ayrı alanlar kullanılıyor olabilir.
    private Integer populationSize;

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

    public Integer getBasRastMin() {
        return basRastMin;
    }

    public void setBasRastMin(Integer basRastMin) {
        this.basRastMin = basRastMin;
    }

    public Integer getBasRastMax() {
        return basRastMax;
    }

    public void setBasRastMax(Integer basRastMax) {
        this.basRastMax = basRastMax;
    }

    public Integer getBasRastN() {
        return basRastN;
    }

    public void setBasRastN(Integer basRastN) {
        this.basRastN = basRastN;
    }

    public Double getSisMin() {
        return sisMin;
    }

    public void setSisMin(Double sisMin) {
        this.sisMin = sisMin;
    }

    public Double getSisMax() {
        return sisMax;
    }

    public void setSisMax(Double sisMax) {
        this.sisMax = sisMax;
    }

    public Integer getSisBuyukN() {
        return sisBuyukN;
    }

    public void setSisBuyukN(Integer sisBuyukN) {
        this.sisBuyukN = sisBuyukN;
    }

    public Integer getSisKucukN() {
        return sisKucukN;
    }

    public void setSisKucukN(Integer sisKucukN) {
        this.sisKucukN = sisKucukN;
    }

    public Double getTabMin() {
        return tabMin;
    }

    public void setTabMin(Double tabMin) {
        this.tabMin = tabMin;
    }

    public Double getTabMax() {
        return tabMax;
    }

    public void setTabMax(Double tabMax) {
        this.tabMax = tabMax;
    }

    public Integer getTabBuyukN() {
        return tabBuyukN;
    }

    public void setTabBuyukN(Integer tabBuyukN) {
        this.tabBuyukN = tabBuyukN;
    }

    public Integer getTabKucukN() {
        return tabKucukN;
    }

    public void setTabKucukN(Integer tabKucukN) {
        this.tabKucukN = tabKucukN;
    }

    public String getTabOranlar() {
        return tabOranlar;
    }

    public void setTabOranlar(String tabOranlar) {
        this.tabOranlar = tabOranlar;
    }
}
