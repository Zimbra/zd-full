/*
 * 
 */

package com.zimbra.cs.util.tnef.mapi;

/**
 *
 * @author gren
 */
public enum BusyStatus {
    FREE (0x00000000),
    TENTATIVE (0x00000001),
    BUSY (0x00000002),
    OOF (0x00000003);

    private final int MapiPropValue;

    BusyStatus(int propValue) {
        MapiPropValue = propValue;
    }

    public int mapiPropValue() {
        return MapiPropValue;
    }

}
