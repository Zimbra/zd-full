/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZDocumentHit;
import com.zimbra.cs.zclient.ZDocument;
import java.util.Date;

public class ZDocumentHitBean extends ZSearchHitBean {

    private ZDocumentHit mHit;

    public ZDocumentHitBean(ZDocumentHit hit) {
        super(hit, HitType.briefcase);
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
