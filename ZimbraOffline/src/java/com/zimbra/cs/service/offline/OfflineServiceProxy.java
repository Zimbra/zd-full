/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import org.dom4j.QName;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineServiceProxy extends DocumentHandler {

    private final String mOp;
    private final boolean mQuiet;
    private final boolean mHandleLocal;

    public OfflineServiceProxy(String op, boolean quiet, boolean handleLocal) {
        mOp = op;
        mQuiet = quiet;
        mHandleLocal = handleLocal;
    }

    public OfflineServiceProxy(String op, boolean quiet, boolean handleLocal, QName responseQname) {
        this(op, quiet, handleLocal);
        setResponseQName(responseQname);
    }

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (!(mbox instanceof ZcsMailbox)) {
            if (mHandleLocal)
                return getResponseElement(ctxt);
            else
                throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
        }

        Element response = ((ZcsMailbox)mbox).proxyRequest(request, ctxt.getResponseProtocol(), mQuiet, mOp);
        if (response != null)
            response.detach();

        if (mQuiet && response == null)
            return getResponseElement(ctxt);

        return response;
    }

    public static OfflineServiceProxy SearchCalendarResources() {
        return new OfflineServiceProxy("search cal resources", false, true);
    }

    public static OfflineServiceProxy GetPermission() {
        return new OfflineServiceProxy("get permission", true, true);
    }

    public static OfflineServiceProxy GrantPermission() {
        return new OfflineServiceProxy("grant permission", false, false);
    }

    public static OfflineServiceProxy RevokePermission() {
        return new OfflineServiceProxy("revoke permission", false, false);
    }

    public static OfflineServiceProxy CheckPermission() {
        return new OfflineServiceProxy("check permission", false, false);
    }

    public static OfflineServiceProxy GetShareInfoRequest() {
        return new OfflineServiceProxy("get share info", false, false);
    }

    public static OfflineServiceProxy AutoCompleteGalRequest() {
        return new OfflineServiceProxy("auto-complete gal", true, true);
    }

    public static OfflineServiceProxy CheckRecurConflictsRequest() {
        return new OfflineServiceProxy("check recur conflicts", false, false);
    }

    public static OfflineServiceProxy GetDLMembersRequest() {
        return new OfflineServiceProxy("get dl members", false, false);
    }

    public static OfflineServiceProxy GetWorkingHoursRequest() {
        return new OfflineServiceProxy("get working hours", false, true);
    }
}



