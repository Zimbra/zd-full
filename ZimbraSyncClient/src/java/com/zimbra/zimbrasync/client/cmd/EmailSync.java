/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
import com.zimbra.cs.mailbox.calendar.ParsedDateTime;
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class EmailSync extends Sync {
    
    public static class EmailAppData {
        String to;
        String cc;
        String from;
        String replyTo;
        String subject;
        long dateReceived;
        String displayTo;
        int importance;
        boolean read;
        boolean mimeTruncated;
        int mimeSize;
        String messageClass;
        String internetCPID;
        String threadTopic;
        
        void setDataReceived(String dateReceivedStr) {
            ICalTimeZone utc = ICalTimeZone.getUTC();
            TimeZoneMap tzmap = new TimeZoneMap(utc);
            try {
                dateReceived = ParsedDateTime.parse(dateReceivedStr, tzmap, null, utc).getUtcTime();
            } catch (ParseException x) {
                ZimbraLog.sync.warn("can't parse DateReceive=" + dateReceivedStr, x);
                dateReceived = System.currentTimeMillis();
            }
        }
        
        public long getDataReceived() {
            return dateReceived;
        }
        
        public boolean isRead() {
            return read;
        }
        
        public int getImportance() {
            return importance;
        }
        
        public int getMimeSize() {
            return mimeSize;
        }
    }
    
    public static final class EmailSyncClientChange extends AirSyncClientChange  {
        private boolean isRead;
        
        //TODO: we'll see about flags
        
        public EmailSyncClientChange(String serverId, AirSyncClientItem clientItem, ClientChangeResponseCallback callback) {
            super(serverId, clientItem, callback);
        }
        
        public void setRead(boolean isRead) {
            this.isRead = isRead;
        }

        @Override
        protected void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException {
            bs.integerElement(NAMESPACE_POOMMAIL, POOMMAIL_READ, isRead ? 1 : 0);
        }
    }
    
    public static final class EmailSyncServerAdd extends AirSyncServerAdd {
        
        private boolean isFetchNeeded;
        
        public interface Executor {
            public void doServerAdd(String serverId, EmailAppData appData, InputStream in) throws CommandCallbackException, IOException;
        }
        
        private Executor executor;
        
        public EmailSyncServerAdd(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }
        
        // 2.5
        // <Sync xmlns="AirSync:" xmlns:A="POOMMAIL:">
        // ...
        // ...
        // <ApplicationData>
        //   <A:To>"J.J. Zhuang" &lt;jjzhuang@LS.local&gt;</A:To>
        //   <A:Cc>"John Doe" &lt;john@LS.local&gt;</A:Cc>
        //   <A:From>"John Doe" &lt;john@LS.local&gt;</A:From>
        //   <A:Subject>Yearly Shopping</A:Subject>
        //   <A:DateReceived>2005-11-26T08:16:30.156Z</A:DateReceived>
        //   <A:DisplayTo>J.J. Zhuang</A:DisplayTo>
        //   <A:Importance>1</A:Importance>
        //   <A:Read>0</A:Read>
        //   <A:Attachments>
        //     <A:Attachment>
        //       <A:AttMethod>1</A:AttMethod>
        //       <A:AttSize>28585</A:AttSize>
        //       <A:DisplayName>Blue hills.jpg</A:DisplayName>
        //       <A:AttName>Inbox/With%20pic%20and%20word%20doc.EML/Blue%20hills.jpg</A:AttName>
        //     </A:Attachment>
        //   </A:Attachments>
        //   <A:BodyTruncated>0</A:BodyTruncated>
        //   <A:Body>
        //     Type:Recurring Meeting
        //     Organizer:John Doe
        //     Start Time:Friday, November 25, 2005 5:30 AM
        //     End Time:Friday, November 25, 2005 11:00 AM
        //     Time Zone:(GMT-08:00) Pacific Time (US &amp; Canada); Tijuana
        //     Location:Various
        //  
        //     *~*~*~*~*~*~*~*~*~*
        //
        //     What do you say?
        //   </A:Body>
        //   <A:MessageClass>IPM.Schedule.Meeting.Request</A:MessageClass>
        //   <A:MeetingRequest>
        //     <A:AllDayEvent>0</A:AllDayEvent>
        //     <A:StartTime>2005-11-25T13:30:00.000Z</A:StartTime>
        //     <A:DtStamp>2005-11-26T08:16:31.000Z</A:DtStamp>
        //     <A:EndTime>2005-11-25T19:00:00.000Z</A:EndTime>
        //     <A:InstanceType>1</A:InstanceType>
        //     <A:Location>Various</A:Location>
        //     <A:Organizer>"John Doe" &lt;john@LS.local&gt;</A:Organizer>
        //     <A:Reminder>900</A:Reminder>
        //     <A:ResponseRequested>1</A:ResponseRequested>
        //     <A:Recurrences>
        //       <A:Recurrence>
        //         <A:Type>6</A:Type>
        //         <A:Interval>1</A:Interval>
        //         <A:Occurrences>10</A:Occurrences>
        //         <A:WeekOfMonth>5</A:WeekOfMonth>
        //         <A:DayOfWeek>32</A:DayOfWeek>
        //         <A:MonthOfYear>11</A:MonthOfYear>
        //       </A:Recurrence>
        //     </A:Recurrences>
        //     <A:Sensitivity>0</A:Sensitivity>
        //     <A:BusyStatus>3</A:BusyStatus>
        //     <A:TimeZone>4AEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAFAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAABAAIAAAAAAAAAxP///w==</A:TimeZone>
        //     <A:GlobalObjId>BAAAAIIA4AB0xbcQGoLgCAAAAAAAAAAAAAAAAAAAAAAAAAAAMwAAAHZDYWwtVWlkAQAAAHtBQUY4MUFDRi03RDM5LTQxQjUtQjIwRi01QTVBREI1NEJBMUZ9AA==</A:GlobalObjId>
        //   </A:MeetingRequest>
        // </ApplicationData>

        // 12.0
        // <ApplicationData>
        //   <POOMMAIL:To>user2@sudipto-mpro.local</POOMMAIL:To>
        //   <POOMMAIL:From>Demo User Two &lt;user2@sudipto-mpro.local&gt;</POOMMAIL:From>
        //   <POOMMAIL:Reply-To>Demo User Two &lt;user2@sudipto-mpro.local&gt;</POOMMAIL:Reply-To>
        //   <POOMMAIL:Subject>Fwd: LinkedIn Network Updates, 8/03/2010</POOMMAIL:Subject>
        //   <POOMMAIL:DateReceived>2010-08-11T09:13:15.000Z</POOMMAIL:DateReceived>
        //   <POOMMAIL:Importance>1</POOMMAIL:Importance>
        //   <POOMMAIL:Read>1</POOMMAIL:Read>
        //   <AirSyncBase:Body>
        //     <AirSyncBase:EstimatedDataSize>16425</AirSyncBase:EstimatedDataSize>
        //     <AirSyncBase:Type>2</AirSyncBase:Type>
        //     <AirSyncBase:Data>.....
        //       ...
        //     </AirSyncBase:Data>
        //     <AirSyncBase:Truncated>1</AirSyncBase:Truncated>
        //     <POOMMAIL:Flag/>
        //     <POOMMAIL:ContentClass>urn:content-classes:message</POOMMAIL:ContentClass>
        //     <AirSyncBase:NativeBodyType>2</AirSyncBase:NativeBodyType>
        //   </AirSyncBase:Body>
        //   <POOMMAIL:MessageClass>IPM.Note</POOMMAIL:MessageClass>
        //   <POOMMAIL:InternetCPID>381</POOMMAIL:InternetCPID>
        // </ApplicationData>

        @Override
        protected void parseApplicationData(BinaryParser bp) throws CommandCallbackException, BinaryCodecException, IOException {
            EmailAppData appData = new EmailAppData();
            while (bp.next() == START_TAG) {
                if (POOMMAIL_TO.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.to = bp.nextText();
                else if (POOMMAIL_CC.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.cc = bp.nextText();
                else if (POOMMAIL_FROM.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.from = bp.nextText();
                else if (POOMMAIL_REPLYTO.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.replyTo = bp.nextText();
                else if (POOMMAIL_SUBJECT.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.subject = bp.nextText();
                else if (POOMMAIL_DATERECEIVED.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.setDataReceived(bp.nextText());
                else if (POOMMAIL_DISPLAYTO.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.displayTo = bp.nextText();
                else if (POOMMAIL_IMPORTANCE.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.importance = bp.nextIntegerContent();
                else if (POOMMAIL_READ.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.read = bp.nextIntegerContent() == 1 ? true : false;
                else if (POOMMAIL_MIMETRUNCATED.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.mimeTruncated = bp.nextIntegerContent() == 1 ? true : false;
                else if (POOMMAIL_MIMESIZE.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.mimeSize = bp.nextIntegerContent();
                else if (POOMMAIL_MESSAGECLASS.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.messageClass = bp.nextText();
                else if (POOMMAIL_INTERNETCPID.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.internetCPID = bp.nextText();
                else if (POOMMAIL_THREADTOPIC.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    appData.threadTopic = bp.nextText();
                else if (POOMMAIL_ATTACHMENTS.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    bp.skipElement();
                else if (AIRSYNCBASE_ATTACHMENTS.equals(bp.getName()) && NAMESPACE_AIRSYNCBASE.equals(bp.getNamespace()))
                    bp.skipElement();
                else if (POOMMAIL_MEETINGREQUEST.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace()))
                    bp.skipElement();
                else if (AIRSYNCBASE_BODY.equals(bp.getName()) && NAMESPACE_AIRSYNCBASE.equals(bp.getNamespace()))
                    bp.skipElement();
                else if (POOMMAIL_MIMEDATA.equals(bp.getName()) && NAMESPACE_POOMMAIL.equals(bp.getNamespace())) {
                    if (!appData.mimeTruncated) {
                        executor.doServerAdd(serverId, appData, bp.nextInputStream());
                    } else {
                        ZimbraLog.sync.debug("need refetching truncated message \"%s\" (ServerId=%s) size=%d", appData.subject, serverId, appData.mimeSize);
                        ByteUtil.skip(bp.nextInputStream(), Long.MAX_VALUE);
                        isFetchNeeded = true;
                    }
                    bp.closeTag();
                } else
                    bp.skipUnknownElement();
            }
        }
        
        @Override
        protected void execute() throws CommandCallbackException {
            //do nothing
        }
        
        @Override
        protected boolean isFetchNeeded() {
            return isFetchNeeded;
        }
    }
    
    public static final class EmailSyncServerChange extends AirSyncServerChange  {
        
        public interface Executor {
            public void doServerChange(String serverId, boolean isRead) throws CommandCallbackException;
        }
        
        private Executor executor;
        private boolean isRead;
        
        public EmailSyncServerChange(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }

        @Override
        protected void parseApplicationData(BinaryParser bp) throws BinaryCodecException, IOException {
            while (bp.next() == START_TAG && NAMESPACE_POOMMAIL.equals(bp.getNamespace())) {
                if (bp.getName().equals(POOMMAIL_READ))
                    isRead = bp.nextIntegerElement(NAMESPACE_POOMMAIL, POOMMAIL_READ) == 0 ? false : true;
                else 
                    bp.skipElement();
            }
        }
        
        @Override
        protected void execute() throws CommandCallbackException {
            executor.doServerChange(serverId, isRead);
        }
    }

    
    public interface EmailSyncServerCommandExcecutor extends EmailSyncServerAdd.Executor, EmailSyncServerChange.Executor, AirSyncServerDelete.Executor {}

    private static final String COLLECTION_CLASS = "Email";
    
    private EmailSyncServerCommandExcecutor serverCommandExecutor;
    
    public EmailSync(String collectionId, String clientSyncKey, EmailSyncServerCommandExcecutor serverCommandExecutor) {
        super(COLLECTION_CLASS, collectionId, clientSyncKey, serverCommandExecutor);
        this.serverCommandExecutor = serverCommandExecutor;
    }
    
    @Override
    protected AirSyncServerAdd newServerAdd(String serverId) {
        return new EmailSyncServerAdd(serverId, serverCommandExecutor);
    }
    
    @Override
    protected AirSyncServerChange newServerChange(String serverId) {
        return new EmailSyncServerChange(serverId, serverCommandExecutor);
    }
}
