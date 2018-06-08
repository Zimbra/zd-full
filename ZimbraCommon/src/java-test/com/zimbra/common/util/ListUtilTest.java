/*
 * 
 */

package com.zimbra.common.util;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class ListUtilTest {

    @Test
    public void newArrayList() {
        Function<Integer, String> intToString = new Function<Integer, String>() {
            public String apply(Integer i) {
                return i.toString();
            }
        };
        
        List<Integer> intList = Lists.newArrayList(1, 2);
        List<String> stringList = ListUtil.newArrayList(intList, intToString);
        
        // Check transformed list.
        assertEquals(2, stringList.size());
        assertEquals("1", stringList.get(0));
        assertEquals("2", stringList.get(1));
        
        // Make changes to the transformed list and make sure the original list isn't affected.
        stringList.remove(0);
        stringList.set(0, "3");
        assertEquals(2, intList.size());
        assertEquals(1, (int) intList.get(0));
        assertEquals(2, (int) intList.get(1));
    }
}
