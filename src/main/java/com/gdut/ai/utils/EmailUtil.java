package com.gdut.ai.utils;

import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * 邮件工具类
 */
@Slf4j
public class EmailUtil {
    static final String username = "2364074108@qq.com"; // Your email
    static final String password = "ahnwpbzcctedecdh"; // Your email password

    public static void sendEmail(String recipientEmail, String code) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.qq.com"); // 使用QQ邮箱的SMTP服务器
        prop.put("mail.smtp.port", "465"); // QQ邮箱的SMTP端口为465
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.enable", "true"); // 使用SSL加密

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );
            message.setSubject("陈峥毕设：安全验证码\n\n");
            message.setText("亲爱的aiPlusChat用户，您好！您的验证码是：\n\n"
                    + code);

            Transport.send(message);
            log.info("Email sent to " + recipientEmail + " successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
