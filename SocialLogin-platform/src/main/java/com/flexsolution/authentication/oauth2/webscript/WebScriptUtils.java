package com.flexsolution.authentication.oauth2.webscript;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;

/**
 * Created by max on 12/7/17 .
 */
public final class WebScriptUtils {

    public static void setSessionAttribute(WebScriptRequest req, String key, Object value) {
        WebScriptServletRuntime.getHttpServletRequest(req).getSession().setAttribute(key, value);
    }

    public static Object getSessionAttribute(WebScriptRequest req, String key) {
        return WebScriptServletRuntime.getHttpServletRequest(req).getSession().getAttribute(key);
    }

    public static void removeSessionAttribute(WebScriptRequest req, String key) {
        WebScriptServletRuntime.getHttpServletRequest(req).getSession().removeAttribute(key);
    }
}
