package com.vipkid.trpm.controller.portal;

import com.vipkid.trpm.controller.AbstractController;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAnyRole('ROLE_PORTAL') and fullyAuthenticated")
public abstract class AbstractPortalController extends AbstractController {

    protected String view(String name) {
        return "portal/" + name;
    }

}
