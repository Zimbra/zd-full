/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZPhoneAccount;
import com.zimbra.cs.zclient.ZPhone;
import com.zimbra.common.service.ServiceException;

public class ZPhoneAccountBean {

    private ZPhoneAccount mAccount;

    public ZPhoneAccountBean(ZPhoneAccount account) {
        mAccount = account;
    }

    public ZFolderBean getRootFolder() {
        return new ZFolderBean(mAccount.getRootFolder());
    }

    public ZPhone getPhone() {
        return mAccount.getPhone();
    }

    public ZCallFeaturesBean getCallFeatures() throws ServiceException {
        return new ZCallFeaturesBean(mAccount.getCallFeatures(), false);
    }

	public boolean getHasVoiceMail() {
		return mAccount.getHasVoiceMail();
	}
}
