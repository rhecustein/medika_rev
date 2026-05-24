package com.medika.spk.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.medika.spk.entity.*;
import com.medika.spk.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LaporanService {

    private final HasilPerhitunganRepository hasilRepo;
    private final KriteriaRepository kriteriaRepo;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public byte[] exportExcel() throws IOException {
        List<HasilPerhitungan> hasilList = hasilRepo.findAllByOrderByRankingAsc();
        List<Kriteria> kriteriaList = kriteriaRepo.findAllByOrderByKodeAsc();

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Hasil Seleksi");

            // Header style
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("LAPORAN HASIL SELEKSI KARYAWAN - SPK SMART");
            CellStyle titleStyle = wb.createCellStyle();
            Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            Row subTitleRow = sheet.createRow(1);
            subTitleRow.createCell(0).setCellValue(
                    "Tanggal: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            // Header row
            Row headerRow = sheet.createRow(3);
            String[] headers = {"No", "Nama Kandidat", "Pendidikan"};
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }
            int col = headers.length;
            for (Kriteria k : kriteriaList) {
                Cell c = headerRow.createCell(col++);
                c.setCellValue(k.getKode() + " (" + k.getNamaKriteria() + ")");
                c.setCellStyle(headerStyle);
            }
            Cell utilCell  = headerRow.createCell(col++);
            utilCell.setCellValue("Nilai Akhir");
            utilCell.setCellStyle(headerStyle);
            Cell rankCell  = headerRow.createCell(col++);
            rankCell.setCellValue("Ranking");
            rankCell.setCellStyle(headerStyle);
            Cell statusCell = headerRow.createCell(col);
            statusCell.setCellValue("Status");
            statusCell.setCellStyle(headerStyle);

            // Data rows
            int rowNum = 4;
            for (HasilPerhitungan h : hasilList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(h.getRanking());
                row.createCell(1).setCellValue(h.getKandidat().getNama());
                row.createCell(2).setCellValue(h.getKandidat().getPendidikanTerakhir() != null
                        ? h.getKandidat().getPendidikanTerakhir() : "-");

                int c = 3;
                for (Kriteria k : kriteriaList) {
                    final Long kId = k.getId();
                    double nilai = h.getDetails().stream()
                            .filter(d -> d.getKriteria().getId().equals(kId))
                            .findFirst().map(DetailPerhitungan::getCOut).orElse(0.0);
                    row.createCell(c++).setCellValue(nilai);
                }
                row.createCell(c++).setCellValue(h.getNilaiAkhir());
                row.createCell(c++).setCellValue(h.getRanking());
                row.createCell(c).setCellValue(h.getKandidat().getStatus().name());
            }

            // Auto size columns
            for (int i = 0; i <= col; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public byte[] exportPdf() throws DocumentException {
        List<HasilPerhitungan> hasilList = hasilRepo.findAllByOrderByRankingAsc();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        PdfWriter.getInstance(doc, out);
        doc.open();

        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 14,
                com.itextpdf.text.Font.BOLD, new BaseColor(192, 57, 43));
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 9,
                com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
        com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(
                com.itextpdf.text.Font.FontFamily.HELVETICA, 8);

        Paragraph title = new Paragraph("LAPORAN HASIL SELEKSI KARYAWAN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        Paragraph subtitle = new Paragraph("PT. Medika Akses Investama - Metode SMART\n" +
                "Tanggal: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                dataFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(10);
        doc.add(subtitle);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.5f, 2f, 1.5f, 1f, 1f, 1f, 1f});

        String[] headers = {"No", "Nama", "Pendidikan", "Nilai Akhir", "Ranking", "Status", "Keputusan"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new BaseColor(192, 57, 43));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        for (HasilPerhitungan h : hasilList) {
            table.addCell(new Phrase(String.valueOf(h.getRanking()), dataFont));
            table.addCell(new Phrase(h.getKandidat().getNama(), dataFont));
            table.addCell(new Phrase(h.getKandidat().getPendidikanTerakhir() != null
                    ? h.getKandidat().getPendidikanTerakhir() : "-", dataFont));
            table.addCell(new Phrase(String.format("%.4f", h.getNilaiAkhir()), dataFont));
            table.addCell(new Phrase(String.valueOf(h.getRanking()), dataFont));
            String statusLabel = switch (h.getKandidat().getStatus()) {
                case DITERIMA -> "Diterima";
                case DITOLAK  -> "Ditolak";
                default       -> "Proses";
            };
            table.addCell(new Phrase(statusLabel, dataFont));
            String keputusan = h.getRanking() == 1 ? "Sangat Direkomendasikan"
                    : h.getRanking() <= 3           ? "Direkomendasikan"
                    :                                 "Perlu Pertimbangan";
            table.addCell(new Phrase(keputusan, dataFont));
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }
}
