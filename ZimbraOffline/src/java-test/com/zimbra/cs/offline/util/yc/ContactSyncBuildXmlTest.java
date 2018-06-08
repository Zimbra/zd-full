/*
 * 
 */
package com.zimbra.cs.offline.util.yc;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zimbra.cs.offline.util.Xml;

public class ContactSyncBuildXmlTest {

    @Test
    public void testBuildXml() {
        DocumentBuilder builder = Xml.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("contactsync");
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("yahoo_contacts_server_add.xml");
            Document contactDoc = builder.parse(stream);
            Element contactRoot = contactDoc.getDocumentElement();
            Assert.assertEquals("contactsync", contactRoot.getNodeName());

            Contact contact = new Contact();
            contact.extractFromXml(Xml.getChildren(contactRoot).get(1));

            Element contactEle = contact.toXml(root.getOwnerDocument());
            root.appendChild(contactEle);

            System.out.println(Xml.toString(root));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
