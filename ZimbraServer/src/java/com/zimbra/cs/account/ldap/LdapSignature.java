/*
 * 
 */

package com.zimbra.cs.account.ldap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AttributeClass;
import com.zimbra.cs.account.AttributeManager;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Signature;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 * @author schemers
 */
 class LdapSignature extends Signature implements LdapEntry {

    private String mDn;

    LdapSignature(Account acct, String dn, Attributes attrs, Provisioning prov) throws NamingException {
        super(  acct,
                LdapUtil.getAttrString(attrs, Provisioning.A_zimbraSignatureName),
                LdapUtil.getAttrString(attrs, Provisioning.A_zimbraSignatureId),
                LdapUtil.getAttrs(attrs), prov);
        mDn = dn;
    }

    public String getDN() {
        return mDn;
    }
    
    /*
     * For backward compatibility (in the case when upgrade script is not run) to preserve the 
     * signature on the account, we need to check for presence of A_zimbraPrefMailSignature.
     * 
     * We also need to check for presence of A_zimbraPrefSignatureName, because in the new scheme
     * a signature can have a name but not a value.
     * 
     * The account signature is considered present if either A_zimbraPrefMailSignature or 
     * A_zimbraPrefSignatureName on the account is set.  If A_zimbraPrefSignatureName is not set,
     * getAccountSignature uses the account's name for the signature name.
     * 
     * Note: we do not set/writeback to LDAP the default signature to the account signature even 
     * if it is the only signature.  The upgrade script should do that.
     * 
     */
    public static boolean hasAccountSignature(Account acct) {
        return (acct.getAttr(Provisioning.A_zimbraPrefMailSignature) != null || 
                acct.getAttr(Provisioning.A_zimbraSignatureName) != null ||
                acct.getAttr(Provisioning.A_zimbraSignatureId) != null);
    }
    
    /*
    private String getAccountSignatureName(Account acct) {
        String sigName = acct.getAttr(Provisioning.A_zimbraSignatureName);
        if (sigName == null)
            sigName = acct.getName();
        
        return sigName;
    }
    */
    
    public static Signature getAccountSignature(LdapProvisioning prov, Account acct) throws ServiceException {
        if (!hasAccountSignature(acct))
            return null;
        
        Map<String, Object> attrs = new HashMap<String, Object>();
        Set<String> signatureAttrs = AttributeManager.getInstance().getAttrsInClass(AttributeClass.signature);
        
        for (String name : signatureAttrs) {
            String value = acct.getAttr(name, null);
            if (value != null) 
                attrs.put(name, value);            
        }
        
        // for backward compatibility, we recognize an existing signature on the account if 
        // it has a A_zimbraPrefMailSignature value.  We write back name and id if they are not
        // present.  This write back should happen only once for the account.
        Map<String, Object> putbackAttrs = new HashMap<String, Object>();
        String sigName = acct.getAttr(Provisioning.A_zimbraSignatureName);
        if (sigName == null) {
            sigName = acct.getName();
            putbackAttrs.put(Provisioning.A_zimbraSignatureName, sigName);
        }
        String sigId = acct.getAttr(Provisioning.A_zimbraSignatureId);
        if (sigId == null) {
            sigId = LdapUtil.generateUUID();
            putbackAttrs.put(Provisioning.A_zimbraSignatureId, sigId);
        }
        if (putbackAttrs.size() > 0)
            prov.modifyAttrs(acct, putbackAttrs);
                
        return new Signature(acct, sigName, sigId, attrs, prov);        
    }
    
    /*
     * Account entry "holds" a signature "slot".  This is because most of the accounts have only 
     * one signature and we don't want to create an ldap signature entry for that.
     */ 
     public static boolean isAccountSignature(Account acct, String signatureId) {
        String acctSigId = acct.getAttr(Provisioning.A_zimbraSignatureId);
        return (signatureId.equals(acctSigId));
    }
    
    public static void modifyAccountSignature(LdapProvisioning prov, Account acct, Map<String, Object>signatureAttrs) throws ServiceException {
        prov.modifyAttrs(acct, signatureAttrs);
    }
    
    public static void createAccountSignature(LdapProvisioning prov, Account acct, Map<String, Object>signatureAttrs, boolean setAsDefault) throws ServiceException {
        if (setAsDefault) {
            String signatureId = (String)signatureAttrs.get(Provisioning.A_zimbraSignatureId);
            signatureAttrs.put(Provisioning.A_zimbraPrefDefaultSignatureId, signatureId);
        }
        prov.modifyAttrs(acct, signatureAttrs);
    }

    public static void deleteAccountSignature(LdapProvisioning prov, Account acct) throws ServiceException {
        Map<String, Object> attrs = new HashMap<String, Object>();
        Set<String> signatureAttrs = AttributeManager.getInstance().getAttrsInClass(AttributeClass.signature);
        
        for (String name : signatureAttrs) {
            String value = acct.getAttr(name, null);
            if (value != null) attrs.put("-" + name, value);            
        }
        
        prov.modifyAttrs(acct, attrs);
    }
    

}
