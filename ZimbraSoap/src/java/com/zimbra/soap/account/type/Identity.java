/*
 * 
 */

package com.zimbra.soap.account.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/*
     <identity name={identity-name} id="...">
       <a name="{name}">{value}</a>
       ...
       <a name="{name}">{value}</a>
     </identity>*

 */
@XmlType(propOrder = {})
public class Identity {

    @XmlAttribute private String name;
    @XmlAttribute private String id;
    @XmlElement(name="a") private List<Attr> attrs = new ArrayList<Attr>();


    public Identity() {
    }

    public Identity(Identity i) {
        name = i.getName();
        id = i.getId();
        attrs.addAll(Lists.transform(i.getAttrs(), Attr.COPY));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Attr> getAttrs() {
        return Collections.unmodifiableList(attrs);
    }

    public void setAttrs(Iterable<Attr> attrs) {
        Iterables.addAll(this.attrs, attrs);
    }

    public Multimap<String, String> getAttrsMultimap() {
        return Attr.toMultimap(attrs);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("name", name)
            .add("id", id)
            .add("attrs", attrs)
            .toString();
    }

}
