package com.vipkid.recruitment.event;

import java.util.Map;

/**
 * @author Austin.Cao  Date: 05/12/2016
 */
public interface AuditHandler {

    public Map<String, Object> onAuditEvent(AuditEvent event);
}
