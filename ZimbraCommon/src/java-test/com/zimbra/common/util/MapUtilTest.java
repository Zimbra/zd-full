/*
 * 
 */

package com.zimbra.common.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.Test;

public class MapUtilTest {

    @Test
    public void newValueListMap() {
        Map<Integer, List<String>> map = MapUtil.newValueListMap();
        map.get(1).add("a");
        map.get(1).add("b");
        map.get(2).add("c");
        
        assertEquals(2, map.size());
        
        List<String> list = map.get(1);
        assertEquals(2, list.size());
        assertTrue(list.contains("a"));
        assertTrue(list.contains("b"));
        
        list = map.get(2);
        assertEquals(1, list.size());
        assertTrue(list.contains("c"));
    }
    
    @Test
    public void newValueSetMap() {
        Map<Integer, Set<String>> map = MapUtil.newValueSetMap();
        map.get(1).add("a");
        map.get(1).add("b");
        map.get(2).add("c");
        
        assertEquals(2, map.size());
        
        Set<String> set = map.get(1);
        assertEquals(2, set.size());
        assertTrue(set.contains("a"));
        assertTrue(set.contains("b"));
        
        set = map.get(2);
        assertEquals(1, set.size());
        assertTrue(set.contains("c"));
    }
}
