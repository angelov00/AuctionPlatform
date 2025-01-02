package com.springproject.auctionplatform.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.springproject.auctionplatform.model.DTO.PromotionDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PDFGenerator {

    public void generateInvoice(PromotionDTO promotion, HttpServletResponse response) throws Exception {

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Paragraph title = new Paragraph("Invoice for Promoted Auction", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Font textFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // Заглавие
        // TODO - add other fields
        document.add(new Paragraph("Auction Title: " + promotion.getAuction().getTitle(), textFont));
        document.add(new Paragraph("Promotion Amount: " + promotion.getAmount().toString(), textFont));
        document.add(new Paragraph("Promotion Duration(in days): " + promotion.getDuration(), textFont));
        document.add(new Paragraph("Payment Method: " + promotion.getPaymentMethod().toString(), textFont));

        LocalDateTime promotionDate = promotion.getPromotionDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = promotionDate.format(formatter);
        document.add(new Paragraph("Promotion Date: " + formattedDate, textFont));

        document.add(new Paragraph("\n-------------------------------------\n"));

        // Затваряне на документа
        document.close();
    }
}
