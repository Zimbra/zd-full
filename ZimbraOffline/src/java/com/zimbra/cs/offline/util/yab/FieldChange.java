/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * Represents a Contact field change.
 */
public class FieldChange extends Entity {
    private final Type type;
    private final Field field;
    private final int fid;

    public static enum Type {
        ADD, UPDATE, REMOVE
    }

    public static FieldChange add(Field field) {
        return new FieldChange(Type.ADD, field, -1);
    }

    public static FieldChange remove(int fid) {
        return new FieldChange(Type.REMOVE, null, fid);
    }

    public static FieldChange update(Field field) {
        return new FieldChange(Type.UPDATE, field, field.getId());
    }
    
    private FieldChange(Type type, Field field, int fid) {
        this.type = type;
        this.field = field;
        this.fid = fid;
    }

    public Type getType() {
        return type;
    }

    public Field getField() {
        return field;
    }

    public int getFieldId() {
        return fid;
    }

    @Override
    public Element toXml(Document doc) {
        switch (type) {
        case ADD:
            return field.toXml(doc, "add-" + field.getName());
        case UPDATE:
            return field.toXml(doc, "update-" + field.getName());
        case REMOVE:
            Element e = doc.createElement("remove-field");
            e.setAttribute(Field.FID, String.valueOf(fid));
            return e;
        }
        return null;
    }
}
