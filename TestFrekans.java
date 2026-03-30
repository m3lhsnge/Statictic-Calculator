import java.io.*;
import java.util.*;

public class TestFrekans {

    static class FrequencyRow {
        public String sinifAraligi;
        public double altSinir;
        public double ustSinir;
        public int frekans;

        public FrequencyRow(String sinifAraligi, double altSinir, double ustSinir, int frekans) {
            this.sinifAraligi = sinifAraligi;
            this.altSinir = altSinir;
            this.ustSinir = ustSinir;
            this.frekans = frekans;
        }

        @Override
        public String toString() {
            return String.format("Aralik: %s, Frekans: %d", sinifAraligi, frekans);
        }
    }

    private static String sayiFormatla(double deger) {
        if (deger == Math.floor(deger) && !Double.isInfinite(deger)) {
            return String.valueOf((int) deger);
        }
        return String.format(Locale.US, "%.2f", deger);
    }

    public static List<FrequencyRow> frekansTablosuOlustur(List<Double> veriler, Integer sinifSayisiParametre) {
        int N = veriler.size();
        double enKucuk = Collections.min(veriler);
        double enBuyuk = Collections.max(veriler);

        int sinifSayisi;
        if (sinifSayisiParametre != null && sinifSayisiParametre > 0) {
            sinifSayisi = sinifSayisiParametre;
        } else {
            sinifSayisi = (int) Math.ceil(1 + 3.322 * Math.log10(N));
        }

        double aralik = enBuyuk - enKucuk;
        double sinifGenisligi = Math.ceil(aralik / sinifSayisi);
        if (sinifGenisligi == 0) sinifGenisligi = 1;

        List<FrequencyRow> satirlar = new ArrayList<>();

        for (int i = 0; i < sinifSayisi; i++) {
            double altSinir = enKucuk + i * sinifGenisligi;
            double ustSinir = altSinir + sinifGenisligi;

            int frekans = 0;
            for (Double deger : veriler) {
                if (i == sinifSayisi - 1) {
                    if (deger >= altSinir && deger <= ustSinir) frekans++;
                } else {
                    if (deger >= altSinir && deger < ustSinir) frekans++;
                }
            }

            String aralikMetni = sayiFormatla(altSinir) + " - " + sayiFormatla(ustSinir);
            satirlar.add(new FrequencyRow(aralikMetni, altSinir, ustSinir, frekans));
        }

        return satirlar;
    }

    public static void main(String[] args) throws Exception {
        PrintWriter out = new PrintWriter(new File("output_clean.txt"));
        
        out.println("Test 1: Normal Tam Sayilar");
        List<Double> data1 = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
        for(FrequencyRow row : frekansTablosuOlustur(data1, null)) out.println(row);

        out.println("\nTest 2: Kucuk Ondalikli Sayilar (Math.ceil Sorunu)");
        List<Double> data2 = Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);
        for(FrequencyRow row : frekansTablosuOlustur(data2, null)) out.println(row);
        
        out.println("\nTest 3: Ufak Aralikli Buyuk Sayilar");
        List<Double> data3 = Arrays.asList(100.1, 100.2, 100.3, 100.4, 100.5, 100.6, 100.7);
        for(FrequencyRow row : frekansTablosuOlustur(data3, null)) out.println(row);
        
        out.println("\nTest 4: Sayi Atlanmasi (Precision Sorunu)");
        List<Double> data4 = Arrays.asList(4.1, 4.2, 4.8);
        for(FrequencyRow row : frekansTablosuOlustur(data4, 3)) out.println(row);
        
        out.close();
    }
}
