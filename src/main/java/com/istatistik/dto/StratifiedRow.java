package com.istatistik.dto;

import java.util.List;

// Tabakalı örneklemin bir tabakasını temsil eder
public class StratifiedRow {

    private String tabakaAdi; // Tabaka ismi (örn: "Tabaka 1 (3 - 10)")
    private int tabakaBuyuklugu; // Tabakadaki toplam eleman sayısı
    private int secilenAdet; // Bu tabakadan seçilen eleman sayısı
    private List<Double> secilenDegerler; // Seçilen değerler

    public StratifiedRow() {
    }

    public StratifiedRow(String tabakaAdi, int tabakaBuyuklugu, int secilenAdet, List<Double> secilenDegerler) {
        this.tabakaAdi = tabakaAdi;
        this.tabakaBuyuklugu = tabakaBuyuklugu;
        this.secilenAdet = secilenAdet;
        this.secilenDegerler = secilenDegerler;
    }

    public String getTabakaAdi() {
        return tabakaAdi;
    }

    public void setTabakaAdi(String tabakaAdi) {
        this.tabakaAdi = tabakaAdi;
    }

    public int getTabakaBuyuklugu() {
        return tabakaBuyuklugu;
    }

    public void setTabakaBuyuklugu(int tabakaBuyuklugu) {
        this.tabakaBuyuklugu = tabakaBuyuklugu;
    }

    public int getSecilenAdet() {
        return secilenAdet;
    }

    public void setSecilenAdet(int secilenAdet) {
        this.secilenAdet = secilenAdet;
    }

    public List<Double> getSecilenDegerler() {
        return secilenDegerler;
    }

    public void setSecilenDegerler(List<Double> secilenDegerler) {
        this.secilenDegerler = secilenDegerler;
    }
}
