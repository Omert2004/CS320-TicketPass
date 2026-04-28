package com.ticketpass.controller;

import com.ticketpass.model.dto.Booking;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

public class QRCodeService {

    private BookingHistoryService historyService;

    public QRCodeService(BookingHistoryService historyService) {
        this.historyService = historyService;
    }

    public Image generateQRCode(int userId, int ticketId) {
        String qrData = fetchQRCodeString(userId, ticketId);
        if (qrData == null) qrData = "INVALID_TICKET";

        try {
            QRCodeWriter barcodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = barcodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 200, 200);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        }
    }

    private String fetchQRCodeString(int userId, int ticketId) {
        List<Booking> bookings = historyService.getUserBookings(userId);
        for (Booking booking : bookings) {
            if (booking.getTicketId() == ticketId) {
                return booking.getQrCode();
            }
        }
        return null;
    }
}