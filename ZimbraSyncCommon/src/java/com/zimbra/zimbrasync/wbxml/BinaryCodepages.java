/*
 * 
 */
package com.zimbra.zimbrasync.wbxml;

public abstract class BinaryCodepages {
	
    static public final int SWITCH_PAGE = 0x00;
    static public final int END = 0x01;
    static public final int ENTITY = 0x02;
    static public final int STR_I = 0x03;
	
	public static final String NAMESPACE_AIRSYNC  = "AirSync";
	public static final String NAMESPACE_POOMCONTACTS = "POOMCONTACTS";
	public static final String NAMESPACE_POOMMAIL = "POOMMAIL";
	public static final String NAMESPACE_AIRNOTIFY = "AirNotify";
	public static final String NAMESPACE_POOMCAL = "POOMCAL";
	public static final String NAMESPACE_MOVE = "Move";
	public static final String NAMESPACE_GETITEMESTIMATE = "GetItemEstimate";
	public static final String NAMESPACE_FOLDERHIERARCHY = "FolderHierarchy";
	public static final String NAMESPACE_MEETINGRESPONSE = "MeetingResponse";
	public static final String NAMESPACE_POOMTASKS = "POOMTASKS";
	public static final String NAMESPACE_POOMCONTACTS2 = "POOMCONTACTS2";
	public static final String NAMESPACE_PING = "Ping";
	public static final String NAMESPACE_PROVISION = "Provision";
	public static final String NAMESPACE_SEARCH = "Search";
	public static final String NAMESPACE_GAL = "GAL";
	public static final String NAMESPACE_AIRSYNCBASE = "AirSyncBase";
	public static final String NAMESPACE_SETTINGS = "Settings";
	public static final String NAMESPACE_ITEMOPERATIONS = "ItemOperations";
	
	public static final String[] NAMESPACES = {
		NAMESPACE_AIRSYNC,
		NAMESPACE_POOMCONTACTS,
		NAMESPACE_POOMMAIL,
		NAMESPACE_AIRNOTIFY,
		NAMESPACE_POOMCAL,
		NAMESPACE_MOVE,
		NAMESPACE_GETITEMESTIMATE,
		NAMESPACE_FOLDERHIERARCHY,
		NAMESPACE_MEETINGRESPONSE,
		NAMESPACE_POOMTASKS,
		null,
		null,
		NAMESPACE_POOMCONTACTS2,
		NAMESPACE_PING,
		NAMESPACE_PROVISION,
		NAMESPACE_SEARCH,
		NAMESPACE_GAL,
		NAMESPACE_AIRSYNCBASE,
		NAMESPACE_SETTINGS,
		null,
		NAMESPACE_ITEMOPERATIONS
	};
	
	public static final String AIRSYNC_SYNC = "Sync";
	public static final String AIRSYNC_RESPONSES = "Responses";
	public static final String AIRSYNC_ADD = "Add";
	public static final String AIRSYNC_CHANGE = "Change";
	public static final String AIRSYNC_DELETE = "Delete";
	public static final String AIRSYNC_FETCH = "Fetch";
	public static final String AIRSYNC_SYNCKEY = "SyncKey";
	public static final String AIRSYNC_CLIENTID = "ClientId";
	public static final String AIRSYNC_SERVERID = "ServerId";
	public static final String AIRSYNC_STATUS = "Status";
	public static final String AIRSYNC_COLLECTION = "Collection";
	public static final String AIRSYNC_CLASS = "Class";
	public static final String AIRSYNC_VERSION = "Version";
	public static final String AIRSYNC_COLLECTIONID = "CollectionId";
	public static final String AIRSYNC_GETCHANGES = "GetChanges";
	public static final String AIRSYNC_MOREAVAILABLE = "MoreAvailable";
	public static final String AIRSYNC_WINDOWSIZE = "WindowSize";
	public static final String AIRSYNC_COMMANDS = "Commands";
	public static final String AIRSYNC_OPTIONS = "Options";
	public static final String AIRSYNC_FILTERTYPE = "FilterType";
	public static final String AIRSYNC_TRUNCATION = "Truncation";
	public static final String AIRSYNC_RTFTRUNCATION = "RtfTruncation";
	public static final String AIRSYNC_CONFLICT = "Conflict";
	public static final String AIRSYNC_COLLECTIONS = "Collections";
	public static final String AIRSYNC_APPLICATIONDATA = "ApplicationData";
	public static final String AIRSYNC_DELETESASMOVES = "DeletesAsMoves";
	public static final String AIRSYNC_SUPPORTED = "Supported";
	public static final String AIRSYNC_SOFTDELETE = "SoftDelete";
	public static final String AIRSYNC_MIMESUPPORT = "MIMESupport";
	public static final String AIRSYNC_MIMETRUNCATION = "MIMETruncation";
	
	//Code Page 00 - AirSync	
	public static final String [] CODEPAGE_00_TAGTABLE = {
		AIRSYNC_SYNC,     		//0x00, 0x05
		AIRSYNC_RESPONSES,      //0x00, 0x06
		AIRSYNC_ADD,    		//0x00, 0x07
		AIRSYNC_CHANGE,         //0x00, 0x08
		AIRSYNC_DELETE, 		//0x00, 0x09
		AIRSYNC_FETCH,     		//0x00, 0x0A
		AIRSYNC_SYNCKEY,        //0x00, 0x0B
		AIRSYNC_CLIENTID,    	//0x00, 0x0C
		AIRSYNC_SERVERID,    	//0x00, 0x0D
		AIRSYNC_STATUS,			//0x00, 0x0E
		AIRSYNC_COLLECTION,    	//0x00, 0x0F
		AIRSYNC_CLASS,    		//0x00, 0x10
		AIRSYNC_VERSION,   		//0x00, 0x11
		AIRSYNC_COLLECTIONID,	//0x00, 0x12
		AIRSYNC_GETCHANGES, 	//0x00, 0x13
		AIRSYNC_MOREAVAILABLE,	//0x00, 0x14
		AIRSYNC_WINDOWSIZE,     //0x00, 0x15
		AIRSYNC_COMMANDS,       //0x00, 0x16
		AIRSYNC_OPTIONS,      	//0x00, 0x17
		AIRSYNC_FILTERTYPE,     //0x00, 0x18
		AIRSYNC_TRUNCATION,     //0x00, 0x19
		AIRSYNC_RTFTRUNCATION,  //0x00, 0x1A
		AIRSYNC_CONFLICT,      	//0x00, 0x1B
		AIRSYNC_COLLECTIONS,    //0x00, 0x1C
		AIRSYNC_APPLICATIONDATA,//0x00, 0x1D
		AIRSYNC_DELETESASMOVES, //0x00, 0x1E
		"P00T1F",       		//0x00, 0x1F
		AIRSYNC_SUPPORTED,  	//0x00, 0x20
		AIRSYNC_SOFTDELETE,    	//0x00, 0x21
		AIRSYNC_MIMESUPPORT,   	//0x00, 0x22
		AIRSYNC_MIMETRUNCATION,	//0x00, 0x23
		"P00T24",        		//0x00, 0x24
		"P00T25",       		//0x00, 0x25
		"P00T26",      			//0x00, 0x26
		"P00T27",      			//0x00, 0x27
		"P00T28"     			//0x00, 0x28
	};
    
	public static final String POOMCONTACTS_ANNIVERSARY = "Anniversary";
	public static final String POOMCONTACTS_ASSISTANTNAME = "AssistantName";
	public static final String POOMCONTACTS_ASSISTNAMEPHONENUMBER = "AssistnamePhoneNumber";
	public static final String POOMCONTACTS_BIRTHDAY = "Birthday";
	public static final String POOMCONTACTS_BODY = "Body";
	public static final String POOMCONTACTS_BUSINESS2PHONENUMBER = "Business2PhoneNumber";
	public static final String POOMCONTACTS_BUSINESSCITY = "BusinessCity";
	public static final String POOMCONTACTS_BUSINESSCOUNTRY = "BusinessCountry";
	public static final String POOMCONTACTS_BUSINESSPOSTALCODE = "BusinessPostalCode";
	public static final String POOMCONTACTS_BUSINESSSTATE = "BusinessState";
	public static final String POOMCONTACTS_BUSINESSSTREET = "BusinessStreet";
	public static final String POOMCONTACTS_BUSINESSFAXNUMBER = "BusinessFaxNumber";
	public static final String POOMCONTACTS_BUSINESSPHONENUMBER = "BusinessPhoneNumber";
	public static final String POOMCONTACTS_CARPHONENUMBER = "CarPhoneNumber";
	public static final String POOMCONTACTS_CATEGORIES = "Categories";
	public static final String POOMCONTACTS_CATEGORY = "Category";
	public static final String POOMCONTACTS_CHILDREN = "Children";
	public static final String POOMCONTACTS_CHILD = "Child";
	public static final String POOMCONTACTS_COMPANYNAME = "CompanyName";
	public static final String POOMCONTACTS_DEPARTMENT = "Department";
	public static final String POOMCONTACTS_EMAIL1ADDRESS = "Email1Address";
	public static final String POOMCONTACTS_EMAIL2ADDRESS = "Email2Address";
	public static final String POOMCONTACTS_EMAIL3ADDRESS = "Email3Address";
	public static final String POOMCONTACTS_FILEAS = "FileAs";
	public static final String POOMCONTACTS_FIRSTNAME = "FirstName";
	public static final String POOMCONTACTS_HOME2PHONENUMBER = "Home2PhoneNumber";
	public static final String POOMCONTACTS_HOMECITY = "HomeCity";
	public static final String POOMCONTACTS_HOMECOUNTRY = "HomeCountry";
	public static final String POOMCONTACTS_HOMEPOSTALCODE = "HomePostalCode";
	public static final String POOMCONTACTS_HOMESTATE = "HomeState";
	public static final String POOMCONTACTS_HOMESTREET = "HomeStreet";
	public static final String POOMCONTACTS_HOMEFAXNUMBER = "HomeFaxNumber";
	public static final String POOMCONTACTS_HOMEPHONENUMBER = "HomePhoneNumber";
	public static final String POOMCONTACTS_JOBTITLE = "JobTitle";
	public static final String POOMCONTACTS_LASTNAME = "LastName";
	public static final String POOMCONTACTS_MIDDLENAME = "MiddleName";
	public static final String POOMCONTACTS_MOBILEPHONENUMBER = "MobilePhoneNumber";
	public static final String POOMCONTACTS_OFFICELOCATION = "OfficeLocation";
	public static final String POOMCONTACTS_OTHERCITY = "OtherCity";
	public static final String POOMCONTACTS_OTHERCOUNTRY = "OtherCountry";
	public static final String POOMCONTACTS_OTHERPOSTALCODE = "OtherPostalCode";
	public static final String POOMCONTACTS_OTHERSTATE = "OtherState";
	public static final String POOMCONTACTS_OTHERSTREET = "OtherStreet";
	public static final String POOMCONTACTS_PAGERNUMBER = "PagerNumber";
	public static final String POOMCONTACTS_RADIOPHONENUMBER = "RadioPhoneNumber";
	public static final String POOMCONTACTS_SPOUSE = "Spouse";
	public static final String POOMCONTACTS_SUFFIX = "Suffix";
	public static final String POOMCONTACTS_TITLE = "Title";
	public static final String POOMCONTACTS_WEBPAGE = "WebPage";
	public static final String POOMCONTACTS_YOMICOMPANYNAME = "YomiCompanyName";
	public static final String POOMCONTACTS_YOMIFIRSTNAME = "YomiFirstName";
	public static final String POOMCONTACTS_YOMILASTNAME = "YomiLastName";
	public static final String POOMCONTACTS_RTF = "Rtf";
	public static final String POOMCONTACTS_PICTURE = "Picture";
	
	//Code Page 01 - POOMCONTACTS
	public static final String [] CODEPAGE_01_TAGTABLE = {
		POOMCONTACTS_ANNIVERSARY,        	//0x01, 0x05
		POOMCONTACTS_ASSISTANTNAME,      	//0x01, 0x06
		POOMCONTACTS_ASSISTNAMEPHONENUMBER,	//0x01, 0x07
		POOMCONTACTS_BIRTHDAY, 				//0x01, 0x08
		POOMCONTACTS_BODY,               	//0x01, 0x09
		"P01T0A",           				//0x01, 0x0A
		"P01T0B",        					//0x01, 0x0B
		POOMCONTACTS_BUSINESS2PHONENUMBER, 	//0x01, 0x0C
		POOMCONTACTS_BUSINESSCITY,			//0x01, 0x0D
		POOMCONTACTS_BUSINESSCOUNTRY,       //0x01, 0x0E
		POOMCONTACTS_BUSINESSPOSTALCODE,    //0x01, 0x0F
		POOMCONTACTS_BUSINESSSTATE,        	//0x01, 0x10
		POOMCONTACTS_BUSINESSSTREET,      	//0x01, 0x11
		POOMCONTACTS_BUSINESSFAXNUMBER,     //0x01, 0x12
		POOMCONTACTS_BUSINESSPHONENUMBER, 	//0x01, 0x13
		POOMCONTACTS_CARPHONENUMBER,        //0x01, 0x14
		POOMCONTACTS_CATEGORIES,        	//0x01, 0x15
		POOMCONTACTS_CATEGORY,      		//0x01, 0x16
		POOMCONTACTS_CHILDREN,     			//0x01, 0x17
		POOMCONTACTS_CHILD, 				//0x01, 0x18
		POOMCONTACTS_COMPANYNAME,           //0x01, 0x19
		POOMCONTACTS_DEPARTMENT,           	//0x01, 0x1A
		POOMCONTACTS_EMAIL1ADDRESS,        	//0x01, 0x1B
		POOMCONTACTS_EMAIL2ADDRESS, 		//0x01, 0x1C
		POOMCONTACTS_EMAIL3ADDRESS,			//0x01, 0x1D
		POOMCONTACTS_FILEAS,          		//0x01, 0x1E
		POOMCONTACTS_FIRSTNAME,          	//0x01, 0x1F
		POOMCONTACTS_HOME2PHONENUMBER,      //0x01, 0x20
		POOMCONTACTS_HOMECITY,      		//0x01, 0x21
		POOMCONTACTS_HOMECOUNTRY,     		//0x01, 0x22
		POOMCONTACTS_HOMEPOSTALCODE, 		//0x01, 0x23
		POOMCONTACTS_HOMESTATE,             //0x01, 0x24
		POOMCONTACTS_HOMESTREET,        	//0x01, 0x25
		POOMCONTACTS_HOMEFAXNUMBER,      	//0x01, 0x26
		POOMCONTACTS_HOMEPHONENUMBER,     	//0x01, 0x27
		POOMCONTACTS_JOBTITLE, 				//0x01, 0x28
		POOMCONTACTS_LASTNAME,              //0x01, 0x29
		POOMCONTACTS_MIDDLENAME,           	//0x01, 0x2A
		POOMCONTACTS_MOBILEPHONENUMBER,     //0x01, 0x2B
		POOMCONTACTS_OFFICELOCATION, 		//0x01, 0x2C
		POOMCONTACTS_OTHERCITY,				//0x01, 0x2D
		POOMCONTACTS_OTHERCOUNTRY,          //0x01, 0x2E
		POOMCONTACTS_OTHERPOSTALCODE,       //0x01, 0x2F
		POOMCONTACTS_OTHERSTATE,        	//0x01, 0x30
		POOMCONTACTS_OTHERSTREET,      		//0x01, 0x31
		POOMCONTACTS_PAGERNUMBER,     		//0x01, 0x32
		POOMCONTACTS_RADIOPHONENUMBER, 		//0x01, 0x33
		POOMCONTACTS_SPOUSE,               	//0x01, 0x34
		POOMCONTACTS_SUFFIX,        		//0x01, 0x35
		POOMCONTACTS_TITLE,      		 	//0x01, 0x36
		POOMCONTACTS_WEBPAGE,     			//0x01, 0x37
		POOMCONTACTS_YOMICOMPANYNAME, 		//0x01, 0x38
		POOMCONTACTS_YOMIFIRSTNAME,         //0x01, 0x39
		POOMCONTACTS_YOMILASTNAME,          //0x01, 0x3A
		POOMCONTACTS_RTF,  					//0x01, 0x3B
		POOMCONTACTS_PICTURE,				//0x01, 0x3C
		"P01T3D",							//0x01, 0x3D
		"P01T3E",          					//0x01, 0x3E
		"P01T3F",          					//0x01, 0x3F
		"P01T40",          					//0x01, 0x40
		"P01T41",          					//0x01, 0x41
		"P01T42",          					//0x01, 0x42
		"P01T43",          					//0x01, 0x43
		"P01T44",          					//0x01, 0x44
		"P01T45",          					//0x01, 0x45
		"P01T46",          					//0x01, 0x46
		"P01T47",          					//0x01, 0x47
		"P01T48",          					//0x01, 0x48
		"P01T49",          					//0x01, 0x49
		"P01T4A",          					//0x01, 0x4A
		"P01T4B",          					//0x01, 0x4B
		"P01T4C",          					//0x01, 0x4C
		"P01T4D",          					//0x01, 0x4D
		"P01T4E",          					//0x01, 0x4E
		"P01T4F"          					//0x01, 0x4F
	};
    
	public static final String POOMMAIL_ATTACHMENT = "Attachment";
	public static final String POOMMAIL_ATTACHMENTS = "Attachments";
	public static final String POOMMAIL_ATTNAME = "AttName";
	public static final String POOMMAIL_ATTSIZE = "AttSize";
	public static final String POOMMAIL_ATTMETHOD = "AttMethod";
	public static final String POOMMAIL_BODY = "Body";
	public static final String POOMMAIL_BODYSIZE = "BodySize";
	public static final String POOMMAIL_BODYTRUNCATED = "BodyTruncated";
	public static final String POOMMAIL_DATERECEIVED = "DateReceived";
	public static final String POOMMAIL_DISPLAYNAME = "DisplayName";
	public static final String POOMMAIL_DISPLAYTO = "DisplayTo";
	public static final String POOMMAIL_IMPORTANCE = "Importance";
	public static final String POOMMAIL_MESSAGECLASS = "MessageClass";
	public static final String POOMMAIL_SUBJECT = "Subject";
	public static final String POOMMAIL_READ = "Read";
	public static final String POOMMAIL_TO = "To";
	public static final String POOMMAIL_CC = "Cc";
	public static final String POOMMAIL_FROM = "From";
	public static final String POOMMAIL_REPLYTO = "Reply-To";
	public static final String POOMMAIL_ALLDAYEVENT = "AllDayEvent";
	public static final String POOMMAIL_DTSTAMP = "DtStamp";
	public static final String POOMMAIL_ENDTIME = "EndTime";
	public static final String POOMMAIL_INSTANCETYPE = "InstanceType";
	public static final String POOMMAIL_BUSYSTATUS = "BusyStatus";
	public static final String POOMMAIL_LOCATION = "Location";
	public static final String POOMMAIL_MEETINGREQUEST = "MeetingRequest";
	public static final String POOMMAIL_ORGANIZER = "Organizer";
	public static final String POOMMAIL_RECURRENCEID = "RecurrenceId";
	public static final String POOMMAIL_REMINDER = "Reminder";
	public static final String POOMMAIL_RESPONSEREQUESTED = "ResponseRequested";
	public static final String POOMMAIL_RECURRENCES = "Recurrences";
	public static final String POOMMAIL_RECURRENCE = "Recurrence";
	public static final String POOMMAIL_TYPE = "Type";
	public static final String POOMMAIL_UNTIL = "Until";
	public static final String POOMMAIL_OCCURRENCES = "Occurrences";
	public static final String POOMMAIL_INTERVAL = "Interval";
	public static final String POOMMAIL_DAYOFWEEK = "DayOfWeek";
	public static final String POOMMAIL_DAYOFMONTH = "DayOfMonth";
	public static final String POOMMAIL_WEEKOFMONTH = "WeekOfMonth";
	public static final String POOMMAIL_MONTHOFYEAR = "MonthOfYear";
	public static final String POOMMAIL_STARTTIME = "StartTime";
	public static final String POOMMAIL_SENSITIVITY = "Sensitivity";
	public static final String POOMMAIL_TIMEZONE = "TimeZone";
	public static final String POOMMAIL_GLOBALOBJID = "GlobalObjId";
	public static final String POOMMAIL_THREADTOPIC = "ThreadTopic";
	public static final String POOMMAIL_MIMEDATA = "MIMEData";
	public static final String POOMMAIL_MIMETRUNCATED = "MIMETruncated";
	public static final String POOMMAIL_MIMESIZE = "MIMESize";
	public static final String POOMMAIL_INTERNETCPID = "InternetCPID";
	public static final String POOMMAIL_FLAG = "Flag";
	public static final String POOMMAIL_FLAGSTATUS = "FlagStatus";
	public static final String POOMMAIL_CONTENTCLASS = "ContentClass";
	public static final String POOMMAIL_FLAGTYPE = "FlagType";
	public static final String POOMMAIL_COMPLETETIME = "CompleteTime";
	
	//Code Page 02 - POOMMAIL	
	public static final String [] CODEPAGE_02_TAGTABLE = {
		POOMMAIL_ATTACHMENT,        	//0x02, 0x05
		POOMMAIL_ATTACHMENTS,  			//0x02, 0x06
		POOMMAIL_ATTNAME,        		//0x02, 0x07
		POOMMAIL_ATTSIZE,        		//0x02, 0x08
		"P02T09",        				//0x02, 0x09
		POOMMAIL_ATTMETHOD,         	//0x02, 0x0A
		"P02T0B",         				//0x02, 0x0B
		POOMMAIL_BODY, 					//0x02, 0x0C
		POOMMAIL_BODYSIZE,        		//0x02, 0x0D
		POOMMAIL_BODYTRUNCATED, 		//0x02, 0x0E
		POOMMAIL_DATERECEIVED,  		//0x02, 0x0F
		POOMMAIL_DISPLAYNAME,        	//0x02, 0x10
		POOMMAIL_DISPLAYTO,				//0x02, 0x11
		POOMMAIL_IMPORTANCE,   			//0x02, 0x12
		POOMMAIL_MESSAGECLASS,  		//0x02, 0x13
		POOMMAIL_SUBJECT,       		//0x02, 0x14
		POOMMAIL_READ,        			//0x02, 0x15
		POOMMAIL_TO,        			//0x02, 0x16
		POOMMAIL_CC,        			//0x02, 0x17
		POOMMAIL_FROM,         			//0x02, 0x18
		POOMMAIL_REPLYTO,  				//0x02, 0x19
		POOMMAIL_ALLDAYEVENT,       	//0x02, 0x1A
		"P02T1B",        				//0x02, 0x1B
		"P02T1C",        				//0x02, 0x1C
		POOMMAIL_DTSTAMP,        		//0x02, 0x1D
		POOMMAIL_ENDTIME,				//0x02, 0x1E
		POOMMAIL_INSTANCETYPE,    		//0x02, 0x1F
		POOMMAIL_BUSYSTATUS,			//0x02, 0x20
		POOMMAIL_LOCATION, 				//0x02, 0x21
		POOMMAIL_MEETINGREQUEST,   		//0x02, 0x22
		POOMMAIL_ORGANIZER,   			//0x02, 0x23
		POOMMAIL_RECURRENCEID,   		//0x02, 0x24
		POOMMAIL_REMINDER,				//0x02, 0x25
		POOMMAIL_RESPONSEREQUESTED,   	//0x02, 0x26
		POOMMAIL_RECURRENCES,  			//0x02, 0x27
		POOMMAIL_RECURRENCE,   			//0x02, 0x28
		POOMMAIL_TYPE,   				//0x02, 0x29
		POOMMAIL_UNTIL,   				//0x02, 0x2A
		POOMMAIL_OCCURRENCES,			//0x02, 0x2B
		POOMMAIL_INTERVAL,   			//0x02, 0x2C
		POOMMAIL_DAYOFWEEK,   			//0x02, 0x2D
		POOMMAIL_DAYOFMONTH,   			//0x02, 0x2E
		POOMMAIL_WEEKOFMONTH,   		//0x02, 0x2F
		POOMMAIL_MONTHOFYEAR,   		//0x02, 0x30
		POOMMAIL_STARTTIME,   			//0x02, 0x31
		POOMMAIL_SENSITIVITY,  			//0x02, 0x32
		POOMMAIL_TIMEZONE,  			//0x02, 0x33
		POOMMAIL_GLOBALOBJID,  			//0X02, 0x34
		POOMMAIL_THREADTOPIC,  			//0x02, 0x35
		POOMMAIL_MIMEDATA, 				//0x02, 0x36
		POOMMAIL_MIMETRUNCATED,  		//0x02, 0x37
		POOMMAIL_MIMESIZE,  			//0x02, 0x38
		POOMMAIL_INTERNETCPID,			//0x02, 0x39
		POOMMAIL_FLAG,                  //0x02, 0x3A
		POOMMAIL_FLAGSTATUS,            //0x02, 0x3B
		POOMMAIL_CONTENTCLASS,          //0x02, 0x3C
		POOMMAIL_FLAGTYPE,              //0x02, 0x3D
		POOMMAIL_COMPLETETIME           //0x02, 0x3E
	};
    
	public static final String AIRNOTIFY_NOTIFY = "Notify";
	public static final String AIRNOTIFY_LIFETIME = "Lifetime";
	public static final String AIRNOTIFY_DEVICEINFO = "DeviceInfo";
	public static final String AIRNOTIFY_ENABLE = "Enable";
	public static final String AIRNOTIFY_FOLDER = "Folder";
	public static final String AIRNOTIFY_SERVERID = "ServerId";
	public static final String AIRNOTIFY_DEVICEADDRESS = "DeviceAddress";
	public static final String AIRNOTIFY_VALIDCARRIERPROFILES = "ValidCarrierProfiles";
	public static final String AIRNOTIFY_STATUS = "Status";
	public static final String AIRNOTIFY_FRIENDLYNAME = "FriendlyName";
	
	//Code Page 3 - AirNotify
	public static final String [] CODEPAGE_03_TAGTABLE = {
		AIRNOTIFY_NOTIFY,        		//0x03, 0x05
		"P03T06",      		 			//0x03, 0x06
		"P03T07",     					//0x03, 0x07
		AIRNOTIFY_LIFETIME,				//0x03, 0x08
		AIRNOTIFY_DEVICEINFO,           //0x03, 0x09
		AIRNOTIFY_ENABLE,      			//0x03, 0x0A
		AIRNOTIFY_FOLDER,    			//0x03, 0x0B
		AIRNOTIFY_SERVERID, 			//0x03, 0x0C
		AIRNOTIFY_DEVICEADDRESS,		//0x03, 0x0D
		AIRNOTIFY_VALIDCARRIERPROFILES,	//0x03, 0x0E
		"P03T0F",          				//0x03, 0x0F
		AIRNOTIFY_STATUS,          		//0x03, 0x10
		"P03T11",          				//0x03, 0x11
		"P03T12",          				//0x03, 0x12
		"P03T13",          				//0x03, 0x13
		"P03T14",          				//0x03, 0x14
		"P03T15",          				//0x03, 0x15
		"P03T16",          				//0x03, 0x16
		AIRNOTIFY_FRIENDLYNAME			//0x03, 0x17
	};
    
	public static final String POOMCAL_TIMEZONE = "Timezone";
	public static final String POOMCAL_ALLDAYEVENT = "AllDayEvent";
	public static final String POOMCAL_ATTENDEES = "Attendees";
	public static final String POOMCAL_ATTENDEE = "Attendee";
	public static final String POOMCAL_EMAIL = "Email";
	public static final String POOMCAL_NAME = "Name";
	public static final String POOMCAL_BODY = "Body";
	public static final String POOMCAL_BUSYSTATUS = "BusyStatus";
	public static final String POOMCAL_CATEGORIES = "Categories";
	public static final String POOMCAL_CATEGORY = "Category";
	public static final String POOMCAL_RTF = "Rtf";
	public static final String POOMCAL_DTSTAMP = "DtStamp";
	public static final String POOMCAL_ENDTIME = "EndTime";
	public static final String POOMCAL_EXCEPTION = "Exception";
	public static final String POOMCAL_EXCEPTIONS = "Exceptions";
	public static final String POOMCAL_DELETED = "Deleted";
	public static final String POOMCAL_EXCEPTIONSTARTTIME = "ExceptionStartTime";
	public static final String POOMCAL_LOCATION = "Location";
	public static final String POOMCAL_MEETINGSTATUS = "MeetingStatus";
	public static final String POOMCAL_ORGANIZEREMAIL = "OrganizerEmail";
	public static final String POOMCAL_ORGANIZERNAME = "OrganizerName";
	public static final String POOMCAL_RECURRENCE = "Recurrence";
	public static final String POOMCAL_TYPE = "Type";
	public static final String POOMCAL_UNTIL = "Until";
	public static final String POOMCAL_OCCURRENCES = "Occurrences";
	public static final String POOMCAL_INTERVAL = "Interval";
	public static final String POOMCAL_DAYOFWEEK = "DayOfWeek";
	public static final String POOMCAL_DAYOFMONTH = "DayOfMonth";
	public static final String POOMCAL_WEEKOFMONTH = "WeekOfMonth";
	public static final String POOMCAL_MONTHOFYEAR = "MonthOfYear";
	public static final String POOMCAL_REMINDER = "Reminder";
	public static final String POOMCAL_SENSITIVITY = "Sensitivity";
	public static final String POOMCAL_SUBJECT = "Subject";
	public static final String POOMCAL_STARTTIME = "StartTime";
	public static final String POOMCAL_UID = "UID";
	public static final String POOMCAL_ATTENDEESTATUS = "AttendeeStatus";
	public static final String POOMCAL_ATTENDEETYPE = "AttendeeType";

	//Code Page 4 - POOMCAL
	public static final String [] CODEPAGE_04_TAGTABLE = {
		POOMCAL_TIMEZONE,        		//0x04, 0x05
		POOMCAL_ALLDAYEVENT,      		//0x04, 0x06
		POOMCAL_ATTENDEES,     			//0x04, 0x07
		POOMCAL_ATTENDEE, 				//0x04, 0x08
		POOMCAL_EMAIL,        	        //0x04, 0x09
		POOMCAL_NAME,           		//0x04, 0x0A
		POOMCAL_BODY,        			//0x04, 0x0B
		"P04T0C", 						//0x04, 0x0C
		POOMCAL_BUSYSTATUS,				//0x04, 0x0D
		POOMCAL_CATEGORIES,				//0x04, 0x0E
		POOMCAL_CATEGORY,  				//0x04, 0x0F
		POOMCAL_RTF,       				//0x04, 0x10
		POOMCAL_DTSTAMP,          		//0x04, 0x11
		POOMCAL_ENDTIME,          		//0x04, 0x12
		POOMCAL_EXCEPTION,          	//0x04, 0x13
		POOMCAL_EXCEPTIONS,          	//0x04, 0x14
		POOMCAL_DELETED,         		//0x04, 0x15
		POOMCAL_EXCEPTIONSTARTTIME,     //0x04, 0x16
		POOMCAL_LOCATION,          		//0x04, 0x17
		POOMCAL_MEETINGSTATUS,          //0x04, 0x18
		POOMCAL_ORGANIZEREMAIL,         //0x04, 0x19
		POOMCAL_ORGANIZERNAME,          //0x04, 0x1A
		POOMCAL_RECURRENCE,          	//0x04, 0x1B
		POOMCAL_TYPE,          			//0x04, 0x1C
		POOMCAL_UNTIL,          		//0x04, 0x1D
		POOMCAL_OCCURRENCES,          	//0x04, 0x1E
		POOMCAL_INTERVAL,          		//0x04, 0x1F
		POOMCAL_DAYOFWEEK,          	//0x04, 0x20
		POOMCAL_DAYOFMONTH,          	//0x04, 0x21
		POOMCAL_WEEKOFMONTH,			//0x04, 0x22
		POOMCAL_MONTHOFYEAR,			//0x04, 0x23
		POOMCAL_REMINDER,          		//0x04, 0x24
		POOMCAL_SENSITIVITY,          	//0x04, 0x25
		POOMCAL_SUBJECT,          		//0x04, 0x26
		POOMCAL_STARTTIME,          	//0x04, 0x27
		POOMCAL_UID,          			//0x04, 0x28
		POOMCAL_ATTENDEESTATUS,         //0x04, 0x29
		POOMCAL_ATTENDEETYPE            //0x04, 0x2A
	};
    
	public static final String MOVE_MOVES = "Moves";
	public static final String MOVE_MOVE = "Move";
	public static final String MOVE_SRCMSGID = "SrcMsgId";
	public static final String MOVE_SRCFLDID = "SrcFldId";
	public static final String MOVE_DSTFLDID = "DstFldId";
	public static final String MOVE_RESPONSE = "Response";
	public static final String MOVE_STATUS = "Status";
	public static final String MOVE_DSTMSGID = "DstMsgId";
	
	//Code Page 5 - Move
	public static final String [] CODEPAGE_05_TAGTABLE = {
		MOVE_MOVES,        		//0x05, 0x05
		MOVE_MOVE,      		//0x05, 0x06
		MOVE_SRCMSGID,     		//0x05, 0x07
		MOVE_SRCFLDID, 			//0x05, 0x08
		MOVE_DSTFLDID,          //0x05, 0x09
		MOVE_RESPONSE,          //0x05, 0x0A
		MOVE_STATUS,        	//0x05, 0x0B
		MOVE_DSTMSGID, 			//0x05, 0x0C
		"P05T0D",				//0x05, 0x0D
		"P05T0E"          		//0x05, 0x0E
	};

	public static final String GETITEMESTIMATE_GETITEMESTIMATE = "GetItemEstimate";
	public static final String GETITEMESTIMATE_VERSION = "Version";
	public static final String GETITEMESTIMATE_COLLECTIONS = "Collections";
	public static final String GETITEMESTIMATE_COLLECTION = "Collection";
	public static final String GETITEMESTIMATE_CLASS = "Class";
	public static final String GETITEMESTIMATE_COLLECTIONID = "CollectionId";
	public static final String GETITEMESTIMATE_ESTIMATE = "Estimate";
	public static final String GETITEMESTIMATE_RESPONSE = "Response";
	public static final String GETITEMESTIMATE_STATUS = "Status";
	
	//Code Page 6 - GetItemEstimate
	public static final String [] CODEPAGE_06_TAGTABLE = {
		GETITEMESTIMATE_GETITEMESTIMATE,	//0x06, 0x05
		GETITEMESTIMATE_VERSION,        	//0x06, 0x06
		GETITEMESTIMATE_COLLECTIONS,        //0x06, 0x07
		GETITEMESTIMATE_COLLECTION,         //0x06, 0x08
		GETITEMESTIMATE_CLASS,   			//0x06, 0x09
		GETITEMESTIMATE_COLLECTIONID,   	//0x06, 0x0A
		"P06T0B",   						//0x06, 0x0B
		GETITEMESTIMATE_ESTIMATE,  			//0x06, 0x0C
		GETITEMESTIMATE_RESPONSE,   		//0x06, 0x0D
		GETITEMESTIMATE_STATUS,  			//0x06, 0x0E
		"P06T0F", 							//0x06, 0x0F
		"P06T10",      						//0x06, 0x10
		"P06T11",               			//0x06, 0x11
		"P06T12",         					//0x06, 0x12
		"P06T13",              				//0x06, 0x13
		"P06T14",      						//0x06, 0x14
		"P06T15",               			//0x06, 0x15
		"P06T16",    						//0x06, 0x16
		"P06T17",      						//0x06, 0x17
		"P06T18"     						//0x06, 0x18
	};

	
	public static final String FOLDERHIERARCHY_FOLDERS = "Folders";
	public static final String FOLDERHIERARCHY_FOLDER = "Folder";
	public static final String FOLDERHIERARCHY_DISPLAYNAME = "DisplayName";
	public static final String FOLDERHIERARCHY_SERVERID = "ServerId";
	public static final String FOLDERHIERARCHY_PARENTID = "ParentId";
	public static final String FOLDERHIERARCHY_TYPE = "Type";
	public static final String FOLDERHIERARCHY_STATUS = "Status";
	public static final String FOLDERHIERARCHY_CHANGES = "Changes";
	public static final String FOLDERHIERARCHY_ADD = "Add";
	public static final String FOLDERHIERARCHY_DELETE = "Delete";
	public static final String FOLDERHIERARCHY_UPDATE = "Update";
	public static final String FOLDERHIERARCHY_SYNCKEY = "SyncKey";
	public static final String FOLDERHIERARCHY_FOLDERCREATE = "FolderCreate";
	public static final String FOLDERHIERARCHY_FOLDERDELETE = "FolderDelete";
	public static final String FOLDERHIERARCHY_FOLDERUPDATE = "FolderUpdate";
	public static final String FOLDERHIERARCHY_FOLDERSYNC = "FolderSync";
	public static final String FOLDERHIERARCHY_COUNT = "Count";
	
	//Code Page 07 - FolderHierarchy
	public static final String [] CODEPAGE_07_TAGTABLE = {
		FOLDERHIERARCHY_FOLDERS,  	    //0x07, 0x05
		FOLDERHIERARCHY_FOLDER,    		//0x07, 0x06
		FOLDERHIERARCHY_DISPLAYNAME,	//0x07, 0x07
		FOLDERHIERARCHY_SERVERID,   	//0x07, 0x08
		FOLDERHIERARCHY_PARENTID,  		//0x07, 0x09
		FOLDERHIERARCHY_TYPE, 			//0x07, 0x0A
		"P07T0B",    					//0x07, 0x0B
		FOLDERHIERARCHY_STATUS,			//0x07, 0x0C
		"P07T0D",        				//0x07, 0x0D
		FOLDERHIERARCHY_CHANGES,    	//0x07, 0x0E
		FOLDERHIERARCHY_ADD,       		//0x07, 0x0F
		FOLDERHIERARCHY_DELETE,			//0x07, 0x10
		FOLDERHIERARCHY_UPDATE,			//0x07, 0x11
		FOLDERHIERARCHY_SYNCKEY,    	//0x07, 0x12
		FOLDERHIERARCHY_FOLDERCREATE,	//0x07, 0x13
		FOLDERHIERARCHY_FOLDERDELETE,   //0x07, 0x14
		FOLDERHIERARCHY_FOLDERUPDATE,	//0x07, 0x15
		FOLDERHIERARCHY_FOLDERSYNC, 	//0x07, 0x16
		FOLDERHIERARCHY_COUNT,      	//0x07, 0x17
		"P07T18",            			//0x07, 0x18
		"P07T19",       				//0x07, 0x19
		"P07T1A",      					//0x07, 0x1A
		"P07T1B",						//0x07, 0x1B
		"P07T1C",    					//0x07, 0x1C
		"P07T1D", 						//0x07, 0x1D
		"P07T1E",						//0x07, 0x1E
		"P07T1F",               		//0x07, 0x1F
		"P07T20",              			//0x07, 0x20
		"P07T21"                		//0x07, 0x21
	};
	
	public static final String MEETINGRESPONSE_CALENDARID = "CalendarId";
	public static final String MEETINGRESPONSE_COLLECTIONID = "CollectionId";
	public static final String MEETINGRESPONSE_MEETINGRESPONSE = "MeetingResponse";
	public static final String MEETINGRESPONSE_REQUESTID = "RequestId";
	public static final String MEETINGRESPONSE_REQUEST = "Request";
	public static final String MEETINGRESPONSE_RESULT = "Result";
	public static final String MEETINGRESPONSE_STATUS = "Status";
	public static final String MEETINGRESPONSE_USERRESPONSE = "UserResponse";
	
    //Code Page 08 - MeetingResponse
	public static final String [] CODEPAGE_08_TAGTABLE = {
		MEETINGRESPONSE_CALENDARID,        	//0x08, 0x05
		MEETINGRESPONSE_COLLECTIONID,      	//0x08, 0x06
		MEETINGRESPONSE_MEETINGRESPONSE,    //0x08, 0x07
		MEETINGRESPONSE_REQUESTID, 			//0x08, 0x08
		MEETINGRESPONSE_REQUEST,            //0x08, 0x09
		MEETINGRESPONSE_RESULT,           	//0x08, 0x0A
		MEETINGRESPONSE_STATUS,        		//0x08, 0x0B
		MEETINGRESPONSE_USERRESPONSE,		//0x08, 0x0C
		"P08T0D",							//0x08, 0x0D
		"P08T0E"          					//0x08, 0x0E
	};
	
	public static final String POOMTASKS_BODY = "Body";
	public static final String POOMTASKS_BODYSIZE = "BodySize";
	public static final String POOMTASKS_BODYTRUNCATED = "BodyTruncated";
	public static final String POOMTASKS_CATEGORIES = "Categories";
	public static final String POOMTASKS_CATEGORY = "Category";
	public static final String POOMTASKS_COMPLETE = "Complete";
	public static final String POOMTASKS_DATECOMPLETED = "DateCompleted";
	public static final String POOMTASKS_DUEDATE = "DueDate";
	public static final String POOMTASKS_UTCDUEDATE = "UtcDueDate";
	public static final String POOMTASKS_IMPORTANCE = "Importance";
	public static final String POOMTASKS_RECURRENCE = "Recurrence";
	public static final String POOMTASKS_TYPE = "Type";
	public static final String POOMTASKS_START = "Start";
	public static final String POOMTASKS_UNTIL = "Until";
	public static final String POOMTASKS_OCCURRENCES = "Occurrences";
	public static final String POOMTASKS_INTERVAL = "Interval";
	public static final String POOMTASKS_DAYOFMONTH = "DayOfMonth";
	public static final String POOMTASKS_DAYOFWEEK = "DayOfWeek";
	public static final String POOMTASKS_WEEKOFMONTH = "WeekOfMonth";
	public static final String POOMTASKS_MONTHOFYEAR = "MonthOfYear";
	public static final String POOMTASKS_REGENERATE = "Regenerate";
	public static final String POOMTASKS_DEADOCCUR = "DeadOccur";
	public static final String POOMTASKS_REMINDERSET = "ReminderSet";
	public static final String POOMTASKS_REMINDERTIME = "ReminderTime";
	public static final String POOMTASKS_SENSITIVITY = "Sensitivity";
	public static final String POOMTASKS_STARTDATE = "StartDate";
	public static final String POOMTASKS_UTCSTARTDATE = "UtcStartDate";
	public static final String POOMTASKS_SUBJECT = "Subject";
	public static final String POOMTASKS_RTF = "Rtf";
	
	//Code Page 09 - Tasks
	public static final String[] CODEPAGE_09_TAGTABLE = {
        POOMTASKS_BODY,         //0x09, 0x05
        POOMTASKS_BODYSIZE,     //0x09, 0x06
        POOMTASKS_BODYTRUNCATED,//0x09, 0x07
        POOMTASKS_CATEGORIES,   //0x09, 0x08
        POOMTASKS_CATEGORY,     //0x09, 0x09
        POOMTASKS_COMPLETE,     //0x09, 0x0A
        POOMTASKS_DATECOMPLETED,//0x09, 0x0B
        POOMTASKS_DUEDATE,      //0x09, 0x09
        POOMTASKS_UTCDUEDATE,   //0x09, 0x0D
        POOMTASKS_IMPORTANCE,   //0x09, 0x0E
        POOMTASKS_RECURRENCE,   //0x09, 0x0F
        POOMTASKS_TYPE,         //0x09, 0x10
        POOMTASKS_START,        //0x09, 0x11
        POOMTASKS_UNTIL,        //0x09, 0x12
        POOMTASKS_OCCURRENCES,  //0x09, 0x13
        POOMTASKS_INTERVAL,     //0x09, 0x14
        POOMTASKS_DAYOFMONTH,   //0x09, 0x15
        POOMTASKS_DAYOFWEEK,    //0x09, 0x16
        POOMTASKS_WEEKOFMONTH,  //0x09, 0x17
        POOMTASKS_MONTHOFYEAR,  //0x09, 0x18
        POOMTASKS_REGENERATE,   //0x09, 0x19
        POOMTASKS_DEADOCCUR,    //0x09, 0x1A
        POOMTASKS_REMINDERSET,  //0x09, 0x1B
        POOMTASKS_REMINDERTIME, //0x09, 0x1C
        POOMTASKS_SENSITIVITY,  //0x09, 0x1D
        POOMTASKS_STARTDATE,    //0x09, 0x1E
        POOMTASKS_UTCSTARTDATE, //0x09, 0x1F
        POOMTASKS_SUBJECT,      //0x09, 0x20
        POOMTASKS_RTF,          //0x09, 0x21
        "P09T22",               //0x09, 0x22
        "P09T23"                //0x09, 0x23
	};
	
	public static final String POOMCONTACTS2_CUSTOMERID = "CustomerId";
	public static final String POOMCONTACTS2_GOVERNMENTID = "GovernmentId";
	public static final String POOMCONTACTS2_IMADDRESS = "IMAddress";
	public static final String POOMCONTACTS2_IMADDRESS2 = "IMAddress2";
	public static final String POOMCONTACTS2_IMADDRESS3 = "IMAddress3";
	public static final String POOMCONTACTS2_MANAGERNAME = "ManagerName";
	public static final String POOMCONTACTS2_COMPANYMAINPHONE = "CompanyMainPhone";
	public static final String POOMCONTACTS2_ACCOUNTNAME = "AccountName";
	public static final String POOMCONTACTS2_NICKNAME = "NickName";

	//Code Page 0C - POOMCONTACTS2
	public static final String[] CODEPAGE_0C_TAGTABLE = {
		POOMCONTACTS2_CUSTOMERID,           //0x0C, 0x05
		POOMCONTACTS2_GOVERNMENTID,         //0x0C, 0x06
		POOMCONTACTS2_IMADDRESS,          	//0x0C, 0x07
		POOMCONTACTS2_IMADDRESS2,          	//0x0C, 0x08
		POOMCONTACTS2_IMADDRESS3,           //0x0C, 0x09
		POOMCONTACTS2_MANAGERNAME,          //0x0C, 0x0A
		POOMCONTACTS2_COMPANYMAINPHONE,     //0x0C, 0x0B
		POOMCONTACTS2_ACCOUNTNAME,          //0x0C, 0x0C
		POOMCONTACTS2_NICKNAME,          	//0x0C, 0x0D
		"P0CT0E",          					//0x0C, 0x0E
		"P0CT0F",         					//0x0C, 0x0F
		"P0CT10",          					//0x0C, 0x10
		"P0CT11",          					//0x0C, 0x11
		"P0CT12",          					//0x0C, 0x12
		"P0CT13",          					//0x0C, 0x13
		"P0CT14",          					//0x0C, 0x14
		"P0CT15",          					//0x0C, 0x15
		"P0CT16",          					//0x0C, 0x16
		"P0CT17",          					//0x0C, 0x17
		"P0CT18",          					//0x0C, 0x18
		"P0CT19",          					//0x0C, 0x19
		"P0CT1A",          					//0x0C, 0x1A
		"P0CT1B",          					//0x0C, 0x1B
		"P0CT1C",          					//0x0C, 0x1C
		"P0CT1D",          					//0x0C, 0x1D
		"P0CT1E",          					//0x0C, 0x1E
		"P0CT1F"         					//0x0C, 0x1F
	};
	
	public static final String PING_PING = "Ping";
	public static final String PING_STATUS = "Status";
	public static final String PING_HEARTBEATINTERVAL = "HeartbeatInterval";
	public static final String PING_FOLDERS = "Folders";
	public static final String PING_FOLDER = "Folder";
	public static final String PING_ID = "Id";
	public static final String PING_CLASS = "Class";
	
    //Code Page 0D - Ping
	public static final String[] CODEPAGE_0D_TAGTABLE = {
		PING_PING,        			//0x0D, 0x05
		"P0DT06",      				//0x0D, 0x06
		PING_STATUS,    			//0x0D, 0x07
		PING_HEARTBEATINTERVAL, 	//0x0D, 0x08
		PING_FOLDERS,            	//0x0D, 0x09
		PING_FOLDER,           		//0x0D, 0x0A
		PING_ID,        			//0x0D, 0x0B
		PING_CLASS,					//0x0D, 0x0C
		"P0DT0D",					//0x0D, 0x0D
		"P0DT0E"          			//0x0D, 0x0E
	};
	
	public static final String PROVISION_PROVISION = "Provision";
	public static final String PROVISION_POLICIES = "Policies";
	public static final String PROVISION_POLICY = "Policy";
	public static final String PROVISION_POLICYTYPE = "PolicyType";
	public static final String PROVISION_POLICYKEY = "PolicyKey";
	public static final String PROVISION_DATA = "Data";
	public static final String PROVISION_STATUS = "Status";
	public static final String PROVISION_REMOTEWIPE = "RemoteWipe";
	public static final String PROVISION_EASPROVISIONDOC = "eas-provisioningdoc";
	public static final String PROVISION_DEVICEPASSWORDENABLED = "DevicePasswordEnabled";
	public static final String PROVISION_ALPHANUMERICDEVICEPASSWORDREQUIRED = "AlphanumericDevicePasswordRequired";
	public static final String PROVISION_DEVICEENCRYPTIONENABLED = "DeviceEncryptionEnabled";
	public static final String PROVISION_PASSWORDRECOVERYENABLED = "PasswordRecoveryEnabled";
	public static final String PROVISION_ATTACHMENTSENABLED = "AttachmentsEnabled";
	public static final String PROVISION_MINDEVICEPASSWORDLENGTH = "MinDevicePasswordLength";
	public static final String PROVISION_MAXINACTIVITYTIMEDEVICELOCK = "MaxInactivityTimeDeviceLock";
	public static final String PROVISION_MAXDEVICEPASSWORDFAILEDATTEMPTS = "MaxDevicePasswordFailedAttempts";
	public static final String PROVISION_MAXATTACHMENTSIZE = "MaxAttachmentSize";
	public static final String PROVISION_ALLOWSIMPLEDEVICEPASSWORD = "AllowSimpleDevicePassword";
	public static final String PROVISION_DEVICEPASSWORDEXPIRATION = "DevicePasswordExpiration";
	public static final String PROVISION_DEVICEPASSWORDHISTORY = "DevicePasswordHistory";

	//Code Page 0E - Provision
	public static final String[] CODEPAGE_0E_TAGTABLE = {
		PROVISION_PROVISION,    						//0x0E, 0x05
		PROVISION_POLICIES,     						//0x0E, 0x06
		PROVISION_POLICY,       						//0x0E, 0x07
		PROVISION_POLICYTYPE,   						//0x0E, 0x08
		PROVISION_POLICYKEY,    						//0x0E, 0x09
		PROVISION_DATA,        							//0x0E, 0x0A
		PROVISION_STATUS,       						//0x0E, 0x0B
		PROVISION_REMOTEWIPE,   						//0x0E, 0x0C
		PROVISION_EASPROVISIONDOC,      				//0x0E, 0x0D
		PROVISION_DEVICEPASSWORDENABLED,        		//0x0E, 0x0E
		PROVISION_ALPHANUMERICDEVICEPASSWORDREQUIRED,   //0x0E, 0x0F
		PROVISION_DEVICEENCRYPTIONENABLED,      		//0x0E, 0x10
		PROVISION_PASSWORDRECOVERYENABLED,      		//0x0E, 0x11
		"P0ET12",        								//0x0E, 0x12
		PROVISION_ATTACHMENTSENABLED,       			//0x0E, 0x13
		PROVISION_MINDEVICEPASSWORDLENGTH,      		//0x0E, 0x14
		PROVISION_MAXINACTIVITYTIMEDEVICELOCK,  		//0x0E, 0x15
		PROVISION_MAXDEVICEPASSWORDFAILEDATTEMPTS,      //0x0E, 0x16
		PROVISION_MAXATTACHMENTSIZE,    				//0x0E, 0x17
		PROVISION_ALLOWSIMPLEDEVICEPASSWORD,    		//0x0E, 0x18
		PROVISION_DEVICEPASSWORDEXPIRATION,     		//0x0E, 0x19
		PROVISION_DEVICEPASSWORDHISTORY,       			//0x0E, 0x1A
		"P0ET1B",        								//0x0E, 0x1B
		"P0ET1C"        								//0x0E, 0x1C
	};
	
	public static final String SEARCH_SEARCH = "Search";
	public static final String SEARCH_STORE = "Store";
	public static final String SEARCH_NAME = "Name";
	public static final String SEARCH_QUERY = "Query";
	public static final String SEARCH_OPTIONS = "Options";
	public static final String SEARCH_RANGE = "Range";
	public static final String SEARCH_STATUS = "Status";
	public static final String SEARCH_RESPONSE = "Response";
	public static final String SEARCH_RESULT = "Result";
	public static final String SEARCH_PROPERTIES = "Properties";
	public static final String SEARCH_TOTAL = "Total";
	public static final String SEARCH_EQUALTO = "EqualTo";
	public static final String SEARCH_VALUE = "Value";
	public static final String SEARCH_AND = "And";
	public static final String SEARCH_OR = "Or";
	public static final String SEARCH_FREETEXT = "FreeText";
	public static final String SEARCH_DEEPTRAVERSAL = "DeepTraversal";
	public static final String SEARCH_LONGID = "LongId";
	public static final String SEARCH_REBUILDRESULTS = "RebuildResults";
	public static final String SEARCH_LESSTHAN = "LessThan";
	public static final String SEARCH_GREATERTHAN = "GreaterThan";
	public static final String SEARCH_SCHEMA = "Schema";
	public static final String SEARCH_SUPPORTED = "Supported";
	
	//Code Page 0F - Search
	public static final String[] CODEPAGE_0F_TAGTABLE = {
		SEARCH_SEARCH,          //0x0F, 0x05
		"P0FT06",				//0x0F, 0x06
		SEARCH_STORE,           //0x0F, 0x07
		SEARCH_NAME,            //0x0F, 0x08
		SEARCH_QUERY,           //0x0F, 0x09
		SEARCH_OPTIONS,         //0x0F, 0x0A
		SEARCH_RANGE,           //0x0F, 0x0B
		SEARCH_STATUS,          //0x0F, 0x0C
		SEARCH_RESPONSE,        //0x0F, 0x0D
		SEARCH_RESULT,          //0x0F, 0x0E
		SEARCH_PROPERTIES,      //0x0F, 0x0F
		SEARCH_TOTAL,           //0x0F, 0x10
		SEARCH_EQUALTO,         //0x0F, 0x11
		SEARCH_VALUE,           //0x0F, 0x12
		SEARCH_AND,             //0x0F, 0x13
		SEARCH_OR,              //0x0F, 0x14
		SEARCH_FREETEXT,        //0x0F, 0x15
		"P0FT16",               //0x0F, 0x16
		SEARCH_DEEPTRAVERSAL,   //0x0F, 0x17
		SEARCH_LONGID,          //0x0F, 0x18
		SEARCH_REBUILDRESULTS,  //0x0F, 0x19
		SEARCH_LESSTHAN,        //0x0F, 0x1A
		SEARCH_GREATERTHAN,     //0x0F, 0x1B
		SEARCH_SCHEMA,          //0x0F, 0x1C
		SEARCH_SUPPORTED        //0x0F, 0x1D
	};
	
	public static final String GAL_DISPLAYNAME = "DisplayName";
	public static final String GAL_PHONE = "Phone";
	public static final String GAL_OFFICE = "Office";
	public static final String GAL_TITLE = "Title";
	public static final String GAL_COMPANY = "Company";
	public static final String GAL_ALIAS = "Alias";
	public static final String GAL_FIRSTNAME = "FirstName";
	public static final String GAL_LASTNAME = "LastName";
	public static final String GAL_HOMEPHONE = "HomePhone";
	public static final String GAL_MOBILEPHONE = "MobilePhone";
	public static final String GAL_EMAILADDRESS = "EmailAddress";
	
	//Code Page 10 - GAL
	public static final String[] CODEPAGE_10_TAGTABLE = {
		GAL_DISPLAYNAME,        //0x10, 0x05
		GAL_PHONE,              //0x10, 0x06
		GAL_OFFICE,             //0x10, 0x07
		GAL_TITLE,              //0x10, 0x08
		GAL_COMPANY,            //0x10, 0x09
		GAL_ALIAS,              //0x10, 0x0A
		GAL_FIRSTNAME,          //0x10, 0x0B
		GAL_LASTNAME,           //0x10, 0x0C
		GAL_HOMEPHONE,          //0x10, 0x0D
		GAL_MOBILEPHONE,        //0x10, 0x0E
		GAL_EMAILADDRESS,       //0x10, 0x0F
	};
	
	public static final String AIRSYNCBASE_BODYPREFERENCE = "BodyPreference";
	public static final String AIRSYNCBASE_TYPE = "Type";
	public static final String AIRSYNCBASE_TRUNCATIONSIZE = "TruncationSize";
	public static final String AIRSYNCBASE_ALLORNONE = "AllOrNone";
	public static final String AIRSYNCBASE_BODY = "Body";
	public static final String AIRSYNCBASE_DATA = "Data";
	public static final String AIRSYNCBASE_ESTIMATEDDATASIZE = "EstimatedDataSize";
	public static final String AIRSYNCBASE_TRUNCATED = "Truncated";
	public static final String AIRSYNCBASE_ATTACHMENTS = "Attachments";
	public static final String AIRSYNCBASE_ATTACHMENT = "Attachment";
	public static final String AIRSYNCBASE_DISPLAYNAME = "DisplayName";
	public static final String AIRSYNCBASE_FILEREFERENCE = "FileReference";
	public static final String AIRSYNCBASE_METHOD = "Method";
	public static final String AIRSYNCBASE_CONTENTID = "ContentId";
	public static final String AIRSYNCBASE_ISINLINE = "IsInline";
	public static final String AIRSYNCBASE_NATIVEBODYTYPE = "NativeBodyType";
	public static final String AIRSYNCBASE_CONTENTTYPE = "ContentType";
	
	//Code Page 0x11 - AirSyncBase
	public static final String[] CODEPAGE_11_TAGTABLE = {
	    AIRSYNCBASE_BODYPREFERENCE,        //0x11, 0x05
	    AIRSYNCBASE_TYPE,                  //0x11, 0x06
	    AIRSYNCBASE_TRUNCATIONSIZE,        //0x11, 0x07
	    AIRSYNCBASE_ALLORNONE,             //0x11, 0x08
	    "P11T09",                          //0x11, 0x09
	    AIRSYNCBASE_BODY,                  //0x11, 0x0A
	    AIRSYNCBASE_DATA,                  //0x11, 0x0B
	    AIRSYNCBASE_ESTIMATEDDATASIZE,     //0x11, 0x0C
	    AIRSYNCBASE_TRUNCATED,             //0x11, 0x0D
	    AIRSYNCBASE_ATTACHMENTS,           //0x11, 0x0E
	    AIRSYNCBASE_ATTACHMENT,            //0x11, 0x0F
	    AIRSYNCBASE_DISPLAYNAME,           //0x11, 0x10
	    AIRSYNCBASE_FILEREFERENCE,         //0x11, 0x11
	    AIRSYNCBASE_METHOD,                //0x11, 0x12
	    AIRSYNCBASE_CONTENTID,             //0x11, 0x13
	    "P11T14",                          //0x11, 0x14
	    AIRSYNCBASE_ISINLINE,              //0x11, 0x15
	    AIRSYNCBASE_NATIVEBODYTYPE,        //0x11, 0x16
	    AIRSYNCBASE_CONTENTTYPE            //0x11, 0x17
	};
	
	public static final String SETTINGS_SETTINGS = "Settings";
	public static final String SETTINGS_STATUS = "Status";
	public static final String SETTINGS_GET = "Get";
	public static final String SETTINGS_SET = "Set";
	public static final String SETTINGS_OOF = "Oof";
	public static final String SETTINGS_OOFSTATE = "OofState";
	public static final String SETTINGS_STARTTIME = "StartTime";
	public static final String SETTINGS_ENDTIME = "EndTime";
	public static final String SETTINGS_OOFMESSAGE = "OofMessage";
	public static final String SETTINGS_APPLIESTOINTERNAL = "AppliesToInternal";
	public static final String SETTINGS_APPLIESTOEXTERNALKNOWN = "AppliesToExternalKnown";
	public static final String SETTINGS_APPLIESTOEXTERNALUNKNOWN = "AppliesToExternalUnknown";
	public static final String SETTINGS_ENABLED = "Enabled";
	public static final String SETTINGS_REPLYMESSAGE = "ReplyMessage";
	public static final String SETTINGS_BODYTYPE = "BodyType";
	public static final String SETTINGS_DEVICEINFORMATION = "DeviceInformation";
	public static final String SETTINGS_MODEL = "Model";
	public static final String SETTINGS_IMEI = "IMEI";
	public static final String SETTINGS_FRIENDLYNAME = "FriendlyName";
	public static final String SETTINGS_OS = "OS";
	public static final String SETTINGS_OSLANGUAGE = "OSLanguage";
	public static final String SETTINGS_PHONENUMBER = "PhoneNumber";
	public static final String SETTINGS_USERINFORMATION = "UserInformation";
	public static final String SETTINGS_EMAILADDRESSES = "EmailAddresses";
	public static final String SETTINGS_SMTPADDRESS = "SmtpAddress";
	
	//Code Page 0x12 - Settings
    public static final String[] CODEPAGE_12_TAGTABLE = {
        SETTINGS_SETTINGS,                  //0x12, 0x05
        SETTINGS_STATUS,                    //0x12, 0x06
        SETTINGS_GET,                       //0x12, 0x07
        SETTINGS_SET,                       //0x12, 0x08
        SETTINGS_OOF,                       //0x12, 0x09
        SETTINGS_OOFSTATE,                  //0x12, 0x0A
        SETTINGS_STARTTIME,                 //0x12, 0x0B
        SETTINGS_ENDTIME,                   //0x12, 0x0C
        SETTINGS_OOFMESSAGE,                //0x12, 0x0D
        SETTINGS_APPLIESTOINTERNAL,         //0x12, 0x0E
        SETTINGS_APPLIESTOEXTERNALKNOWN,    //0x12, 0x0F
        SETTINGS_APPLIESTOEXTERNALUNKNOWN,  //0x12, 0x10
        SETTINGS_ENABLED,                   //0x12, 0x11
        SETTINGS_REPLYMESSAGE,              //0x12, 0x12
        SETTINGS_BODYTYPE,                  //0x12, 0x13
        "P12T14",                           //0x12, 0x14
        "P12T15",                           //0x12, 0x15
        SETTINGS_DEVICEINFORMATION,         //0x12, 0x16
        SETTINGS_MODEL,                     //0x12, 0x17
        SETTINGS_IMEI,                      //0x12, 0x18
        SETTINGS_FRIENDLYNAME,              //0x12, 0x19
        SETTINGS_OS,                        //0x12, 0x1A
        SETTINGS_OSLANGUAGE,                //0x12, 0x1B
        SETTINGS_PHONENUMBER,               //0x12, 0x1C
        SETTINGS_USERINFORMATION,           //0x12, 0x1D
        SETTINGS_EMAILADDRESSES,            //0x12, 0x1E
        SETTINGS_SMTPADDRESS                //0x12, 0x1F
    };
	
    
	public static final String ITEMOPERATIONS_ITEMOPERATIONS = "ItemOperations";
	public static final String ITEMOPERATIONS_FETCH = "Fetch";
	public static final String ITEMOPERATIONS_STORE = "Store";
	public static final String ITEMOPERATIONS_OPTIONS = "Options";
	public static final String ITEMOPERATIONS_RANGE = "Range";
	public static final String ITEMOPERATIONS_TOTAL = "Total";
	public static final String ITEMOPERATIONS_PROPERTIES = "Properties";
	public static final String ITEMOPERATIONS_DATA = "Data";
	public static final String ITEMOPERATIONS_STATUS = "Status";
	public static final String ITEMOPERATIONS_RESPONSE = "Response";
	public static final String ITEMOPERATIONS_PART = "Part";
	
	//Code Page 0x14 - ItemOperations
	public static final String[] CODEPAGE_14_TAGTABLE = {
	    ITEMOPERATIONS_ITEMOPERATIONS,     //0x14, 0x05
	    ITEMOPERATIONS_FETCH,              //0x14, 0x06
	    ITEMOPERATIONS_STORE,              //0x14, 0x07
	    ITEMOPERATIONS_OPTIONS,            //0x14, 0x08
	    ITEMOPERATIONS_RANGE,              //0x14, 0x09
	    ITEMOPERATIONS_TOTAL,              //0x14, 0x0A
	    ITEMOPERATIONS_PROPERTIES,         //0x14, 0x0B
	    ITEMOPERATIONS_DATA,               //0x14, 0x0C
	    ITEMOPERATIONS_STATUS,             //0x14, 0x0D
	    ITEMOPERATIONS_RESPONSE,           //0x14, 0x0E
	    "P14T0F",                          //0x14, 0x0F
	    "P14T10",                          //0x14, 0x10
	    ITEMOPERATIONS_PART,               //0x14, 0x11
	};

	
	public static final String[][] TAGTABLES = {
		CODEPAGE_00_TAGTABLE,
		CODEPAGE_01_TAGTABLE,
		CODEPAGE_02_TAGTABLE,
		CODEPAGE_03_TAGTABLE,
		CODEPAGE_04_TAGTABLE,
		CODEPAGE_05_TAGTABLE,
		CODEPAGE_06_TAGTABLE,
		CODEPAGE_07_TAGTABLE,
		CODEPAGE_08_TAGTABLE,
		CODEPAGE_09_TAGTABLE,
		null,
		null,
		CODEPAGE_0C_TAGTABLE,
		CODEPAGE_0D_TAGTABLE,
		CODEPAGE_0E_TAGTABLE,
		CODEPAGE_0F_TAGTABLE,
		CODEPAGE_10_TAGTABLE,
		CODEPAGE_11_TAGTABLE,
		CODEPAGE_12_TAGTABLE,
		null,
		CODEPAGE_14_TAGTABLE
	};
}
