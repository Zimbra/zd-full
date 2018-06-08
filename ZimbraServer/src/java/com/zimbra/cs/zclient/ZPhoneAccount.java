/*
 * 
 */

package com.zimbra.cs.zclient;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.VoiceConstants;
import com.zimbra.common.service.ServiceException;

public class ZPhoneAccount {
    private ZFolder mFolder;
    private ZPhone mPhone;
    private ZCallFeatures mCallFeatures;
	private boolean mHasVoiceMail;

	public ZPhoneAccount(Element e, ZMailbox mbox) throws ServiceException {
        mPhone = new ZPhone(e.getAttribute(MailConstants.A_NAME));
        mFolder = new ZVoiceFolder(e.getElement(MailConstants.E_FOLDER), null, mbox);
        mCallFeatures = new ZCallFeatures(mbox, mPhone, e.getElement(VoiceConstants.E_CALL_FEATURES));
		mHasVoiceMail = e.getAttributeBool(VoiceConstants.E_VOICEMSG);
	}

    public ZFolder getRootFolder() {
        return mFolder;
    }

    public ZPhone getPhone() {
        return mPhone;
    }

    public ZCallFeatures getCallFeatures() throws ServiceException {
        mCallFeatures.loadCallFeatures();
        return mCallFeatures;
    }

	public boolean getHasVoiceMail() {
		return mHasVoiceMail;
	}
}
