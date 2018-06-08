/*
 * 
 */

package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZPhone;
import com.zimbra.cs.zclient.ZVoiceMailItemHit;
import com.zimbra.common.service.ServiceException;

import java.util.Date;

public class ZVoiceMailItemHitBean extends ZSearchHitBean {

    private ZVoiceMailItemHit mHit;

    public ZVoiceMailItemHitBean(ZVoiceMailItemHit hit) {
        super(hit, HitType.voiceMailItem);
        mHit = hit;
    }

    public static ZVoiceMailItemHitBean deserialize(String value, String phone) throws ServiceException {
        return new ZVoiceMailItemHitBean(ZVoiceMailItemHit.deserialize(value, phone));
    }

    public String toString() { return mHit.toString(); }

    public boolean getIsFlagged() { return mHit.isFlagged(); }

    public boolean getIsUnheard() { return mHit.isUnheard(); }

    public boolean getIsPrivate() { return mHit.isPrivate(); }

    public ZPhone getCaller() { return mHit.getCaller(); }

    public String getDisplayCaller() { return mHit.getDisplayCaller(); }

    public String getSoundUrl() { return mHit.getSoundUrl(); }

    public Date getDate() { return new Date(mHit.getDate()); }

    public long getDuration() { return mHit.getDuration(); }

    public String getSerialize() { return  mHit.serialize(); }
}

