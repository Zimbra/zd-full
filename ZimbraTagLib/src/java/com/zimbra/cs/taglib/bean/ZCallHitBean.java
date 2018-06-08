/*
 * 
 */

package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZCallHit;
import com.zimbra.cs.zclient.ZPhone;

import java.util.Date;

public class ZCallHitBean extends ZSearchHitBean {

    private ZCallHit mHit;

    public ZCallHitBean(ZCallHit hit) {
        super(hit, HitType.call);
        mHit = hit;
    }

    public String toString() { return mHit.toString(); }

    public ZPhone getCaller() { return mHit.getCaller(); }

    public ZPhone getRecipient() { return mHit.getRecipient(); }

    public String getDisplayCaller() { return mHit.getDisplayCaller(); }

	public String getDisplayRecipient() { return mHit.getDisplayRecipient(); }

    public Date getDate() { return new Date(mHit.getDate()); }

    public long getDuration() { return mHit.getDuration(); }

}
