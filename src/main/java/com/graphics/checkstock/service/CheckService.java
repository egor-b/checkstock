package com.graphics.checkstock.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckService {

    private int i = 1;
    Thread thread = new Thread(new Runnable() {
        @SneakyThrows
        public void run() {
            while (i > 0) {
                log.info("----------- " + i + " iteration -----------");
                bestbuy(60000);
                evgaWeb(60000);
                Random rand = new Random();
                int r = rand.nextInt(12);
                log.info("----------- SLEEP FOR " + r + " SEC -----------");
                TimeUnit.SECONDS.sleep(r);
                i++;
            }
        }
    });

    public void start() {
        thread.start();
    }

    public int getStat() {
        return i;
    }

    private void bestbuy(int timeout) throws IOException {
        Document doc = Jsoup
                .connect("https://www.bestbuy.com/site/searchpage.jsp?_dyncharset=UTF-8&id=pcat17071&iht=y&keys=keys&ks=960&list=n&qp=brand_facet%3DBrand~EVGA%5Ebrand_facet%3DBrand~MSI%5Ebrand_facet%3DBrand~NVIDIA%5Ecategory_facet%3Dname~abcat0507002%5Egpusv_facet%3DGraphics%20Processing%20Unit%20(GPU)~NVIDIA%20GeForce%20RTX%203080&sc=Global&st=rtx%203080&type=page&usc=All%20Categories")
                .referrer("http://google.com")
                .userAgent("Mozilla")
                .timeout(timeout)
                .get();

        String evga = doc.select("button[data-sku-id=6436191]").first().toString();
        String nvidia = doc.select("button[data-sku-id=6429440]").first().toString();



        if (!evga.contains("Coming Soon")) {
            sendEmail("https://www.bestbuy.com/site/evga-geforce-rtx-3080-ftw3-gaming-10gb-gddr6x-pci-express-4-0-graphics-card/6436191.p?skuId=6436191");
        }

        if (!nvidia.contains("Sold Out")) {
            sendEmail("https://www.bestbuy.com/site/nvidia-geforce-rtx-3080-10gb-gddr6x-pci-express-4-0-graphics-card-titanium-and-black/6429440.p?skuId=6429440");
        }

        log.info("checked BestBuy");

    }

    private void evgaWeb(int timeout) throws IOException {

        Document doc = Jsoup
                .connect("https://www.evga.com/products/ProductList.aspx?type=0&family=GeForce+30+Series+Family&chipset=RTX+3080")
                .referrer("http://google.com")
                .userAgent("Mozilla")
                .timeout(timeout)
                .get();

        Elements evga = doc.select("div[class=pl-list-info]");

        for (Element ev : evga) {
            Elements href = ev.select("a[href]");
            if (href.toString().contains("10G-P5-3895-KR")) {
                if (!ev.select("p[class=message message-information]").toString().contains("Out of Stock")) {
                    sendEmail("https://www.evga.com/products/product.aspx?pn=10G-P5-3895-KR");
                }
            }

            if (href.toString().contains("10G-P5-3897-KR")) {
                if (!ev.select("p[class=message message-information]").toString().contains("Out of Stock")) {
                    sendEmail("https://www.evga.com/products/product.aspx?pn=10G-P5-3897-KR");
                }
            }

        }

        log.info("checked EVGA");
    }

    public static void sendEmail(String body) {
        String from = "gpusalert@gmail.com";
        String[] to = {"2025053977@vtext.com"};
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required","true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", "Samba!12");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.transport.protocol", "smtp");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for( int i = 0; i < to.length; i++ ) {
                toAddress[i] = new InternetAddress(to[i]);
            }

            for( int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject("GPU");
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, "Samba!12");
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (AddressException ae) {
            ae.printStackTrace();
        }
        catch (MessagingException me) {
            me.printStackTrace();
        }
    }
}
