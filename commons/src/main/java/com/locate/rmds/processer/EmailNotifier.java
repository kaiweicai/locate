package com.locate.rmds.processer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.locate.common.logging.err.ErrorLogHandler;
import com.locate.common.utils.SystemProperties;
import com.locate.rmds.processer.face.INotifier;

/**
 * @author CloudWei kaiweicai@163.com create time 20142014年8月12日
 * @copyRight by Author
 */
@Component
public class EmailNotifier implements INotifier {
	private Logger logger = LoggerFactory.getLogger(EmailNotifier.class);
	private ErrorLogHandler errorLogHandler = ErrorLogHandler.getLogger(getClass());
	@Override
	public void notifyAdmin(String title, String content) {
		InetAddress localAddress = null;
		String hostName = "";
		try {
			localAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			errorLogHandler.error("Can not get the localHost name.", e1);
		}
		hostName = localAddress.getHostName();
		title = title + ", Message from host " + hostName;

		String[] adminEmail = SystemProperties.getProperties(SystemProperties.ADMIN_USER_EMAIL).split(",");
		Address[] ccAdresses = new InternetAddress[adminEmail.length];
		String mailSmtpHost = SystemProperties.getProperties(SystemProperties.SMTP_ADMIN_USER_EMAIL);
		String smtpMailUserName = SystemProperties.getProperties(SystemProperties.SMTP_MAIL_USER_NAME);
		String smtpMailPassword = SystemProperties.getProperties(SystemProperties.SMTP_MAIL_PASSWORD);
		String adminEmailFrom = SystemProperties.getProperties(SystemProperties.ADMIN_EMAIL_FROM);
		Properties properties = new Properties();
		properties.put("mail.smtp.host", mailSmtpHost);// 指定SMTP服务器
		properties.put("mail.smtp.auth", "true");// 指定是否需要SMTP验证
		// title = new String(title.getBytes("iso-8859-1"));
		Session emailSession = Session.getDefaultInstance(properties);
		Message emailMessage = new MimeMessage(emailSession);
		try {
			for(int i = 0;i<adminEmail.length;i++){
				ccAdresses[i]=new InternetAddress(adminEmail[i]);
			}
			emailMessage.setRecipients(Message.RecipientType.TO, ccAdresses);
			emailMessage.setFrom(new InternetAddress(adminEmailFrom));
			emailMessage.setSubject(title);
			content = content + "\n happen time " + new Date();
			emailMessage.setText(content);
			emailMessage.saveChanges();
			Transport transport = emailSession.getTransport("smtp");
			transport.connect(mailSmtpHost, smtpMailUserName, smtpMailPassword);
			transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
			transport.close();
		} catch (AddressException e) {
			logger.error("EmailSender, AddressException: " + e.getMessage());
		} catch (MessagingException e) {
			logger.error("EmailSender, MessagingException: " + e.getMessage());
		}
	}

}
