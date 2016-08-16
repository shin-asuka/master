package com.vipkid.trpm.controller.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vipkid.trpm.controller.AbstractController;

@PreAuthorize("hasAnyRole('ROLE_PORTAL') and fullyAuthenticated")
public abstract class AbstractPortalController extends AbstractController {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractPortalController.class);

	protected String view(String name) {
		return "portal/" + name;
	}

}
