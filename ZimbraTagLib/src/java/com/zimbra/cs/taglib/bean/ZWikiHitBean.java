/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZDocument;
import com.zimbra.cs.zclient.ZWikiHit;

import java.util.Date;

public class ZWikiHitBean extends ZSearchHitBean {

    private ZWikiHit mHit;

    public ZWikiHitBean(ZWikiHit hit) {
        super(hit, HitType.wiki);
        mHit = hit;
    }

    public ZDocument getDocument() {
        return mHit.getDocument();
    }

    public String getDocId() {
        return mHit.getId();
    }

    public String getDocSortField() {
        return mHit.getSortField();
    }

    public Date getCreatedDate() {
        return new Date(mHit.getDocument().getCreatedDate()/1000);
    }

    public Date getModifiedDate() {
        return new Date(mHit.getDocument().getModifiedDate()/1000);
    }

    public Date getMetaDataChangedDate() {
        return new Date(mHit.getDocument().getMetaDataChangedDate()/1000);
    }

}
