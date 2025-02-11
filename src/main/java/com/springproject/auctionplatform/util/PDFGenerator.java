package com.springproject.auctionplatform.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.springproject.auctionplatform.model.DTO.PromotionDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PDFGenerator {

    public void generateInvoice(PromotionDTO promotion, HttpServletResponse response) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
        Paragraph title = new Paragraph("Invoice for Promoted Auction", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Company Info
        Font companyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Paragraph companyInfo = new Paragraph("Auction Platform Ltd.\n123 Auction Street\nauctionplatform.com\n", companyFont);
        companyInfo.setAlignment(Element.ALIGN_RIGHT);
        companyInfo.setSpacingAfter(20);
        document.add(companyInfo);

        // Add Date
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        Paragraph date = new Paragraph("Invoice Generated On: " + now.format(formatter), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);

        // Promotion Details Table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);
        table.setWidths(new int[]{2, 3}); // Adjust column widths

        // Table Header Style
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell headerCell;

        headerCell = new PdfPCell(new Phrase("Details", headerFont));
        headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
        headerCell.setPadding(8);
        table.addCell(headerCell);

        headerCell = new PdfPCell(new Phrase("Information", headerFont));
        headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
        headerCell.setPadding(8);
        table.addCell(headerCell);

        // Add Promotion Data
        addTableRow(table, "Amount", promotion.getAmount().toString());
        addTableRow(table, "Duration", promotion.getDuration() + " days");
        addTableRow(table, "Payment Method", promotion.getPaymentMethod().toString());
        addTableRow(table, "Promotion Date", promotion.getPromotionDate().format(formatter));

        // User Info
        addTableRow(table, "User", promotion.getUser().getFullname());
        addTableRow(table, "Username", promotion.getUser().getUsername());

        // Auction Info
        addTableRow(table, "Auction ID", String.valueOf(promotion.getAuction().getId()));
        addTableRow(table, "Auction Title", promotion.getAuction().getTitle());
        addTableRow(table, "Auction Description", promotion.getAuction().getDescription());

        document.add(table);

        // Footer
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph("This document was system-generated and does not require a signature.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);

        document.close();
    }

    private void addTableRow(PdfPTable table, String key, String value) {
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        PdfPCell keyCell = new PdfPCell(new Phrase(key, cellFont));
        keyCell.setPadding(5);
        table.addCell(keyCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, cellFont));
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }
}
