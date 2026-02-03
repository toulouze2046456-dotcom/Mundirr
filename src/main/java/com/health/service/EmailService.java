package com.health.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Email service for sending transactional emails.
 * Supports both plain text and HTML emails.
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.mail.from}")
    private String fromEmail;
    
    @Value("${app.mail.frontend-url}")
    private String frontendUrl;
    
    /**
     * Send a simple plain text email
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
    
    /**
     * Send an HTML email
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            
            mailSender.send(message);
            logger.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send HTML email to {}: {}", to, e.getMessage());
        }
    }
    
    /**
     * Send password reset email with code
     */
    public void sendPasswordResetEmail(String to, String resetCode, String userName) {
        String subject = "Vellbeing OS - Password Reset Code";
        
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background: #f5f5f7; margin: 0; padding: 20px; }
                    .container { max-width: 500px; margin: 0 auto; background: white; border-radius: 16px; padding: 40px; box-shadow: 0 4px 24px rgba(0,0,0,0.1); }
                    .logo { text-align: center; margin-bottom: 30px; }
                    .logo h1 { color: #1a1a2e; margin: 0; font-size: 28px; }
                    .code-box { background: #f8f9fa; border-radius: 12px; padding: 24px; text-align: center; margin: 30px 0; border: 2px dashed #e0e0e0; }
                    .code { font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #1a1a2e; font-family: monospace; }
                    .text { color: #666; line-height: 1.6; }
                    .footer { text-align: center; margin-top: 30px; color: #999; font-size: 12px; }
                    .warning { background: #fff3cd; border-radius: 8px; padding: 12px; margin-top: 20px; color: #856404; font-size: 13px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="logo">
                        <h1>🌿 Vellbeing OS</h1>
                    </div>
                    
                    <p class="text">Hello%s,</p>
                    
                    <p class="text">You requested to reset your password. Use the code below to complete the process:</p>
                    
                    <div class="code-box">
                        <div class="code">%s</div>
                    </div>
                    
                    <p class="text">This code will expire in <strong>1 hour</strong>.</p>
                    
                    <div class="warning">
                        ⚠️ If you didn't request this password reset, please ignore this email. Your account is safe.
                    </div>
                    
                    <div class="footer">
                        <p>© 2026 Vellbeing OS - Your Wellness Ecosystem</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                userName != null && !userName.isEmpty() ? " " + userName : "",
                resetCode
            );
        
        sendHtmlEmail(to, subject, htmlContent);
        logger.info("Password reset email sent to: {} with code: {}", to, resetCode);
    }
    
    /**
     * Send welcome email after registration
     */
    public void sendWelcomeEmail(String to, String userName) {
        String subject = "Welcome to Vellbeing OS! 🌿";
        
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background: #f5f5f7; margin: 0; padding: 20px; }
                    .container { max-width: 500px; margin: 0 auto; background: white; border-radius: 16px; padding: 40px; box-shadow: 0 4px 24px rgba(0,0,0,0.1); }
                    .logo { text-align: center; margin-bottom: 30px; }
                    .logo h1 { color: #1a1a2e; margin: 0; font-size: 28px; }
                    .text { color: #666; line-height: 1.8; }
                    .feature { display: flex; align-items: center; margin: 12px 0; }
                    .feature-icon { margin-right: 10px; }
                    .cta { display: block; text-align: center; background: #1a1a2e; color: white; padding: 16px 32px; border-radius: 12px; text-decoration: none; font-weight: 600; margin: 30px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #999; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="logo">
                        <h1>🌿 Vellbeing OS</h1>
                    </div>
                    
                    <p class="text">Welcome%s to your personal wellness ecosystem!</p>
                    
                    <p class="text">Track and optimize every aspect of your life:</p>
                    
                    <div style="margin: 24px 0;">
                        <div class="feature"><span class="feature-icon">💪</span> Physical Health & Workouts</div>
                        <div class="feature"><span class="feature-icon">🍎</span> Nutrition & Supplements</div>
                        <div class="feature"><span class="feature-icon">😴</span> Sleep Quality</div>
                        <div class="feature"><span class="feature-icon">🧠</span> Mental & Hormonal Balance</div>
                        <div class="feature"><span class="feature-icon">💰</span> Financial Wellness</div>
                        <div class="feature"><span class="feature-icon">📚</span> Cultural Growth</div>
                    </div>
                    
                    <a href="%s" class="cta">Start Your Journey →</a>
                    
                    <div class="footer">
                        <p>© 2026 Vellbeing OS - Your Wellness Ecosystem</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                userName != null && !userName.isEmpty() ? " " + userName : "",
                frontendUrl
            );
        
        sendHtmlEmail(to, subject, htmlContent);
    }
}
