/*
 * 
 */

package com.zimbra.common.stats;

import java.util.Map;

public interface RealtimeStatsCallback {
    public Map<String, Object> getStatData();
}
