/*
 * 
 */

package com.zimbra.soap.account.type;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.soap.type.ContactsDataSource;

@XmlRootElement(name="contacts")
@XmlType(propOrder = {})
public class AccountContactsDataSource
extends AccountDataSource
implements ContactsDataSource {

}
