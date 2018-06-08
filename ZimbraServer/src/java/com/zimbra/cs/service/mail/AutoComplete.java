/*
 * 
 */
package com.zimbra.cs.service.mail;

import java.util.ArrayList;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.ContactAutoComplete;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.ContactAutoComplete.AutoCompleteResult;
import com.zimbra.cs.mailbox.ContactAutoComplete.ContactEntry;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.soap.ZimbraSoapContext;

public class AutoComplete extends MailDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account account = getRequestedAccount(getZimbraSoapContext(context));
        OperationContext octxt = getOperationContext(zsc, context);

        String name = request.getAttribute(MailConstants.A_NAME);

        // remove commas (bug 46540)
        name = name.replace(",", " ").trim();

        Provisioning.GalSearchType type = Provisioning.GalSearchType.fromString(request.getAttribute(MailConstants.A_TYPE, "account"));
        int limit = account.getContactAutoCompleteMaxResults();
        boolean needCanExpand = request.getAttributeBool(MailConstants.A_NEED_EXP, false);

        AutoCompleteResult result = query(request, zsc, account, false, name, limit, type, needCanExpand, octxt);
        Element response = zsc.createElement(MailConstants.AUTO_COMPLETE_RESPONSE);
        toXML(response, result, zsc.getAuthtokenAccountId());

        return response;
    }

    @Override
    public boolean needsAuth(Map<String, Object> context) {
        return true;
    }

    private ArrayList<Integer> csvToArray(String csv) {
        if (csv == null)
            return null;
        ArrayList<Integer> array = new ArrayList<Integer>();
        for (String f : csv.split(",")) {
            array.add(Integer.parseInt(f));
        }
        return array;
    }

    protected AutoCompleteResult query(Element request, ZimbraSoapContext zsc, Account account,
            boolean excludeGal, String name, int limit, Provisioning.GalSearchType type, OperationContext octxt) throws ServiceException {
        return query(request, zsc, account, excludeGal, name, limit, type, false, octxt);
    }

    protected AutoCompleteResult query(Element request, ZimbraSoapContext zsc, Account account,
            boolean excludeGal, String name, int limit, Provisioning.GalSearchType type, boolean needCanExpand, OperationContext octxt) throws ServiceException {
       if (!canAccessAccount(zsc, account))
            throw ServiceException.PERM_DENIED("can not access account");

       ArrayList<Integer> folders = csvToArray(request.getAttribute(MailConstants.A_FOLDERS, null));
       ContactAutoComplete autoComplete = new ContactAutoComplete(account, zsc, octxt);
       autoComplete.setNeedCanExpand(needCanExpand);
       autoComplete.setSearchType(type);
       boolean includeGal = !excludeGal && request.getAttributeBool(MailConstants.A_INCLUDE_GAL, autoComplete.includeGal());
       autoComplete.setIncludeGal(includeGal);
       return autoComplete.query(name, folders, limit);
    }

    protected void toXML(Element response, AutoCompleteResult result, String authAccountId) {
        response.addAttribute(MailConstants.A_CANBECACHED, result.canBeCached);
        for (ContactEntry entry : result.entries) {
            Element cn = response.addElement(MailConstants.E_MATCH);
            cn.addAttribute(MailConstants.A_EMAIL, entry.getEmail());
            cn.addAttribute(MailConstants.A_MATCH_TYPE, getType(entry));
            cn.addAttribute(MailConstants.A_RANKING, Integer.toString(entry.getRanking()));
            cn.addAttribute(MailConstants.A_IS_GROUP, entry.isGroup());
            if (entry.isGroup() && entry.canExpandGroupMembers())
                cn.addAttribute(MailConstants.A_EXP, true);

            ItemId id = entry.getId();
            if (id != null)
                cn.addAttribute(MailConstants.A_ID, id.toString(authAccountId));
            int folderId = entry.getFolderId();
            if (folderId > 0)
                cn.addAttribute(MailConstants.A_FOLDER, Integer.toString(folderId));
            if (entry.isDlist())
                cn.addAttribute(MailConstants.A_DISPLAYNAME, entry.getDisplayName());
        }
    }

    private String getType(ContactEntry entry) {
        if (entry.getFolderId() == ContactAutoComplete.FOLDER_ID_GAL)
            return "gal";
        else if (entry.getFolderId() == ContactAutoComplete.FOLDER_ID_UNKNOWN)
            return "rankingTable";
        else
            return "contact";
    }
}
