/*
 * 
 */

package com.zimbra.cs.index;

import com.zimbra.common.soap.Element;

public abstract interface QueryInfo {
    public abstract Element toXml(Element parent);
}
