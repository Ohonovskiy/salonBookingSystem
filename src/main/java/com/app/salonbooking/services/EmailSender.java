package com.app.salonbooking.services;

import com.app.salonbooking.view.BookingView;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailSender {

    private final BarberService barberService;
    private final ServiceItemService itemService;

    final String username = "beauty.studio.in.ukraine@gmail.com";
    final String password = "delx xely mcaf bqgf";

    @Autowired
    public EmailSender(BarberService barberService, ServiceItemService serviceItemService) {
        this.barberService = barberService;
        this.itemService = serviceItemService;
    }

    public void sendEmail(BookingView bookingView){

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(bookingView.getCustomerEmail())
            );
            message.setSubject("Онлайн запис «Мартель»");
            message.setText("Вітаємо " + bookingView.getCustomerName() + "!"
                    + "\n\nВас було записано до " + barberService.getBarberById(bookingView.getBarberId()).getName()
                    + "\nНа " + bookingView.getBookingDate() + " о " + bookingView.getStartTime()
                    + "\nТип послуги: " +itemService.getServiceById(bookingView.getServiceId()).getName()
                    + "\nВартість: " +itemService.getServiceById(bookingView.getServiceId()).getPrice() + "грн"
                    + "\nТривалість: " +itemService.getServiceById(bookingView.getServiceId()).getDuration() + "хв");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}