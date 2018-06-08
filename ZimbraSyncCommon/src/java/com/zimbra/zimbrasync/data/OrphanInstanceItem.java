/*
 * 
 */
package com.zimbra.zimbrasync.data;

import java.text.ParseException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mailbox.calendar.ParsedDateTime;
import com.zimbra.cs.mailbox.calendar.RecurId;

public class OrphanInstanceItem extends PriorityItem {
    private RecurId rid;
    private long dtStamp;
    private int sequence;

    public OrphanInstanceItem(int itemId) {
        super(itemId);
    }
    
    public OrphanInstanceItem(Invite inv, int id) {
        this(id);
        rid = inv.getRecurId();
        dtStamp = inv.getDTStamp();
        sequence = inv.getSeqNo();
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
     
        if (!(other instanceof OrphanInstanceItem))
            return false;
        
        OrphanInstanceItem rhs = (OrphanInstanceItem)other;
        if (this.rid.equals(rhs.getRid()) &&
                this.dtStamp == rhs.getDtStamp() &&
                this.sequence == rhs.getSequence())
            return true;

        return false;
    }

    @Override
    public String encode() {
        StringBuilder sb = new StringBuilder();
        sb.append("OrphanInstanceItem:{");
        sb.append("rid:").append(encodeRid(rid)).append(",");
        sb.append("dts:").append(ParsedDateTime.fromUTCTime(dtStamp).getUtcString()).append(",");
        sb.append("seq:").append(sequence);
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public void decode(String metaData) throws ServiceException {
        if (metaData == null)
            throw ServiceException.FAILURE("Invalid metadata", null);
        
        int ridStartIndex = metaData.indexOf("rid:") + 4;
        int ridEndIndex = metaData.indexOf(",");
        if (ridStartIndex == -1 || ridEndIndex == -1 || ridEndIndex <= ridStartIndex)
            throw ServiceException.FAILURE("Invalid metadata " + metaData, null);
        
        String ridStr = metaData.substring(ridStartIndex, ridEndIndex);
        try {
            rid = parseRidStr(ridStr);
        } catch (Exception e) {
            throw ServiceException.FAILURE("Could not parse rid " + ridStr, e);
        }
        
        int dtsStartIndex = metaData.indexOf("dts:") + 4;
        int dtsEndIndex = metaData.indexOf(",", ridEndIndex + 1);
        if (dtsStartIndex == -1 || dtsEndIndex == -1 || dtsEndIndex <= dtsStartIndex)
            throw ServiceException.FAILURE("Invalid metadata " + metaData, null);
        
        int seqStartIndex = metaData.indexOf("seq:") + 4;
        int seqEndIndex = metaData.indexOf("}");
        if (seqStartIndex == -1 || seqEndIndex == -1 || seqEndIndex <= seqStartIndex)
            throw ServiceException.FAILURE("Invalid metadata " + metaData, null);

        try {
            dtStamp = ParsedDateTime.parseUtcOnly(metaData.substring(dtsStartIndex, dtsEndIndex)).getUtcTime();
            sequence = Integer.parseInt(metaData.substring(seqStartIndex, seqEndIndex));
        } catch (NumberFormatException e) {
            throw ServiceException.FAILURE("Invalid metadata " + metaData, e);
        } catch (ParseException e) {
            throw ServiceException.FAILURE("Invalid metadata " + metaData, e);
        }
    }
    
    private static String encodeRid(RecurId rid) {
        if (rid == null)
            return "";
        
        StringBuilder sb = new StringBuilder();
        sb.append(rid.getDt().getUtcString());
        sb.append(";RANGE=").append(rid.getRange());
        return sb.toString();
    }
    
    private static RecurId parseRidStr(String ridStr) throws Exception {
        if (ridStr == null || ridStr.length() < 9 || !ridStr.contains(";RANGE="))
            throw ServiceException.FAILURE("Invalid recurrenceId metadata", null);
        
        String utcTime = ridStr.substring(0, ridStr.indexOf(";RANGE="));
        ParsedDateTime dt = ParsedDateTime.parseUtcOnly(utcTime);
        int range = Integer.parseInt(ridStr.substring(ridStr.indexOf(";RANGE=") + 7));
        return new RecurId(dt, range);
    }

    public RecurId getRid() {
        return rid;
    }

    public long getDtStamp() {
        return dtStamp;
    }

    public int getSequence() {
        return sequence;
    }
    
    public static void main(String args[]) throws ServiceException {
        long currTimeMillis = System.currentTimeMillis();
        String utcTime = ParsedDateTime.fromUTCTime(currTimeMillis).getUtcString();
        PriorityItem item = new OrphanInstanceItem(256);
        String metaData = "OrphanInstanceItem:{rid:" + utcTime + ";RANGE=1,dts:" + utcTime + ",seq:0}";
        System.out.println(metaData);
        item.decode(metaData);
        System.out.println(item.encode());
    }

}
