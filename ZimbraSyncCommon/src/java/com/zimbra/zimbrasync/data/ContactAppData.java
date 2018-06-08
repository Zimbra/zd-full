/*
 * 
 */
package com.zimbra.zimbrasync.data;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.zimbrasync.util.SyncUtil;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class ContactAppData extends AppData {
    private static Map<String, String> zimbraToXsyncMap = new HashMap<String, String>();
    private static Map<String, String> xsyncToZimbraMap = new HashMap<String, String>();
    private static Set<String> nsPoomContacts2 = new HashSet<String>();
    
    static {
        zimbraToXsyncMap.put("assistantPhone", POOMCONTACTS_ASSISTNAMEPHONENUMBER);
        zimbraToXsyncMap.put(ContactConstants.A_birthday, POOMCONTACTS_BIRTHDAY);
        //zimbraToAsyncMap.put(ContactConstants.A_callbackPhone, POOMCONTACTS_);
        zimbraToXsyncMap.put(ContactConstants.A_carPhone, POOMCONTACTS_CARPHONENUMBER);
        zimbraToXsyncMap.put(ContactConstants.A_company, POOMCONTACTS_COMPANYNAME);
        zimbraToXsyncMap.put(ContactConstants.A_companyPhone, POOMCONTACTS2_COMPANYMAINPHONE);
        zimbraToXsyncMap.put(ContactConstants.A_department, POOMCONTACTS_DEPARTMENT);
        zimbraToXsyncMap.put(ContactConstants.A_email, POOMCONTACTS_EMAIL1ADDRESS);
        zimbraToXsyncMap.put(ContactConstants.A_email2, POOMCONTACTS_EMAIL2ADDRESS);
        zimbraToXsyncMap.put(ContactConstants.A_email3, POOMCONTACTS_EMAIL3ADDRESS);
        //zimbraToAsyncMap.put(ContactConstants.A_fileAs, POOMCONTACTS_);
        zimbraToXsyncMap.put(ContactConstants.A_firstName, POOMCONTACTS_FIRSTNAME);
        zimbraToXsyncMap.put(ContactConstants.A_fullName, POOMCONTACTS_FILEAS);
        zimbraToXsyncMap.put(ContactConstants.A_homeCity, POOMCONTACTS_HOMECITY);
        zimbraToXsyncMap.put(ContactConstants.A_homeCountry, POOMCONTACTS_HOMECOUNTRY);
        zimbraToXsyncMap.put(ContactConstants.A_homeFax, POOMCONTACTS_HOMEFAXNUMBER);
        zimbraToXsyncMap.put(ContactConstants.A_homePhone, POOMCONTACTS_HOMEPHONENUMBER);
        zimbraToXsyncMap.put(ContactConstants.A_homePhone2, POOMCONTACTS_HOME2PHONENUMBER);
        zimbraToXsyncMap.put(ContactConstants.A_homePostalCode, POOMCONTACTS_HOMEPOSTALCODE);
        zimbraToXsyncMap.put(ContactConstants.A_homeState, POOMCONTACTS_HOMESTATE);
        zimbraToXsyncMap.put(ContactConstants.A_homeStreet, POOMCONTACTS_HOMESTREET);
        zimbraToXsyncMap.put(ContactConstants.A_homeURL, POOMCONTACTS_WEBPAGE);
        //zimbraToAsyncMap.put(ContactConstants.A_initials, POOMCONTACTS_);
        zimbraToXsyncMap.put(ContactConstants.A_jobTitle, POOMCONTACTS_JOBTITLE);
        zimbraToXsyncMap.put(ContactConstants.A_lastName, POOMCONTACTS_LASTNAME);
        zimbraToXsyncMap.put(ContactConstants.A_middleName, POOMCONTACTS_MIDDLENAME);
        zimbraToXsyncMap.put(ContactConstants.A_mobilePhone, POOMCONTACTS_MOBILEPHONENUMBER);
        zimbraToXsyncMap.put(ContactConstants.A_namePrefix, POOMCONTACTS_TITLE);
        zimbraToXsyncMap.put(ContactConstants.A_nameSuffix, POOMCONTACTS_SUFFIX);
        zimbraToXsyncMap.put(ContactConstants.A_nickname, POOMCONTACTS2_NICKNAME);
        zimbraToXsyncMap.put(ContactConstants.A_notes, POOMCONTACTS_BODY);
        zimbraToXsyncMap.put(ContactConstants.A_office, POOMCONTACTS_OFFICELOCATION);
        zimbraToXsyncMap.put(ContactConstants.A_otherCity, POOMCONTACTS_OTHERCITY);
        zimbraToXsyncMap.put(ContactConstants.A_otherCountry, POOMCONTACTS_OTHERCOUNTRY);
        //zimbraToAsyncMap.put(ContactConstants.A_otherFax, POOMCONTACTS_);
        //zimbraToAsyncMap.put(ContactConstants.A_otherPhone, POOMCONTACTS_);
        zimbraToXsyncMap.put(ContactConstants.A_otherPostalCode, POOMCONTACTS_OTHERPOSTALCODE);
        zimbraToXsyncMap.put(ContactConstants.A_otherState, POOMCONTACTS_OTHERSTATE);
        zimbraToXsyncMap.put(ContactConstants.A_otherStreet, POOMCONTACTS_OTHERSTREET);
        //zimbraToAsyncMap.put(ContactConstants.A_otherURL, POOMCONTACTS_);
        zimbraToXsyncMap.put(ContactConstants.A_pager, POOMCONTACTS_PAGERNUMBER);
        zimbraToXsyncMap.put(ContactConstants.A_workCity, POOMCONTACTS_BUSINESSCITY);
        zimbraToXsyncMap.put(ContactConstants.A_workCountry, POOMCONTACTS_BUSINESSCOUNTRY);
        zimbraToXsyncMap.put(ContactConstants.A_workFax, POOMCONTACTS_BUSINESSFAXNUMBER);
        zimbraToXsyncMap.put(ContactConstants.A_workPhone, POOMCONTACTS_BUSINESSPHONENUMBER);
        zimbraToXsyncMap.put(ContactConstants.A_workPhone2, POOMCONTACTS_BUSINESS2PHONENUMBER);
        zimbraToXsyncMap.put(ContactConstants.A_workPostalCode, POOMCONTACTS_BUSINESSPOSTALCODE);
        zimbraToXsyncMap.put(ContactConstants.A_workState, POOMCONTACTS_BUSINESSSTATE);
        zimbraToXsyncMap.put(ContactConstants.A_workStreet, POOMCONTACTS_BUSINESSSTREET);
        //zimbraToAsyncMap.put(ContactConstants.A_workURL, POOMCONTACTS_);
        zimbraToXsyncMap.put(ContactConstants.A_imAddress1, POOMCONTACTS2_IMADDRESS);
        zimbraToXsyncMap.put(ContactConstants.A_imAddress2, POOMCONTACTS2_IMADDRESS2);
        zimbraToXsyncMap.put(ContactConstants.A_imAddress3, POOMCONTACTS2_IMADDRESS3);
        zimbraToXsyncMap.put(POOMCONTACTS_ANNIVERSARY, POOMCONTACTS_ANNIVERSARY);
        zimbraToXsyncMap.put(POOMCONTACTS_ASSISTANTNAME, POOMCONTACTS_ASSISTANTNAME);
        //zimbraToAsyncMap.put(POOMCONTACTS_CATEGORIES, POOMCONTACTS_CATEGORIES);
        zimbraToXsyncMap.put(POOMCONTACTS_CHILDREN, POOMCONTACTS_CHILDREN);
        zimbraToXsyncMap.put(POOMCONTACTS_RADIOPHONENUMBER, POOMCONTACTS_RADIOPHONENUMBER);
        zimbraToXsyncMap.put(POOMCONTACTS_SPOUSE, POOMCONTACTS_SPOUSE);
        zimbraToXsyncMap.put(POOMCONTACTS_YOMICOMPANYNAME, POOMCONTACTS_YOMICOMPANYNAME);
        zimbraToXsyncMap.put(POOMCONTACTS_YOMIFIRSTNAME, POOMCONTACTS_YOMIFIRSTNAME);
        zimbraToXsyncMap.put(POOMCONTACTS_YOMILASTNAME, POOMCONTACTS_YOMILASTNAME);
        //zimbraToAsyncMap.put(POOMCONTACTS2_ACCOUNTNAME, POOMCONTACTS2_ACCOUNTNAME);
        //zimbraToAsyncMap.put(POOMCONTACTS2_CUSTOMERID, POOMCONTACTS2_CUSTOMERID);
        //zimbraToAsyncMap.put(POOMCONTACTS2_GOVERNMENTID, POOMCONTACTS2_GOVERNMENTID);
        zimbraToXsyncMap.put(POOMCONTACTS2_MANAGERNAME, POOMCONTACTS2_MANAGERNAME);
        
        xsyncToZimbraMap.put(POOMCONTACTS_ANNIVERSARY, POOMCONTACTS_ANNIVERSARY);
        xsyncToZimbraMap.put(POOMCONTACTS_ASSISTANTNAME, POOMCONTACTS_ASSISTANTNAME);
        xsyncToZimbraMap.put(POOMCONTACTS_ASSISTNAMEPHONENUMBER, "assistantPhone");
        xsyncToZimbraMap.put(POOMCONTACTS_BIRTHDAY, ContactConstants.A_birthday);
        xsyncToZimbraMap.put(POOMCONTACTS_BODY, ContactConstants.A_notes);
        xsyncToZimbraMap.put(POOMCONTACTS_BUSINESS2PHONENUMBER, ContactConstants.A_workPhone2);
        xsyncToZimbraMap.put(POOMCONTACTS_BUSINESSCITY, ContactConstants.A_workCity);
        xsyncToZimbraMap.put(POOMCONTACTS_BUSINESSCOUNTRY, ContactConstants.A_workCountry);
        xsyncToZimbraMap.put(POOMCONTACTS_BUSINESSPOSTALCODE, ContactConstants.A_workPostalCode);
        xsyncToZimbraMap.put(POOMCONTACTS_BUSINESSSTATE, ContactConstants.A_workState);
        xsyncToZimbraMap.put(POOMCONTACTS_BUSINESSSTREET, ContactConstants.A_workStreet);
        xsyncToZimbraMap.put(POOMCONTACTS_BUSINESSFAXNUMBER, ContactConstants.A_workFax);
        xsyncToZimbraMap.put(POOMCONTACTS_BUSINESSPHONENUMBER, ContactConstants.A_workPhone);
        xsyncToZimbraMap.put(POOMCONTACTS_CARPHONENUMBER, ContactConstants.A_carPhone);
        xsyncToZimbraMap.put(POOMCONTACTS_CHILDREN, POOMCONTACTS_CHILDREN);
        xsyncToZimbraMap.put(POOMCONTACTS_COMPANYNAME, ContactConstants.A_company);
        xsyncToZimbraMap.put(POOMCONTACTS_DEPARTMENT, ContactConstants.A_department);
        xsyncToZimbraMap.put(POOMCONTACTS_EMAIL1ADDRESS, ContactConstants.A_email);
        xsyncToZimbraMap.put(POOMCONTACTS_EMAIL2ADDRESS, ContactConstants.A_email2);
        xsyncToZimbraMap.put(POOMCONTACTS_EMAIL3ADDRESS, ContactConstants.A_email3);
        xsyncToZimbraMap.put(POOMCONTACTS_FILEAS, ContactConstants.A_fullName);
        xsyncToZimbraMap.put(POOMCONTACTS_FIRSTNAME, ContactConstants.A_firstName);
        xsyncToZimbraMap.put(POOMCONTACTS_HOME2PHONENUMBER, ContactConstants.A_homePhone2);
        xsyncToZimbraMap.put(POOMCONTACTS_HOMECITY, ContactConstants.A_homeCity);
        xsyncToZimbraMap.put(POOMCONTACTS_HOMECOUNTRY, ContactConstants.A_homeCountry);
        xsyncToZimbraMap.put(POOMCONTACTS_HOMEPOSTALCODE, ContactConstants.A_homePostalCode);
        xsyncToZimbraMap.put(POOMCONTACTS_HOMESTATE, ContactConstants.A_homeState);
        xsyncToZimbraMap.put(POOMCONTACTS_HOMESTREET, ContactConstants.A_homeStreet);
        xsyncToZimbraMap.put(POOMCONTACTS_HOMEFAXNUMBER, ContactConstants.A_homeFax);
        xsyncToZimbraMap.put(POOMCONTACTS_HOMEPHONENUMBER, ContactConstants.A_homePhone);
        xsyncToZimbraMap.put(POOMCONTACTS_JOBTITLE, ContactConstants.A_jobTitle);
        xsyncToZimbraMap.put(POOMCONTACTS_LASTNAME, ContactConstants.A_lastName);
        xsyncToZimbraMap.put(POOMCONTACTS_MIDDLENAME, ContactConstants.A_middleName);
        xsyncToZimbraMap.put(POOMCONTACTS_MOBILEPHONENUMBER, ContactConstants.A_mobilePhone);
        xsyncToZimbraMap.put(POOMCONTACTS_OFFICELOCATION, ContactConstants.A_office);
        xsyncToZimbraMap.put(POOMCONTACTS_OTHERCITY, ContactConstants.A_otherCity);
        xsyncToZimbraMap.put(POOMCONTACTS_OTHERCOUNTRY, ContactConstants.A_otherCountry);
        xsyncToZimbraMap.put(POOMCONTACTS_OTHERPOSTALCODE, ContactConstants.A_otherPostalCode);
        xsyncToZimbraMap.put(POOMCONTACTS_OTHERSTATE, ContactConstants.A_otherState);
        xsyncToZimbraMap.put(POOMCONTACTS_OTHERSTREET, ContactConstants.A_otherStreet);
        xsyncToZimbraMap.put(POOMCONTACTS_PAGERNUMBER, ContactConstants.A_pager);
        xsyncToZimbraMap.put(POOMCONTACTS_RADIOPHONENUMBER, POOMCONTACTS_RADIOPHONENUMBER);
        xsyncToZimbraMap.put(POOMCONTACTS_SPOUSE, POOMCONTACTS_SPOUSE);
        xsyncToZimbraMap.put(POOMCONTACTS_SUFFIX, ContactConstants.A_nameSuffix);
        xsyncToZimbraMap.put(POOMCONTACTS_TITLE, ContactConstants.A_namePrefix);
        xsyncToZimbraMap.put(POOMCONTACTS_WEBPAGE, ContactConstants.A_homeURL);
        xsyncToZimbraMap.put(POOMCONTACTS_YOMICOMPANYNAME, POOMCONTACTS_YOMICOMPANYNAME);
        xsyncToZimbraMap.put(POOMCONTACTS_YOMIFIRSTNAME, POOMCONTACTS_YOMIFIRSTNAME);
        xsyncToZimbraMap.put(POOMCONTACTS_YOMILASTNAME, POOMCONTACTS_YOMILASTNAME);
        xsyncToZimbraMap.put(POOMCONTACTS_RTF, ContactConstants.A_notes);
        xsyncToZimbraMap.put(POOMCONTACTS2_COMPANYMAINPHONE, ContactConstants.A_companyPhone);
        xsyncToZimbraMap.put(POOMCONTACTS2_IMADDRESS, ContactConstants.A_imAddress1);
        xsyncToZimbraMap.put(POOMCONTACTS2_IMADDRESS2, ContactConstants.A_imAddress2);
        xsyncToZimbraMap.put(POOMCONTACTS2_IMADDRESS3, ContactConstants.A_imAddress3);
        xsyncToZimbraMap.put(POOMCONTACTS2_NICKNAME, ContactConstants.A_nickname);
        
        nsPoomContacts2.add(POOMCONTACTS2_CUSTOMERID);
        nsPoomContacts2.add(POOMCONTACTS2_GOVERNMENTID);
        nsPoomContacts2.add(POOMCONTACTS2_IMADDRESS);
        nsPoomContacts2.add(POOMCONTACTS2_IMADDRESS2);
        nsPoomContacts2.add(POOMCONTACTS2_IMADDRESS3);
        nsPoomContacts2.add(POOMCONTACTS2_MANAGERNAME);
        nsPoomContacts2.add(POOMCONTACTS2_COMPANYMAINPHONE);
        nsPoomContacts2.add(POOMCONTACTS2_ACCOUNTNAME);
        nsPoomContacts2.add(POOMCONTACTS2_NICKNAME);
    }
    
    private static String getZimbraAttrName(String xsyncAttrName) {
        String attr = xsyncToZimbraMap.get(xsyncAttrName);
        if (attr != null)
            return attr;
        return xsyncAttrName;
    }
    
    private static String getXsyncAttrName(String zimbraAttrName) {
        return zimbraToXsyncMap.get(zimbraAttrName);
    }
    
    private static String getNamespace(String xsyncAttrName) {
        return nsPoomContacts2.contains(xsyncAttrName) ? NAMESPACE_POOMCONTACTS2 : NAMESPACE_POOMCONTACTS;
    }
    
    
    Map<String, String> attrs = new HashMap<String, String>();
    byte[] image;
    boolean isImageCleared;
    ProtocolVersion protocolVersion = new ProtocolVersion("2.5");
    
    public void addAttr(String key, String val) {
        if (key == null || key.length() == 0) {
            ZimbraLog.sync.warn("Contact attribute key is null");
            return;
        }
        if (val == null) {
            ZimbraLog.sync.warn("Contact attribute key=%s has null value", key);
            return;
        }
        attrs.put(key, val);
    }
    
    //<?xml version="1.0" encoding="utf-8"?><Sync xmlns="AirSync:" xmlns:A="POOMCONTACTS:">
    //    <Collections>
    //        <Collection>
    //            <Class>Contacts</Class>
    //            <SyncKey>{D7ECF3B7-8542-4AA6-8B3E-4EC83E1BC822}2</SyncKey>
    //            <CollectionId>0286243fce792f4a82f3686ac614bc47-2737</CollectionId>
    //            <DeletesAsMoves/>
    //            <GetChanges/>
    //            <WindowSize>100</WindowSize>
    //            <Options>
    //                <Truncation>4</Truncation>
    //                <RtfTruncation>4</RtfTruncation>
    //                <Conflict>1</Conflict>
    //            </Options>
    //            <Commands>
    //                <Add>
    //                    <ClientId>3400007900000002</ClientId>
    //                    <ApplicationData>
    // ---->                  <A:Anniversary>2005-10-17T02:06:06.000Z</A:Anniversary>
    //                        <A:AssistantName>Go Getter</A:AssistantName>
    //                        <A:AssistnamePhoneNumber>6504445353</A:AssistnamePhoneNumber>
    //                        <A:Birthday>1980-11-17T02:05:53.000Z</A:Birthday>
    //                        <A:Body>What to say</A:Body>
    //                        <A:Business2PhoneNumber>6506667777</A:Business2PhoneNumber>
    //                        <A:BusinessCity>Springfield</A:BusinessCity>
    //                        <A:BusinessCountry>USA</A:BusinessCountry>
    //                        <A:BusinessPostalCode>90000</A:BusinessPostalCode>
    //                        <A:BusinessState>CA</A:BusinessState>
    //                        <A:BusinessStreet>3000 S. Broadway St.</A:BusinessStreet>
    //                        <A:BusinessFaxNumber>6505773264</A:BusinessFaxNumber>
    //                        <A:BusinessPhoneNumber>6504447777</A:BusinessPhoneNumber>
    //                        <A:CarPhoneNumber>5462784</A:CarPhoneNumber>
    //                        <A:Categories>
    //                            <A:Category>Miscellaneous</A:Category>
    //                        </A:Categories>
    //                        <A:Children>
    //                            <A:Child>Many More</A:Child>
    //                        </A:Children>
    //                        <A:CompanyName>Foo Foods</A:CompanyName>
    //                        <A:Department>Shipping</A:Department>
    //                        <A:FileAs>Doe, John</A:FileAs>
    //                        <A:FirstName>John</A:FirstName>
    //                        <A:Home2PhoneNumber>5058884444</A:Home2PhoneNumber>
    //                        <A:HomeCity>Fremont</A:HomeCity>
    //                        <A:HomeCountry>USA</A:HomeCountry>
    //                        <A:HomePostalCode>95000</A:HomePostalCode>
    //                        <A:HomeState>CA</A:HomeState>
    //                        <A:HomeStreet>432 Quiet St.</A:HomeStreet>
    //                        <A:HomeFaxNumber>5056224321</A:HomeFaxNumber>
    //                        <A:HomePhoneNumber>5059996666</A:HomePhoneNumber>
    //                        <A:JobTitle>Don't know</A:JobTitle>
    //                        <A:LastName>Doe</A:LastName>
    //                        <A:MiddleName/>
    //                        <A:MobilePhoneNumber>6501234567</A:MobilePhoneNumber>
    //                        <A:OfficeLocation/>
    //                        <A:OtherCity>Which City</A:OtherCity>
    //                        <A:OtherCountry>US</A:OtherCountry>
    //                        <A:OtherPostalCode>00000</A:OtherPostalCode>
    //                        <A:OtherState>MA</A:OtherState>
    //                        <A:OtherStreet>3 Which Ave.</A:OtherStreet>
    //                        <A:PagerNumber>4752356</A:PagerNumber>
    //                        <A:RadioPhoneNumber>6508765432</A:RadioPhoneNumber>
    //                        <A:Spouse>Wife Two</A:Spouse>
    //                        <A:Suffix/>
    //                        <A:Title/>
    //                        <A:WebPage>http://bar.foo.com</A:WebPage>
    //                        <A:YomiCompanyName/>
    //                        <A:YomiFirstName/>
    //                        <A:YomiLastName/>
    //                        <A:Email1Address>john.doe@foo.com</A:Email1Address>
    //                        <A:Email2Address>jd@yahoo.com</A:Email2Address>
    //                        <A:Email3Address>jd@hotmail.com</A:Email3Address>
    // <----              </ApplicationData>
    //                </Add>
    //                <Change>
    //                    <ServerId>rid:ca8f94d7f36b83489b8b1e3bb4715c02000000510801</ServerId>
    //                    <ApplicationData>
    // ---->                  <A:Anniversary>1980-02-29T08:00:00.000Z</A:Anniversary>
    //                        <A:AssistantName>Don't have one</A:AssistantName>
    //                        <A:AssistnamePhoneNumber/>
    //                        <A:Birthday>1945-01-23T08:00:00.000Z</A:Birthday>
    //                        <A:Body/>
    //                        <A:Business2PhoneNumber/>
    //                        <A:BusinessCity>San Mateo</A:BusinessCity>
    //                        <A:BusinessCountry>United States of America</A:BusinessCountry>
    //                        <A:BusinessPostalCode>94000-1234</A:BusinessPostalCode>
    //                        <A:BusinessState>CA</A:BusinessState>
    //                        <A:BusinessStreet>123 Main St.</A:BusinessStreet>
    //                        <A:BusinessFaxNumber>(650) 555-1313</A:BusinessFaxNumber>
    //                        <A:BusinessPhoneNumber>(650) 555-1212</A:BusinessPhoneNumber>
    //                        <A:CarPhoneNumber/>
    //                        <A:CompanyName>You Know Where, Inc.</A:CompanyName>
    //                        <A:Department>Pretty Good</A:Department>
    //                        <A:FileAs>Guy, Good</A:FileAs>
    //                        <A:FirstName>Mingzhe</A:FirstName>
    //                        <A:Home2PhoneNumber/>
    //                        <A:HomeCity/>
    //                        <A:HomeCountry/>
    //                        <A:HomePostalCode/>
    //                        <A:HomeState/>
    //                        <A:HomeStreet/>
    //                        <A:HomeFaxNumber/>
    //                        <A:HomePhoneNumber>(408) 339-4416</A:HomePhoneNumber>
    //                        <A:JobTitle>Chief of Something</A:JobTitle>
    //                        <A:LastName>Guy</A:LastName>
    //                        <A:MiddleName>Good</A:MiddleName>
    //                        <A:MobilePhoneNumber>(650) 777-8686</A:MobilePhoneNumber>
    //                        <A:OfficeLocation>Cube</A:OfficeLocation>
    //                        <A:OtherCity/>
    //                        <A:OtherCountry/>
    //                        <A:OtherPostalCode/>
    //                        <A:OtherState/>
    //                        <A:OtherStreet/>
    //                        <A:PagerNumber/>
    //                        <A:RadioPhoneNumber/>
    //                        <A:Spouse>Wife One</A:Spouse>
    //                        <A:Suffix>I</A:Suffix>
    //                        <A:Title>Mr.</A:Title>
    //                        <A:WebPage>http://www.zimbra.com</A:WebPage>
    //                        <A:YomiCompanyName/>
    //                        <A:YomiFirstName/>
    //                        <A:YomiLastName/>
    //                        <A:Email1Address>"gg@zimbra.com" &lt;gg@zimbra.com&gt;</A:Email1Address>
    //                        <A:Email2Address/>
    // <----                  <A:Email3Address/>
    //                    </ApplicationData>
    //                </Change>
    //            </Commands>
    //        </Collection>
    //    </Collections>
    //</Sync>
    //
    // 12.0
    //<?xml version="1.0" encoding="utf-8"?><Sync xmlns="AirSync:" xmlns:A="AirSyncBase:" xmlns:B="POOMCONTACTS:">
    //    <Collections>
    //        <Collection>
    //            <Class>Contacts</Class>
    //            <SyncKey>{F7867E40-6B65-3884-9CE9-2E7E2271CC75}2</SyncKey>
    //            <CollectionId>7</CollectionId>
    //            <DeletesAsMoves/>
    //            <GetChanges/>
    //            <WindowSize>100</WindowSize>
    //            <Options>
    //                <A:BodyPreference>
    //                    <A:Type>1</A:Type>
    //                    <A:TruncationSize>5120</A:TruncationSize>
    //                </A:BodyPreference>
    //                <A:BodyPreference>
    //                    <A:Type>3</A:Type>
    //                    <A:TruncationSize>5120</A:TruncationSize>
    //                    <A:AllOrNone>1</A:AllOrNone>
    //                </A:BodyPreference>
    //                <Conflict>1</Conflict>
    //            </Options>
    //            <Commands>
    //                <Add>
    //                    <ClientId>2147483652</ClientId>
    //                    <ApplicationData>
    //                        <A:Body>
    //                            <A:Type>3</A:Type>
    //                            <A:Data>3wAAAC0CAABMWkZ1NvMtWj8ACQMwAQMB9wKnAgBjaBEKwHNldALRcHJx4DAgVGFoA3ECgwBQ6wNUDzcyD9MyBgAGwwKDpxIBA+MReDA0EhUgAoArApEI5jsJbzAVwzEyvjgJtBdCCjIXQRb0ORIAHxeEGOEYExjgFcMyNTX/CbQaYgoyGmEaHBaKCaUa9v8c6woUG3YdTRt/Hwwabxbt/xyPF7gePxg4JY0YVyRMKR+dJfh9CoEBMAOyMTYDMYksgSc0CzAnNzktQPkMASc2EgAtkBqALhAUYBcuky2gCoV9L8A=</A:Data>
    //                        </A:Body>
    //                        <B:CompanyName>Zimbra</B:CompanyName>
    //                        <B:Email1Address>sudipto@zimbra.com</B:Email1Address>
    //                        <B:FileAs>Mukhopadhyay, Sudipto</B:FileAs>
    //                        <B:FirstName>Sudipto</B:FirstName>
    //                        <B:LastName>Mukhopadhyay</B:LastName>
    //                        <B:Title>Mr</B:Title>
    //                        <B:Picture/>
    //                    </ApplicationData>
    //                </Add>
    //            </Commands>
    //        </Collection>
    //    </Collections>
    //</Sync>
    //
    public void parse(BinaryParser parser) throws BinaryCodecException, IOException {
        while (parser.next() == START_TAG) {
            String asyncName = parser.getName();
            String value = null;
            
            if (asyncName.equals(POOMCONTACTS_BODY) && NAMESPACE_POOMCONTACTS.equals(parser.getNamespace())) {
                value = parser.nextText();
                if (value != null && value.length() > 0)
                    addAttr(getZimbraAttrName(asyncName), value);
                else
                    addAttr(getZimbraAttrName(asyncName), "");
            } else if (asyncName.equals(POOMCONTACTS_RTF) && NAMESPACE_POOMCONTACTS.equals(parser.getNamespace())) {
                value = parser.nextText();
                if (value != null && value.length() > 0)
                    addAttr(getZimbraAttrName(asyncName), SyncUtil.decodeCompressedRtf(value));
                else
                    addAttr(getZimbraAttrName(asyncName), "");
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
                        if (BodyType.getBodyType(type) == BodyType.RTF) {
                            value = SyncUtil.decodeCompressedRtf(parser.nextText());
                            asyncName = POOMCONTACTS_RTF;
                        } else {
                            value = parser.nextText();
                            asyncName = POOMCONTACTS_BODY;
                        }
                        if (value != null && value.length() > 0)
                            addAttr(getZimbraAttrName(asyncName), value);
                        else
                            addAttr(getZimbraAttrName(asyncName), ""); 
                    } else
                        parser.skipElement();
                }
                parser.require(END_TAG, NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_BODY); // end of AirSyncBase:Body
            } else if (asyncName.equals(POOMCONTACTS_CATEGORIES) && NAMESPACE_POOMCONTACTS.equals(parser.getNamespace())) {
                parseCategories(parser, NAMESPACE_POOMCONTACTS, POOMCONTACTS_CATEGORY);
                parser.require(END_TAG, NAMESPACE_POOMCONTACTS, POOMCONTACTS_CATEGORIES);
            } else if (asyncName.equals(POOMCONTACTS_CHILDREN) && NAMESPACE_POOMCONTACTS.equals(parser.getNamespace())) {
                while (parser.next() == START_TAG && NAMESPACE_POOMCONTACTS.equals(parser.getNamespace())) {
                    if (POOMCONTACTS_CHILD.equals(parser.getName())) {
                        if (value == null) {
                            value = parser.nextText();
                        } else {
                            value += ',' + parser.nextText();
                        }
                    }
                }
                parser.require(END_TAG, NAMESPACE_POOMCONTACTS, POOMCONTACTS_CHILDREN);
                addAttr(getZimbraAttrName(asyncName), value);
            } else if (asyncName.equals(POOMCONTACTS_BIRTHDAY) && NAMESPACE_POOMCONTACTS.equals(parser.getNamespace())) {
                value = parser.nextText();
                try {
                    //client uses value in format of 1980-11-17T08:00:00.000Z
                    //need to convert to local date of 1980-11-17
                    value = SyncUtil.formattedUtcToLocalDate(value);
                    addAttr(getZimbraAttrName(asyncName), value);
                } catch (ParseException x) {
                    ZimbraLog.sync.warn("Can't parse birthday: " + value);
                }
            } else if (asyncName.equals(POOMCONTACTS_PICTURE) && NAMESPACE_POOMCONTACTS.equals(parser.getNamespace())) {
                String base64Jpeg = parser.nextText();
                if (base64Jpeg != null && base64Jpeg.length() > 0)
                    image = Base64.decodeBase64(base64Jpeg.getBytes());
                else
                    isImageCleared = true;
            } else if (NAMESPACE_POOMCONTACTS.equals(parser.getNamespace())) {
                value = parser.nextText();
                addAttr(getZimbraAttrName(asyncName), value);
            } else {
                parser.skipElement();
            }
        }
    }
    
    //<?xml version="1.0" encoding="utf-8"?><Sync xmlns="AirSync:" xmlns:A="POOMCONTACTS:">
    //    <Collections>
    //        <Collection>
    //            <Class>Contacts</Class>
    //            <SyncKey>{D7ECF3B7-8542-4AA6-8B3E-4EC83E1BC822}2</SyncKey>
    //            <CollectionId>0286243fce792f4a82f3686ac614bc47-2737</CollectionId>
    //            <Status>1</Status>
    //            <Responses>
    //                <Add>
    //                    <ClientId>3400007900000002</ClientId>
    //                    <ServerId>rid:0286243fce792f4a82f3686ac614bc47000000009203</ServerId>
    //                    <Status>1</Status>
    //                </Add>
    //            </Responses>
    //            <Commands>
    //                <Add>
    //                    <ServerId>rid:ca8f94d7f36b83489b8b1e3bb4715c02000000510801</ServerId>
    //                    <ApplicationData>
    // ---->                  <A:Anniversary>1980-02-29T08:00:00.000Z</A:Anniversary>
    //                        <A:Birthday>1945-01-23T08:00:00.000Z</A:Birthday>
    //                        <A:WebPage>http://www.zimbra.com</A:WebPage>
    //                        <A:BusinessCountry>United States of America</A:BusinessCountry>
    //                        <A:Department>No Good</A:Department>
    //                        <A:Email1Address>"gg@zimbra.com" &lt;gg@zimbra.com&gt;</A:Email1Address>
    //                        <A:BusinessFaxNumber>(650) 555-1313</A:BusinessFaxNumber>
    //                        <A:FileAs>Guy, Good</A:FileAs>
    //                        <A:FirstName>Good</A:FirstName>
    //                        <A:HomePhoneNumber>(408) 339-4416</A:HomePhoneNumber>
    //                        <A:BusinessCity>San Mateo</A:BusinessCity>
    //                        <A:MiddleName>Good</A:MiddleName>
    //                        <A:MobilePhoneNumber>(650) 777-8686</A:MobilePhoneNumber>
    //                        <A:Suffix>I</A:Suffix>
    //                        <A:CompanyName>You Know Where, Inc.</A:CompanyName>
    //                        <A:Title>Mr.</A:Title>
    //                        <A:BusinessPostalCode>94000-1234</A:BusinessPostalCode>
    //                        <A:AssistantName>Don't have one</A:AssistantName>
    //                        <A:LastName>Guy</A:LastName>
    //                        <A:Spouse>Wife One</A:Spouse>
    //                        <A:BusinessState>CA</A:BusinessState>
    //                        <A:BusinessStreet>123 Main St.</A:BusinessStreet>
    //                        <A:BusinessPhoneNumber>(650) 555-1212</A:BusinessPhoneNumber>
    //                        <A:JobTitle>Chief of Something</A:JobTitle>
    // <-----                 <A:OfficeLocation>Cube</A:OfficeLocation>
    //                    </ApplicationData>
    //                </Add>
    //            </Commands>
    //        </Collection>
    //    </Collections>
    //</Sync>
    public void encode(BinarySerializer serializer) throws BinaryCodecException, IOException {
        encode(serializer, true, true, false, -1, false);
    }
    
    public void encode(BinarySerializer serializer, boolean useCategories, boolean isEmptyElementOK, boolean useRtf, int truncationSize, boolean allOrNone)
            throws BinaryCodecException, IOException {
        if (useCategories)
            encodeCategories(serializer, NAMESPACE_POOMCONTACTS, POOMCONTACTS_CATEGORIES, POOMCONTACTS_CATEGORY);
        
        //We must always return FileAs even if it's not present in attrs.
        //There's no default in ZimbraSync client.
        String fileAs = null;
        try {
            fileAs = Contact.getFileAsString(attrs);
            if (fileAs == null || fileAs.length() == 0) {
                fileAs = attrs.get(ContactConstants.A_fileAs);
            }
        } catch (ServiceException x) {
            fileAs = attrs.get(ContactConstants.A_fileAs);
        }
        if (fileAs == null || fileAs.length() == 0) {
            fileAs = attrs.get(ContactConstants.A_email);
            if (fileAs == null || fileAs.length() == 0) {
                fileAs = attrs.get(ContactConstants.A_mobilePhone);
                if (fileAs == null || fileAs.length() == 0) {
                    fileAs = attrs.get(ContactConstants.A_imAddress1);
                    if (fileAs == null || fileAs.length() == 0) {
                        assert !attrs.isEmpty();
                        fileAs = attrs.values().iterator().next();
                        if (fileAs == null || fileAs.length() == 0)
                            fileAs = "Contact:" + attrs.toString(); //have to set to something non-empty
                    }
                }
            }
        }
        serializer.textElement(NAMESPACE_POOMCONTACTS, POOMCONTACTS_FILEAS, fileAs);
        
        Set<Entry<String, String>> entries = attrs.entrySet();
        for (Entry<String, String> entry : entries) {
            String asyncName = getXsyncAttrName(entry.getKey());
            if (asyncName != null) {
                
                if (asyncName.equals(POOMCONTACTS_FILEAS)) {
                    //We've done FileAs, so skip
                } else if (asyncName.equals(POOMCONTACTS_CHILDREN)) {
                    serializer.openTag(NAMESPACE_POOMCONTACTS, POOMCONTACTS_CHILDREN);
                    String[] kids = entry.getValue().split(",");
                    for (String kid : kids) {
                        serializer.textElement(NAMESPACE_POOMCONTACTS, POOMCONTACTS_CHILD, kid);
                    }
                    serializer.closeTag(); //Children
                } else if (asyncName.equals(POOMCONTACTS_BIRTHDAY)) {
                    String birthday = entry.getValue();
                    try {
                        //client uses value in format of 1980-11-17T08:00:00.000Z
                        //need to convert from local date of 1980-11-17
                        birthday = SyncUtil.localDateToFormattedUtc(birthday);
                        serializer.textElement(NAMESPACE_POOMCONTACTS, asyncName, birthday);
                    } catch (ParseException x) {
                        ZimbraLog.sync.warn("Can't parse birthday: " + birthday);
                    }
                } else if (asyncName.equals(POOMCONTACTS_BODY)) {
                    if (entry.getValue() != null && entry.getValue().trim().length() > 0) {
                        String body = entry.getValue().trim();
                        
                        if (protocolVersion.getMajor() >= 12) {
                            boolean skipBody = false;
                            String rtfBody = null;
                            int bodySize = body.length();
                            int bodyType = BodyType.getBodyType(BodyType.PlainText);
                            if (useRtf) {
                                rtfBody = SyncUtil.textToRtf(body);
                                bodySize = rtfBody.length();
                                bodyType = BodyType.getBodyType(BodyType.RTF);
                            }
                            
                            if (allOrNone && (bodySize > truncationSize))
                                skipBody = true;
                            
                            if (!skipBody) {
                                serializer.openTag(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_BODY);
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
                                // end of AirSyncBase:Body
                                serializer.closeTag();
                                serializer.integerElement(NAMESPACE_AIRSYNCBASE, AIRSYNCBASE_NATIVEBODYTYPE, BodyType.getBodyType(BodyType.PlainText));
                            }
                        } else {
                            serializer.textElement(getNamespace(asyncName), asyncName, body);
                        }
                    } else {
                        if (protocolVersion.getMajor() < 12 && isEmptyElementOK)
                            serializer.emptyElement(getNamespace(asyncName), asyncName); 
                    }
                } else {
                    String ns = getNamespace(asyncName);
                    if (entry.getValue() != null && entry.getValue().trim().length() > 0) {
                        serializer.textElement(ns, asyncName, entry.getValue().trim());
                    } else if (isEmptyElementOK) {
                        serializer.emptyElement(ns, asyncName);
                    }
                }
            }
        }
        
        if (image != null) {
            serializer.textElement(NAMESPACE_POOMCONTACTS, POOMCONTACTS_PICTURE, new String(Base64.encodeBase64(image)));
        }
    }
}
