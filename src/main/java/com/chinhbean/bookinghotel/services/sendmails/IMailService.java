package com.chinhbean.bookinghotel.services.sendmails;

import com.chinhbean.bookinghotel.dtos.DataMailDTO;
import jakarta.mail.MessagingException;

public interface IMailService {
    void sendHtmlMail(DataMailDTO dataMail, String templateName) throws MessagingException;
}
