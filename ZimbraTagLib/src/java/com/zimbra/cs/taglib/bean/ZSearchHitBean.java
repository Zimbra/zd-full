/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZSearchHit;

public abstract class ZSearchHitBean {

    public enum HitType { conversation, contact, message, voiceMailItem, call, task, briefcase, appointment, wiki }

    private HitType mHitType;
    private ZSearchHit mHit;

    protected ZSearchHitBean(ZSearchHit hit, HitType hitType) {
        mHit = hit;
        mHitType = hitType;
    }

    public final String getId() { return mHit.getId(); }

    public final String getSortField() { return mHit.getSortField(); }

    public final String getHitType() { return mHitType.name(); }

    public final boolean getIsConversation() { return mHitType == HitType.conversation; }

    public final boolean getIsMessage() { return mHitType == HitType.message; }

    public final boolean getIsContact() { return mHitType == HitType.contact; }

    public final boolean getIsTask() { return mHitType == HitType.task; }

    public final boolean getIsAppointment() { return mHitType == HitType.appointment; }

    public final boolean getIsBriefcase() { return mHitType == HitType.briefcase; }

    public final boolean getIsWiki() { return mHitType == HitType.wiki; }

    public final boolean getIsVoiceMailItem() { return mHitType == HitType.voiceMailItem; }

    public final boolean getIsCall() { return mHitType == HitType.call; }

    public final ZConversationHitBean getConversationHit() { return getIsConversation() ? (ZConversationHitBean) this : null; }

    public final ZMessageHitBean getMessageHit() { return getIsMessage() ? (ZMessageHitBean) this : null; }

    public final ZContactHitBean getContactHit() { return getIsContact() ? (ZContactHitBean) this : null; }

    public final ZDocumentHitBean getBriefcaseHit() { return getIsBriefcase()? (ZDocumentHitBean) this : null; }

    public final ZWikiHitBean getWikiHit() { return getIsWiki() ? (ZWikiHitBean) this : null; }

    public final ZAppointmentHitBean getAppointmentHit() { return getIsAppointment() ? (ZAppointmentHitBean) this : null; }

    public final ZTaskHitBean getTaskHit() { return getIsTask() ? (ZTaskHitBean) this : null; }

    public final ZVoiceMailItemHitBean getVoiceMailItemHit() { return getIsVoiceMailItem() ? (ZVoiceMailItemHitBean) this : null; }

    public final ZCallHitBean getCallHit() { return getIsCall() ? (ZCallHitBean) this : null; }

}
