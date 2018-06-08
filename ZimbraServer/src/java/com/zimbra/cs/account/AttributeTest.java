/*
 * 
 */
package com.zimbra.cs.account;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.CliUtil;

public class AttributeTest {
    public static void main(String args[]) throws ServiceException {
        CliUtil.toolSetup("INFO");
        AttributeManager mgr = AttributeManager.getInstance();
        HashMap<String, String> attrs = new HashMap<String, String>();
        attrs.put(Provisioning.A_zimbraAccountStatus, Provisioning.ACCOUNT_STATUS_ACTIVE);
        attrs.put(Provisioning.A_zimbraImapBindPort, "143");
        attrs.put("xxxzimbraImapBindPort", "143");
        attrs.put(Provisioning.A_zimbraPrefOutOfOfficeReply, null);
        attrs.put(Provisioning.A_zimbraPrefOutOfOfficeReplyEnabled, "FALSE");
        Map context = new HashMap();
        mgr.preModify(attrs, null, context, false, true);
        // modify
        mgr.postModify(attrs, null, context, false);
    }
}
