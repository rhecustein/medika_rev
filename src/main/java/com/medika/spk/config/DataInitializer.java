package com.medika.spk.config;

import com.medika.spk.entity.*;
import com.medika.spk.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepo;
    private final KriteriaRepository kriteriaRepo;
    private final SubKriteriaRepository subKriteriaRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        initUsers();
        initKriteria();
        log.info("Data initializer selesai.");
    }

    private void initUsers() {
        if (userRepo.count() > 0) return;

        userRepo.saveAll(List.of(
            User.builder().nama("Administrator").username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@medika.co.id").role(User.Role.ADMIN)
                .status(User.StatusUser.AKTIF).build(),
            User.builder().nama("HRD Manager").username("hrd")
                .password(passwordEncoder.encode("hrd123"))
                .email("hrd@medika.co.id").role(User.Role.HRD)
                .status(User.StatusUser.AKTIF).build(),
            User.builder().nama("Penilai 1").username("penilai")
                .password(passwordEncoder.encode("penilai123"))
                .email("penilai@medika.co.id").role(User.Role.PENILAI)
                .status(User.StatusUser.AKTIF).build()
        ));
        log.info("Users seed selesai.");
    }

    private void initKriteria() {
        if (kriteriaRepo.count() > 0) return;

        // C1: Pendidikan
        Kriteria c1 = kriteriaRepo.save(Kriteria.builder()
                .kode("C1").namaKriteria("Pendidikan")
                .bobot(25.0).atribut(Kriteria.Atribut.BENEFIT).build());
        subKriteriaRepo.saveAll(List.of(
            SubKriteria.builder().kriteria(c1).namaSub("S2").nilai(100.0).build(),
            SubKriteria.builder().kriteria(c1).namaSub("S1").nilai(80.0).build(),
            SubKriteria.builder().kriteria(c1).namaSub("D3").nilai(60.0).build(),
            SubKriteria.builder().kriteria(c1).namaSub("SMA/SMK").nilai(40.0).build()
        ));

        // C2: Pengalaman Kerja
        Kriteria c2 = kriteriaRepo.save(Kriteria.builder()
                .kode("C2").namaKriteria("Pengalaman Kerja")
                .bobot(25.0).atribut(Kriteria.Atribut.BENEFIT).build());
        subKriteriaRepo.saveAll(List.of(
            SubKriteria.builder().kriteria(c2).namaSub(">6 Tahun").nilai(100.0).build(),
            SubKriteria.builder().kriteria(c2).namaSub("4-6 Tahun").nilai(80.0).build(),
            SubKriteria.builder().kriteria(c2).namaSub("1-3 Tahun").nilai(60.0).build(),
            SubKriteria.builder().kriteria(c2).namaSub("<1 Tahun").nilai(40.0).build()
        ));

        // C3: Tes Tertulis (nilai langsung)
        kriteriaRepo.save(Kriteria.builder()
                .kode("C3").namaKriteria("Tes Tertulis")
                .bobot(20.0).atribut(Kriteria.Atribut.BENEFIT).build());

        // C4: Wawancara (nilai langsung)
        kriteriaRepo.save(Kriteria.builder()
                .kode("C4").namaKriteria("Wawancara")
                .bobot(20.0).atribut(Kriteria.Atribut.BENEFIT).build());

        // C5: Hasil Kesehatan (nilai langsung)
        kriteriaRepo.save(Kriteria.builder()
                .kode("C5").namaKriteria("Hasil Kesehatan")
                .bobot(10.0).atribut(Kriteria.Atribut.BENEFIT).build());

        log.info("Kriteria & sub kriteria seed selesai.");
    }
}
