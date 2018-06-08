/*
 * 
 */
package com.zimbra.zimbrasync.data;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.CalendarItem.Instance;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.Task;
import com.zimbra.cs.mailbox.calendar.Alarm;
import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
import com.zimbra.cs.mailbox.calendar.IcalXmlStrMap;
import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mailbox.calendar.InviteInfo;
import com.zimbra.cs.mailbox.calendar.ParsedDateTime;
import com.zimbra.cs.mailbox.calendar.ParsedDuration;
import com.zimbra.cs.mailbox.calendar.RecurId;
import com.zimbra.cs.mailbox.calendar.Recurrence;
import com.zimbra.cs.mailbox.calendar.Recurrence.IRecurrence;
import com.zimbra.cs.mailbox.calendar.TimeZoneMap;
import com.zimbra.cs.mailbox.calendar.ZAttendee;
import com.zimbra.cs.mailbox.calendar.ZCalendar.ICalTok;
import com.zimbra.cs.mailbox.calendar.ZOrganizer;
import com.zimbra.cs.mailbox.calendar.ZRecur;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.util.AccountUtil;
import com.zimbra.cs.util.AccountUtil.AccountAddressMatcher;
import com.zimbra.zimbrasync.data.CalendarAppData.AttendeeStatus;
import com.zimbra.zimbrasync.data.CalendarAppData.AttendeeType;
import com.zimbra.zimbrasync.data.CalendarAppData.EventData;
import com.zimbra.zimbrasync.data.CalendarAppData.EventException;
import com.zimbra.zimbrasync.data.CalendarAppData.EventRecurrence;
import com.zimbra.zimbrasync.data.CalendarAppData.EventUser;
import com.zimbra.zimbrasync.util.CalendarSyncUtil;
import com.zimbra.zimbrasync.util.SyncUtil;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class MailboxCalendarAppData extends MailboxAppData {
    static final int PRIORITY_LOW = 0;
    static final int PRIORITY_NORMAL = 1;
    static final int PRIORITY_HIGH = 2;

    public static class MailboxEventRecurrence extends EventRecurrence {

        private static int dayOfWeek(List<ZRecur.ZWeekDayNum> byDayList) {
            if (byDayList == null || byDayList.isEmpty())
                return 0;
            int dayOfWeek = 0;
            for (ZRecur.ZWeekDayNum wd : byDayList) {
                dayOfWeek |= dayOfWeek(wd.mDay.getCalendarDay());
            }
            return dayOfWeek;
        }
        
        private static int dayOfWeek(int calendarDay) {
            return 1 << (calendarDay - Calendar.SUNDAY);
        }
        
        private static int weekOfMonth(ZRecur recur) {
            int offset = SyncUtil.weekOfMonth(recur);
            if (offset < 0) {
                offset += 6; //ZimbraSync uses 5 to mean last; this formular is of course flawed
            }
            return offset;
        }
        
        private static String makeWeekDayList(int wkdays) {
            StringBuffer listBuf = new StringBuffer();
            for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; ++i) {
                if ((wkdays & (1 << (i - Calendar.SUNDAY))) != 0) {
                    if (listBuf.length() > 0) {
                        listBuf.append(',');
                    }
                    switch (i) {
                        case Calendar.SUNDAY: listBuf.append("SU"); break;
                        case Calendar.MONDAY: listBuf.append("MO"); break;
                        case Calendar.TUESDAY: listBuf.append("TU"); break;
                        case Calendar.WEDNESDAY: listBuf.append("WE"); break;
                        case Calendar.THURSDAY: listBuf.append("TH"); break;
                        case Calendar.FRIDAY: listBuf.append("FR"); break;
                        case Calendar.SATURDAY: listBuf.append("SA"); break;
                    }
                }
            }
            return listBuf.toString();
        }
        
        private static ZRecur toZRecur(Mailbox mbox, EventRecurrence evRecur, boolean isTask,
                ICalTimeZone tz, ICalTimeZone accountTz) throws ServiceException {
            StringBuffer recurBuf = new StringBuffer(100);
            switch (evRecur.recurType) {
            case RECURRENCE_TYPE_DAILY:
                recurBuf.append("FREQ=").append(ZRecur.Frequency.DAILY);
                break;
            case RECURRENCE_TYPE_WEEKLY:
                recurBuf.append("FREQ=").append(ZRecur.Frequency.WEEKLY);
                break;
            case RECURRENCE_TYPE_MONTHLY_BY_MONTHDAY:
            case RECURRENCE_TYPE_MONTHLY_BY_WEEKDAY:
                recurBuf.append("FREQ=").append(ZRecur.Frequency.MONTHLY);
                break;
            case RECURRENCE_TYPE_YEARLY_BY_MONTHDAY:
            case RECURRENCE_TYPE_YEARLY_BY_WEEKDAY:
                recurBuf.append("FREQ=").append(ZRecur.Frequency.YEARLY);
                break;
            default:
                assert false;
                break;
            }
            
            if (evRecur.interval > 0) {
                recurBuf.append(";INTERVAL=").append(evRecur.interval);
            }
            if (evRecur.count > 0) {
                assert evRecur.until == null;
                recurBuf.append(";COUNT=").append(evRecur.count);
            }
            if (evRecur.until != null) {
                assert evRecur.count == 0;
                
                //NOTE: we need to fix UNTIL for bug #6659
                //Basically the client always uses this kind of time format for UNTIL: 20061218T080000Z
                //This will mean to make the last instance on or before 12/18 in PST (080000Z) is midnight in PST.
                //However if we use UNTIL=20061218T080000Z to set the RRULE, the server will not include 12/18 as
                //the last day because the last occurrence would happen after midnight.
                //The fix is to remove the time part and make it date only.  But in order to calculated the date
                //we also need to consider the client's timezone and client's timezone maybe different from user
                //account's.
                try {
                    ParsedDateTime pdt = ParsedDateTime.parse(evRecur.until, new TimeZoneMap(accountTz), tz, accountTz);
                    evRecur.until = SyncUtil.getFormattedLocalDate(pdt.getUtcTime(), tz);
                } catch (ParseException x) {}
                recurBuf.append(";UNTIL=").append(evRecur.until);
            }
            if (evRecur.dayOfWeek > 0) {
                assert evRecur.dayOfMonth == 0;
                if (evRecur.weekOfMonth == 0) {
                    recurBuf.append(";BYDAY=").append(makeWeekDayList(evRecur.dayOfWeek));
                } else {
                    assert evRecur.recurType == RECURRENCE_TYPE_MONTHLY_BY_WEEKDAY ||
                           evRecur.recurType == RECURRENCE_TYPE_YEARLY_BY_WEEKDAY;
                    String weekdayStr = makeWeekDayList(evRecur.dayOfWeek);
                    assert weekdayStr.length() == 2;
                    recurBuf.append(";BYDAY=").append(evRecur.weekOfMonth == 5 ? -1 : evRecur.weekOfMonth).append(weekdayStr);
                }
            }
            if (evRecur.dayOfMonth > 0) {
                assert evRecur.dayOfWeek == 0;
                recurBuf.append(";BYMONTHDAY=").append(evRecur.dayOfMonth);
            }
            if (evRecur.monthOfYear > 0) {
                recurBuf.append(";BYMONTH=").append(evRecur.monthOfYear);
            }
            
            return new ZRecur(recurBuf.toString(), new TimeZoneMap(accountTz));
        }
        
        public MailboxEventRecurrence(ZRecur recur, Calendar dtStart, boolean isTask) throws ServiceException {
            switch (recur.getFrequency()) {
            case DAILY:
                recurType = RECURRENCE_TYPE_DAILY;
                dayOfWeek = dayOfWeek(recur.getByDayList());
                break;
            case WEEKLY:
                recurType = RECURRENCE_TYPE_WEEKLY;
                dayOfWeek = dayOfWeek(recur.getByDayList());
                if (dayOfWeek == 0)
                    dayOfWeek = dayOfWeek(dtStart.get(Calendar.DAY_OF_WEEK));
                break;
            case MONTHLY: {
                List<Integer> monthdayList = recur.getByMonthDayList();
                if (monthdayList != null && !monthdayList.isEmpty()) {
                    assert monthdayList.size() == 1; //can only handle one
                    recurType = RECURRENCE_TYPE_MONTHLY_BY_MONTHDAY;
                    dayOfMonth = monthdayList.get(0).intValue();
                } else { //could be by weekday
                    List<ZRecur.ZWeekDayNum> weekdayList = recur.getByDayList();
                    if (weekdayList != null && !weekdayList.isEmpty()) {
                        recurType = RECURRENCE_TYPE_MONTHLY_BY_WEEKDAY;
                        dayOfWeek = dayOfWeek(weekdayList);
                        weekOfMonth = weekOfMonth(recur);
                    } else { //assume by month day, use DTSTART to figure out which one
                        recurType = RECURRENCE_TYPE_MONTHLY_BY_MONTHDAY;
                        dayOfMonth = dtStart.get(Calendar.DAY_OF_MONTH);
                    }
                }
                break;
            }
            case YEARLY: {
                List<Integer> monthdayList = recur.getByMonthDayList();
                if (monthdayList != null && !monthdayList.isEmpty()) {
                    assert monthdayList.size() == 1; //can only handle one
                    recurType = RECURRENCE_TYPE_YEARLY_BY_MONTHDAY;
                    dayOfMonth = monthdayList.get(0).intValue();
                } else { //could be by weekday
                    List<ZRecur.ZWeekDayNum> weekdayList = recur.getByDayList();
                    if (weekdayList != null && !weekdayList.isEmpty()) {
                        assert weekdayList.size() == 1; //can only handle one
                        recurType = RECURRENCE_TYPE_YEARLY_BY_WEEKDAY;
                        dayOfWeek = dayOfWeek(weekdayList);
                        weekOfMonth = weekOfMonth(recur);
                    } else { //assume by month day, use DTSTART to figure out which month day
                        recurType = RECURRENCE_TYPE_YEARLY_BY_MONTHDAY;
                        dayOfMonth = dtStart.get(Calendar.DAY_OF_MONTH);
                    }
                }
                List<Integer> monthList = recur.getByMonthList();
                if (monthList != null && !monthList.isEmpty()) {
                    assert monthList.size() == 1; //can only handle one
                    monthOfYear = monthList.get(0).intValue();
                } else { //use DTSTART to figure out which month
                    monthOfYear = dtStart.get(Calendar.MONTH) + 1;
                }
                break;
            }
            default:
                assert false;
            }

            interval = recur.getInterval() > 1 ? recur.getInterval() : 1;
            count = recur.getCount();
            if (recur.getUntil() != null) {
                //TODO: we shouldn't interpret the until Date as UTC
                until = SyncUtil.getFormattedUtcDateTime(recur.getUntil().getUtcTime(), isTask);
            }
            
            if (isTask)
                recurStart = SyncUtil.getFormattedUtcDateTime(dtStart.getTimeInMillis(), true);
        }
    }
    
    public static class MailboxEventData extends EventData {
        
        private static Invite eventDataToInvite(Mailbox mbox, EventData data, boolean isTask, ParsedDateTime dtStart, ParsedDateTime dtEnd,
                ICalTimeZone tz, TimeZoneMap tzMap, String uid, RecurId rid, Invite oldInv)
                throws Exception {
            String status = null;
            if (isTask) {
                status = data.isComplete ? IcalXmlStrMap.STATUS_COMPLETED : IcalXmlStrMap.STATUS_IN_PROCESS;
            } else {
                switch (data.meetingStatus) {
                case CalendarAppData.MEETING_STATUS_SELF_ONLY:
                case CalendarAppData.MEETING_STATUS_CONFIRMED:
                case CalendarAppData.MEETING_STATUS_RECEIVED:
                    status = IcalXmlStrMap.STATUS_CONFIRMED;
                    break;
                case CalendarAppData.MEETING_STATUS_CANCELLED:
                    status = IcalXmlStrMap.STATUS_CANCELLED;
                    break;
                default:
                    status = IcalXmlStrMap.STATUS_CONFIRMED;
                    break;
                }
            }
    
            String freeBusy = IcalXmlStrMap.FBTYPE_BUSY_TENTATIVE;
            switch (data.busyStatus) {
            case CalendarAppData.BUSY_STATUS_FREE:
                freeBusy = IcalXmlStrMap.FBTYPE_FREE;
                break;
            case CalendarAppData.BUSY_STATUS_TENTATIVE:
                freeBusy = IcalXmlStrMap.FBTYPE_BUSY_TENTATIVE;
                break;
            case CalendarAppData.BUSY_STATUS_BUSY:
                freeBusy = IcalXmlStrMap.FBTYPE_BUSY;
                break;
            case CalendarAppData.BUSY_STATUS_OUT_OF_OFFICE:
                freeBusy = IcalXmlStrMap.FBTYPE_BUSY_UNAVAILABLE;
                break;
            default:
                break;
            }
            if (isTask)
                freeBusy = IcalXmlStrMap.FBTYPE_FREE;
    
            boolean isOrganizer = false;
            ZOrganizer organizer = null;
            Account acct = mbox.getAccount();
            AccountAddressMatcher acctMatcher = new AccountAddressMatcher(acct);
            if (data.organizer != null) {
                isOrganizer = acctMatcher.matches(data.organizer.email);
                organizer = new ZOrganizer(data.organizer.email, data.organizer.name);
            } else if (oldInv != null) { //some phones (e.g. wm6) can omit organizer when change an existing appointment
                organizer = oldInv.getOrganizer();
                isOrganizer = oldInv.isOrganizer();
            } else { //null to mean self
                String addr = acct.getName();
                isOrganizer = true;
                organizer = new ZOrganizer(addr, addr); //use addr also as cn
            }
    
            List<ZAttendee> attendees = new ArrayList<ZAttendee>();
            EventUser deviceUser = null;
            
            //If the user is an attendee; user cannot change any other attendee except self.
            //iPhone doesn't send Attendee status and type while changing an existing appointment.
            //In that case use the status and type from the existing invite...
            if (oldInv != null && !oldInv.isOrganizer()) {
                ZAttendee self = null;
                for (ZAttendee zattendee : oldInv.getAttendees()) {
                    if (acctMatcher.matches(zattendee.getAddress()))
                        self = zattendee;
                    else
                        attendees.add(zattendee);
                }
                if (data.attendees != null) {
                    for (EventUser user : data.attendees) {
                        if (acctMatcher.matches(user.email)) {
                            deviceUser = user;
                            if (user.status != AttendeeStatus.RESPONSE_UNKNOWN)
                                self = getZAttendee(user);
                            break; //found device user
                        }
                    }
                }
                if (self != null)
                    attendees.add(self);
            } else if (data.attendees != null) {
                for (Iterator<EventUser> i = data.attendees.iterator(); i.hasNext();) {
                    EventUser attendee = i.next();
                    attendees.add(getZAttendee(attendee));
                }
            } else if (rid != null && oldInv != null) { //some phones (e.g. wm6) doesn't send attendees in exceptions
                attendees = oldInv.getAttendees();
            }
            
            String partStat = IcalXmlStrMap.PARTSTAT_TENTATIVE;
            if (isOrganizer)
                partStat = IcalXmlStrMap.PARTSTAT_ACCEPTED;
            else if (oldInv != null) { //attendee
                if (deviceUser == null)
                    partStat = oldInv.getPartStat();
                else if (deviceUser != null && deviceUser.status != AttendeeStatus.RESPONSE_UNKNOWN) { 
                    if (deviceUser.status == AttendeeStatus.ACCEPT)
                        partStat = IcalXmlStrMap.PARTSTAT_ACCEPTED;
                    else if (deviceUser.status == AttendeeStatus.DECLINE)
                        partStat = IcalXmlStrMap.PARTSTAT_DECLINED;
                    else if (deviceUser.status == AttendeeStatus.NOT_RESPONDED)
                        partStat = IcalXmlStrMap.PARTSTAT_NEEDS_ACTION;
                }
                //devices using protocol 2.5 would have PartStat tentative while 
                //creating local exception as they don't send attendee status...
            }
    
            long dtStamp = 0;
            if (data.timeStamp != null) {
                dtStamp = ParsedDateTime.parse(data.timeStamp, tzMap, ICalTimeZone.getUTC(), tz).getUtcTime();
            }
    
            long completed = (data.timeCompleted != null ? ParsedDateTime.parseUtcOnly(data.timeCompleted).getUtcTime() : 0);
    
            int priority = 0;
            switch (data.importance) {
            case PRIORITY_HIGH:
                priority = 1;
                break;
            case PRIORITY_LOW:
                priority = 9;
                break;
            default:
                break;
            }
    
            String classProp = null;
            switch (data.sensitivity) {
            case CalendarAppData.SENSITIVITY_CONFIDENTIAL:
                classProp = IcalXmlStrMap.CLASS_CONFIDENTIAL;
                break;
            case CalendarAppData.SENSITIVITY_PRIVATE:
            case CalendarAppData.SENSITIVITY_PERSONAL:
                classProp = IcalXmlStrMap.CLASS_PRIVATE;
                break;
            default:
                classProp = IcalXmlStrMap.CLASS_PUBLIC;
            }
    
            if (data.body == null && oldInv != null) { //some phones (e.g. wm6) can omit body when change existing appointment
                data.body = SyncUtil.getNotesFromTextPart(oldInv.getDescription());
            }
    
            String method = ICalTok.REQUEST.toString();
            Invite invite = Invite.createInvite(
                    mbox.getId(),
                    isTask ? MailItem.TYPE_TASK : MailItem.TYPE_APPOINTMENT,
                    method,
                    tzMap,
                    uid,
                    status,
                    Integer.toString(priority),  // priority
                    (data.isComplete ? "100" : null),  // percent complete (for VTODOs)
                    completed,     // completed date (for VTODO)
                    freeBusy,
                    IcalXmlStrMap.TRANSP_OPAQUE, //we don't have a notion of transparency
                    classProp,
                    data.allDayEvent == 1,
                    dtStart,
                    dtEnd,
                    null, //duration not needed with endTime 
                    rid, //RecurId
                    null, //Recurrence.IRecurrence
                    isOrganizer,
                    organizer,
                    attendees,
                    data.subject,
                    data.location,
                    data.body != null ? data.body : "",
                    null,
                    null, // comments
                    null, // categories
                    null, // contacts
                    null, // geo
                    null, // url
                    dtStamp,
                    dtStamp,
                    oldInv == null ? 0 : oldInv.getSeqNo(), //sequence number
                    partStat,
                    true, // RSVP
                    true); //sentByMe
    
            if (data.reminder > 0) { //web ui uses 0 to mean disabled
                invite.addAlarm(Alarm.fromSimpleReminder(data.reminder));
            } else if (data.reminderTime != null) {
                invite.addAlarm(Alarm.fromSimpleTime(ParsedDateTime.parseUtcOnly(data.reminderTime)));
            }
    
            return invite;
        }
        
        public MailboxEventData(OperationContext octxt, Invite invite, ICalTimeZone tz, boolean isTask, EventData main, int defaultReminder, ProtocolVersion protocolVersion) throws ServiceException {
            this(octxt, invite, tz, isTask, main, defaultReminder, isTask, protocolVersion);
        }
        
                
        public MailboxEventData(OperationContext octxt, CalendarItem calItem, Invite invite, int defaultReminder, ProtocolVersion protocolVersion) throws ServiceException {
            this(octxt, invite, null, false, null, defaultReminder, true, protocolVersion);
            
            //skip the event to be synced on the device for orphan invite
            if (invite != null && invite.hasRecurId() && calItem != null && !calItem.isRecurring())
                throw SyncServiceException.CANNOT_PERMIT();
        }
        
        public MailboxEventData(OperationContext octxt, Invite invite, ICalTimeZone tz, boolean isTask, EventData main, int defaultReminder, boolean useDelim, ProtocolVersion protocolVersion) throws ServiceException {
            this.protocolVersion = protocolVersion;
            uid = invite.getUid();
            invId = invite.getMailItemId();
            componentNum = invite.getComponentNum();
            
            timeStamp = SyncUtil.getFormattedUtcDateTime(invite.getDTStamp(), useDelim);
            ParsedDateTime invStartTime = invite.getStartTime();
            if (invStartTime != null) {
                startTimeUtc = SyncUtil.getFormattedUtcDateTime(invStartTime.getDate().getTime(), useDelim);
                if (tz != null)
                    startDate = SyncUtil.getFormattedLocalDateTime(invStartTime.getUtcTime(), tz, useDelim) + "Z";
            }
            ParsedDateTime invEndTime = invite.getEffectiveEndTime();
            if (invEndTime != null) {
                endTimeUtc = SyncUtil.getFormattedUtcDateTime(invEndTime.getDate().getTime(), useDelim);
                if (tz != null)
                    endDate = SyncUtil.getFormattedLocalDateTime(invEndTime.getUtcTime(), tz, useDelim) + "Z";
            }
            
            if (isTask) {
                isComplete = invite.getStatus().equals(IcalXmlStrMap.STATUS_COMPLETED);
                if (invite.getCompleted() > 0)
                    timeCompleted = SyncUtil.getFormattedUtcDateTime(invite.getCompleted(), useDelim);
                else if (isComplete)
                    timeCompleted = SyncUtil.getFormattedUtcDateTime(invite.getDTStamp(), useDelim);
            }
            
            subject = invite.getName();
            location = invite.getLocation();
            body = SyncUtil.getNotesFromTextPart(invite.getDescription());
            
            String userPartStat = null;
            boolean isOrganizer = false;
            Set<String> acctAddrs = AccountUtil.getEmailAddresses(octxt.getAuthenticatedUser());

            if (invite.hasOrganizer()) {
                ZOrganizer zor = invite.getOrganizer();
                setOrganizerName(zor.getCn());
                setOrganizerEmail(zor.getAddress());
                isOrganizer = acctAddrs.contains(zor.getAddress().toLowerCase());
            }
            
            boolean isAttendee = false;
            List<ZAttendee> zas = invite.getAttendees();
            for (Iterator<ZAttendee> it = zas.iterator(); it.hasNext();) {
                ZAttendee zat = it.next();
                EventUser user = new EventUser();
                user.name = zat.getCn();
                user.email = zat.getAddress();
                
                if (zat.getRsvp() != null)
                    user.rsvp = zat.getRsvp().booleanValue();
                
                // Bug: 50656
                // Attendee's address in meta data was null. (For this bug, the appointment 
                // was really old and was set by some other client like Outlook or, wm)
                // We should add a null pointer check for the attendee's address and
                // if the address is null, skip the attendee from the attendee list.                
                if (user.email != null && user.email.length() > 0) {
                    
                    if (!isOrganizer && acctAddrs.contains(user.email)) {
                        isAttendee = true;

                        if (userPartStat == null)
                            userPartStat = zat.getPartStat();
                        
                        if (zat.getRsvp() != null)
                            rsvp = zat.getRsvp().booleanValue() == true ? 1 : 0; 
                    }
                    
                    if (protocolVersion.getMajor() >= 12) {
                        String role = zat.getRole();
                        String cuType = zat.getCUType();
                        String partStat = zat.getPartStat();
                        
                        if (role.equals(IcalXmlStrMap.ROLE_REQUIRED))
                            user.type = CalendarAppData.AttendeeType.REQUIRED;
                        else if (role.equals(IcalXmlStrMap.ROLE_OPT_PARTICIPANT))
                            user.type = CalendarAppData.AttendeeType.OPTIONAL;
                        
                        if (cuType.equals(IcalXmlStrMap.CUTYPE_RESOURCE) || cuType.equals(IcalXmlStrMap.CUTYPE_ROOM))
                            user.type = CalendarAppData.AttendeeType.RESOURCE;
                        
                        if (partStat.equals(IcalXmlStrMap.PARTSTAT_ACCEPTED))
                            user.status = CalendarAppData.AttendeeStatus.ACCEPT;
                        else if (partStat.equals(IcalXmlStrMap.PARTSTAT_DECLINED))
                            user.status = CalendarAppData.AttendeeStatus.DECLINE;
                        else if (partStat.equals(IcalXmlStrMap.PARTSTAT_TENTATIVE))
                            user.status = CalendarAppData.AttendeeStatus.TENTATIVE;
                        else if (partStat.equals(IcalXmlStrMap.PARTSTAT_NEEDS_ACTION))
                            user.status = CalendarAppData.AttendeeStatus.NOT_RESPONDED;
                        else if (partStat.equals(IcalXmlStrMap.PARTSTAT_IN_PROCESS))
                            user.status = CalendarAppData.AttendeeStatus.NOT_RESPONDED;
                        else
                            user.status = CalendarAppData.AttendeeStatus.RESPONSE_UNKNOWN;         
                    }
                    
                    addAttendee(user);
                }
            }
            
            if (!isOrganizer && !isAttendee) {
                //invitation might have been forwarded to the user by somebody else; In this case set the rsvp to true
                ZimbraLog.sync.debug("User is not an organizer or, attendee, setting the rsvp to true");
                rsvp = 1;
            }
            
            allDayEvent = invite.isAllDayEvent() ? 1 : 0;

            if (invite.isHighPriority())
                importance = PRIORITY_HIGH;
            else if (invite.isLowPriority())
                importance = PRIORITY_LOW;
            else
                importance = PRIORITY_NORMAL;

            if (invite.getClassProp().equals(IcalXmlStrMap.CLASS_CONFIDENTIAL)) {
                sensitivity = CalendarAppData.SENSITIVITY_CONFIDENTIAL;
            } else if (invite.getClassProp().equals(IcalXmlStrMap.CLASS_PRIVATE)) {
                sensitivity = CalendarAppData.SENSITIVITY_PRIVATE;
            } else {
                sensitivity = CalendarAppData.SENSITIVITY_NORMAL;
            }
            
            // If the user is the organizer; then set the busy status from invite 
            // free busy status. For attendee, the busy status is mapped from
            // attendee's part status.
            if (isOrganizer) {
                String freeBusyActual = invite.getFreeBusyActual();
                if (freeBusyActual.equals(IcalXmlStrMap.FBTYPE_FREE)) {
                    busyStatus = CalendarAppData.BUSY_STATUS_FREE;
                } else if (freeBusyActual.equals(IcalXmlStrMap.FBTYPE_BUSY_TENTATIVE)) {
                    busyStatus = CalendarAppData.BUSY_STATUS_TENTATIVE;
                } else if (freeBusyActual.equals(IcalXmlStrMap.FBTYPE_BUSY_UNAVAILABLE)) {
                    busyStatus = CalendarAppData.BUSY_STATUS_OUT_OF_OFFICE;
                } else {
                    busyStatus = CalendarAppData.BUSY_STATUS_BUSY;
                }
            } else {
                busyStatus = CalendarAppData.BUSY_STATUS_BUSY;
                if (userPartStat != null && userPartStat.equals(IcalXmlStrMap.PARTSTAT_TENTATIVE))
                    busyStatus = CalendarAppData.BUSY_STATUS_TENTATIVE; 
            }
            
            if (attendees == null || attendees.size() == 0) {
                meetingStatus = CalendarAppData.MEETING_STATUS_SELF_ONLY;
            } else if (invite.getStatus().equals(IcalXmlStrMap.STATUS_CONFIRMED)) {
                meetingStatus = CalendarAppData.MEETING_STATUS_CONFIRMED;
                // if the user is not the organizer then, must set the status to received!!
                // This will suppress all attendee's status on device.
                if (!isOrganizer)
                    meetingStatus = CalendarAppData.MEETING_STATUS_RECEIVED;
            } else if (invite.getStatus().equals(IcalXmlStrMap.STATUS_CANCELLED)) {
                meetingStatus = CalendarAppData.MEETING_STATUS_CANCELLED;
            } else {
                meetingStatus = CalendarAppData.MEETING_STATUS_RECEIVED;
            }
            
            if (main == null) { //this is main itself, and only main can have recur
                ZRecur zr = SyncUtil.getRecur(invite);
                if (zr != null)
                    evRecur = new MailboxEventRecurrence(zr, invite.getStartTime().getCalendarCopy(), isTask);
            }
            if (invite.getRecurId() != null) {
                recurIdUtc = invite.getRecurId().getDt().getUtcTime();
                recurId = SyncUtil.getFormattedUtcDateTime(recurIdUtc, true);
            }
            //mobile only supports a single reminder in terms of number of minutes before meeting starts
            //we'll just handle the simplest case, which is to check the first Alarm on the inivte.
            Iterator<Alarm> ai = invite.alarmsIterator();
            if (ai.hasNext()) {
                Alarm alarm = ai.next();
                if (!invite.isTodo()) {
                    setReminder(invite, alarm);
                } else {
                    //DTSTART is not required for VTODO, don't set reminder if it's not there.
                    if (invite.getStartTime() != null) {
                        setReminder(invite, alarm);
                    }
                }
            }
            if (main != null) //this is an exception
                normalize(main);
        }

        private void setReminder(Invite invite, Alarm alarm) {
            long startTime = 0;
            if (invite.getStartTime() != null) {
                startTime = invite.getStartTime().getUtcTime();
            }
            long endTime = 0;
            if (invite.getEffectiveEndTime() != null) {
                endTime = invite.getEffectiveEndTime().getUtcTime();
            }
            long gooff = alarm.getTriggerTime(startTime, endTime);
            if (gooff <= startTime) {
                reminder = (int)((startTime - gooff) / 1000 / 60);
                reminderTime = SyncUtil.getFormattedUtcDateTime(gooff, true);
                isReminderSet = true;
            }
        }
    }

    private CalendarAppData appData;
    private boolean isDeclined;
    private ProtocolVersion protocolVersion = new ProtocolVersion("2.5");
    
    //this is used by eas server
    public MailboxCalendarAppData() {
        appData = new CalendarAppData();
    }
    
    //this is used by eas client during execution
    public MailboxCalendarAppData(CalendarAppData appData) {
        this.appData = appData;
    }
    
    public MailboxCalendarAppData(OperationContext octxt, CalendarItem calItem, ProtocolVersion version) throws ServiceException, IOException {
        protocolVersion = version;
        Invite mainInv = getDefaultInvite(calItem);
        if (mainInv == null) {
            throw ServiceException.FAILURE("Missing default invite", null);
        }
        if (IcalXmlStrMap.PARTSTAT_DECLINED.equals(mainInv.getEffectivePartStat())) {
            isDeclined = true;
            return;
        }
        
        ParsedDateTime pdt = mainInv.getStartTime();
        ICalTimeZone tz = null;
        if (pdt != null)
            tz = mainInv.getStartTime().getTimeZone();
        
        if (tz == null)
            tz = mainInv.getTimeZoneMap().getLocalTimeZone();
        if (tz == null)
            tz = ICalTimeZone.getAccountTimeZone(calItem.getAccount());
        int defaultReminder = calItem.getMailbox().getAccount().getIntAttr("zimbraPrefCalendarApptReminderWarningTime", 15);
        EventData main = new MailboxEventData(octxt, mainInv, tz, calItem instanceof Task, null, defaultReminder, protocolVersion);
        
        appData = new CalendarAppData(main);
        appData.uid = calItem.getUid();
        if (appData.uid == null || appData.uid.length() == 0) {
            throw ServiceException.FAILURE("Invalid calendar uid=" + appData.uid, null);
        }
        appData.timezone = SyncUtil.encodeTimezoneInformation(tz);
        
        if (calItem.isRecurring()) {
            assert appData.main.evRecur != null;

            Recurrence.IRecurrence r = calItem.getRecurrence();
            assert r.getType() == Recurrence.TYPE_RECURRENCE;
            Recurrence.RecurrenceRule masterRule = (Recurrence.RecurrenceRule)r;
        
            for (Iterator<IRecurrence> iter = masterRule.subRulesIterator(); iter != null && iter.hasNext();) {
                r = iter.next();
                
                switch (r.getType()) { 
                case Recurrence.TYPE_SINGLE_DATES:
                    Recurrence.SingleDates sd = (Recurrence.SingleDates) r;
                    //Express each EXDATE as an exception to exclude an instance
                    List<Instance> instances = Recurrence.expandInstances(sd, calItem.getId(), 0, Long.MAX_VALUE);
                    for (Instance inst : instances) {
                        EventException evx = new EventException(appData.main);
                        evx.exceptionStartTimeUtc = inst.getStart();
                        evx.exceptionStartTime = SyncUtil.getFormattedUtcDateTime(evx.exceptionStartTimeUtc);
                        evx.isDeleted = true;
                        addException(evx);
                    }
                    break;
                case Recurrence.TYPE_REPEATING:
                    //Recurrence.SimpleRepeatingRule srr = (Recurrence.SimpleRepeatingRule)r;
                    assert false; //we don't support EXRULE
                    break;
                default:
                    assert false; //can't be anything else
                    break;
                }
            }
            
            //Now deal with real exceptions
            for (Iterator<Recurrence.IException> iter = masterRule.exceptionsIter(); iter.hasNext();) {
                Recurrence.IException x = iter.next();
                EventException evx = new EventException(appData.main);
                evx.exceptionStartTimeUtc = x.getRecurId().getDt().getDate().getTime();
                evx.exceptionStartTime = SyncUtil.getFormattedUtcDateTime(evx.exceptionStartTimeUtc);
                
                switch (x.getType()) {
                case Recurrence.TYPE_CANCELLATION:
                    evx.isDeleted = true;
                    addException(evx);
                    break;
                case Recurrence.TYPE_EXCEPTION:
                    Recurrence.ExceptionRule exception = (Recurrence.ExceptionRule)x; //we don't do RRULE, RDATE, EXRULE nad EXDATE here
                    InviteInfo invId = exception.getInviteInfo();
                    Invite invite = calItem.getInvite(invId.getRecurrenceId());
                    if (invite.getEffectivePartStat().equals(IcalXmlStrMap.PARTSTAT_DECLINED)) {
                        //treat a declined exception as cancellation
                        evx.isDeleted = true;
                    } else {
                        evx.instance = new MailboxEventData(octxt, invite, tz, calItem instanceof Task, appData.main, defaultReminder, protocolVersion);
                    }
                    addException(evx);
                    break;
                default:
                    ZimbraLog.sync.warn("Unexpected calendar exception type=" + x.getType() + "; exception ignored");
                    break;
                }
            }
        }
    }

    public static boolean isOrphan(CalendarItem calItem) throws ServiceException {
        Invite defaultInvite = calItem.getDefaultInviteOrNull();
        if (defaultInvite == null)
            throw ServiceException.FAILURE("Missing default invite", null);
        
        if (defaultInvite.hasRecurId() && !calItem.isRecurring())
            return true;
        
        return false;
    }
    
    public static Invite getDefaultInvite(CalendarItem calItem) throws ServiceException {
        Invite defaultInvite = calItem.getDefaultInviteOrNull();
        if (defaultInvite == null)
            throw ServiceException.FAILURE("Missing default invite", null);
        
        //Bug: 59646
        //item is an orphan instance!!
        //In the case where the item has more than one exception; we need to pick the one 
        //which is priority to the user as due to certain limitations we cannot fetch 
        //all the exception instances of such item; 
        if (defaultInvite.hasRecurId() && !calItem.isRecurring()) {
            
            Invite[] invs = calItem.getInvites();
            if (invs.length > 1) {
                //extract the most current orphan instance!!
                // 1. If the current time is within the invite start and end time, then pick that one!!
                // 2. Extract the most recent one in the future
                // 3. Extract the most recent one in the past
                long currTimeMillis = System.currentTimeMillis();
                long minPast = 0;
                long minFuture = 0;
                Invite mostRecentFuture = null;
                Invite mostRecentPast = null;
                
                for (Invite inv : invs) {
                    RecurId rid = inv.getRecurId();
                    if (rid != null && !inv.isCancel()) {
                        long startTime = inv.getStartTime().getUtcTime();
                        long endTime = inv.getEndTime().getUtcTime();
                        
                        if (startTime <= currTimeMillis && endTime > currTimeMillis)
                            return inv;
                        
                        if (startTime < currTimeMillis) {
                            //most recent past case!!
                            long diff = currTimeMillis - startTime;
                            assert diff != 0;
                            if (minPast == 0) {
                                minPast = diff;
                                mostRecentPast = inv;
                            } else if (diff < minPast) {
                                minPast = diff;
                                mostRecentPast = inv;
                            }
                        } else {
                            //most recent future case!!
                            long diff = startTime - currTimeMillis;
                            assert diff != 0;
                            if (minFuture == 0) {
                                minFuture = diff;
                                mostRecentFuture = inv;
                            } else if (diff < minFuture) {
                                minFuture = diff;
                                mostRecentFuture = inv;
                            }
                        }
                    }
                }
                
                //This means that all the instances are canceled; return the default invite.
                if (mostRecentFuture == null && mostRecentPast == null)
                    return defaultInvite;
                
                if (mostRecentFuture == null)
                    return mostRecentPast;
                else
                    return mostRecentFuture;
                
                //we can apply special rules on choosing the recent past or, future invite here!!
            }
        }
        return defaultInvite;
    }
    
    void addException(EventException evx) {
        if (appData.exceptions == null) {
            appData.exceptions = new TreeSet<EventException>(new Comparator<EventException>() {
                public int compare(EventException o1, EventException o2) {
                    long diff = o1.exceptionStartTimeUtc - o2.exceptionStartTimeUtc;
                    return diff == 0 ? 0 : (diff > 0 ? 1 : -1);
                }
            });
        }
        if (evx.exceptionStartTimeUtc == 0) {
            try {
                evx.exceptionStartTimeUtc = ParsedDateTime.parseUtcOnly(evx.exceptionStartTime).getUtcTime();
            } catch (ParseException e) {
                ZimbraLog.sync.warn(e);
            }
        }
        appData.exceptions.add(evx);
    }

    @Override
    AppData getAppData() {
        return appData;
    }
    
    public CalendarAppData getCalendarAppData() {
        return appData;
    }
    
    public void parse(BinaryParser parser, boolean isTask) throws BinaryCodecException, IOException {
        if (isTask)
            appData.parseTask(parser);
        else
            appData.parse(parser);
    }
    
    public void encode(BinarySerializer serializer, boolean isTask, boolean useCategories, boolean useRtf, int truncationSize)
            throws BinaryCodecException, IOException {
        if (isTask)
            appData.encodeTask(serializer, useCategories, useRtf, truncationSize);
        else
            appData.encode(serializer, useCategories, useRtf, truncationSize);
    }
    
    public boolean isDeclined() {
        return isDeclined;
    }
    
    public boolean isEquivalent(MailboxCalendarAppData mboxAppData) {
        return appData.isEquivalent(mboxAppData.appData);
    }

    public CalendarItem setCalendarItem(Mailbox mbox, OperationContext octxt, int folderId, CalendarItem oldCalItem, boolean isTask)
            throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        ICalTimeZone accountTz = ICalTimeZone.getAccountTimeZone(mbox.getAccount());
        TimeZoneMap tzMap = new TimeZoneMap(accountTz);
        ICalTimeZone tz = null;
        if (appData.timezone != null) {
            try {
                tz = SyncUtil.decodeTimezoneInformation(appData.timezone);
                tzMap.add(tz);
            } catch (Exception x) {
                if (x instanceof SyncServiceException 
                        && StringUtil.equal(SyncServiceException.UNEXPECTED_DATA,
                                ((SyncServiceException) x).getCode())) {
                    ZimbraLog.sync.debug("[TIME ZONE DECODING] unexpected data: %s, time zone string: [%s]",
                            ((SyncServiceException) x).getMessage(), appData.timezone);
                } else {
                    ZimbraLog.sync.warn("[TIME ZONE DECODING] Can't parse time zone string: [%s]", appData.timezone, x);    
                }
                tz = accountTz;
            }
        } else {
            tz = accountTz;
        }

        ParsedDateTime dtStart = null;
        ParsedDateTime dtEnd = null;
        if (isTask) {
            dtStart = (appData.main.startTimeUtc != null) ? ParsedDateTime.parseUtcOnly(appData.main.startTimeUtc) : null;
            dtEnd = (appData.main.endTimeUtc != null) ? ParsedDateTime.parseUtcOnly(appData.main.endTimeUtc) : null;
        } else {
            dtStart = SyncUtil.localDateTimeFromUtcString(appData.main.startTimeUtc, tz, tzMap);
            dtEnd = SyncUtil.localDateTimeFromUtcString(appData.main.endTimeUtc, tz, tzMap);
            if (appData.main.allDayEvent == 1) {
                dtStart.setHasTime(false);
                dtEnd.setHasTime(false);
            }
        }

        Invite oldInv = oldCalItem == null ? null : oldCalItem.getDefaultInviteOrNull();
        Invite main = MailboxEventData.eventDataToInvite(mbox, appData.main, isTask, dtStart, dtEnd, tz, tzMap, appData.uid, null, oldInv);
        Mailbox.SetCalendarItemData defaultInv = new Mailbox.SetCalendarItemData();
        defaultInv.mInv = main;
        MimeMessage mm = CalendarSyncUtil.encodeInviteIntoMimeMsg(main, oldCalItem != null, mbox.getAccount(),
                oldInv == null ? null : getAttachments(oldInv));
        defaultInv.mPm = new ParsedMessage(mm, mbox.attachmentsIndexingEnabled());

        Mailbox.SetCalendarItemData[] exceptionAppData = null;
        if (appData.main.evRecur != null) {
            ParsedDateTime recurStart = dtStart;
            if (recurStart == null && appData.main.evRecur.recurStart != null)
                recurStart = ParsedDateTime.parseUtcOnly(appData.main.evRecur.recurStart);
            
            if (recurStart != null && dtEnd != null) {
                ParsedDuration duration = dtEnd.difference(dtStart);
                //InviteInfo invId = new InviteInfo(main);
                
                Recurrence.SimpleRepeatingRule srr = new Recurrence.SimpleRepeatingRule(
                        dtStart, duration, MailboxEventRecurrence.toZRecur(mbox, appData.main.evRecur, isTask, tz, accountTz), null);
                List<IRecurrence> addRules = new ArrayList<IRecurrence>();
                addRules.add(srr);
                
                Recurrence.RecurrenceRule masterRule = new Recurrence.RecurrenceRule(dtStart, duration, null, addRules, new ArrayList<IRecurrence>());
                
                ArrayList<Invite> exceptionList = new ArrayList<Invite>();
                if (appData.exceptions != null) {
                    Invite oldDefaultInv = oldCalItem == null ? null : oldCalItem.getDefaultInviteOrNull();
                    for (Iterator<EventException> i = appData.exceptions.iterator(); i.hasNext();) {
                        EventException evx = (EventException)i.next();
                        
                        assert evx.exceptionStartTime != null;
                        ParsedDateTime xStart = ParsedDateTime.parse(evx.exceptionStartTime, tzMap, ICalTimeZone.getUTC(), tz);
                        RecurId rid = new RecurId(xStart, RecurId.RANGE_NONE);
                        
                        oldInv = oldCalItem == null ? null : oldCalItem.getInvite(rid);
                        
                        if (evx.instance == null) {
                            assert (evx.isDeleted);
                            evx.instance = new EventData();
                        }
                        
                        //fill out missing fields from main
                        if (evx.instance.startTimeUtc == null) {
                            evx.instance.startTimeUtc = appData.main.startTimeUtc;
                        }
                        if (evx.instance.endTimeUtc == null) {
                            evx.instance.endTimeUtc = appData.main.endTimeUtc;
                        }
                        
                        if (evx.instance.subject == null) {
                            evx.instance.subject = appData.main.subject;
                        }
                        if (evx.instance.location == null) {
                            evx.instance.location = appData.main.location;
                        }
                        if (evx.instance.body == null && oldInv == null) {
                            evx.instance.body = appData.main.body;
                        }
                        
                        if (evx.instance.organizer == null) {
                            //some phones (e.g. wm6) can omit organizer when creating an exception
                            //look for organizer in event's main
                            evx.instance.organizer = appData.main.organizer;
                            if (evx.instance.organizer == null) { //main is also missing organizer
                                ZOrganizer organizer = null;
                                if (oldInv != null && oldInv.hasOrganizer())
                                    organizer = oldInv.getOrganizer();
                                else if (oldDefaultInv != null && oldDefaultInv.hasOrganizer())
                                    organizer = oldDefaultInv.getOrganizer();
                                
                                if (organizer != null) {
                                    evx.instance.organizer = new EventUser();
                                    evx.instance.organizer.email = organizer.getAddress();
                                    evx.instance.organizer.name = organizer.getCn();
                                }
                            }
                        }
                        if (evx.instance.attendees == null && oldInv == null) {
                            evx.instance.attendees = appData.main.attendees;
                        }
                        
                        if (evx.instance.sensitivity == -1) {
                            evx.instance.sensitivity = appData.main.sensitivity;
                        }
                        if (evx.instance.busyStatus == -1) {
                            evx.instance.busyStatus = appData.main.busyStatus;
                        }
                        if (evx.instance.allDayEvent == -1) {
                            evx.instance.allDayEvent = appData.main.allDayEvent;
                        }
                        if (evx.instance.meetingStatus == -1) {
                            evx.instance.meetingStatus = appData.main.meetingStatus;
                        }
                        
                        //If the exception is canceled previously there is no way the device can change it later 
                        //as the instance should just disappear from the device!!
                        if (evx.isDeleted || (oldInv != null && oldInv.getStatus().equals(CalendarAppData.MEETING_STATUS_CANCELLED))) {
                            evx.instance.meetingStatus = CalendarAppData.MEETING_STATUS_CANCELLED;
                        }
                        
                        ParsedDateTime evxStart = SyncUtil.localDateTimeFromUtcString(evx.instance.startTimeUtc, tz, tzMap);
                        ParsedDateTime evxEnd = SyncUtil.localDateTimeFromUtcString(evx.instance.endTimeUtc, tz, tzMap);
                        if (evx.instance.allDayEvent == 1) {
                            evxStart.setHasTime(false);
                            evxEnd.setHasTime(false);
                        }
                        if (evx.instance.timeStamp == null) {
                            evx.instance.timeStamp = SyncUtil.getFormattedUtcDateTime(currentTimeMillis);
                        }
                        
                        Invite xData = MailboxEventData.eventDataToInvite(mbox, evx.instance, isTask, evxStart, evxEnd, tz, tzMap, appData.uid, rid, oldInv == null ? oldDefaultInv : oldInv);
                        exceptionList.add(xData);
                    }
                }
                
                main.setRecurrence(masterRule);
                
                if (exceptionList.size() > 0) {
                    exceptionAppData = new Mailbox.SetCalendarItemData[exceptionList.size()];
                    for (int i = 0; i < exceptionAppData.length; ++i) {
                        exceptionAppData[i] = new Mailbox.SetCalendarItemData();
                        exceptionAppData[i].mInv = exceptionList.get(i);
                        mm = CalendarSyncUtil.encodeInviteIntoMimeMsg(exceptionAppData[i].mInv, oldCalItem != null, mbox.getAccount(),
                                oldInv == null ? null : getAttachments(oldInv));
                        exceptionAppData[i].mPm = new ParsedMessage(mm, mbox.attachmentsIndexingEnabled());
                    }
                }
            }
        }

        CalendarItem calItem = mbox.setCalendarItem(octxt, folderId, 0, 0, defaultInv, exceptionAppData, null, CalendarItem.NEXT_ALARM_KEEP_CURRENT);
        int calItemId = calItem != null ? calItem.getId() : 0;
        saveMailItemCategories(octxt, mbox, calItemId, calItem.getType());
        return calItem;
    }
    
    private List<BodyPart> getAttachments(Invite invite) throws ServiceException, MessagingException, IOException {
        List<BodyPart> attachments = null;
        if (invite.hasAttachment()) {
            MimeMessage mm = invite.getMimeMessage();
            if (mm.getContentType().startsWith("multipart/mixed")) {
                MimeMultipart mmp = (MimeMultipart)mm.getContent();
                attachments = new ArrayList<BodyPart>(mmp.getCount());
                for (int i = 0; i < mmp.getCount(); ++i) {
                    BodyPart bp = mmp.getBodyPart(i);
                    if (!bp.getContentType().startsWith("multipart/alternative") &&
                            !bp.getContentType().startsWith("text/calendar")) {
                        attachments.add(bp);
                    }
                }
            }
        }
        return attachments;
    }
    
    @SuppressWarnings("unused")
    private static EventUser getEventUser(ZAttendee zattendee) {
        assert zattendee != null;
        
        EventUser eventUser = new EventUser();
        eventUser.name = zattendee.getCn();
        eventUser.email = zattendee.getAddress();
        if (zattendee.getRsvp() != null)
            eventUser.rsvp = zattendee.getRsvp().booleanValue();
        
        if (zattendee.getPartStat().equals(IcalXmlStrMap.PARTSTAT_TENTATIVE))
            eventUser.status = AttendeeStatus.TENTATIVE;
        else if (zattendee.getPartStat().equals(IcalXmlStrMap.PARTSTAT_ACCEPTED))
            eventUser.status = AttendeeStatus.ACCEPT;
        else if (zattendee.getPartStat().equals(IcalXmlStrMap.PARTSTAT_DECLINED))
            eventUser.status = AttendeeStatus.DECLINE;
        else if (zattendee.getPartStat().equals(IcalXmlStrMap.PARTSTAT_NEEDS_ACTION))
            eventUser.status = AttendeeStatus.NOT_RESPONDED;
        else
            eventUser.status = AttendeeStatus.RESPONSE_UNKNOWN;
        
        eventUser.type = AttendeeType.UNKNOWN;
        if (zattendee.getRole().equals(IcalXmlStrMap.ROLE_REQUIRED))
            eventUser.type = AttendeeType.REQUIRED;
        else if (zattendee.getRole().equals(IcalXmlStrMap.ROLE_OPT_PARTICIPANT))
            eventUser.type = AttendeeType.OPTIONAL;
        
        if (zattendee.getRole().equals(IcalXmlStrMap.ROLE_REQUIRED) && zattendee.getCUType().equals(IcalXmlStrMap.CUTYPE_RESOURCE))
            eventUser.type = AttendeeType.RESOURCE;
        
        return eventUser;   
    }
    
    private static ZAttendee getZAttendee(EventUser attendee) {
        assert attendee != null;
        
        String role = null;
        String cuType = null;
        switch(attendee.type) {
        case OPTIONAL:
            role = IcalXmlStrMap.ROLE_OPT_PARTICIPANT;
            cuType = IcalXmlStrMap.CUTYPE_INDIVIDUAL;
            break;
        case REQUIRED:
            role = IcalXmlStrMap.ROLE_REQUIRED;
            cuType = IcalXmlStrMap.CUTYPE_INDIVIDUAL;
            break;
        case RESOURCE:
            role = IcalXmlStrMap.ROLE_REQUIRED;
            cuType = IcalXmlStrMap.CUTYPE_RESOURCE;
            break;
        default:
            break;
        }

        String partStat = null; // participation status
        switch(attendee.status) {
        case TENTATIVE:
            partStat =  IcalXmlStrMap.PARTSTAT_TENTATIVE;
            break;
        case ACCEPT:
            partStat = IcalXmlStrMap.PARTSTAT_ACCEPTED;
            break;
        case DECLINE:
            partStat = IcalXmlStrMap.PARTSTAT_DECLINED;
            break;
        case NOT_RESPONDED:
            partStat = IcalXmlStrMap.PARTSTAT_NEEDS_ACTION;
            break;
        default:
            break;
        }
        
        return new ZAttendee(attendee.email, attendee.name,
                    null, null, null, cuType, role, partStat,
                    attendee.rsvp, null, null, null, null);
    }
    
    public boolean isOrganizer(Mailbox mbox) throws ServiceException {
        return appData.main.organizer == null || AccountUtil.addressMatchesAccount(mbox.getAccount(), appData.main.organizer.email);
    }
}
