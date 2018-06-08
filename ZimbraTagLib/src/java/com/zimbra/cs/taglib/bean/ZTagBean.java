/*
 * 
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZTag;

public class ZTagBean {

    private ZTag mTag;
    
    public ZTagBean(ZTag tag){
        mTag = tag;
    }

    public String getId() { return mTag.getId(); }

    public String getName() { return mTag.getName(); }

    public int getUnreadCount() { return mTag.getUnreadCount(); }
    
    public boolean getHasUnread() { return getUnreadCount() > 0; }

    public String getColor() { return mTag.getColor().name(); }
    
    public String getImage() {
        switch(mTag.getColor()) {
        case blue:
            return "zimbra/ImgTagBlue.png";
        case cyan:
            return "zimbra/ImgTagCyan.png";
        case green:
            return "zimbra/ImgTagGreen.png";
        case purple: 
            return "zimbra/ImgTagPurple.png";
        case red:
            return "zimbra/ImgTagRed.png";
        case yellow: 
            return "zimbra/ImgTagYellow.png";
        case orange:
        case defaultColor:
        default:
            return "zimbra/ImgTagOrange.png";
        }
    }
    
    public String getMiniImage() {
        switch(mTag.getColor()) {
        case blue:
            return "zimbra/ImgTagBlue.png";
        case cyan:
            return "zimbra/ImgTagCyan.png";
        case green:
            return "zimbra/ImgTagGreen.png";
        case purple: 
            return "zimbra/ImgTagPurple.png";
        case red:
            return "zimbra/ImgTagRed.png";
        case yellow: 
            return "zimbra/ImgTagYellow.png";
        case orange:
        case defaultColor:
        default:
            return "zimbra/ImgTagOrange.png";
        }
    }

}
