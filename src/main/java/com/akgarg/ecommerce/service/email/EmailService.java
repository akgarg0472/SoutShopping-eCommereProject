package com.akgarg.ecommerce.service.email;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface EmailService {

    boolean sendEmail(String to, String subject, String message);

}
