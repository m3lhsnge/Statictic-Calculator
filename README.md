# 📊 İstatistik Örneklem ve Seri Hesaplama Uygulaması

Kullanıcıdan alınan veri seti üzerinde çeşitli **istatistiksel örnekleme yöntemlerini** ve **seri/frekans analizlerini** otomatik olarak gerçekleştiren, modern arayüzlü bir full-stack web uygulamasıdır.

> **Spring Boot** backend ve **Vanilla JS** frontend ile geliştirilmiş olup, istatistik derslerinde öğrenilen temel kavramları uygulamalı olarak hesaplar ve görselleştirir.

---

## ✨ Özellikler

| Özellik | Açıklama |
|---------|----------|
| **Basit Rastgele Örneklem** | Veri setinden rastgele *n* birim seçer |
| **Sistematik Örneklem** | *k = N/n* aralığıyla belirli bir düzende seçim yapar |
| **Tabakalı Örneklem** | Veriyi tabakalara ayırıp her tabakadan oransal (proportional) seçim yapar |
| **Basit Seri** | Verileri küçükten büyüğe sıralar |
| **Frekans Serisi** | Her değerin tekrar sayısını hesaplar |
| **Gruplandırılmış Frekans Tablosu** | Sınıf aralıkları, frekans, bağıl frekans, kümülatif frekans ve kümülatif bağıl frekans hesaplar |

---

## 🛠️ Kullanılan Teknolojiler

### Backend
- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Web (REST API)**
- **Maven** (bağımlılık yönetimi)

### Frontend
- **HTML5** — Semantik yapı
- **CSS3** — Glassmorphism efektleri, animasyonlu arka plan parçacıkları, responsive tasarım
- **JavaScript (ES6+)** — Async/await, Fetch API, DOM manipülasyonu
- **Google Fonts (Inter)** — Modern tipografi

---

## 🏗️ Proje Mimarisi

```
src/main/java/com/istatistik/
├── IstatistikApplication.java        # Spring Boot uygulama giriş noktası
├── controller/
│   └── StatisticsController.java     # REST API endpoint (POST /api/calculate)
├── service/
│   └── StatisticsService.java        # İstatistiksel hesaplama iş mantığı
└── dto/
    ├── DataRequest.java              # İstek modeli (veri seti, örneklem büyüklüğü, vb.)
    ├── StatisticsResponse.java       # Yanıt modeli (tüm hesaplama sonuçları)
    ├── FrequencyRow.java             # Frekans tablosu satır modeli
    └── StratifiedRow.java            # Tabakalı örneklem satır modeli

src/main/resources/static/
├── index.html                        # Ana sayfa (tek sayfa uygulama)
├── css/style.css                     # Stil dosyası
└── js/app.js                         # Frontend mantığı ve API iletişimi
```

---

## 📐 Uygulanan İstatistiksel Yöntemler

### 1. Basit Rastgele Örneklem
Popülasyondaki tüm birimlerin eşit seçilme şansına sahip olduğu örnekleme yöntemi. Tüm indeksler karıştırılarak ilk *n* tanesi seçilir.

### 2. Sistematik Örneklem
Adım aralığı **k = N/n** formülüyle hesaplanır. 0 ile k arasında rastgele bir başlangıç noktası belirlenir ve her k. eleman örnekleme dahil edilir.

### 3. Tabakalı Örneklem
Veriler sıralanarak eşit büyüklükte tabakalara ayrılır. Her tabakadan **oransal dağılım** (nᵢ = Nᵢ/N × n) formülüyle rastgele seçim yapılır. Tabaka sayısı belirtilmezse **√N** kuralı uygulanır.

### 4. Basit Seri
Veriler küçükten büyüğe doğru sıralanarak sunulur.

### 5. Frekans Serisi
Her benzersiz değerin veri setinde kaç kez tekrarlandığını hesaplar. Sonuçlar `TreeMap` ile sıralı tutulur.

### 6. Gruplandırılmış Frekans Tablosu
- **Sınıf sayısı**: Kullanıcı belirleyebilir veya **Sturges Kuralı** *(k = 1 + 3.322 × log₁₀N)* otomatik olarak hesaplar
- **Sınıf genişliği**: (Max − Min) / k
- Hesaplanan değerler: **Frekans (fᵢ)**, **Bağıl Frekans (fᵢ/n)**, **Kümülatif Frekans**, **Kümülatif Bağıl Frekans**

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

# 2. Bağımlılıkları yükleyin ve uygulamayı başlatın
mvn spring-boot:run
```

Uygulama başlatıldığında tarayıcınızda otomatik olarak **http://localhost:8080** adresi açılır.

---

## 📖 API Kullanımı

### `POST /api/calculate`

Tüm istatistiksel hesaplamaları tek bir endpoint üzerinden gerçekleştirir.

**İstek Gövdesi:**
```json
{
  "data": [5, 12, 8, 3, 15, 7, 20, 11, 9, 6, 14, 2, 18, 10, 4],
  "sampleSize": 5,
  "classCount": null,
  "strataCount": null
}
```

| Parametre | Tip | Zorunlu | Açıklama |
|-----------|-----|---------|----------|
| `data` | `number[]` | ✅ | Analiz edilecek veri seti |
| `sampleSize` | `integer` | ✅ | Örneklem büyüklüğü (n) |
| `classCount` | `integer` | ❌ | Frekans tablosu sınıf sayısı (boş ise Sturges kuralı) |
| `strataCount` | `integer` | ❌ | Tabaka sayısı (boş ise √N kuralı) |

**Yanıt:**
```json
{
  "basitRastgeleOrneklem": [3, 8, 12, 15, 20],
  "sistematikOrneklem": [5, 9, 14, 18, 4],
  "basitSeri": [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 18, 20],
  "frekansSeries": { "2": 1, "3": 1, "4": 1, "...": "..." },
  "frekansTablosu": [
    {
      "classInterval": "2 - 6",
      "frequency": 5,
      "relativeFrequency": 0.3333,
      "cumulativeFrequency": 5,
      "cumulativeRelativeFrequency": 0.3333
    }
  ],
  "tabakaliOrneklem": [
    {
      "tabakaAdi": "Tabaka 1 (2 - 6)",
      "tabakaBuyuklugu": 5,
      "secilenAdet": 2,
      "secilenDegerler": [3, 5]
    }
  ]
}
```

---

## 🎨 Arayüz Özellikleri

- **Glassmorphism** tasarım dili ile modern ve şık görünüm
- **Animasyonlu parçacık arka planı** ile dinamik kullanıcı deneyimi
- **Responsive tasarım** — mobil ve masaüstü uyumlu
- **Real-time input validasyonu** — geçersiz karakterler otomatik filtrelenir
- **Hata yönetimi** — kullanıcı dostu hata mesajları ve animasyonlu geri bildirim

---

## 📂 Proje Yapısı

```
İSTATİK PROJE/
├── pom.xml                           # Maven yapılandırması
├── README.md
└── src/
    └── main/
        ├── java/com/istatistik/      # Backend kaynak kodu
        │   ├── controller/           # REST API katmanı
        │   ├── service/              # İş mantığı katmanı
        │   └── dto/                  # Veri transfer objeleri
        └── resources/
            └── static/               # Frontend dosyaları
                ├── index.html
                ├── css/style.css
                └── js/app.js
```

---

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.
