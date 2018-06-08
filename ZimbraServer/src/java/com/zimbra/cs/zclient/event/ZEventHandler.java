/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.common.service.ServiceException;

public class ZEventHandler {

    /**
     * default implementation is a no-op.
     * 
     * @param refreshEvent the refresh event
     * @param mailbox the mailbox that had the event
     */
    public void handleRefresh(ZRefreshEvent refreshEvent, ZMailbox mailbox) throws ServiceException {
        // do nothing by default
    }

    /**
     *
     * default implementation is a no-op
     *
     * @param event the create event
     * @param mailbox the mailbox that had the event
     */
    public void handleCreate(ZCreateEvent event, ZMailbox mailbox) throws ServiceException {
        // do nothing by default
    }

    /**
     *
     * default implementation is a no-op
     *
     * @param event the modify event
     * @param mailbox the mailbox that had the event
     */
    public void handleModify(ZModifyEvent event, ZMailbox mailbox) throws ServiceException {
        // do nothing by default
    }

        /**
     *
     * default implementation is a no-op
     *
     * @param event the delete event
     * @param mailbox the mailbox that had the event
     */
    public void handleDelete(ZDeleteEvent event, ZMailbox mailbox) throws ServiceException {
        // do nothing by default
    }
}
