/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import java.util.Date;

import com.zimbra.common.mime.ContentType;
import com.zimbra.cs.zclient.ZDocument;

public class ZDocumentBean {

    private ZDocument mDoc;

    public ZDocumentBean(ZDocument doc) {
        mDoc = doc;
    }

    public Date getCreatedDate() {
        return new Date(mDoc.getCreatedDate());
    }

    public Date getModifiedDate() {
        return new Date(mDoc.getModifiedDate() / 1000);
    }

    public Date getMetaDataChangedDate() {
        return new Date(mDoc.getMetaDataChangedDate());
    }

    public String getId() {
        return mDoc.getId();
    }

    public String getName() {
        return mDoc.getName();
    }

    public String getFolderId() {
        return mDoc.getFolderId();
    }

    public String getVersion() {
        return mDoc.getVersion();
    }

    public String getEditor() {
        return mDoc.getEditor();
    }

    public String getCreator() {
        return mDoc.getCreator();
    }

    public String getRestUrl() {
        return mDoc.getRestUrl();
    }

    public boolean isWiki() {
        return mDoc.isWiki();
    }

    public String getContentType() {
        String contentType = mDoc.getContentType();
        return new ContentType(contentType).getContentType();
    }

    public long getSize() {
        return mDoc.getSize();
    }

    public String getTagIds() {
        return mDoc.getTagIds();
    }

}