package com.vipkid.portal.pesupervisor.service;

import com.vipkid.portal.pesupervisor.model.PeSupervisorData;

public interface PeSupervisorRestService {
	public PeSupervisorData getPeSupervisorData(long teacherId, int page);
}
