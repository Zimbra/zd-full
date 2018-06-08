/*
 * 
 */
package com.zimbra.cs.index.query;

import com.zimbra.cs.index.LuceneFields;

/**
 * Query by MIME type.
 *
 * @author tim
 * @author ysasaki
 */
public final class TypeQuery extends LuceneQuery {

    public TypeQuery(String what) {
        super("type:", LuceneFields.L_MIMETYPE, lookup(AttachmentQuery.MAP, what));
    }

}
