package com.planet_learning.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.planet_learning.interfaces.EmailService;

import jakarta.mail.internet.MimeMessage;

@Component
public class MailgunEmailService implements EmailService 
{
	private JavaMailSender mailSender;
	
	public MailgunEmailService(
			JavaMailSender mailSender)
	{	
		this.mailSender = mailSender;
	}
	
	@Override
	public void sendEmail(String to, String subject, String body, Long rfpId) throws Exception
	{
		// Create a MimeMessage ( i have to put my unique rfpId in mail, so that we have to keep track email uniquely )
		MimeMessage message = mailSender.createMimeMessage();
		
		// create a mime helper to configure the email
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom("planetlearning@sandboxf8f906404dd04c8c84312f45474.mailgun.org"); // Matches your domain
		helper.setTo(to);
		helper.setSubject(subject + ", and your " + "unique-tracking-id-" + rfpId.toString());
		helper.setText(body);
        
		// ADD THE CUSTOM HEADER
        // The prefix "v:" tells Mailgun this is a custom variable
		message.addHeader("v:rfpId", rfpId.toString()); // When Webhook returns this will not work for INBOUND ROUTING
		
        mailSender.send(message);
        System.out.println("Email sent via Mailgun to " + to);
	}
	
	@Override
	public void sendEmail(String to, String subject, String body) throws Exception{
		// Leave it blank
	}

	public void sendEmailOld(String to, String subject, String body) 
	{
		try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("planetlearning@sandboxf8f906404dd04c8c84312ffcd4445474.mailgun.org"); // Matches your domain
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            System.out.println("Email sent via Mailgun to " + to);
        } catch (Exception e) {
            System.err.println("Error sending email: ");
            throw e;
        }
	}
}