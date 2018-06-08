/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZConversationHit;
import com.zimbra.cs.zclient.ZEmailAddress;

import java.util.Date;
import java.util.List;

public class ZConversationHitBean extends ZSearchHitBean {

    private ZConversationHit mHit;
    
    public ZConversationHitBean(ZConversationHit hit) {
        super(hit, HitType.conversation);
        mHit = hit;
    }

    public Date getDate() { return new Date(mHit.getDate()); }
    
    public boolean getHasFlags() { return mHit.hasFlags(); }
    
    public boolean getHasMultipleTags() { return mHit.hasTags() && mHit.getTagIds().indexOf(',') != -1; }
    
    public String getTagIds() { return mHit.getTagIds(); }
    
    public boolean getHasTags() { return mHit.hasTags(); }
    
    public boolean getIsUnread() { return mHit.isUnread(); }

    public boolean getIsFlagged() { return mHit.isFlagged(); }

    public boolean getIsHighPriority() { return mHit.isHighPriority(); }

    public boolean getIsLowPriority() { return mHit.isLowPriority(); }

    public boolean getIsDraft() { return mHit.isDraft(); }

    public boolean getIsSentByMe() { return mHit.isSentByMe(); }

    public boolean getHasAttachment() { return mHit.hasAttachment(); }

    public String getSubject() { return mHit.getSubject(); }
    
    public String getFragment() { return mHit.getFragment(); }
    
    public int getMessageCount() { return mHit.getMessageCount(); }
    
    public List<String> getMatchedMessageIds() { return mHit.getMatchedMessageIds(); }
    
    public List<ZEmailAddress> getRecipients() { return mHit.getRecipients(); }
    
    public String getDisplayRecipients() { return BeanUtils.getAddrs(mHit.getRecipients()); }    
}
