/*
 * 
 */

package com.zimbra.cs.stats;

public interface JmxServerStatsMBean
{
    long getBlobInputStreamReads();
    long getBlobInputStreamSeekRate();
    long getDatabaseConnectionGets();
    long getDatabaseConnectionGetMs();
    long getDatabaseConnectionsInUse();
    long getImapRequests();
    long getImapResponseMs();
    long getItemCacheHitRate();
    long getLdapDirectoryContextGetMs();
    long getLdapDirectoryContextGets();
    long getLmtpDeliveredBytes();
    long getLmtpDeliveredMessages();
    long getLmtpReceivedBytes();
    long getLmtpReceivedMessages();
    long getLmtpRecipients();
    long getMailboxCacheHitRate();
    long getMailboxCacheSize();
    long getMailboxGetMs();
    long getMailboxGets();
    long getMessageAddMs();
    long getMessageCacheSize();
    long getMessageCacheHitRate();
    long getMessagesAdded();
    long getPopRequests();
    long getPopResponseMs();
    long getSoapRequests();
    long getSoapResponseMs();
}
