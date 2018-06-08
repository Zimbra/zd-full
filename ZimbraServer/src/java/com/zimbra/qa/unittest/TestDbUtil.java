/*
 * 
 */

package com.zimbra.qa.unittest;

import junit.framework.TestCase;

import com.zimbra.cs.db.Db;
import com.zimbra.cs.db.DbMailbox;
import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.db.DbUtil;
import com.zimbra.cs.db.DbPool.Connection;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * @author bburtin
 */
public class TestDbUtil extends TestCase
{
    public void testNormalizeSql()
    throws Exception {
        String sql = " \t SELECT a, 'b', 1, '', ',', NULL, '\\'' FROM table1\n\nWHERE c IN (1, 2, 3) ";
        String normalized = DbUtil.normalizeSql(sql);
        String expected = "SELECT a, XXX, XXX, XXX, XXX, XXX, XXX FROM tableXXX WHERE c IN (...)";
        assertEquals(expected, normalized);
    }
    
    public void testDatabaseExists()
    throws Exception {
        Mailbox mbox = TestUtil.getMailbox("user1");
        Db db = Db.getInstance();
        String dbName = DbMailbox.getDatabaseName(mbox);
        Connection conn = DbPool.getConnection();
        
        assertTrue("Could not find database " + dbName, db.databaseExists(conn, dbName));
        assertFalse("False positive", db.databaseExists(conn, "foobar"));
        
        DbPool.quietClose(conn);
    }
}
