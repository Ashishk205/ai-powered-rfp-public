package com.planet_learning.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.planet_learning.interfaces.EmailService;

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
	public void sendEmail(String to, String subject, String body) 
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
