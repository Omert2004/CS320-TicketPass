import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

public class PaymentAndTicketingService {

    public boolean processPayment(int userId, String paymentToken) {
        return true;
    }

    public boolean retryPayment(int transactionId, String newPaymentToken) {
        return true;
    }

    public void holdSeatLockOnFail(int transactionId) {
    }

    public File generatePDFTicket(int bookingId) {
        return new File("ticket_" + bookingId + ".pdf");
    }

    public Image generateQRCode(int bookingId) {
        return new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
    }
}