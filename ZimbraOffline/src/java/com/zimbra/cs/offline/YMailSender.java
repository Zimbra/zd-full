/*
 * 
 */
package com.zimbra.cs.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.mailbox.MailSender;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.offline.util.ymail.YMailClient;
import com.zimbra.cs.offline.util.ymail.YMailException;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class YMailSender extends MailSender {
    private final YMailClient ymc;
    private final boolean saveCopy;

    public static YMailSender newInstance(OfflineDataSource ds)
        throws ServiceException {
        try {
            YMailClient ymc = new YMailClient(OfflineYAuth.authenticate(ds));
            ymc.setTrace(ds.isDebugTraceEnabled());
            // Have YMail save a copy of the message to Sent folder if user
            // has chosen this option and we don't save it ourselves.
            boolean saveCopy = !ds.isSaveToSent() && ds.getAccount().isPrefSaveToSent();
            return new YMailSender(ymc, saveCopy);
        } catch (Exception e) {
            throw ServiceException.FAILURE("Unable to create initialize YMail client", e);
        }
    }

    private YMailSender(YMailClient ymc, boolean saveCopy) {
        this.ymc = ymc;
        this.saveCopy = saveCopy;
        setTrackBadHosts(false);
    }

    @Override
    protected Collection<Address> sendMessage(Mailbox mbox,
                               MimeMessage mm,
                               Collection<RollbackData> rollbacks) throws IOException {
        try {
        	Address[] rcpts = mm.getAllRecipients();
            ymc.sendMessage(mm, saveCopy);
            return Arrays.asList(rcpts);
        } catch (MessagingException e) {
            throw new YMailException("Unable get recipient list", e);
        }
    }
}
