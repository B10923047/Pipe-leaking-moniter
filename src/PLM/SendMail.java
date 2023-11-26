package PLM;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	Session session;
	MimeMessage message;
    private String userName = "icewm2469@gmail.com";
    private String password = "gibyjsvmpcdudjif";
    private String receiver;
    private String subject = "Water Pipe Leak Notification";
    private String txt;
    private String state;
    
    SendMail() {
    	Properties prop = new Properties();
    	prop.setProperty("mail.transport.protocol", "smtp");
    	prop.setProperty("mail.host", "smtp.gmail.com");
    	prop.put("mail.smtp.port", "465");
    	prop.put("mail.smtp.auth", "true");
    	prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    	prop.put("mail.smtp.socketFactory.port", "465");
    	
    	session = Session.getDefaultInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
    	});
    	message = new MimeMessage(session);
    }
    public void send(String mail, float lost) {
    	receiver = mail;
    	txt = "A " + state + " amount of water leakage was detected, and NT$ " + lost + " has been lost. You can use the user interface to make an appointment for maintenance";
    	
    	try {
			message.setSender(new InternetAddress(userName));
			message.setRecipient(RecipientType.TO, new InternetAddress(receiver));
			message.setSubject(subject);
			message.setContent(txt, "text/html;charset=UTF-8");
			
			Transport transport = session.getTransport();
			transport.send(message);
			transport.close();
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }
    void setState(String state) {
    	this.state = state;
    }
}