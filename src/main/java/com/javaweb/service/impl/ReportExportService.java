package com.javaweb.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import com.javaweb.service.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Service
public class ReportExportService {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private ProductService productService;

    @Autowired
    private NguyenLieuService nguyenLieuService;

    /**
     * Tạo báo cáo Excel từ dữ liệu thống kê
     */
    public ByteArrayResource generateExcelReport(String startDate, String endDate) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Tạo các sheet báo cáo
            createSummarySheet(workbook, startDate, endDate);
            createSalesSheet(workbook, startDate, endDate);
            createCategorySheet(workbook, startDate, endDate);
            createTopProductsSheet(workbook, startDate, endDate);
            createPaymentMethodsSheet(workbook, startDate, endDate);
            createInventorySheet(workbook, startDate, endDate);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo báo cáo Excel: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo báo cáo PDF từ dữ liệu thống kê
     */
    public ByteArrayResource generatePdfReport(String startDate, String endDate) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();

            // Thêm tiêu đề
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
            Paragraph title = new Paragraph("BÁO CÁO THỐNG KÊ", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Thêm ngày báo cáo
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String reportPeriod = "Từ ngày: " + (startDate != null ? startDate : "...") +
                    " đến ngày: " + (endDate != null ? endDate : dateFormat.format(new Date()));
            Paragraph dateParagraph = new Paragraph(reportPeriod);
            dateParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(dateParagraph);
            document.add(Chunk.NEWLINE);

            // Thêm thông tin tổng quan
            addSummaryToPdf(document, startDate, endDate);
            document.add(Chunk.NEWLINE);

            // Thêm thông tin top sản phẩm
            addTopProductsToPdf(document, startDate, endDate);
            document.add(Chunk.NEWLINE);

            // Thêm thông tin danh mục
            addCategoriesToPdf(document, startDate, endDate);
            document.add(Chunk.NEWLINE);

            // Thêm thông tin phương thức thanh toán
            addPaymentMethodsToPdf(document, startDate, endDate);
            document.add(Chunk.NEWLINE);

            // Thêm thông tin tồn kho
            addInventoryToPdf(document, startDate, endDate);

            document.close();

            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo báo cáo PDF: " + e.getMessage(), e);
        }
    }

    // Các phương thức hỗ trợ tạo Excel
    private void createSummarySheet(XSSFWorkbook workbook, String startDate, String endDate) {
        XSSFSheet sheet = workbook.createSheet("Tổng quan");

        // Lấy dữ liệu tổng quan
        Map<String, Object> summary = hoaDonService.calculateTotalRevenueAndOrders(startDate, endDate);

        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO TỔNG QUAN");

        // Dữ liệu
        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("Chỉ tiêu");
        headerRow.createCell(1).setCellValue("Giá trị");

        Row row1 = sheet.createRow(3);
        row1.createCell(0).setCellValue("Tổng số đơn hàng");
        row1.createCell(1).setCellValue(summary.get("totalOrders").toString());

        Row row2 = sheet.createRow(4);
        row2.createCell(0).setCellValue("Tổng doanh thu");
        row2.createCell(1).setCellValue(formatCurrency((Long) summary.get("totalRevenue")) + " VNĐ");

        Row row3 = sheet.createRow(5);
        row3.createCell(0).setCellValue("Số lượng khách hàng mới");
        row3.createCell(1).setCellValue(summary.get("totalCustomers").toString());

        Row row4 = sheet.createRow(6);
        row4.createCell(0).setCellValue("Tăng trưởng");
        row4.createCell(1).setCellValue(summary.get("growthRate") + "%");

        // Định dạng
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createSalesSheet(XSSFWorkbook workbook, String startDate, String endDate) {
        XSSFSheet sheet = workbook.createSheet("Doanh thu theo ngày");

        // Lấy dữ liệu doanh thu
        List<Map<String, Object>> salesData = hoaDonService.getSalesReportByTimeRange(startDate, endDate, "daily");

        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO DOANH THU THEO NGÀY");

        // Header
        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("Ngày");
        headerRow.createCell(1).setCellValue("Số đơn hàng");
        headerRow.createCell(2).setCellValue("Doanh thu (VNĐ)");

        // Data
        int rowNum = 3;
        for (Map<String, Object> data : salesData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.get("date").toString());
            row.createCell(1).setCellValue(Integer.parseInt(data.get("orders").toString()));
            row.createCell(2).setCellValue(formatCurrency(Long.parseLong(data.get("revenue").toString())));
        }

        // Định dạng
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void createCategorySheet(XSSFWorkbook workbook, String startDate, String endDate) {
        XSSFSheet sheet = workbook.createSheet("Doanh thu theo danh mục");

        // Lấy dữ liệu danh mục
        List<Map<String, Object>> categoryData = productService.getCategorySalesReport(startDate, endDate);

        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO DOANH THU THEO DANH MỤC");

        // Header
        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("Danh mục");
        headerRow.createCell(1).setCellValue("Số lượng");
        headerRow.createCell(2).setCellValue("Doanh thu (VNĐ)");
        headerRow.createCell(3).setCellValue("Tỷ trọng (%)");

        // Data
        int rowNum = 3;
        for (Map<String, Object> data : categoryData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.get("category").toString());
            row.createCell(1).setCellValue(Integer.parseInt(data.get("quantity").toString()));
            row.createCell(2).setCellValue(formatCurrency(Long.parseLong(data.get("revenue").toString())));
            row.createCell(3).setCellValue(Double.parseDouble(data.get("percentage").toString()));
        }

        // Định dạng
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
    }

    private void createTopProductsSheet(XSSFWorkbook workbook, String startDate, String endDate) {
        XSSFSheet sheet = workbook.createSheet("Sản phẩm bán chạy");

        // Lấy dữ liệu sản phẩm bán chạy
        List<Map<String, Object>> topProducts = productService.getTopSellingProducts(startDate, endDate, 10);

        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO SẢN PHẨM BÁN CHẠY");

        // Header
        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("Mã SP");
        headerRow.createCell(1).setCellValue("Tên sản phẩm");
        headerRow.createCell(2).setCellValue("Danh mục");
        headerRow.createCell(3).setCellValue("Số lượng");
        headerRow.createCell(4).setCellValue("Doanh thu (VNĐ)");
        headerRow.createCell(5).setCellValue("Tỷ trọng (%)");

        // Data
        int rowNum = 3;
        for (Map<String, Object> product : topProducts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.get("id").toString());
            row.createCell(1).setCellValue(product.get("name").toString());
            row.createCell(2).setCellValue(product.get("category").toString());
            row.createCell(3).setCellValue(Integer.parseInt(product.get("quantity").toString()));
            row.createCell(4).setCellValue(formatCurrency(Long.parseLong(product.get("revenue").toString())));
            row.createCell(5).setCellValue(Double.parseDouble(product.get("percentage").toString()));
        }

        // Định dạng
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createPaymentMethodsSheet(XSSFWorkbook workbook, String startDate, String endDate) {
        XSSFSheet sheet = workbook.createSheet("Phương thức thanh toán");

        // Lấy dữ liệu phương thức thanh toán
        List<Map<String, Object>> paymentMethods = hoaDonService.getPaymentMethodsReport(startDate, endDate);

        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("THỐNG KÊ PHƯƠNG THỨC THANH TOÁN");

        // Header
        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("Phương thức");
        headerRow.createCell(1).setCellValue("Số đơn hàng");
        headerRow.createCell(2).setCellValue("Doanh thu (VNĐ)");
        headerRow.createCell(3).setCellValue("Tỷ trọng (%)");

        // Data
        int rowNum = 3;
        for (Map<String, Object> method : paymentMethods) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(method.get("method").toString());
            row.createCell(1).setCellValue(Integer.parseInt(method.get("orders").toString()));
            row.createCell(2).setCellValue(formatCurrency(Long.parseLong(method.get("revenue").toString())));
            row.createCell(3).setCellValue(Double.parseDouble(method.get("percentage").toString()));
        }

        // Định dạng
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
    }

    private void createInventorySheet(XSSFWorkbook workbook, String startDate, String endDate) {
        XSSFSheet sheet = workbook.createSheet("Tồn kho");

        // Lấy dữ liệu tồn kho
        List<Map<String, Object>> inventory = nguyenLieuService.getInventoryMovementReport(startDate, endDate);

        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO BIẾN ĐỘNG KHO HÀNG");

        // Header
        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("Nguyên liệu");
        headerRow.createCell(1).setCellValue("Tồn đầu kỳ");
        headerRow.createCell(2).setCellValue("Nhập kho");
        headerRow.createCell(3).setCellValue("Xuất kho");
        headerRow.createCell(4).setCellValue("Tồn cuối kỳ");
        headerRow.createCell(5).setCellValue("Đơn vị");

        // Data
        int rowNum = 3;
        for (Map<String, Object> item : inventory) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.get("name").toString());
            row.createCell(1).setCellValue(Long.parseLong(item.get("initialStock").toString()));
            row.createCell(2).setCellValue(Long.parseLong(item.get("import").toString()));
            row.createCell(3).setCellValue(Long.parseLong(item.get("export").toString()));
            row.createCell(4).setCellValue(Long.parseLong(item.get("finalStock").toString()));
            row.createCell(5).setCellValue(item.get("unit").toString());
        }

        // Định dạng
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // Các phương thức hỗ trợ tạo PDF
    private void addSummaryToPdf(Document document, String startDate, String endDate) throws DocumentException {
        // Lấy dữ liệu
        Map<String, Object> summary = hoaDonService.calculateTotalRevenueAndOrders(startDate, endDate);

        // Tạo tiêu đề
        Font sectionFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        Paragraph sectionTitle = new Paragraph("TỔNG QUAN", sectionFont);
        document.add(sectionTitle);
        document.add(Chunk.NEWLINE);

        // Tạo bảng dữ liệu
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        // Header
        PdfPCell cell1 = new PdfPCell(new Phrase("Chỉ tiêu"));
        PdfPCell cell2 = new PdfPCell(new Phrase("Giá trị"));
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell1);
        table.addCell(cell2);

        // Data
        table.addCell("Tổng số đơn hàng");
        table.addCell(summary.get("totalOrders").toString());

        table.addCell("Tổng doanh thu");
        table.addCell(formatCurrency((Long) summary.get("totalRevenue")) + " VNĐ");

        table.addCell("Số lượng khách hàng mới");
        table.addCell(summary.get("totalCustomers").toString());

        table.addCell("Tăng trưởng");
        table.addCell(summary.get("growthRate") + "%");

        document.add(table);
    }

    private void addTopProductsToPdf(Document document, String startDate, String endDate) throws DocumentException {
        // Lấy dữ liệu
        List<Map<String, Object>> topProducts = productService.getTopSellingProducts(startDate, endDate, 10);

        // Tạo tiêu đề
        Font sectionFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        Paragraph sectionTitle = new Paragraph("SẢN PHẨM BÁN CHẠY", sectionFont);
        document.add(sectionTitle);
        document.add(Chunk.NEWLINE);

        // Tạo bảng dữ liệu
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        // Header
        String[] headers = {"Mã SP", "Tên sản phẩm", "Danh mục", "Số lượng", "Doanh thu", "Tỷ trọng"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Data
        for (Map<String, Object> product : topProducts) {
            table.addCell(product.get("id").toString());
            table.addCell(product.get("name").toString());
            table.addCell(product.get("category").toString());
            table.addCell(product.get("quantity").toString());
            table.addCell(formatCurrency(Long.parseLong(product.get("revenue").toString())) + " VNĐ");
            table.addCell(product.get("percentage") + "%");
        }

        document.add(table);
    }

    private void addCategoriesToPdf(Document document, String startDate, String endDate) throws DocumentException {
        // Lấy dữ liệu
        List<Map<String, Object>> categories = productService.getCategorySalesReport(startDate, endDate);

        // Tạo tiêu đề
        Font sectionFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        Paragraph sectionTitle = new Paragraph("DOANH THU THEO DANH MỤC", sectionFont);
        document.add(sectionTitle);
        document.add(Chunk.NEWLINE);

        // Tạo bảng dữ liệu
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // Header
        String[] headers = {"Danh mục", "Số lượng", "Doanh thu", "Tỷ trọng"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Data
        for (Map<String, Object> category : categories) {
            table.addCell(category.get("category").toString());
            table.addCell(category.get("quantity").toString());
            table.addCell(formatCurrency(Long.parseLong(category.get("revenue").toString())) + " VNĐ");
            table.addCell(category.get("percentage") + "%");
        }

        document.add(table);
    }

    private void addPaymentMethodsToPdf(Document document, String startDate, String endDate) throws DocumentException {
        // Lấy dữ liệu
        List<Map<String, Object>> methods = hoaDonService.getPaymentMethodsReport(startDate, endDate);

        // Tạo tiêu đề
        Font sectionFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        Paragraph sectionTitle = new Paragraph("THỐNG KÊ PHƯƠNG THỨC THANH TOÁN", sectionFont);
        document.add(sectionTitle);
        document.add(Chunk.NEWLINE);

        // Tạo bảng dữ liệu
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // Header
        String[] headers = {"Phương thức", "Số đơn hàng", "Doanh thu", "Tỷ trọng"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Data
        for (Map<String, Object> method : methods) {
            table.addCell(method.get("method").toString());
            table.addCell(method.get("orders").toString());
            table.addCell(formatCurrency(Long.parseLong(method.get("revenue").toString())) + " VNĐ");
            table.addCell(method.get("percentage") + "%");
        }

        document.add(table);
    }

    private void addInventoryToPdf(Document document, String startDate, String endDate) throws DocumentException {
        // Lấy dữ liệu
        List<Map<String, Object>> inventory = nguyenLieuService.getInventoryMovementReport(startDate, endDate);

        // Tạo tiêu đề
        Font sectionFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        Paragraph sectionTitle = new Paragraph("BIẾN ĐỘNG KHO HÀNG", sectionFont);
        document.add(sectionTitle);
        document.add(Chunk.NEWLINE);

        // Tạo bảng dữ liệu
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        // Header
        String[] headers = {"Nguyên liệu", "Tồn đầu kỳ", "Nhập kho", "Xuất kho", "Tồn cuối kỳ", "Đơn vị"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // Data
        for (Map<String, Object> item : inventory) {
            table.addCell(item.get("name").toString());
            table.addCell(item.get("initialStock").toString());
            table.addCell(item.get("import").toString());
            table.addCell(item.get("export").toString());
            table.addCell(item.get("finalStock").toString());
            table.addCell(item.get("unit").toString());
        }

        document.add(table);
    }

    // Tiện ích
    private String formatCurrency(long amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    // Thêm phương thức tiện ích để lấy font Unicode
    private Font getUnicodeFont(float size, int style) throws Exception {
        InputStream fontStream = getClass().getResourceAsStream("/fonts/arial.ttf");
        BaseFont baseFont = BaseFont.createFont("arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, BaseFont.CACHED, fontStream.readAllBytes(), null);
        return new Font(baseFont, size, style);
    }
}