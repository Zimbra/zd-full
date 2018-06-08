/*
 * 
 */

package com.zimbra.cs.zclient;

import java.util.Map;

import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.DataSource;

public interface ZDataSource  {
    
    public Element toElement(Element parent);
    public Element toIdElement(Element parent);

    public DataSource.Type getType();
    public String getName();
    public String getId();
    public Map<String,Object> getAttrs();
}
