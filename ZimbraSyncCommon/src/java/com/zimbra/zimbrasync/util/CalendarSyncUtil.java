/*
 * 
 */
package com.zimbra.zimbrasync.util;

import java.util.List;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ExceptionToString;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.zmime.ZMimeBodyPart;
import com.zimbra.common.zmime.ZMimeMultipart;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.calendar.CalendarDataSource;
import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mailbox.calendar.ZAttendee;
import com.zimbra.cs.mailbox.calendar.ZCalendar;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ZVCalendar;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.util.JMSession;

public class CalendarSyncUtil {

    public static MimeMessage encodeInviteIntoMimeMsg(Invite invite, boolean isModified, Account account, List<BodyPart> attachments) throws ServiceException {
        try {
            MimeMessage mm = new Mime.FixedMimeMessage(JMSession.getSmtpSession(account));
            String subject = invite.getName() != null ? invite.getName() : "";
            ZVCalendar zcal = invite.newToICalendar(true);
            reformatCalendarMessage(mm, subject, null, zcal, attachments);

            List<ZAttendee> attendees = invite.getAttendees();
            for (ZAttendee attendee : attendees) {
                mm.addRecipients(Message.RecipientType.TO, attendee.getAddress());
            }
            return mm;
        } catch (MessagingException me) {
            String excepStr = ExceptionToString.ToString(me);
            ZimbraLog.sync.warn(excepStr);
            throw ServiceException.FAILURE("MessagingExecption", me);
        }
    }

    public static void reformatCalendarMessage(MimeMessage mm, String subject, String desc, ZCalendar.ZVCalendar cal) throws ServiceException {
        reformatCalendarMessage(mm, subject, desc, cal, null);
    }

    public static void reformatCalendarMessage(MimeMessage mm, String subject, String desc, ZCalendar.ZVCalendar cal, List<BodyPart> attachments)
    throws ServiceException {
        try {
            if (desc != null) {
                cal.addDescription(desc, null);
            }
            mm.setSubject(subject);
            if (attachments == null || attachments.size() == 0) {
                mm.setDataHandler(new DataHandler(new CalendarDataSource(cal, null)));
            } else {
                MimeMultipart mmp = new ZMimeMultipart();
                MimeBodyPart mbp = new ZMimeBodyPart();
                mbp.setDataHandler(new DataHandler(new CalendarDataSource(cal, null)));
                mmp.addBodyPart(mbp);
                for (BodyPart bp : attachments) {
                    mmp.addBodyPart(bp);
                }
                mm.setContent(mmp);
            }
            mm.saveChanges();
        } catch (MessagingException e) {
            throw ServiceException.FAILURE("Messaging Exception while building MimeMessage from invite", e);
        }
    }
}
