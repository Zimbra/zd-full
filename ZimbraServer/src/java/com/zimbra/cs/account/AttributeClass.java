/*
 * 
 */
/**
 * 
 */
package com.zimbra.cs.account;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.accesscontrol.Right.RightType;

public enum AttributeClass {
    mailRecipient("zimbraMailRecipient",        false), 
    account("zimbraAccount",                    true), 
    alias("zimbraAlias",                        true), 
    distributionList("zimbraDistributionList",  true), 
    cos("zimbraCOS",                            true), 
    globalConfig("zimbraGlobalConfig",          true), 
    domain("zimbraDomain",                      true),
    securityGroup("zimbraSecurityGroup",        false), 
    server("zimbraServer",                      true), 
    mimeEntry("zimbraMimeEntry",                true), 
    objectEntry("zimbraObjectEntry",            false), 
    timeZone("zimbraTimeZone",                  false), 
    zimletEntry("zimbraZimletEntry",            true),
    calendarResource("zimbraCalendarResource",  true), 
    identity("zimbraIdentity",                  true), 
    dataSource("zimbraDataSource",              true), 
    pop3DataSource("zimbraPop3DataSource",      true), 
    imapDataSource("zimbraImapDataSource",      true),
    rssDataSource("zimbraRssDataSource",        true),
    liveDataSource("zimbraLiveDataSource",      true),
    galDataSource("zimbraGalDataSource",        true),
    signature("zimbraSignature",                true),
    xmppComponent("zimbraXMPPComponent",        true),
    aclTarget("zimbraAclTarget",                true),
    group("zimbraGroup",                        false);
    
    private static class TM {
        static Map<String, AttributeClass> sOCMap = new HashMap<String, AttributeClass>();
    }
    
    String mOCName;
    boolean mProvisionable;
    
    AttributeClass(String ocName, boolean provisionable) {
        mOCName = ocName;
        mProvisionable = provisionable;
        
        TM.sOCMap.put(ocName, this);
    }
    
    public static AttributeClass getAttributeClass(String ocName) {
        return TM.sOCMap.get(ocName);
    }
    
    public static AttributeClass fromString(String s) throws ServiceException {
        try {
            return AttributeClass.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw ServiceException.PARSE_ERROR("unknown attribute class: " + s, e);
        }
    }
    
    public String getOCName() {
        return mOCName;
    }
    
    public boolean isProvisionable() {
        return mProvisionable;
    }
    
}
