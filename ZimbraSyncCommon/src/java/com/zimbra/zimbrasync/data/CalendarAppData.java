/*
 * 
 */
package com.zimbra.zimbrasync.data;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.zimbrasync.util.SyncUtil;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class CalendarAppData extends AppData {
    static final int SENSITIVITY_NORMAL = 0;
    static final int SENSITIVITY_PERSONAL = 1;
    static final int SENSITIVITY_PRIVATE = 2;
    static final int SENSITIVITY_CONFIDENTIAL = 3;
    
    static final int BUSY_STATUS_FREE = 0;
    static final int BUSY_STATUS_TENTATIVE = 1;
    static final int BUSY_STATUS_BUSY = 2;
    static final int BUSY_STATUS_OUT_OF_OFFICE = 3;

    static final int MEETING_STATUS_SELF_ONLY = 0;
    static final int MEETING_STATUS_CONFIRMED = 1;
    static final int MEETING_STATUS_RECEIVED = 3;
    static final int MEETING_STATUS_CANCELLED = 5;
    
    // 12.0
    public static enum AttendeeType {
        UNKNOWN,  // 0
        REQUIRED, // 1
        OPTIONAL, // 2
        RESOURCE; // 3
        
        public static final AttendeeType getAttendeeType(int type) {
            switch(type) {
            case 1:
                return REQUIRED;
            case 2:
                return OPTIONAL;
            case 3:
                return RESOURCE;
            default:
                return UNKNOWN;
            }
        }
        
        public static final int getAttendeeType(AttendeeType type) {
            switch(type) {
            case REQUIRED:
                return 1;
            case OPTIONAL:
                return 2;
            case RESOURCE:
                return 3;
            default:
                return 0;
            }
        }
    }
    
    // 12.0
    public static enum AttendeeStatus {
        RESPONSE_UNKNOWN,   // 0
        TENTATIVE,          // 2
        ACCEPT,             // 3
        DECLINE,            // 4
        NOT_RESPONDED;      // 5

        public static final AttendeeStatus getAttendeeStatus(int status) {
            switch(status) {
            case 2:
                return TENTATIVE;
            case 3:
                return ACCEPT;
            case 4:
                return DECLINE;
            case 5:
                return NOT_RESPONDED;
            default:
                return RESPONSE_UNKNOWN;
            }
        }
        
        public static final int getAttendeeStatus(AttendeeStatus status) {
            switch(status) {
            case TENTATIVE:
                return 2;
            case ACCEPT:
                return 3;
            case DECLINE:
                return 4;
            case NOT_RESPONDED:
                return 5;
            default:
                return 0;
            }
        }   
    }
    
    static class EventUser {
        String name;
        String email;
        AttendeeType type = AttendeeType.UNKNOWN;
        AttendeeStatus status = AttendeeStatus.RESPONSE_UNKNOWN;
        boolean rsvp;
        
        public String toString() {
            String user = "";
            if (name != null && name.length() > 0) {
                user = "\"" + name + "\"";
                if (email != null && email.length() > 0) {
                    user += " <" + email + ">";
                }
            } else if (email != null && email.length() > 0) {
                user = email;
            }
            return user;
        }
        
        public boolean isEquivalent(EventUser other) {
            return email.equals(other.email);
        }
    }
    
    static class EventRecurrence {
        static final int RECURRENCE_TYPE_DAILY = 0;
        static final int RECURRENCE_TYPE_WEEKLY = 1;
        static final int RECURRENCE_TYPE_MONTHLY_BY_MONTHDAY = 2;
        static final int RECURRENCE_TYPE_MONTHLY_BY_WEEKDAY = 3;
        static final int RECURRENCE_TYPE_YEARLY_BY_MONTHDAY = 5;
        static final int RECURRENCE_TYPE_YEARLY_BY_WEEKDAY = 6;
        
        int recurType;
        int dayOfWeek;
        int dayOfMonth;
        int weekOfMonth;
        int monthOfYear;
        int interval;
        int count;
        String until;
        String recurStart; //used by task
        
        public boolean isEquivalent(EventRecurrence other) {
            if (recurType != other.recurType || interval != other.interval || count != other.count ||
                    dayOfWeek != other.dayOfWeek || dayOfMonth != other.dayOfMonth ||
                    weekOfMonth != other.weekOfMonth || monthOfYear != other.monthOfYear)
                return false;
            
            if (until != null && other.until != null) {
                if (!until.equals(other.until))
                    return false;
            } else if (until == null && other.until != null || until != null && other.until == null) {
                return false;
            }
            
            if (recurStart != null && other.recurStart != null) {
                if (!recurStart.equals(other.recurStart))
                    return false;
            } else if (recurStart == null && other.recurStart != null || recurStart != null && other.recurStart == null) {
                return false;
            }
            
            return true;
        }
        
        void encode(BinarySerializer serializer, boolean isTask, boolean isMeetingRequest)
                throws BinaryCodecException, IOException {
            
            String namespace   = isTask ? NAMESPACE_POOMTASKS    : (isMeetingRequest ? NAMESPACE_POOMMAIL    : NAMESPACE_POOMCAL   );
            String RECURRENCE  = isTask ? POOMTASKS_RECURRENCE   : (isMeetingRequest ? POOMMAIL_RECURRENCE   : POOMCAL_RECURRENCE  );
            String TYPE        = isTask ? POOMTASKS_TYPE         : (isMeetingRequest ? POOMMAIL_TYPE         : POOMCAL_TYPE        );
            String DAYOFWEEK   = isTask ? POOMTASKS_DAYOFWEEK    : (isMeetingRequest ? POOMMAIL_DAYOFWEEK    : POOMCAL_DAYOFWEEK   );
            String DAYOFMONTH  = isTask ? POOMTASKS_DAYOFMONTH   : (isMeetingRequest ? POOMMAIL_DAYOFMONTH   : POOMCAL_DAYOFMONTH  );
            String WEEKOFMONTH = isTask ? POOMTASKS_WEEKOFMONTH  : (isMeetingRequest ? POOMMAIL_WEEKOFMONTH  : POOMCAL_WEEKOFMONTH );
            String MONTHOFYEAR = isTask ? POOMTASKS_MONTHOFYEAR  : (isMeetingRequest ? POOMMAIL_MONTHOFYEAR  : POOMCAL_MONTHOFYEAR );
            String INTERVAL    = isTask ? POOMTASKS_INTERVAL     : (isMeetingRequest ? POOMMAIL_INTERVAL     : POOMCAL_INTERVAL    );
            String OCCURRENCES = isTask ? POOMTASKS_OCCURRENCES  : (isMeetingRequest ? POOMMAIL_OCCURRENCES  : POOMCAL_OCCURRENCES );
            String UNTIL       = isTask ? POOMTASKS_UNTIL        : (isMeetingRequest ? POOMMAIL_UNTIL        : POOMCAL_UNTIL       );
            
            serializer.openTag(namespace, RECURRENCE);
            
            if (isTask) {
                serializer.integerElement(namespace, POOMTASKS_REGENERATE, 0);
                serializer.integerElement(namespace, POOMTASKS_DEADOCCUR, 0);
            }
            
            serializer.integerElement(namespace, TYPE, recurType);
            switch (recurType) {
            case RECURRENCE_TYPE_DAILY:
                if (dayOfWeek != 0) //everyday if not specified
                    serializer.integerElement(namespace, DAYOFWEEK, dayOfWeek);
                break;
            case RECURRENCE_TYPE_WEEKLY:
                assert dayOfWeek != 0;
                serializer.integerElement(namespace, DAYOFWEEK, dayOfWeek);
                break;
            case RECURRENCE_TYPE_MONTHLY_BY_MONTHDAY:
                assert dayOfMonth != 0;
                serializer.integerElement(namespace, DAYOFMONTH, dayOfMonth);
                break;
            case RECURRENCE_TYPE_MONTHLY_BY_WEEKDAY:
                assert dayOfWeek != 0;
                assert weekOfMonth != 0;
                serializer.integerElement(namespace, DAYOFWEEK, dayOfWeek);
                serializer.integerElement(namespace, WEEKOFMONTH, weekOfMonth);
                break;
            case RECURRENCE_TYPE_YEARLY_BY_MONTHDAY:
                // Bug: 52737
                //assert dayOfMonth != 0;
                //assert monthOfYear != 0;
                if (dayOfMonth == 0)
                    ZimbraLog.sync.warn("dayOfMonth is 0 for RECURRENCE_TYPE_YEARLY_BY_MONTHDAY");
                if (monthOfYear == 0)
                    ZimbraLog.sync.warn("monthOfYear is 0 for RECURRENCE_TYPE_YEARLY_BY_MONTHDAY");
                
                serializer.integerElement(namespace, DAYOFMONTH, dayOfMonth);
                serializer.integerElement(namespace, MONTHOFYEAR, monthOfYear);
                break;
            case RECURRENCE_TYPE_YEARLY_BY_WEEKDAY:
                assert dayOfWeek != 0;
                assert weekOfMonth != 0;
                assert monthOfYear != 0;
                serializer.integerElement(namespace, DAYOFWEEK, dayOfWeek);
                serializer.integerElement(namespace, WEEKOFMONTH, weekOfMonth);
                serializer.integerElement(namespace, MONTHOFYEAR, monthOfYear);
                break;
            default:
                assert false;
            }
            
            assert interval >= 1;
            serializer.integerElement(namespace, INTERVAL, interval);
            if (count > 0)
                serializer.integerElement(namespace, OCCURRENCES, count);
            if (until != null)
                serializer.textElement(namespace, UNTIL, until);
            
            if (isTask)
                serializer.textElement(namespace, POOMTASKS_START, recurStart);
            
            serializer.closeTag(); //Recurrence
        }
        
        void parse(BinaryParser parser, boolean isTask) throws BinaryCodecException, IOException {
            String namespace   = isTask ? NAMESPACE_POOMTASKS   : NAMESPACE_POOMCAL;
            String RECURRENCE  = isTask ? POOMTASKS_RECURRENCE  : POOMCAL_RECURRENCE;
            String TYPE        = isTask ? POOMTASKS_TYPE        : POOMCAL_TYPE;
            String DAYOFWEEK   = isTask ? POOMTASKS_DAYOFWEEK   : POOMCAL_DAYOFWEEK;
            String DAYOFMONTH  = isTask ? POOMTASKS_DAYOFMONTH  : POOMCAL_DAYOFMONTH;
            String WEEKOFMONTH = isTask ? POOMTASKS_WEEKOFMONTH : POOMCAL_WEEKOFMONTH;
            String MONTHOFYEAR = isTask ? POOMTASKS_MONTHOFYEAR : POOMCAL_MONTHOFYEAR;
            String INTERVAL    = isTask ? POOMTASKS_INTERVAL    : POOMCAL_INTERVAL;
            String OCCURRENCES = isTask ? POOMTASKS_OCCURRENCES : POOMCAL_OCCURRENCES;
            String UNTIL       = isTask ? POOMTASKS_UNTIL       : POOMCAL_UNTIL;
            
            while (parser.next() == START_TAG && namespace.equals(parser.getNamespace())) {
                if (TYPE.equals(parser.getName())) {
                    recurType = parser.nextIntegerContent(recurType);
                } else if (INTERVAL.equals(parser.getName())) {
                    interval = parser.nextIntegerContent(interval);
                } else if (OCCURRENCES.equals(parser.getName())) {
                    count = parser.nextIntegerContent(count);
                } else if (UNTIL.equals(parser.getName())) {
                    until = parser.nextText();
                } else if (DAYOFWEEK.equals(parser.getName())) {
                    dayOfWeek = parser.nextIntegerContent(dayOfWeek);
                } else if (DAYOFMONTH.equals(parser.getName())) {
                    dayOfMonth = parser.nextIntegerContent(dayOfMonth);
                } else if (WEEKOFMONTH.equals(parser.getName())) {
                    weekOfMonth = parser.nextIntegerContent(weekOfMonth);
                } else if (MONTHOFYEAR.equals(parser.getName())) {
                    monthOfYear = parser.nextIntegerContent(monthOfYear);
                } else if (POOMTASKS_START.equals(parser.getName())) {
                    recurStart = parser.nextText();
                } else if (POOMTASKS_REGENERATE.equals(parser.getName()) || POOMTASKS_DEADOCCUR.equals(parser.getName())) {
                    parser.nextText();
                } else {
                    parser.skipUnknownElement();
                }
            } //end of Recurrence
            parser.require(END_TAG, namespace, RECURRENCE);
        }
    }
    
    public static class EventData {
        ProtocolVersion protocolVersion = new ProtocolVersion("2.5");
        String uid;
        int invId;
        int componentNum;
        
        String timeStamp; //last update
        String startDate;
        String startTimeUtc;
        String endDate;
        String endTimeUtc;
        
        boolean isComplete;
        String timeCompleted;
        
        String subject;
        String location;
        String body;
        
        EventUser organizer;
        
        Set<EventUser> attendees;
        
        int importance = -1;
        int sensitivity = -1;
        int busyStatus = -1;
        int allDayEvent = -1;
        int meetingStatus = -1;

        boolean isReminderSet;
        int reminder = -1;
        String reminderTime;
        
        EventRecurrence evRecur;
        
        String recurId; //exception if not null; used in meetingRequest
        long recurIdUtc; //exception if not 0; used for computing GlobalObjId
        
        int rsvp = 0; //user's rsvp used in meeting request
        
        public boolean isEquivalent(EventData other) {
            if ((startTimeUtc !=null && !startTimeUtc.equals(other.startTimeUtc)) || (endTimeUtc != null && !endTimeUtc.equals(other.endTimeUtc)))
                return false;
            if (!equal(subject, other.subject))
                return false;
            if (!equal(location, other.location))
                return false;
            if (other.body != null && !equal(body, other.body)) //other should be the newer one, so null means no change
                return false;
            
            if (other.getAttendeeCount() != 0)
                if (other.getAttendeeCount() != getAttendeeCount())
                    return false;
                else
                    for (Iterator<EventUser> i = attendees.iterator(), j = other.attendees.iterator(); i.hasNext();)
                        if (!i.next().isEquivalent(j.next()))
                            return false;
            
            if (sensitivity != other.sensitivity || busyStatus != other.busyStatus ||
                    allDayEvent != other.allDayEvent || meetingStatus != other.meetingStatus)
                return false;
            
            if (evRecur == null && other.evRecur == null)
                return true;
            if (evRecur == null || other.evRecur == null)
                return false;
            return evRecur.isEquivalent(other.evRecur);
        }

        private static boolean equal(String s1, String s2) {
            s1 = s1 == null ? "" : s1;
            s2 = s2 == null ? "" : s2;
            return s1.equals(s2);
        }
        
        void setOrganizerName(String name) {
            if (organizer == null) {
                organizer = new EventUser();
            }
            organizer.name = name;
        }
        
        void setOrganizerEmail(String email) {
            if (organizer == null) {
                organizer = new EventUser();
            }
            organizer.email = email;
        }
        
        void addAttendee(EventUser attendee) {
            if (attendees == null) {
                attendees = new TreeSet<EventUser>(new Comparator<EventUser>() {
                    public int compare(EventUser o1, EventUser o2) {
                        return o1.email.compareTo(o2.email);
                    }
                });
            }
            attendees.add(attendee);
        }
        
        int getAttendeeCount() {
            return attendees == null ? 0 : attendees.size();
        }
        
        //remove fields identical to default in an exception
        void normalize(EventData main) {
            organizer = null; //exception doesn't support organizer
            attendees = null; //exception doesn't support attendees
            if (subject != null && subject.equals(main.subject))
                subject = null;
            if (location != null && location.equals(main.location))
                location = null;
            if (body != null && body.equals(main.body))
                body = null;
            if (sensitivity == main.sensitivity)
                sensitivity = -1;
            if (busyStatus == main.busyStatus)
                busyStatus = -1;
            if (meetingStatus == main.meetingStatus)
                meetingStatus = -1;
        }       
        
        void encode(BinarySerializer serializer, boolean useRtf, int truncationSize) throws BinaryCodecException, IOException{
            serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_DTSTAMP, timeStamp);
            serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_STARTTIME, startTimeUtc);
            serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_ENDTIME, endTimeUtc);
            
            if (subject != null && subject.length() > 0) {
                serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_SUBJECT, subject);
            }
            if (location != null && location.length() > 0) {
                serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_LOCATION, location);
            }
            if (body != null && body.length() > 0) {
                if (protocolVersion.getMajor() >= 12) {
                    serializer.openTag(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_BODY);
                    
                    String rtfBody = null;
                    int bodySize = body.length();
                    int bodyType = BodyType.getBodyType(BodyType.PlainText);
                    if (useRtf) {
                        rtfBody = SyncUtil.textToRtf(body);
                        bodySize = rtfBody.length();
                        bodyType = BodyType.getBodyType(BodyType.RTF);
                    }
                    
                    serializer.integerElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_ESTIMATEDDATASIZE, bodySize);
                    serializer.integerElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_TYPE, bodyType);
                    
                    int bodyTruncated = (bodySize > truncationSize) ? 1 : 0;
                    if (bodyTruncated > 0) {
                        String truncatedBody = null;
                        
                        if (useRtf)
                            truncatedBody = rtfBody.substring(0, truncationSize - 3) + "...";
                        else
                            truncatedBody = body.substring(0, truncationSize - 3) + "...";
                        
                        serializer.textElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_DATA, truncatedBody);
                        serializer.integerElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_TRUNCATED, bodyTruncated);
                    } else {
                        if (useRtf)
                            serializer.textElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_DATA, rtfBody);
                        else
                            serializer.textElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_DATA, body); 
                    }
                    serializer.integerElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_NATIVEBODYTYPE, BodyType.getBodyType(BodyType.PlainText));
                    
                    // end of AirSyncBase:Body
                    serializer.closeTag();
                } else {
                    serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_BODY, body);
                    if (useRtf) {
                        serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_RTF, SyncUtil.textToRtf(body));
                    }
                }
            }
            
            if (organizer != null) {
                if (organizer.email != null && organizer.email.length() > 0) {
                    serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_ORGANIZEREMAIL, organizer.email);
                }
                if (organizer.name != null && organizer.name.length() > 0) {
                    serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_ORGANIZERNAME, organizer.name);
                } else if (organizer.email != null && organizer.email.length() > 0) {
                    serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_ORGANIZERNAME, organizer.email);
                }
            }
            
            if (attendees != null && attendees.size() > 0) {
                serializer.openTag(NAMESPACE_POOMCAL, POOMCAL_ATTENDEES);
                for (Iterator<EventUser> j = attendees.iterator(); j.hasNext();) {
                    EventUser user =j.next();
                    serializer.openTag(NAMESPACE_POOMCAL, POOMCAL_ATTENDEE);
                    if (user.email != null && user.email.length() > 0) {
                        serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_EMAIL, user.email);
                    }
                    if (user.name != null && user.name.length() > 0) {
                        serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_NAME, user.name);
                    } else if (user.email != null && user.email.length() > 0) {
                        serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_NAME, user.email); //must have Name; if missing, use email
                    }
                    // 12.0
                    if (protocolVersion.getMajor() >= 12) {
                        if (user.type != AttendeeType.UNKNOWN)
                            serializer.integerElement(NAMESPACE_POOMCAL, POOMCAL_ATTENDEETYPE, AttendeeType.getAttendeeType(user.type));
                        serializer.integerElement(NAMESPACE_POOMCAL, POOMCAL_ATTENDEESTATUS, AttendeeStatus.getAttendeeStatus(user.status));
                    }   
                    serializer.closeTag(); //Attendee
                }
                serializer.closeTag(); //Attendees
            }
            
            if (sensitivity != -1) {
                serializer.integerElement(NAMESPACE_POOMCAL, POOMCAL_SENSITIVITY, sensitivity);
            }
            if (busyStatus != -1) {
                serializer.integerElement(NAMESPACE_POOMCAL, POOMCAL_BUSYSTATUS, busyStatus);
            }
            if (allDayEvent != -1) {
                serializer.integerElement(NAMESPACE_POOMCAL, POOMCAL_ALLDAYEVENT, allDayEvent);
            }
            if (reminder > 0) { //web ui uses 0 to mean disabled
                serializer.integerElement(NAMESPACE_POOMCAL, POOMCAL_REMINDER, reminder);
            }
            if (meetingStatus != -1) {
                serializer.integerElement(NAMESPACE_POOMCAL, POOMCAL_MEETINGSTATUS, meetingStatus);
            }
            
            if (evRecur != null) {
                evRecur.encode(serializer, false, false);
            }
        }
        
        //  <A:AllDayEvent>0</A:AllDayEvent>
        //  <A:StartTime>2005-11-25T13:30:00.000Z</A:StartTime>
        //  <A:DtStamp>2005-11-26T08:16:31.000Z</A:DtStamp>
        //  <A:EndTime>2005-11-25T19:00:00.000Z</A:EndTime>
        //  <A:InstanceType>1</A:InstanceType>
        //  <A:Location>Various</A:Location>
        //  <A:Organizer>"John Doe" &lt;john@LS.local&gt;</A:Organizer>
        //  <A:Reminder>900</A:Reminder>
        //  <A:ResponseRequested>1</A:ResponseRequested>
        //  <A:Recurrences>
        //      <A:Recurrence>
        //          <A:Type>6</A:Type>
        //          <A:Interval>1</A:Interval>
        //          <A:Occurrences>10</A:Occurrences>
        //          <A:WeekOfMonth>5</A:WeekOfMonth>
        //          <A:DayOfWeek>32</A:DayOfWeek>
        //          <A:MonthOfYear>11</A:MonthOfYear>
        //      </A:Recurrence>
        //  </A:Recurrences>
        //  <A:Sensitivity>0</A:Sensitivity>
        //  <A:BusyStatus>3</A:BusyStatus>
        //  <A:TimeZone>4AEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAFAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAABAAIAAAAAAAAAxP///w==</A:TimeZone>
        //  <A:GlobalObjId>BAAAAIIA4AB0xbcQGoLgCAAAAAAAAAAAAAAAAAAAAAAAAAAAMwAAAHZDYWwtVWlkAQAAAHtBQUY4MUFDRi03RDM5LTQxQjUtQjIwRi01QTVBREI1NEJBMUZ9AA==</A:GlobalObjId>
        public void encodeMeetingRequest(BinarySerializer serializer) throws BinaryCodecException, IOException{
            
            if (allDayEvent != -1) {
                serializer.integerElement(NAMESPACE_POOMMAIL, POOMMAIL_ALLDAYEVENT, allDayEvent);
            }

            serializer.textElement(NAMESPACE_POOMMAIL, POOMMAIL_STARTTIME, startTimeUtc);

            if (timeStamp != null) {
                serializer.textElement(NAMESPACE_POOMMAIL, POOMMAIL_DTSTAMP, timeStamp);
            } else {
                ZimbraLog.sync.warn("DtStamp is null");
            }

            if (endTimeUtc != null) {
                serializer.textElement(NAMESPACE_POOMMAIL, POOMMAIL_ENDTIME, endTimeUtc);
            } else {
                ZimbraLog.sync.warn("EndTime is null");
            }

            int instanceType = 0;
            if (recurId != null) {
                assert evRecur == null;
                instanceType = 3;
            } else if (evRecur != null) {
                instanceType = 1;
            }
            
            serializer.integerElement(NAMESPACE_POOMMAIL, POOMMAIL_INSTANCETYPE, instanceType);
            
            if (location != null && location.length() > 0) {
                serializer.textElement(NAMESPACE_POOMMAIL, POOMMAIL_LOCATION, location);
            }
            
            assert organizer != null;
            if (organizer != null) {
                String org = null;
                if (organizer.name != null) {
                    org = organizer.toString();
                } else {
                    assert organizer.email != null;
                    org = "<" + organizer.email + ">"; //Treo650 doesn't like organizer without <>
                }
                serializer.textElement(NAMESPACE_POOMMAIL, POOMMAIL_ORGANIZER, org);
            }
            
            if (recurId != null) {
                assert evRecur == null; //we can't handle exception with its own recurrence
                serializer.textElement(NAMESPACE_POOMMAIL, POOMMAIL_RECURRENCEID, recurId);
            }
            
            if (reminder != -1) {
                serializer.integerElement(NAMESPACE_POOMMAIL, POOMMAIL_REMINDER, reminder * 60); //Reminder in MeetingRequest is in seconds instead of minutes
            }
            
            serializer.integerElement(NAMESPACE_POOMMAIL, POOMMAIL_RESPONSEREQUESTED, rsvp);
            
            if (evRecur != null) {
                serializer.openTag(NAMESPACE_POOMMAIL, POOMMAIL_RECURRENCES);
                evRecur.encode(serializer, false, true);
                serializer.closeTag(); //Recurrences
            }

            if (sensitivity != -1) {
                serializer.integerElement(NAMESPACE_POOMMAIL, POOMMAIL_SENSITIVITY, sensitivity);
            }
            
            if (busyStatus != -1) {
                serializer.integerElement(NAMESPACE_POOMMAIL, POOMMAIL_BUSYSTATUS, busyStatus);
            }

            //Even exchange always uses this bogus timezone
            serializer.textElement(NAMESPACE_POOMMAIL, POOMMAIL_TIMEZONE, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==");
            
            try {
                String globalObjId = CalendarUID.uidToGlobalObjId(uid, recurIdUtc);
                serializer.textElement(NAMESPACE_POOMMAIL, POOMMAIL_GLOBALOBJID, globalObjId);
            } catch (Exception t) {
                ZimbraLog.sync.warn("Can't convert UID to GlobalObjId: " + uid);
            }
        }
        
        boolean decodeNextElement(BinaryParser parser) throws BinaryCodecException, IOException {
            if (POOMCAL_DTSTAMP.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                timeStamp = parser.nextText();
            } else if (POOMCAL_STARTTIME.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                startTimeUtc = parser.nextText();
            } else if (POOMCAL_ENDTIME.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                endTimeUtc = parser.nextText();
            } else if (POOMCAL_SUBJECT.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                subject = parser.nextText();
            } else if (POOMCAL_LOCATION.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                location = parser.nextText();
            } else if (POOMCAL_BODY.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                String text = parser.nextText();
                if (body == null) { //rtf takes priority
                    body = text;
                }
            } else if (POOMCAL_RTF.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                body = SyncUtil.decodeCompressedRtf(parser.nextText());
            } else if (AIRSYNCBASE_BODY.equals(parser.getName()) && NAMESPACE_AIRSYNCBASE.equals(parser.getNamespace())) {
                int type = -1;
                @SuppressWarnings("unused")
                int size = 0;
                while (parser.next() == START_TAG && NAMESPACE_AIRSYNCBASE.equals(parser.getNamespace())) {
                    if (AIRSYNCBASE_TYPE.equals(parser.getName()))
                        type = parser.nextIntegerContent();
                    else if (AIRSYNCBASE_ESTIMATEDDATASIZE.equals(parser.getName()))
                        size = parser.nextIntegerContent();
                    else if (AIRSYNCBASE_DATA.equals(parser.getName())) {
                        if (BodyType.getBodyType(type) == BodyType.RTF)
                            body = SyncUtil.decodeCompressedRtf(parser.nextText());
                        else
                            body = parser.nextText();
                    } else
                        parser.skipElement();
                }
                parser.require(END_TAG, NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_BODY); // end of AirSyncBase:Body
            } else if (POOMCAL_ORGANIZERNAME.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                setOrganizerName(parser.nextText());
            } else if (POOMCAL_ORGANIZEREMAIL.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                setOrganizerEmail(parser.nextText());
            } else if (POOMCAL_ATTENDEES.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                
                while (parser.next() == START_TAG &&
                       NAMESPACE_POOMCAL.equals(parser.getNamespace()) &&
                       POOMCAL_ATTENDEE.equals(parser.getName())) {
                
                    EventUser attendee = new EventUser();
                    attendee.rsvp = true; //There is no protocol level mapping for rsvp; always set rsvp to true!!
                    while (parser.next() == START_TAG &&
                           NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                        if (POOMCAL_NAME.equals(parser.getName())) {
                            attendee.name = parser.nextText();
                        } else if (POOMCAL_EMAIL.equals(parser.getName())) {
                            attendee.email = parser.nextText();
                        } else if (POOMCAL_ATTENDEETYPE.equals(parser.getName())) {
                            attendee.type = AttendeeType.getAttendeeType(parser.nextIntegerContent());
                        } else if (POOMCAL_ATTENDEESTATUS.equals(parser.getName())) {
                            attendee.status = AttendeeStatus.getAttendeeStatus(parser.nextIntegerContent());
                        } else {
                            assert false;
                        }
                    } //end of Attendee
                    parser.require(END_TAG, NAMESPACE_POOMCAL, POOMCAL_ATTENDEE);
                    addAttendee(attendee);
                } //end of Attendees
                parser.require(END_TAG, NAMESPACE_POOMCAL, POOMCAL_ATTENDEES);
                
            } else if (POOMCAL_SENSITIVITY.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                sensitivity = parser.nextIntegerContent(sensitivity);
            } else if (POOMCAL_BUSYSTATUS.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                busyStatus = parser.nextIntegerContent(busyStatus);
            } else if (POOMCAL_ALLDAYEVENT.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                allDayEvent = parser.nextIntegerContent(allDayEvent);
            } else if (POOMCAL_REMINDER.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                reminder = parser.nextIntegerContent(reminder);
            } else if (POOMCAL_MEETINGSTATUS.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                meetingStatus = parser.nextIntegerContent(meetingStatus);
            } else {
                return false;
            }
            return true;
        }
        
        //  <Sync xmlns="AirSync:" xmlns:A="POOMTASKS:">
        //    <Collections>
        //      <Collection>
        //        <Class>Tasks</Class>
        //        <SyncKey>2</SyncKey>
        //        <CollectionId>12</CollectionId>
        //        <DeletesAsMoves/>
        //        <GetChanges/>
        //        <WindowSize>100</WindowSize>
        //        <Options>
        //          <Truncation>4</Truncation>
        //          <RtfTruncation>4</RtfTruncation>
        //          <Conflict>1</Conflict>
        //        </Options>
        //        <Commands>
        //          <Change>
        //            <ServerId>12:1</ServerId>
        //            <ApplicationData>
        // ---->        <A:Categories>
        //                <A:Category>Business</A:Category>
        //              </A:Categories>
        //              <A:Complete>1</A:Complete>
        //              <A:DateCompleted>2009-03-15T08:00:00.000Z</A:DateCompleted>
        //              <A:DueDate>2009-03-18T00:00:00.000Z</A:DueDate>
        //              <A:UtcDueDate>2009-03-18T08:00:00.000Z</A:UtcDueDate>
        //              <A:Importance>2</A:Importance>
        //              <A:Sensitivity>2</A:Sensitivity>
        //              <A:StartDate>2009-03-15T00:00:00.000Z</A:StartDate>
        //              <A:UtcStartDate>2009-03-15T08:00:00.000Z</A:UtcStartDate>
        //              <A:Subject>Simple Task</A:Subject>
        // <----        <A:Rtf>3QAAAD0CAABMWkZ1PHha+T8ACQMwAQMB9wKnAgBjaBEKwHNldALRcHJx4DAgVGFoA3ECgwBQ6wNUDzcyD9MyBgAGwwKDpxIBA+MReDA0EhUgAoArApEI5jsJbzAVwzEyvjgJtBdCCjIXQRb0ORIAHxeEGOEYExjgFcMyNTX/CbQaYgoyGmEaHBaKCaUa9v8c6woUG3YdTRt/Hwwabxbt/xyPF7gePxg4JY0YVyRMKR+dJfh9CoEBMAOyMTYDMYksgSc0AFAnNzMtQPY2FGAt0DYtwS19LaMtcQst5AqFfTDA</A:Rtf>
        //            </ApplicationData>
        //          </Change>
        //        </Commands>
        //      </Collection>
        //    </Collections>
        //  </Sync>
        boolean decodeTaskData(BinaryParser parser) throws BinaryCodecException, IOException {
            if (POOMTASKS_STARTDATE.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                startDate = parser.nextText();
            } else if (POOMTASKS_UTCSTARTDATE.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                startTimeUtc = parser.nextText();
            } else if (POOMTASKS_DUEDATE.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                endDate = parser.nextText();
            } else if (POOMTASKS_UTCDUEDATE.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                endTimeUtc = parser.nextText();
            } else if (POOMTASKS_COMPLETE.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                isComplete = (parser.nextIntegerContent() == 0) ? false : true;
            } else if (POOMTASKS_DATECOMPLETED.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                timeCompleted = parser.nextText();
            } else if (POOMTASKS_REMINDERSET.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                isReminderSet = (parser.nextIntegerContent() == 0) ? false : true;
            } else if (POOMTASKS_REMINDERTIME.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                reminderTime = parser.nextText();
            } else if (POOMTASKS_SUBJECT.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                subject = parser.nextText();
            } else if (POOMTASKS_BODY.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                String text = parser.nextText();
                if (body == null) //rtf takes priority
                    body = text;
            } else if (POOMTASKS_RTF.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                body = SyncUtil.decodeCompressedRtf(parser.nextText());
            } else if (POOMTASKS_IMPORTANCE.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                importance = parser.nextIntegerContent();
            } else if (POOMTASKS_SENSITIVITY.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                sensitivity = parser.nextIntegerContent();
            } else if (POOMTASKS_RECURRENCE.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                evRecur = new EventRecurrence();
                evRecur.parse(parser, true);
            } else if (AIRSYNCBASE_BODY.equals(parser.getName()) && NAMESPACE_AIRSYNCBASE.equals(parser.getNamespace())) {
                int type = -1;
                @SuppressWarnings("unused")
                int size = 0;
                while (parser.next() == START_TAG && NAMESPACE_AIRSYNCBASE.equals(parser.getNamespace())) {
                    if (AIRSYNCBASE_TYPE.equals(parser.getName()))
                        type = parser.nextIntegerContent();
                    else if (AIRSYNCBASE_ESTIMATEDDATASIZE.equals(parser.getName()))
                        size = parser.nextIntegerContent();
                    else if (AIRSYNCBASE_DATA.equals(parser.getName())) {
                        if (BodyType.getBodyType(type) == BodyType.RTF)
                            body = SyncUtil.decodeCompressedRtf(parser.nextText());
                        else
                            body = parser.nextText();
                    } else
                        parser.skipElement();
                }
                parser.require(END_TAG, NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_BODY); // end of AirSyncBase:Body
            } else {
                return false;
            }
            return true;
        }
        
        //  <Sync xmlns="AirSync:" xmlns:A="POOMTASKS:">
        //    <Collections>
        //      <Collection>
        //        <Class>Tasks</Class>
        //        <SyncKey>4</SyncKey>
        //        <CollectionId>12</CollectionId>
        //        <Status>1</Status>
        //        <Commands>
        //          <Add>
        //            <ServerId>12:2</ServerId>
        //            <ApplicationData>
        // ---->        <A:BodyTruncated>1</A:BodyTruncated>
        //              <A:BodySize>9245</A:BodySize>
        //              <A:Body>...</A:Body>
        //              <A:Subject>Recurring</A:Subject>
        //              <A:Importance>1</A:Importance>
        //              <A:UtcStartDate>2009-03-15T07:00:00.000Z</A:UtcStartDate>
        //              <A:StartDate>2009-03-15T00:00:00.000Z</A:StartDate>
        //              <A:UtcDueDate>2009-03-16T07:00:00.000Z</A:UtcDueDate>
        //              <A:DueDate>2009-03-16T00:00:00.000Z</A:DueDate>
        //              <A:Recurrence>
        //                <A:Regenerate>0</A:Regenerate>
        //                <A:DeadOccur>0</A:DeadOccur>
        //                <A:Type>1</A:Type>
        //                <A:Start>2009-03-16T00:00:00.000Z</A:Start>
        //                <A:Until>2009-07-23T00:00:00.000Z</A:Until>
        //                <A:Interval>2</A:Interval>
        //              </A:Recurrence>
        //              <A:Complete>0</A:Complete>
        //              <A:DateCompleted>2009-03-15T07:00:00.000Z</A:DateCompleted>
        //              <A:Sensitivity>0</A:Sensitivity>
        //              <A:ReminderTime>2009-03-18T11:30:00.000Z</A:ReminderTime>
        // <----        <A:ReminderSet>0</A:ReminderSet>
        //            </ApplicationData>
        //          </Add>
        //        </Commands>
        //      </Collection>
        //    </Collections>
        //  </Sync>
        void encodeTaskData(BinarySerializer serializer, boolean useRtf, int truncationSize) throws BinaryCodecException, IOException{
            if (body != null && body.length() > 0) {
                if (protocolVersion.getMajor() >= 12) {
                    serializer.openTag(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_BODY);
                    
                    String rtfBody = null;
                    int bodySize = body.length();
                    int bodyType = BodyType.getBodyType(BodyType.PlainText);
                    if (useRtf) {
                        rtfBody = SyncUtil.textToRtf(body);
                        bodySize = rtfBody.length();
                        bodyType = BodyType.getBodyType(BodyType.RTF);
                    }
                    
                    serializer.integerElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_ESTIMATEDDATASIZE, bodySize);
                    serializer.integerElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_TYPE, bodyType);
                    
                    int bodyTruncated = (bodySize > truncationSize) ? 1 : 0;
                    if (bodyTruncated > 0) {
                        String truncatedBody = null;
                        if (useRtf)
                            truncatedBody = rtfBody.substring(0, truncationSize - 3) + "...";
                        else
                            truncatedBody = body.substring(0, truncationSize - 3) + "...";
                        
                        serializer.textElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_DATA, truncatedBody);
                        serializer.integerElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_TRUNCATED, bodyTruncated);
                    } else {
                        if (useRtf)
                            serializer.textElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_DATA, rtfBody);
                        else
                            serializer.textElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_DATA, body);  
                    }
                    serializer.integerElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_NATIVEBODYTYPE, BodyType.getBodyType(BodyType.PlainText));
                    
                    // end of AirSyncBase:Body
                    serializer.closeTag();
                } else {
                    serializer.integerElement(NAMESPACE_POOMTASKS, POOMTASKS_BODYTRUNCATED, body.length() > truncationSize ? 1 : 0);
                    serializer.integerElement(NAMESPACE_POOMTASKS, POOMTASKS_BODYSIZE, body.length());
                    if (truncationSize > 0 && body.length() > truncationSize)
                        body = body.substring(0, truncationSize);
                    serializer.textElement(NAMESPACE_POOMTASKS, POOMTASKS_BODY, body);
                }
            }
            
            if (subject != null && subject.length() > 0)
                serializer.textElement(NAMESPACE_POOMTASKS, POOMTASKS_SUBJECT, subject);
            
            if (importance != -1)
                serializer.integerElement(NAMESPACE_POOMTASKS, POOMTASKS_IMPORTANCE, importance);
            
            if (startTimeUtc != null)
                serializer.textElement(NAMESPACE_POOMTASKS, POOMTASKS_UTCSTARTDATE, startTimeUtc);
            if (startDate != null)
                serializer.textElement(NAMESPACE_POOMTASKS, POOMTASKS_STARTDATE, startDate);
            
            if (endTimeUtc != null)
                serializer.textElement(NAMESPACE_POOMTASKS, POOMTASKS_UTCDUEDATE, endTimeUtc);
            if (endDate != null)
                serializer.textElement(NAMESPACE_POOMTASKS, POOMTASKS_DUEDATE, endDate);
            
            if (evRecur != null)
                evRecur.encode(serializer, true, false);
            
            serializer.integerElement(NAMESPACE_POOMTASKS, POOMTASKS_COMPLETE, isComplete ? 1 : 0);
            if (timeCompleted != null)
                serializer.textElement(NAMESPACE_POOMTASKS, POOMTASKS_DATECOMPLETED, timeCompleted);
            
            if (sensitivity != -1)
                serializer.integerElement(NAMESPACE_POOMTASKS, POOMTASKS_SENSITIVITY, sensitivity);
            
            if (reminderTime != null)
                serializer.textElement(NAMESPACE_POOMTASKS, POOMTASKS_REMINDERTIME, reminderTime);
            serializer.integerElement(NAMESPACE_POOMTASKS, POOMTASKS_REMINDERSET, isReminderSet ? 1 : 0);
        }

        /**
         * @return false if checking fails
         */
        public boolean checkMandatoryFields() {
            return startTimeUtc != null;
        }
    }

    static class EventException {
        boolean isDeleted = false;
        String exceptionStartTime;
        EventData instance; //from an additional invite; or null if to cancel an instance
        EventData main;
        
        long exceptionStartTimeUtc; //used by MailboxCalendarAppData.addException() for sorting
        
        EventException(EventData main) {
            this.main = main;
        }
        
        public boolean isEquivalent(EventException other) {
            assert exceptionStartTime != null;
            assert other.exceptionStartTime != null;
            if (exceptionStartTime.equals(other.exceptionStartTime))
                return false;
            if (instance == null && other.instance == null)
                return true;
            else if (instance == null || other.instance == null)
                return false;
            return instance.isEquivalent(other.instance);
        }
        
        void encode(BinarySerializer serializer, boolean useRtf, int truncationSize) throws BinaryCodecException, IOException{
            serializer.openTag(NAMESPACE_POOMCAL, POOMCAL_EXCEPTION);
            if (isDeleted || instance == null) {
                assert instance == null;
                serializer.integerElement(NAMESPACE_POOMCAL, POOMCAL_DELETED, 1);
            } else { //a real exception
                instance.encode(serializer, useRtf, truncationSize);
            }
            serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_EXCEPTIONSTARTTIME, exceptionStartTime);
            serializer.closeTag(); //Exception
        }
        
        private void parse(BinaryParser parser) throws BinaryCodecException, IOException {
            while (parser.next() == START_TAG) {
                if (POOMCAL_DELETED.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                    isDeleted = parser.nextIntegerContent() == 1;
                    if (!isDeleted && instance == null) {
                        instance = new EventData();
                    }
                } else if (POOMCAL_EXCEPTIONSTARTTIME.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                    exceptionStartTime = parser.nextText();
                } else {
                    if (instance == null) {
                        instance = new EventData();
                    }
                    if (!instance.decodeNextElement(parser)) {
                        parser.skipUnknownElement();
                    }
                }
            } //end of Exception
            parser.require(END_TAG, NAMESPACE_POOMCAL, POOMCAL_EXCEPTION);
            
            if (instance != null)
                instance.normalize(main);
        }
    }
    
    public String uid;
    String timezone; //this is the encoded version of tz
    EventData main;
    Set<EventException> exceptions;
    
    public CalendarAppData() {
        main = new EventData();
    }
    
    public CalendarAppData(EventData main) {
        this.main = main;
    }
    
    //<Sync xmlns="AirSync:" xmlns:A="POOMCAL">
    //    <Collections>
    //        <Collection>
    //            <Class>Calendar</Class>
    //            <SyncKey>{407C7DEB-904F-48AF-8718-E1037C9C725F}2</SyncKey>
    //            <CollectionId>0286243fce792f4a82f3686ac614bc47-2736</CollectionId>
    //            <Status>1</Status>
    //            <Commands>
    //                <Add>
    //                    <ServerId>rid:0286243fce792f4a82f3686ac614bc47000000002a66</ServerId>
    //                    <ApplicationData>
    // ---->                  <A:Timezone>4AEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAFAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAABAAIAAAAAAAAAxP///w==</A:Timezone>
    //                        <A:DtStamp>20050726T043026Z</A:DtStamp>
    //                        <A:StartTime>20050726T150000Z</A:StartTime>
    //                        <A:Subject>Talk about love</A:Subject>
    //                        <A:UID>040000008200E00074C5B7101A82E008000000005050C7116091C5010000000000000000100000009ACE24A2853ACB46A29B0B984CE115A5</A:UID>
    //                        <A:Attendees>
    //                            <A:Attendee>
    //                                <A:Email>jjzhuang@LS.local</A:Email>
    //                                <A:Name>J.J. Zhuang</A:Name>
    //                            </A:Attendee>
    //                        </A:Attendees>
    //                        <A:OrganizerName>John Doe</A:OrganizerName>
    //                        <A:OrganizerEmail>john@LS.local</A:OrganizerEmail>
    //                        <A:Location>TBD</A:Location>
    //                        <A:EndTime>20050726T180000Z</A:EndTime>
    //                        <A:Body>What'd you say?</A:Body>
    //                        <A:Sensitivity>0</A:Sensitivity>
    //                        <A:BusyStatus>1</A:BusyStatus>
    //                        <A:AllDayEvent>0</A:AllDayEvent>
    //                        <A:Reminder>15</A:Reminder>
    // <----                  <A:MeetingStatus>3</A:MeetingStatus>
    //                    </ApplicationData>
    //                </Add>
    //            </Commands>
    //        </Collection>
    //    </Collections>
    //</Sync>
    public void parse(BinaryParser parser) throws BinaryCodecException, IOException {
        while (parser.next() == START_TAG) {
            if (POOMCAL_CATEGORIES.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                parseCategories(parser, NAMESPACE_POOMCAL, POOMCAL_CATEGORY);
                parser.require(END_TAG, NAMESPACE_POOMCAL, POOMCAL_CATEGORIES);
            } else if (POOMCAL_TIMEZONE.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                timezone = parser.nextText();
            } else if (POOMCAL_UID.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                uid = parser.nextText();
            } else if (POOMCAL_RECURRENCE.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                main.evRecur = new EventRecurrence();
                main.evRecur.parse(parser, false);
            } else if (POOMCAL_EXCEPTIONS.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                while (parser.next() == START_TAG &&
                       NAMESPACE_POOMCAL.equals(parser.getNamespace()) &&
                       POOMCAL_EXCEPTION.equals(parser.getName())) {
                    EventException evx = new EventException(main);
                    evx.parse(parser);
                    assert evx.exceptionStartTime != null;
                    addException(evx);
                }
            } else if (!main.decodeNextElement(parser)) {
                parser.skipUnknownElement();
            }
        }
    }
    
    public void parseTask(BinaryParser parser) throws BinaryCodecException, IOException {
        while (parser.next() == START_TAG) {
            if (POOMTASKS_CATEGORIES.equals(parser.getName()) && NAMESPACE_POOMTASKS.equals(parser.getNamespace())) {
                parseCategories(parser, NAMESPACE_POOMTASKS, POOMTASKS_CATEGORY);
                parser.require(END_TAG, NAMESPACE_POOMTASKS, POOMTASKS_CATEGORIES);
            } else if (POOMCAL_RECURRENCE.equals(parser.getName()) && NAMESPACE_POOMCAL.equals(parser.getNamespace())) {
                main.evRecur = new EventRecurrence();
                main.evRecur.parse(parser, true);
            } else if (!main.decodeTaskData(parser)) {
                parser.skipUnknownElement();
            }
        }
        main.allDayEvent = 1;
    }
    
    //<Sync xmlns="AirSync:" xmlns:A="POOMCAL">
    //    <Collections>
    //        <Collection>
    //            <Class>Calendar</Class>
    //            <SyncKey>{407C7DEB-904F-48AF-8718-E1037C9C725F}2</SyncKey>
    //            <CollectionId>0286243fce792f4a82f3686ac614bc47-2736</CollectionId>
    //            <Status>1</Status>
    //            <Commands>
    //                <Add>
    //                    <ServerId>rid:0286243fce792f4a82f3686ac614bc47000000002a66</ServerId>
    //                    <ApplicationData>
    // ---->                  <A:Timezone>4AEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAoAAAAFAAIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAABAAIAAAAAAAAAxP///w==</A:Timezone>
    //                        <A:DtStamp>20050726T043026Z</A:DtStamp>
    //                        <A:StartTime>20050726T150000Z</A:StartTime>
    //                        <A:Subject>Talk about love</A:Subject>
    //                        <A:UID>040000008200E00074C5B7101A82E008000000005050C7116091C5010000000000000000100000009ACE24A2853ACB46A29B0B984CE115A5</A:UID>
    //                        <A:Attendees>
    //                            <A:Attendee>
    //                                <A:Email>bob@LS.local</A:Email>
    //                                <A:Name>Bob Doe</A:Name>
    //                            </A:Attendee>
    //                        </A:Attendees>
    //                        <A:OrganizerName>John Doe</A:OrganizerName>
    //                        <A:OrganizerEmail>john@LS.local</A:OrganizerEmail>
    //                        <A:Location>TBD</A:Location>
    //                        <A:EndTime>20050726T180000Z</A:EndTime>
    //                        <A:Body>What'd you say?</A:Body>
    //                        <A:Sensitivity>0</A:Sensitivity>
    //                        <A:BusyStatus>1</A:BusyStatus>
    //                        <A:AllDayEvent>0</A:AllDayEvent>
    //                        <A:Reminder>15</A:Reminder>
    // <----                  <A:MeetingStatus>3</A:MeetingStatus>
    //                    </ApplicationData>
    //                </Add>
    //            </Commands>
    //        </Collection>
    //    </Collections>
    //</Sync>
    public void encode(BinarySerializer serializer) throws BinaryCodecException, IOException {
        encode(serializer, true, false, -1);
    }
    
    public void encode(BinarySerializer serializer, boolean useCategories, boolean useRtf, int truncationSize) throws BinaryCodecException, IOException {
        if (useCategories)
            encodeCategories(serializer, NAMESPACE_POOMCAL, POOMCAL_CATEGORIES, POOMCAL_CATEGORY);
        
        serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_TIMEZONE, timezone);
        serializer.textElement(NAMESPACE_POOMCAL, POOMCAL_UID, uid);

        main.encode(serializer, useRtf, truncationSize);
        
        if (getExceptionCount() > 0) {
            serializer.openTag(NAMESPACE_POOMCAL, POOMCAL_EXCEPTIONS);
            for (Iterator<EventException> j = exceptions.iterator(); j.hasNext();) {
                EventException evx = j.next();
                evx.encode(serializer, useRtf, truncationSize);
            }
            serializer.closeTag(); //Exceptions
        }
    }
    
    public void encodeTask(BinarySerializer serializer, boolean useCategories, boolean useRtf, int truncationSize) throws BinaryCodecException, IOException {
        if (useCategories)
            encodeCategories(serializer, NAMESPACE_POOMTASKS, POOMTASKS_CATEGORIES, POOMTASKS_CATEGORY);
        main.encodeTaskData(serializer, useRtf, truncationSize);
    }
    
    private void addException(EventException evx) {
        if (exceptions == null) {
            exceptions = new HashSet<EventException>();
        }
        exceptions.add(evx);
    }
    
    int getExceptionCount() {
        return exceptions == null ? 0 : exceptions.size();
    }
    
    public boolean isEquivalent(CalendarAppData other) {
        if (!main.isEquivalent(other.main))
            return false;

        if (getExceptionCount() != other.getExceptionCount())
            return false;
        
        if (exceptions != null)
            for (Iterator<EventException> i = exceptions.iterator(), j = other.exceptions.iterator(); i.hasNext();)
                if (!i.next().isEquivalent(j.next()))
                    return false;

        return true;
    }
}
