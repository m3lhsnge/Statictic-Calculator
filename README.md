# 📊 İstatistik Örneklem ve Seri Hesaplama Uygulaması

Modern arayüzlü, kullanıcı dostu ve akademik yöntemlere tam uyumlu bir full-stack web uygulamasıdır. Veri setleri üzerinde **istatistiksel örnekleme yöntemlerini** (Basit Rastgele, Sistematik, Tabakalı) ve **seri/frekans analizlerini** (Basit Seri, Frekans Serisi, Frekans Tablosu) 4 bağımsız modül üzerinden gerçekleştirir.

> **Spring Boot** backend ve **Vanilla JS** frontend ile geliştirilmiştir. İstatistik derslerindeki kesin **akademik matematiği** (örn: duyarlılık tabanlı ayrık sınıf limitleri) baz alacak şekilde hesaplamalar yapar.

---

## ✨ Özellikler ve Modüller

Uygulama **4 farklı bağımsız modülden** oluşur. Her modül kendi parametrelerini alır ve sadece kendi sonucunu üretir:

### 1. 🎲 Basit Rastgele Örneklem
Kullanıcının belirlediği Min ve Max aralığında rastgele tam sayılar üretir.
- **Yeniden Yazma Olmadan (Tekrarsız):** İstenen örneklem sayısı, aralıktaki toplam eleman sayısına (kapasiteye) eşit veya küçükse sayılar **sırasıyla ve tekrarsız** olarak seçilir.
- **Yeniden Yazma İle (Tekrarlı):** Kapasite aşılırsa, ana veriler aynen aktarılır, kalan ihtiyaç rastgele ve tekrar edilebilir sayılarla tamamlanır.

### 2. 📐 Sistematik Örneklem
Akademik "Atlamalı Seçim" kuralına göre çalışır. Rastgele bir üretimi değil, sıralı indeksleme üzerinden formülsel ilerlemeyi baz alır.
- Seçim Aralığı: **k = N (Popülasyon) / n (Örneklem)**
- Başlangıç değeri $a$, $1$ ile $k$ arasında rastgele atanır.
- Çıktı serisi tam olarak aritmetik dizi formatındadır: $a, a+k, a+2k...$

### 3. 🗂️ Tabakalı Örneklem
Ağırlıklarına göre popülasyonu dilimlere ayırır.
- Dinamik form yapısı sayesinde **"Tabaka Sayısı"** girildiğinde, o sayıya uygun oranda input otomatik açılır.
- Örneğin `Tabaka: 2` => `1. Tabaka Oranı`, `2. Tabaka Oranı` olarak ayrı ayrı girilebilir.
- Sınırlar oranlara göre hesaplanır, ardından alt tabakalardan rastgele tam sayılar çekilir.

### 4. 📈 Veri Seti (Basit Seri & Frekans Tabloları)
Serbest metin olarak (virgülle ayırarak) girilmiş karmaşık verileri analiz eder.
- **Basit Seri:** Verileri küçükten büyüğe sıralar.
- **Frekans Serisi:** Her benzersiz değerin tekrar sayısını bulur.
- **Gruplandırılmış Frekans Tablosu (Tam Akademik Algoritma):** Programlama kütüphaneleri (Sturges vb.) yerine **10 Adımlı Özel Akademik Yöntem** kullanır:
  1. $k = \sqrt{n}$ kuralıyla sınıf sayısını hesaplar.
  2. Verilerin maksimum ondalık hassasiyetini ($d$ - duyarlılığını) otomatik algılar (Tam sayı için $d=1.0$, $1.55$ için $d=0.01$ vb.).
  3. Sınıf genişliğini ($h$) bu duyarlılık katsayısına göre matematikte yokuş yukarı yuvarlar.
  4. Sınıf limitlerini ($AltLimit[i] + h - d$) ayrık (disjoint) yapıda inşa eder.
  5. Sınıf sınırlarını limitlerin birleşimi ve ortalaması üzerinden kesintisiz sürekli (continuous) hale getirir.
  6. **Alt Limit**, **Üst Limit**, **Alt Sınır**, **Üst Sınır**, **Orta Nokta ($y_k$)**, **Frekans ($f_i$)**, **Bağıl**, **Eklemeli** statlarını barındıran zengin bir tablo döner.

---

## 🛠️ Kullanılan Teknolojiler

### Backend
- **Java 17 & Spring Boot 3.2.3**
- **Spring Web** (REST API mimarisi)
- **Maven** (Bağımlılık yöntemi)

### Frontend
- **Vanilla JavaScript (ES6+)** — Fetch API, DOM Manipülasyonu, Asenkron işleyiş.
- **CSS3 Glassmorphism** — Yarı saydam arka planlar, bulanıklık efektleri.
- **HTML5** — Semantik dizilim ve Dinamik arayüz gridleri (CSS Grid/Flexbox).

---

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- **Java 17** veya üzeri
- **Maven 3.6+**

### Adımlar

```bash
# 1. Projeyi klonlayın
git clone https://github.com/m3lhsnge/Statictic-Calculator.git
cd Statictic-Calculator

# 2. Kurulum ve Çalıştırma
mvn spring-boot:run
```

Uygulama çalıştıktan sonra tarayıcınızda otomatik olarak **http://localhost:8080** adresi üzerinden erişebilirsiniz.

---

## 📖 API Referansı

Backend `POST /api/calculate` adresi üzerinden birden fazla DTO varyasyonunu destekleyen, akıllı bir eşleşim kullanır.

### Örnek İstek (Frekans Tablosu İçin)
```json
{
  "data": [1.5, 1.5, 2.6, 2.6, 3.4, 3.8, 3.8, 4.1, 4.1, 4.6, 4.6, 4.6, 5.2, 5.2],
  "classCount": null
}
```

### Örnek İstek (Sistematik Örneklem İçin)
```json
{
  "sisBuyukN": 100,
  "sisKucukN": 20
}
```

### Örnek İstek (Tabakalı Örneklem İçin)
```json
{
  "tabMin": 1,
  "tabMax": 100,
  "tabBuyukN": 100,
  "tabKucukN": 10,
  "tabOranlar": "60, 40"
}
```

**Esnek DTO:** Uygulama gönderilen JSON parametrelerindeki boş olmayan alanlara (null olmayan) odaklanır ve eşleştiği modülün sonucunu `StatisticsResponse` objesi içerisinde, yine ilgili modülün alan karşılığına yazar (`sistematikOrneklem`, `frekansTablosu` vb.).

---

## 🎨 Arayüz Özellikleri
- **Bağımsız Modüler Yapı:** Her bölüm kendi bağımsız girişlerine ve `Hesapla` butonuna sahiptir.
- **Dinamik Veri Girişleri:** Seçime göre yeni girdi bölmeleri (örneğin Tabaka Oranları Inputları) türeyip silinebilir.
- **Real-time Validasyon:** Tam sayı gereken kısımlar, harf ve işaret engellemeleri dinamik gerçekleşir. Sayfaya yerleştirilmiş custom uyarı bildirimleriyle anında hata geri bildirimi verir.
- **Karanlık Tema ve Glass Görünüm:** Sayfa parlamaları engellenmiştir, gece stili mat tonlara oturtulmuştur.

---

## 📄 Lisans

Bu proje akademik ve eğitim amaçlı metotların görselleştirilmesi üzerine geliştirilmiştir.
