package com.locate.rmds.processer;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
 * @author CloudWei kaiweicai@163.com create time 20142014年8月12日
 * @copyRight by Author
 */
@Component
public class EmailNotifier implements INotifier {
	private Logger logger = Logger.getLogger(EmailNotifier.class);

	@Override
	public void notifyAdmin(String title,String content) {
		InetAddress localAddress = null;
		String hostName = "";
		try {
			localAddress = InetAddress.getLocalHost();
			
		} catch (UnknownHostException e1) {
			logger.error("Can not get the localHost name.",e1);
		}
		hostName = localAddress.getHostName();
		title = title +", Message from host " + hostName;
		
		String needNotify = SystemProperties.getProperties(SystemProperties.ADMIN_NEED_NOTIFY);
		if (needNotify.equalsIgnoreCase("true")) {
			String adminEmail = SystemProperties.getProperties(SystemProperties.ADMIN_USER_EMAIL);
			String mailSmtpHost = SystemProperties.getProperties(SystemProperties.SMTP_ADMIN_USER_EMAIL);
			String smtpMailUserName = SystemProperties.getProperties(SystemProperties.SMTP_MAIL_USER_NAME);
			String smtpMailPassword = SystemProperties.getProperties(SystemProperties.SMTP_MAIL_PASSWORD);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", mailSmtpHost);// 指定SMTP服务器
			properties.put("mail.smtp.auth", "true");// 指定是否需要SMTP验证
			// title = new String(title.getBytes("iso-8859-1"));
			Session emailSession = Session.getDefaultInstance(properties);
			Message emailMessage = new MimeMessage(emailSession);
			try {
				emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(adminEmail));
				emailMessage.setFrom(new InternetAddress(adminEmail));
				emailMessage.setSubject(title);
				content = content + "\n happen time "+ new Date();
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
	
	public static void main(String[] args) throws Throwable {
		InetAddress localAddress = InetAddress.getLocalHost();
		String hostAddress = localAddress.getHostAddress();
		String hostName = localAddress.getHostName();
		System.out.println(hostAddress);
		System.out.println(hostName);
	}
}
