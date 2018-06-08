/*
 * 
 */

package com.zimbra.cs.taglib.bean;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.VoiceConstants;
import com.zimbra.cs.zclient.ZCallFeatures;

public class ZCallFeaturesBean {

    private ZCallFeatures mFeatures;

    public ZCallFeaturesBean(ZCallFeatures features, boolean modify) {
        mFeatures = features;
    }

    public ZCallFeatures getCallFeatures() {
        return mFeatures;
    }

    public ZVoiceMailPrefsBean getVoiceMailPrefs() {
        return new ZVoiceMailPrefsBean(mFeatures.getVoiceMailPrefs());
    }

    public ZCallForwardingBean getCallForwardingAll() throws ServiceException {
        return new ZCallForwardingBean(mFeatures.getFeature(VoiceConstants.E_CALL_FORWARD));
    }
    
    public ZCallForwardingBean getCallForwardingNoAnswer() throws ServiceException {
	return new ZCallForwardingBean(mFeatures.getFeature(VoiceConstants.E_CALL_FORWARD_NO_ANSWER));
    }

    public ZSelectiveCallForwardingBean getSelectiveCallForwarding() throws ServiceException {
        return new ZSelectiveCallForwardingBean(mFeatures.getSelectiveCallForwarding());
    }
    
    public ZSelectiveCallRejectionBean getSelectiveCallRejection() throws ServiceException {
        return new ZSelectiveCallRejectionBean(mFeatures.getSelectiveCallRejection());
    }
    
    public ZCallFeatureBean getAnonymousCallRejection() throws ServiceException {
	return new ZCallFeatureBean(mFeatures.getFeature(VoiceConstants.E_ANON_CALL_REJECTION));
    }

    public boolean isEmpty() { return mFeatures.isEmpty(); }
}
