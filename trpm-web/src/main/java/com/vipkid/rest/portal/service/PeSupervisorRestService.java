package com.vipkid.rest.portal.service;

import com.vipkid.rest.portal.model.PeSupervisorData;

public interface PeSupervisorRestService {
	public PeSupervisorData getPeSupervisorData(long teacherId, int page);
}
