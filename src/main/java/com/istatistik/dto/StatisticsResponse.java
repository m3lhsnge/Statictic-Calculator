package com.istatistik.dto;

import java.util.List;
import java.util.Map;

// Hesaplama sonuçlarını tutan sınıf
public class StatisticsResponse {

    private List<Double> basitRastgeleOrneklem;         // Rastgele seçilen elemanlar
    private List<Double> sistematikOrneklem;             // Sistematik seçilen elemanlar
    private List<Double> basitSeri;                      // Sıralı veri
    private Map<Double, Integer> frekansSeries;          // Değer -> tekrar sayısı
    private List<FrequencyRow> frekansTablosu;            // Gruplandırılmış frekans tablosu
    private List<StratifiedRow> tabakaliOrneklem;         // Tabakalı örneklem sonuçları

    public StatisticsResponse() {
    }

    public List<Double> getBasitRastgeleOrneklem() {
        return basitRastgeleOrneklem;
    }

    public void setBasitRastgeleOrneklem(List<Double> basitRastgeleOrneklem) {
        this.basitRastgeleOrneklem = basitRastgeleOrneklem;
    }

    public List<Double> getSistematikOrneklem() {
        return sistematikOrneklem;
    }

    public void setSistematikOrneklem(List<Double> sistematikOrneklem) {
        this.sistematikOrneklem = sistematikOrneklem;
    }

    public List<Double> getBasitSeri() {
        return basitSeri;
    }

    public void setBasitSeri(List<Double> basitSeri) {
        this.basitSeri = basitSeri;
    }

    public Map<Double, Integer> getFrekansSeries() {
        return frekansSeries;
    }

    public void setFrekansSeries(Map<Double, Integer> frekansSeries) {
        this.frekansSeries = frekansSeries;
    }

    public List<FrequencyRow> getFrekansTablosu() {
        return frekansTablosu;
    }

    public void setFrekansTablosu(List<FrequencyRow> frekansTablosu) {
        this.frekansTablosu = frekansTablosu;
    }

    public List<StratifiedRow> getTabakaliOrneklem() {
        return tabakaliOrneklem;
    }

    public void setTabakaliOrneklem(List<StratifiedRow> tabakaliOrneklem) {
        this.tabakaliOrneklem = tabakaliOrneklem;
    }
}
