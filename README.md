# SPK Medika — Sistem Pendukung Keputusan Rekrutmen Karyawan

Aplikasi **Sistem Pendukung Keputusan (SPK)** berbasis metode **SMART** *(Simple Multi-Attribute Rating Technique)* untuk proses seleksi dan perankingan kandidat karyawan di **PT Medika Akses Investama**.

---

## Fitur Utama

- **Manajemen Kandidat** — tambah, edit, hapus data pelamar beserta status (Proses / Diterima / Ditolak)
- **Penilaian Multi Kriteria** — input nilai per kandidat berdasarkan 5 kriteria yang telah ditentukan
- **Perhitungan SMART otomatis** — normalisasi bobot, perhitungan utility, dan nilai akhir per kandidat
- **Ranking kandidat** — hasil perankingan tersimpan dan dapat dilihat kapan saja
- **Laporan** — ekspor hasil ke **Excel** (.xlsx) dan **PDF**
- **Manajemen Pengguna** — tiga role: Admin, HRD, Penilai
- **Autentikasi JWT** — login aman dengan token berbasis JSON Web Token

---

## Teknologi

| Layer | Teknologi |
|---|---|
| Backend | Spring Boot 3.2.5, Spring Security, Spring Data JPA |
| Autentikasi | JWT (jjwt 0.12.5) |
| Database | MySQL 8 / MariaDB 11.4 |
| Frontend | Bootstrap 5.3, Vanilla JavaScript |
| Ekspor | Apache POI 5.2.5 (Excel), iText PDF 5.5.13 |
| Build | Maven 3.9, Java 17 |

---

## Kriteria Penilaian

| Kode | Kriteria | Bobot | Atribut | Tipe Input |
|---|---|---|---|---|
| C1 | Pendidikan | 25% | Benefit | Pilihan (S2/S1/D3/SMA) |
| C2 | Pengalaman Kerja | 25% | Benefit | Pilihan (>6 thn / 4-6 / 1-3 / <1 thn) |
| C3 | Tes Tertulis | 20% | Benefit | Angka 0–100 |
| C4 | Wawancara | 20% | Benefit | Angka 0–100 |
| C5 | Hasil Kesehatan | 10% | Benefit | Angka 0–100 |

---

## Algoritma SMART

### 1. Normalisasi Bobot
```
Wj = wj / Σ wj
```

### 2. Nilai Utility
```
Benefit: uj(ai) = (Cout - Cmin) / (Cmax - Cmin)
Cost:    uj(ai) = (Cmax - Cout) / (Cmax - Cmin)
```
- **Cmin/Cmax** untuk kriteria berbasis pilihan → diambil dari range nilai SubKriteria yang terdefinisi
- **Cmin/Cmax** untuk kriteria input langsung → batas tetap 0 dan 100

### 3. Nilai Akhir
```
U(ai) = Σ Wj × uj(ai)
```

---

## Cara Menjalankan

### Prasyarat
- Java 17+ (atau akan diunduh otomatis oleh `start.bat`)
- Maven 3.9+ (atau akan diunduh otomatis oleh `start.bat`)
- MySQL / MariaDB (atau akan diunduh otomatis oleh `start.bat`)

### Menjalankan Aplikasi

**Windows — cukup jalankan satu perintah:**
```bat
start.bat
```

`start.bat` akan otomatis:
1. Mendeteksi / mengunduh Java 17
2. Mendeteksi / mengunduh Maven 3.9
3. Mendeteksi / menjalankan MySQL atau MariaDB
4. Membuat database `spk_medika` bila belum ada
5. Build JAR dan menjalankan aplikasi

Setelah muncul pesan `Aplikasi berjalan di: http://localhost:8080`, buka browser dan akses:

```
http://localhost:8080
```

### Konfigurasi Database Manual

Edit `src/main/resources/application.properties` jika koneksi database berbeda:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spk_medika
spring.datasource.username=root
spring.datasource.password=
```

---

## Akun Default

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| HRD | `hrd` | `hrd123` |
| Penilai | `penilai` | `penilai123` |

> Ganti password setelah login pertama kali.

---

## Struktur Proyek

```
medika_dev/
├── src/
│   └── main/
│       ├── java/com/medika/spk/
│       │   ├── config/          # SecurityConfig, DataInitializer, CorsConfig
│       │   ├── controller/      # REST controllers
│       │   ├── dto/             # Request & Response DTOs
│       │   ├── entity/          # JPA Entities
│       │   ├── repository/      # Spring Data JPA Repositories
│       │   ├── security/        # JWT Filter & UserDetailsService
│       │   └── service/         # Business logic & SMART calculation
│       └── resources/
│           ├── static/          # HTML, CSS, JS frontend
│           └── application.properties
├── tools/                       # Auto-downloaded Java/Maven/MySQL (bila perlu)
├── start.bat                    # Script one-click untuk Windows
└── pom.xml
```

---

## Halaman Aplikasi

| URL | Keterangan |
|---|---|
| `/login.html` | Halaman login |
| `/dashboard.html` | Dashboard ringkasan |
| `/kandidat.html` | Manajemen kandidat |
| `/penilaian.html` | Form penilaian per kandidat |
| `/smart.html` | Proses perhitungan SMART |
| `/hasil.html` | Hasil & ranking kandidat |
| `/hasil-akhir.html` | Rekap hasil akhir |
| `/laporan.html` | Ekspor laporan Excel / PDF |
| `/kriteria.html` | Manajemen kriteria |
| `/sub-kriteria.html` | Manajemen sub kriteria |
| `/user.html` | Manajemen pengguna (Admin) |

---

## Lisensi

Dikembangkan untuk keperluan internal **PT Medika Akses Investama**.
