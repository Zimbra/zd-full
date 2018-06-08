/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.cs.zclient.ToZJSONObject;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZJSONObject;
import com.zimbra.cs.zclient.ZTag;
import org.json.JSONException;

import java.util.List;

public class ZRefreshEvent implements ToZJSONObject {

    private long mSize;
    private ZFolder mUserRoot;
    private List<ZTag> mTags;

    public ZRefreshEvent(long size, ZFolder userRoot, List<ZTag> tags) {
    	mSize = size;
    	mUserRoot = userRoot;
    	mTags = tags;
    }

    /**
     * @return size of mailbox in bytes
     */
    public long getSize() {
        return mSize;
    }

    /**
     * return the root user folder
     * @return user root folder
     */
    public ZFolder getUserRoot() {
        return mUserRoot;
    }

    public List<ZTag> getTags() {
        return mTags;
    }
    
    public ZJSONObject toZJSONObject() throws JSONException {
        ZJSONObject zjo = new ZJSONObject();
        zjo.put("size", getSize());
    	zjo.put("userRoot", mUserRoot);
    	zjo.put("tags", mTags);
    	return zjo;
    }

    public String toString() {
        return "[ZRefreshEvent]";
    }

    public String dump() {
        return ZJSONObject.toString(this);
    }
}
