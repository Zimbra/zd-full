/*
 * 
 */

package com.zimbra.soap.account.type;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/*
   <pref name="{name}" modified="{modified-time}">{value}</pref>
 */
public class Pref {
    
    @XmlAttribute private String name;
    @XmlAttribute(name="modified") private Long modifiedTimestamp;
    @XmlValue private String value;

    public Pref() {
    }
    
    public Pref(String name) {
        setName(name);
    }
    
    public Pref(String name, String value) {
        setName(name);
        setValue(value);
    }
    
    public String getName() { return name; }
    public Pref setName(String name) { this.name = name; return this; }
    
    public Long getModifiedTimestamp() { return modifiedTimestamp; }
    public Pref setModifiedTimestamp(Long timestamp) { this.modifiedTimestamp = timestamp; return this; }
    
    public String getValue() { return value; }
    public Pref setValue(String value) { this.value = value; return this; }
    
    public static Multimap<String, String> toMultimap(Iterable<Pref> prefs) {
        Multimap<String, String> map = ArrayListMultimap.create();
        for (Pref p : prefs) {
            map.put(p.getName(), p.getValue());
        }
        return map;
    }
}
