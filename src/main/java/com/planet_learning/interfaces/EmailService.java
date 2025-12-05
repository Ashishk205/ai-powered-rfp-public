package com.planet_learning.interfaces;

public interface EmailService 
{
	void sendEmail(String to, String subject, String body) throws Exception;
	void sendEmail(String to, String subject, String body, Long rfpdId) throws Exception;
}