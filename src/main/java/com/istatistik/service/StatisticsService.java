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

        StatisticsResponse sonuc = new StatisticsResponse();

        // ======================================================
        // 1. BASİT RASTGELE ÖRNEKLEM
        // ======================================================
        if (istek.getBasRastMin() != null && istek.getBasRastMax() != null && istek.getBasRastN() != null) {
            int min = istek.getBasRastMin();
            int max = istek.getBasRastMax();
            int n = istek.getBasRastN();
            
            if (min >= max) {
                throw new IllegalArgumentException("Basit Rastgele: Min değer, max değerden küçük olmalıdır.");
            }
            if (n <= 0) {
                throw new IllegalArgumentException("Basit Rastgele: n değeri 0'dan büyük olmalıdır.");
            }
            
            List<Double> broList = new ArrayList<>();
            Random rng = new Random();
            int kapasite = max - min + 1;
            
            if (n <= kapasite) {
                // Örneklem sayısı max kapasiteye eşit veya küçükse: yeniden yazma olmasın
                List<Double> havuz = new ArrayList<>();
                for (int i = min; i <= max; i++) {
                    havuz.add((double) i);
                }
                Collections.shuffle(havuz, rng);
                broList.addAll(havuz.subList(0, n));
                Collections.sort(broList);
            } else {
                // Örneklem sayısı kapasiteden büyükse: önce tüm değerleri ekle, kalanını rastgele doldur
                for (int i = min; i <= max; i++) {
                    broList.add((double) i);
                }
                // Kalan slotları rastgele değerlerle doldur
                int kalan = n - kapasite;
                for (int i = 0; i < kalan; i++) {
                    double rndVal = rng.nextInt(kapasite) + min;
                    broList.add(rndVal);
                }
            }
            
            sonuc.setBasitRastgeleOrneklem(broList);
        }

        // ======================================================
        // 2. SİSTEMATİK ÖRNEKLEM
        // ======================================================
        if (istek.getSisBuyukN() != null && istek.getSisKucukN() != null) {
            int sBuyukN = istek.getSisBuyukN();
            int sKucukN = istek.getSisKucukN();
            
            if (sBuyukN <= 0) throw new IllegalArgumentException("Sistematik: Büyük N 0'dan büyük olmalıdır.");
            if (sKucukN <= 0 || sKucukN > sBuyukN) throw new IllegalArgumentException("Sistematik: n, 0'dan büyük ve N'den küçük/eşit olmalıdır.");
            
            // k = N / n
            int k = sBuyukN / sKucukN;
            if (k < 1) k = 1;
            
            // a (başlangıç noktası) 1 ile k arasında rastgele seçilir
            Random rastgele = new Random();
            int a = rastgele.nextInt(k) + 1;
            
            List<Double> orneklem = new ArrayList<>();
            // a, a+k, a+2k... şeklinde giderek seçilir
            for (int i = 0; i < sKucukN; i++) {
                double deger = a + (i * k);
                if (deger > sBuyukN) break;
                orneklem.add(deger);
            }
            sonuc.setSistematikOrneklem(orneklem);
        }

        // ======================================================
        // 3. TABAKALI ÖRNEKLEM
        // ======================================================
        if (istek.getTabMin() != null && istek.getTabMax() != null && istek.getTabBuyukN() != null && istek.getTabKucukN() != null && istek.getTabOranlar() != null) {
            double tMin = istek.getTabMin();
            double tMax = istek.getTabMax();
            int tBuyukN = istek.getTabBuyukN();
            int tKucukN = istek.getTabKucukN();
            String tOranlar = istek.getTabOranlar().trim();
            
            if (tMin >= tMax) throw new IllegalArgumentException("Tabakalı: Min, max'tan küçük olmalıdır.");
            if (tBuyukN <= 0) throw new IllegalArgumentException("Tabakalı: Büyük N 0'dan büyük olmalıdır.");
            if (tKucukN <= 0 || tKucukN > tBuyukN) throw new IllegalArgumentException("Tabakalı: n, 0'dan büyük ve N'den küçük/eşit olmalıdır.");
            if (tOranlar.isEmpty()) throw new IllegalArgumentException("Tabakalı: Oranlar boş bırakılamaz.");
            
            sonuc.setTabakaliOrneklem(yeniTabakaliOrneklem(tMin, tMax, tBuyukN, tKucukN, tOranlar));
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
            // Tam sayı olarak yuvarla
            deger = (double) Math.round(deger);
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
    // Akademik 10-adım yöntemine göre hesaplanır (Sturges kuralı KULLANILMAZ)
    
    private List<FrequencyRow> frekansTablosuOlustur(List<Double> veriler, Integer sinifSayisiParametre) {

        // ================================================================
        // ADIM 1: Gözlem Sayısı (n)
        // ================================================================
        int n = veriler.size();

        // ================================================================
        // ADIM 2: Değişim Genişliği (R = L - S)
        // ================================================================
        double S = Collections.min(veriler); // En küçük değer
        double L = Collections.max(veriler); // En büyük değer
        double R = L - S;                    // Değişim genişliği

        // ================================================================
        // ADIM 3: Sınıf Sayısı (k) → √n ≤ k
        // Kullanıcı girmediyse √n'in üstüne yuvarla (tam sayı)
        // ================================================================
        int k;
        if (sinifSayisiParametre != null && sinifSayisiParametre > 0) {
            k = sinifSayisiParametre;
        } else {
            k = (int) Math.ceil(Math.sqrt(n));
        }
        if (k < 1) k = 1;

        // ================================================================
        // YENİ ADIM: Duyarlılık (d) Tespiti
        // ================================================================
        double d = 1.0;
        int maxDecimals = 0;
        for (Double val : veriler) {
            String s = String.valueOf(val);
            if (s.contains(".")) {
                int decimals = s.length() - s.indexOf('.') - 1;
                // Double formatında ".0" gelen tam sayılar decimal sayılmaz
                if (val == Math.floor(val)) continue;
                if (decimals > maxDecimals) {
                    maxDecimals = decimals;
                }
            }
        }
        if (maxDecimals > 0) {
            d = 1.0 / Math.pow(10, maxDecimals);
        }

        // ================================================================
        // ADIM 4: Sınıf Genişliği (h) → R / k ≤ h
        // h değeri d (duyarlılık) biriminin tam katı olacak şekilde yukarı yuvarlanır
        // ================================================================
        double h;
        if (R == 0) {
            h = d; // Değerler aynıysa genişlik min duyarlılık kadar
        } else {
            h = Math.ceil((R / k) / d) * d;
            // Float precision hatalarını önlemek için (örn: 1.0000000000001 -> 1.0)
            h = Math.round(h / d) * d;
        }

        // ================================================================
        // ADIM 5: Sınıf Limitleri (Ayrık Yapı)
        // Her eleman Alt Limit[i] = S + (i * h), Ust Limit[i] = Alt Limit[i] + h - d
        // ================================================================
        double[][] limitler = new double[k][2];
        for (int i = 0; i < k; i++) {
            limitler[i][0] = S + (i * h);
            limitler[i][1] = limitler[i][0] + h - d;
            
            // Yuvarlama hatalarını tamir et
            limitler[i][0] = Math.round(limitler[i][0] / d) * d;
            limitler[i][1] = Math.round(limitler[i][1] / d) * d;
        }
        
        // Kapsama garantisi: Son sınıf L'yi içine almıyorsa kuralı esneterek L'yi zorla içine al
        if (limitler[k - 1][1] < L - (d/10.0)) {
            double over = L - limitler[k - 1][1];
            double steps = Math.ceil(over / d);
            limitler[k - 1][1] += steps * d;
            limitler[k - 1][1] = Math.round(limitler[k - 1][1] / d) * d;
        }

        // ================================================================
        // ADIM 6: Sınıf Sınırları (Süreklilik)
        // Üst Sınır[i] = (Üst Limit[i] + Alt Limit[i+1]) / 2
        // ================================================================
        double[][] sinirlar = new double[k][2];
        for (int i = 0; i < k; i++) {
            if (i == 0) {
                sinirlar[i][0] = limitler[i][0] - (d / 2.0);
            } else {
                sinirlar[i][0] = (limitler[i - 1][1] + limitler[i][0]) / 2.0;
            }
            
            if (i == k - 1) {
                sinirlar[i][1] = limitler[i][1] + (d / 2.0);
            } else {
                sinirlar[i][1] = (limitler[i][1] + limitler[i + 1][0]) / 2.0;
            }
        }

        // ================================================================
        // ADIM 7: Sınıf Frekansları (fi)
        // Alt Limit <= Veri <= Üst Limit
        // ================================================================
        int[] frekanslar = new int[k];
        double epsilon = d / 10.0; // float hatalarına karşı tolerans
        for (Double val : veriler) {
            boolean yerlesti = false;
            for (int i = 0; i < k; i++) {
                if (val >= limitler[i][0] - epsilon && val <= limitler[i][1] + epsilon) {
                    frekanslar[i]++;
                    yerlesti = true;
                    break;
                }
            }
            if (!yerlesti) {
                if (val > limitler[k - 1][1]) frekanslar[k - 1]++;
                else if (val < limitler[0][0]) frekanslar[0]++;
            }
        }

        // ================================================================
        // ADIM 8: Sınıf Orta Noktası (yk)
        // ================================================================
        double[] ortaNoktalar = new double[k];
        for (int i = 0; i < k; i++) {
            ortaNoktalar[i] = (limitler[i][0] + limitler[i][1]) / 2.0;
        }

        // ================================================================
        // ADIM 9: Frekans Kontrolü → Σfi = n
        // ================================================================
        @SuppressWarnings("unused")
        int toplamFrekans = 0;
        for (int f : frekanslar) toplamFrekans += f;

        // ================================================================
        // ADIM 10: Eklemeli (Kümülatif) Frekans
        // ================================================================
        List<FrequencyRow> satirlar = new ArrayList<>();
        int kumulatifFrekans = 0;

        for (int i = 0; i < k; i++) {
            kumulatifFrekans += frekanslar[i];

            double bagilFrekans = Math.round(((double) frekanslar[i] / n) * 10000.0) / 10000.0;
            double kumulatifBagilFrekans = Math.round(((double) kumulatifFrekans / n) * 10000.0) / 10000.0;

            String limitMetni;
            if (d == 1.0) {
                limitMetni = (int)Math.round(limitler[i][0]) + " - " + (int)Math.round(limitler[i][1]);
            } else {
                limitMetni = sayiFormatla(limitler[i][0]) + " - " + sayiFormatla(limitler[i][1]);
            }

            satirlar.add(new FrequencyRow(
                    limitMetni,
                    limitler[i][0], limitler[i][1],             // Alt Limit, Üst Limit
                    sinirlar[i][0], sinirlar[i][1],             // Alt Sınır, Üst Sınır
                    ortaNoktalar[i],                            // Orta Nokta (yk)
                    frekanslar[i], bagilFrekans,                 // Frekans, Bağıl Frekans
                    kumulatifFrekans, kumulatifBagilFrekans      // Eklemeli Frekans, Küm. Bağıl
            ));
        }

        return satirlar;
    }

    


    // Yeni tabakalı örneklem mantığı: Verilen oranlara göre aralığı min-max üzerinden böl
    // N sayı üret, bu dilimlerdeki kurallara göre dağıtıp örneklem çek.
    private List<StratifiedRow> yeniTabakaliOrneklem(double min, double max, int buyukN, int kucukN, String oranlarStr) {
        String[] oranParcalari = oranlarStr.split("[,\\s;]+");
        List<Double> oranlar = new ArrayList<>();
        double toplamOran = 0;
        
        for(String parca : oranParcalari) {
            if(!parca.trim().isEmpty()) {
                try {
                    double v = Double.parseDouble(parca.trim().replace(",", "."));
                    if(v <= 0) throw new IllegalArgumentException("Oranlar pozitif olmalıdır.");
                    oranlar.add(v);
                    toplamOran += v;
                } catch(NumberFormatException ex) {
                    throw new IllegalArgumentException("Geçersiz oran formatı: " + parca);
                }
            }
        }
        
        if(oranlar.isEmpty()) throw new IllegalArgumentException("Geçerli bir oran bulunamadı.");
        
        // Tabakaları belirle (Aralıkları hesapla)
        double genislik = max - min;
        List<Double> sinirlar = new ArrayList<>();
        sinirlar.add(min);
        
        double gecerliSinir = min;
        for(int i = 0; i < oranlar.size(); i++) {
            double tabakaGenislik = (oranlar.get(i) / toplamOran) * genislik;
            gecerliSinir += tabakaGenislik;
            if(i == oranlar.size() - 1) gecerliSinir = max; // rounding bypass for the last boundary
            sinirlar.add(gecerliSinir);
        }
        
        List<StratifiedRow> sonuc = new ArrayList<>();
        Random rng = new Random();
        
        int kalanBuyukN = buyukN;
        int kalanKucukN = kucukN;
        
        for(int i = 0; i < oranlar.size(); i++) {
            double oran = oranlar.get(i) / toplamOran;
            
            // Tabaka büyüklüğü
            int tabBuyukluk = (int) Math.round(oran * buyukN);
            if(i == oranlar.size() - 1) tabBuyukluk = kalanBuyukN; // give remaining to last
            kalanBuyukN -= tabBuyukluk;
            
            // Tabakadan seçilecek örneklem büyüklüğü
            int tabKucukluk = (int) Math.round(oran * kucukN);
            if(i == oranlar.size() - 1) tabKucukluk = kalanKucukN; // give remaining to last
            kalanKucukN -= tabKucukluk;
            
            // Generate `tabBuyukluk` numbers inside [sinirlar.get(i), sinirlar.get(i+1)]
            double tMin = sinirlar.get(i);
            double tMax = sinirlar.get(i+1);
            
            List<Double> tabakaDegerleri = new ArrayList<>();
            for(int j=0; j<tabBuyukluk; j++) {
                double val = tMin + (tMax - tMin) * rng.nextDouble();
                // Tam sayı olarak yuvarlat
                val = (double) Math.round(val);
                tabakaDegerleri.add(val);
            }
            
            // Sınır kontrolü 
            if (tabKucukluk > tabBuyukluk) tabKucukluk = tabBuyukluk;
            if (tabKucukluk < 0) tabKucukluk = 0;
            
            // Seç
            Collections.shuffle(tabakaDegerleri, rng);
            List<Double> secilenler = new ArrayList<>(tabakaDegerleri.subList(0, tabKucukluk));
            Collections.sort(secilenler);
            
            String etiket = "Tabaka " + (i + 1) + " (" + sayiFormatla(tMin) + " - " + sayiFormatla(tMax) + ")";
            sonuc.add(new StratifiedRow(etiket, tabBuyukluk, tabKucukluk, secilenler));
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
