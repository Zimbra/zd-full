/*
 * 
 */

package com.zimbra.cs.mailbox;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants.AutoArchiveFrequency;

public class AutoArchiveTimer {
    private static Timer autoArchiveTimer;
    private static AutoArchiveTask autoArchiveTask;

    /**
     * Schedules auto archive task.
     * @throws ServiceException
     */
    public static void initialize() throws ServiceException {
        if (AutoArchive.isAutoArchivingEnabled()) {
            scheduleAutoArchive(false);
        }
    }

    public static synchronized void cancelAutoArchivingTimer() {
        OfflineLog.offline.debug("Auto archive timer is cancelled");
        if (autoArchiveTimer != null) {
                autoArchiveTimer.cancel();
        }
        autoArchiveTimer = null;
    }

    public static void cancelAutoArchiveTask() {
        OfflineLog.offline.debug("Auto archive task is cancelled.");
        if (autoArchiveTimer != null) {
            autoArchiveTimer.cancel();
            autoArchiveTask = null;
        }
    }

    /**
     * Schedules auto archiving task.
     * @param boolean isPreviousAttemeptFailed - used for re-attempting
     * @throws ServiceException */
    public static void scheduleAutoArchive(boolean isPreviousAttemeptFailed) throws ServiceException {
        AutoArchiveFrequency autoArchiveFreq = AutoArchive.getFrequency();

        Date firstTime = getTimeForInitialAutoArchive(autoArchiveFreq, isPreviousAttemeptFailed);
        autoArchiveTask = new AutoArchiveTask();
        OfflineLog.offline.debug("Auto archiving is scheduled at %s with %d number of days.(%s)", firstTime.toString(),
                AutoArchive.getAgeInDays(), autoArchiveFreq);
        autoArchiveTimer = new Timer("autoArchiveTimer");

        if (autoArchiveFreq == AutoArchiveFrequency.ON_APP_LAUNCH) {
            autoArchiveTimer.schedule(autoArchiveTask, firstTime);
        } else {
            autoArchiveTimer.scheduleAtFixedRate(autoArchiveTask, firstTime, autoArchiveFreq.getDuration());
        }
    }

    /**
     * @param Reschedules auto-archiving because last attempt is failed.
     */
    public static void lastAutoArchiveFailed() throws ServiceException {
        OfflineLog.offline.debug("Auto archiving is rescheduled");
        cancelAutoArchivingTimer();
        scheduleAutoArchive(true);
    }

    /**
     * Reschedule auto-archiving if frequency is changed.
     * Cancel already scheduled tasks. Schedule auto-archiving(skip scheduling for ON_APP_LAUNCH)
     * @throws ServiceException
     */
    public static void rescheduleAutoArchiving() throws ServiceException {
        //cancel existing timer
        cancelAutoArchivingTimer();
        AutoArchiveFrequency freq = AutoArchive.getFrequency();
        //Initialize auto-archiving with new setting
        try {
            //If user updates frequency to ON_APP_LAUNCH then auto-archiving will start after restart, no scheduling needed
            if (!(freq == AutoArchiveFrequency.ON_APP_LAUNCH)) {
                OfflineLog.offline.debug("Updating auto-archiving task for updated frequency");
                AutoArchiveTimer.scheduleAutoArchive(false);
            }
        } catch(ServiceException e) {
            OfflineLog.offline.error("Auto-archiving initialization failed.", e);
        }
    }

    /**
     * Return timestamp for initial auto archiving.
     * @param freq
     * @param isPreviousAttemeptFailed
     * @return
     */
    private static Date getTimeForInitialAutoArchive(AutoArchiveFrequency freq, boolean isPreviousAttemeptFailed) {
        Calendar c = Calendar.getInstance();

        if (isPreviousAttemeptFailed) {
            c.add(Calendar.MINUTE, OfflineLC.zdesktop_auto_archive_retry_time.intValue());
            return c.getTime();
        }

        if (freq == AutoArchiveFrequency.ON_APP_LAUNCH) {
            c.add(Calendar.MINUTE, OfflineLC.zdesktop_auto_archive_on_startup_time.intValue());
            return c.getTime();
        }

        String lastArchive = AutoArchive.getLastAutoArchiveTimestamp();

        long elapsedTime = System.currentTimeMillis() - Long.parseLong(lastArchive);
        long expectedTime = freq.getDuration();

        //if elapsed time is more than expected duration then schedule auto archive immediately in few minutes.
        if (elapsedTime > expectedTime) {
            c.add(Calendar.MINUTE, OfflineLC.zdesktop_auto_archive_start_time.intValue());
            return c.getTime();
        }

        long time = expectedTime - elapsedTime;
        c.add(Calendar.MILLISECOND, (int)time);
        return c.getTime();
    }

    /**
     * Shutdown auto archive timer.
     */
    public static void shutdown() {
        cancelAutoArchiveTask();
        cancelAutoArchivingTimer();
    }
}
