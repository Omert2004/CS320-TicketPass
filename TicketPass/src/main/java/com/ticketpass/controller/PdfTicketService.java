package com.ticketpass.controller;

import com.ticketpass.model.dto.Booking;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font; // Removed PDType1Font and Standard14Fonts
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfTicketService {

    private QRCodeService qrCodeService;
    private BookingHistoryService historyService;

    public PdfTicketService(QRCodeService qrCodeService, BookingHistoryService historyService) {
        this.qrCodeService = qrCodeService;
        this.historyService = historyService;
    }

    public File generatePDFTicket(int userId, int ticketId) {
        File ticketFile = new File("ticket_" + ticketId + ".pdf");

        Booking info = null;
        List<Booking> bookings = historyService.getUserBookings(userId);
        for (Booking b : bookings) {
            if (b.getTicketId() == ticketId) {
                info = b;
                break;
            }
        }

        if (info == null) return ticketFile;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            java.io.InputStream fontStream = getClass().getResourceAsStream("/fonts/arial.ttf");

            if (fontStream == null) {
                throw new IOException("Could not find arial.ttf in the resources/fonts/ folder!");
            }

            PDType0Font customFont = PDType0Font.load(document, fontStream);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(customFont, 24);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("TicketPass Official Ticket");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(customFont, 16);
                contentStream.newLineAtOffset(50, 650);
                contentStream.showText("Event: " + info.getEventName());
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Date: " + info.getEventDate());
                contentStream.newLineAtOffset(0, -30);
                contentStream.showText("Seat: Row " + info.getRowLabel() + " | Number " + info.getSeatNumber());
                contentStream.endText();

                Image qrAwtImage = qrCodeService.generateQRCode(userId, ticketId);

                File tempQrFile = new File("temp_qr_" + ticketId + ".png");
                javax.imageio.ImageIO.write((BufferedImage) qrAwtImage, "png", tempQrFile);

                PDImageXObject pdImage = PDImageXObject.createFromFile(tempQrFile.getAbsolutePath(), document);
                contentStream.drawImage(pdImage, 50, 400, 150, 150);

                tempQrFile.delete();
            }
            document.save(ticketFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticketFile;
    }
}