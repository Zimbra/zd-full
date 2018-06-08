/*
 * 
 */

package com.zimbra.cs.taglib.tag.i18n;

import com.zimbra.cs.taglib.tag.i18n.I18nUtil;

import java.io.*;
import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import javax.servlet.http.HttpServletRequest;

public class GetLocaleRequestTag extends SimpleTagSupport  {

    protected String var = "locale";
    protected int scope = I18nUtil.DEFAULT_SCOPE_VALUE;

    public void setVar(String var) {
        this.var = var;
    }

    public void setScope(String scope) {
        this.scope = I18nUtil.getScope(scope);
    }

    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        Locale locale = request.getLocale();
        String localeId = request.getParameter("localeId");

        if (localeId != null) {
            int index = localeId.indexOf("_");

            if (index == -1) {
                locale = new Locale(localeId);
            } else {
                String language = localeId.substring(0, index);
                String country = localeId.substring(localeId.length() - 2);
                locale = new Locale(language, country);
            }
        }

        pageContext.setAttribute(this.var, locale, this.scope);
    }
}