package com.istatistik.service;

import com.istatistik.dto.DataRequest;
import com.istatistik.dto.FrequencyRow;
import com.istatistik.dto.StatisticsResponse;
import com.istatistik.dto.StratifiedRow;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsService {

    //tüm istatistiksel işlemleri çalıştırır
    public StatisticsResponse hesapla(DataRequest istek) {
        List<Double> veriler = istek.getData();
        int orneklemBuyuklugu = istek.getSampleSize();

        StatisticsResponse sonuc = new StatisticsResponse();

        // ======================================================
        // BRO, Sistematik, Tabakalı: min/max/N ile popülasyon üret
        // ======================================================
        Double minDeger = istek.getMinValue();
        Double maxDeger = istek.getMaxValue();
        Integer populasyonBuyuklugu = istek.getPopulationSize();

        if (minDeger != null && maxDeger != null && populasyonBuyuklugu != null) {
            // Validasyon
            if (minDeger >= maxDeger) {
                throw new IllegalArgumentException("Min değer, max değerden küçük olmalıdır.");
            }
            if (populasyonBuyuklugu <= 0) {
                throw new IllegalArgumentException("Popülasyon büyüklüğü (N) 0'dan büyük olmalıdır.");
            }
            if (orneklemBuyuklugu <= 0 || orneklemBuyuklugu > populasyonBuyuklugu) {
                throw new IllegalArgumentException("Örneklem büyüklüğü (n) 0'dan büyük ve N'den küçük veya eşit olmalıdır.");
            }

            // Min-Max arası N adet rastgele double sayı üret
            List<Double> populasyon = populasyonUret(minDeger, maxDeger, populasyonBuyuklugu);

            // Üretilen popülasyonu sonuca ekle (sıralı göster)
            List<Double> siraliPopulasyon = new ArrayList<>(populasyon);
            Collections.sort(siraliPopulasyon);
            sonuc.setUretilenPopulasyon(siraliPopulasyon);

            // BRO, Sistematik ve Tabakalı: üretilen popülasyondan örneklem al
            sonuc.setBasitRastgeleOrneklem(basitRastgeleOrneklemOlustur(populasyon, orneklemBuyuklugu));
            sonuc.setSistematikOrneklem(sistematikOrneklemOlustur(populasyon, orneklemBuyuklugu));
            sonuc.setTabakaliOrneklem(tabakaliOrneklemOlustur(populasyon, orneklemBuyuklugu, istek.getStrataCount()));
        }

        // ======================================================
        // Basit Seri, Frekans Serisi, Frekans Tablosu: veri setinden
        // ======================================================
        if (veriler != null && !veriler.isEmpty()) {
            sonuc.setBasitSeri(basitSeriOlustur(veriler));
            sonuc.setFrekansSeries(frekansSerisiOlustur(veriler));
            sonuc.setFrekansTablosu(frekansTablosuOlustur(veriler, istek.getClassCount()));

            // Veri setinden örneklem seçimi (dataSampleSize verilmişse)
            Integer veriOrneklemBuyuklugu = istek.getDataSampleSize();
            if (veriOrneklemBuyuklugu != null && veriOrneklemBuyuklugu > 0) {
                if (veriOrneklemBuyuklugu > veriler.size()) {
                    throw new IllegalArgumentException("Veri seti örneklem büyüklüğü (n), veri sayısından (" + veriler.size() + ") büyük olamaz.");
                }
                sonuc.setVeriSetiOrneklem(basitRastgeleOrneklemOlustur(veriler, veriOrneklemBuyuklugu));
            }
        }

        return sonuc;
    }

    /**
     * Min-Max arasında N adet rastgele double sayı üretir.
     * Sayılar 2 ondalık basamağa yuvarlanır.
     */
    private List<Double> populasyonUret(double min, double max, int N) {
        Random rastgele = new Random();
        List<Double> populasyon = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            double deger = min + (max - min) * rastgele.nextDouble();
            // 2 ondalık basamağa yuvarla
            deger = Math.round(deger * 100.0) / 100.0;
            populasyon.add(deger);
        }
        return populasyon;
    }

    
    // 1. BASİT RASTGELE ÖRNEKLEM
    // Popülasyondan rastgele n tane eleman seçer
    
    private List<Double> basitRastgeleOrneklemOlustur(List<Double> veriler, int n) {
        // Tüm indeksleri bir listeye koy
        List<Integer> indeksler = new ArrayList<>();
        for (int i = 0; i < veriler.size(); i++) {
            indeksler.add(i);
        }

        // İndeksleri karıştır 
        Collections.shuffle(indeksler);

        // İlk n indeksi seç ve sırala
        List<Integer> secilenIndeksler = indeksler.subList(0, n);
        Collections.sort(secilenIndeksler);

        // Seçilen indekslerdeki değerleri al
        List<Double> orneklem = new ArrayList<>();
        for (int indeks : secilenIndeksler) {
            orneklem.add(veriler.get(indeks));
        }

        return orneklem;
    }

    
    // 2. SİSTEMATİK ÖRNEKLEM
    // k = N/n aralığıyla, rastgele başlayarak seçer

    private List<Double> sistematikOrneklemOlustur(List<Double> veriler, int n) {
        int N = veriler.size();

        // Adım aralığını hesapla: k = N / n
        int k = N / n;
        if (k < 1) k = 1;

        // 0 ile k arasında rastgele bir başlangıç noktası seç
        Random rastgele = new Random();
        int baslangic = rastgele.nextInt(k);

        // Başlangıçtan itibaren her k. elemanı seç
        List<Double> orneklem = new ArrayList<>();
        for (int i = baslangic; i < N && orneklem.size() < n; i += k) {
            orneklem.add(veriler.get(i));
        }

        return orneklem;
    }

    
    // 3. BASİT SERİ
    // Verileri küçükten büyüğe sırala
    
    private List<Double> basitSeriOlustur(List<Double> veriler) {
        List<Double> sirali = new ArrayList<>(veriler);
        Collections.sort(sirali);
        return sirali;
    }

    
    // 4. FREKANS SERİSİ
    // Her değerin kaç kez tekrarlandığı

    private Map<Double, Integer> frekansSerisiOlustur(List<Double> veriler) {
        // TreeMap kullanarak değerleri sıralı tutar
        Map<Double, Integer> frekansSayaci = new TreeMap<>();

        for (Double deger : veriler) {
            if (frekansSayaci.containsKey(deger)) {
                frekansSayaci.put(deger, frekansSayaci.get(deger) + 1);
            } else {
                frekansSayaci.put(deger, 1);
            }
        }

        return frekansSayaci;
    }

    
    // 5. FREKANS TABLOSU
    // Verileri sınıf aralıklarına gruplar
    // Sturges kuralı: k = 1 + 3.322 * log10(N)
    
    private List<FrequencyRow> frekansTablosuOlustur(List<Double> veriler, Integer sinifSayisiParametre) {
        int N = veriler.size();
        double enKucuk = Collections.min(veriler);
        double enBuyuk = Collections.max(veriler);

        // Sınıf sayısını belirle (kullanıcı girmediyse Sturges kuralı)
        int sinifSayisi;
        if (sinifSayisiParametre != null && sinifSayisiParametre > 0) {
            sinifSayisi = sinifSayisiParametre;
        } else {
            sinifSayisi = (int) Math.ceil(1 + 3.322 * Math.log10(N));
        }

        // Sınıf genişliğini hesapla
        double aralik = enBuyuk - enKucuk;
        double sinifGenisligi = aralik / sinifSayisi;
        if (sinifGenisligi == 0) sinifGenisligi = 1.0;

        // Her sınıf aralığı için frekansları hesapla
        List<FrequencyRow> satirlar = new ArrayList<>();
        int kumulatifFrekans = 0;

        for (int i = 0; i < sinifSayisi; i++) {
            double altSinir = enKucuk + i * sinifGenisligi;
            double ustSinir = altSinir + sinifGenisligi;

            // Bu aralığa düşen verileri say
            int frekans = 0;
            for (Double deger : veriler) {
                if (i == sinifSayisi - 1) {
                    // Son sınıf üst sınırı dahil eder (kayan nokta hassasiyeti için küçük bir tolerans payı eklendi)
                    if (deger >= altSinir && deger <= ustSinir + 1e-9) frekans++;
                } else {
                    if (deger >= altSinir && deger < ustSinir) frekans++;
                }
            }

            // Kümülatif frekansı güncelle
            kumulatifFrekans += frekans;

            // Bağıl frekans = frekans / toplam veri sayısı
            double bagilFrekans = Math.round(((double) frekans / N) * 10000.0) / 10000.0;
            double kumulatifBagilFrekans = Math.round(((double) kumulatifFrekans / N) * 10000.0) / 10000.0;

            String aralikMetni = sayiFormatla(altSinir) + " - " + sayiFormatla(ustSinir);

            satirlar.add(new FrequencyRow(
                    aralikMetni, altSinir, ustSinir,
                    frekans, bagilFrekans,
                    kumulatifFrekans, kumulatifBagilFrekans));
        }

        return satirlar;
    }

    
    // 6. TABAKALI ÖRNEKLEM
    // veriyi tabakalara ayırıp her tabakadan
    // oransal  seçim 
    
    private List<StratifiedRow> tabakaliOrneklemOlustur(List<Double> veriler, int orneklemBuyuklugu, Integer tabakaSayisiParametre) {
        // Adım 1: Verileri sırala
        List<Double> sirali = new ArrayList<>(veriler);
        Collections.sort(sirali);
        int N = sirali.size();

        // Adım 2: Tabaka sayısını belirle (kullanıcı girmediyse karekök kuralı)
        int tabakaSayisi;
        if (tabakaSayisiParametre != null && tabakaSayisiParametre > 0) {
            tabakaSayisi = tabakaSayisiParametre;
        } else {
            tabakaSayisi = (int) Math.round(Math.sqrt(N));
        }
        if (tabakaSayisi < 2) tabakaSayisi = 2;
        if (tabakaSayisi > N) tabakaSayisi = N;

        // Adım 3: Sıralı veriyi eşit parçalara böl
        List<List<Double>> tabakalar = new ArrayList<>();
        int temelBoyut = N / tabakaSayisi;
        int kalan = N % tabakaSayisi;
        int indeks = 0;

        for (int i = 0; i < tabakaSayisi; i++) {
            int boyut = temelBoyut + (i < kalan ? 1 : 0);
            tabakalar.add(new ArrayList<>(sirali.subList(indeks, indeks + boyut)));
            indeks += boyut;
        }

        // Adım 4: Her tabakadan oransal seçim yap
        List<StratifiedRow> sonuc = new ArrayList<>();
        int toplamSecilen = 0;
        Random rastgele = new Random();

        for (int i = 0; i < tabakalar.size(); i++) {
            List<Double> tabaka = tabakalar.get(i);

            // Oransal dağılım: n_i = round(N_i / N * n)
            int secilenAdet;
            if (i == tabakalar.size() - 1) {
                // Son tabaka kalan elemanları alır
                secilenAdet = orneklemBuyuklugu - toplamSecilen;
            } else {
                double oran = (double) tabaka.size() / N;
                secilenAdet = (int) Math.round(oran * orneklemBuyuklugu);
            }

            // Sınır kontrolüd
            secilenAdet = Math.min(secilenAdet, tabaka.size());
            secilenAdet = Math.max(secilenAdet, 0);

            // Tabakadan rastgele seç
            List<Double> tabakaKopya = new ArrayList<>(tabaka);
            Collections.shuffle(tabakaKopya, rastgele);
            List<Double> secilenler = new ArrayList<>(tabakaKopya.subList(0, secilenAdet));
            Collections.sort(secilenler);

            toplamSecilen += secilenAdet;

            // Tabaka etiketini oluştur
            String etiket = "Tabaka " + (i + 1) + " (" + sayiFormatla(tabaka.get(0))
                    + " - " + sayiFormatla(tabaka.get(tabaka.size() - 1)) + ")";

            sonuc.add(new StratifiedRow(etiket, tabaka.size(), secilenAdet, secilenler));
        }

        return sonuc;
    }

    // Sayıyı güzel formatta gösterir (tam sayıysa ondalık göstermez)
    private String sayiFormatla(double deger) {
        if (deger == Math.floor(deger) && !Double.isInfinite(deger)) {
            return String.valueOf((int) deger);
        }
        return String.format("%.2f", deger);
    }
}
