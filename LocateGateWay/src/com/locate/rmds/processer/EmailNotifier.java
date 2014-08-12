package com.locate.rmds.processer;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.locate.rmds.processer.face.INotifier;
import com.locate.rmds.util.SystemProperties;

/**
 * @author CloudWei kaiweicai@163.com create time 2014年8月12日
 * @copyRight by Author
 */
@Component
public class EmailNotifier implements INotifier {
	private Logger logger = Logger.getLogger(EmailNotifier.class);
	public Date lastEmailSendingDate = new Date();

	private long count = 0;

	private String mailSmtpHost = "smtp.163.com";

	@Override
	public void notifyAdmin() {
		SystemProperties.init("config/rfaConfig.properties");
		String adminEmail = SystemProperties.getProperties(SystemProperties.ADMIN_USER_EMAIL);
		String mailSmtpHost = SystemProperties.getProperties(SystemProperties.SMTP_ADMIN_USER_EMAIL);
		Properties properties = new Properties();
		properties.put("mail.smtp.host", mailSmtpHost);// 指定SMTP服务器
		properties.put("mail.smtp.auth", "true");// 指定是否需要SMTP验证
		// title = new String(title.getBytes("iso-8859-1"));
		Session emailSession = Session.getDefaultInstance(properties);
		Message emailMessage = new MimeMessage(emailSession);
		try {
			emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(adminEmail));
			emailMessage.setFrom(new InternetAddress(adminEmail));
			emailMessage.setSubject("Locate server status notify");
			emailMessage.setText("Locate server is shuting down for some reseaon.");
			emailMessage.saveChanges();
			Transport transport = emailSession.getTransport("smtp");
			transport.connect(mailSmtpHost, "kaiweicai", "yousini"); // 这个邮箱可随便使用
			transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
			transport.close();
			count++;
		} catch (AddressException e) {
			logger.error("EmailSender, AddressException: " + e.getMessage());
		} catch (MessagingException e) {
			logger.error("EmailSender, MessagingException: " + e.getMessage());
		}
	}
}
