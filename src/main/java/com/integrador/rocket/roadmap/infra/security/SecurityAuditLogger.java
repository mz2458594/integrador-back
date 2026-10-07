package com.integrador.rocket.roadmap.infra.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;


@Component
public class SecurityAuditLogger {

    private static final Logger log = LoggerFactory.getLogger("SECURITY_AUDIT");


    @EventListener
    public void onLoginFailure(AbstractAuthenticationFailureEvent event){
        log.warn("LOGIN FAILED user={} ip={} reason={}",
                clean(event.getAuthentication().getName()),
                ipFrom(event.getAuthentication()),
                event.getException().getClass().getSimpleName()
                );
    }

    @EventListener
    public void onLoginSuccess(AuthenticationSuccessEvent event){
        log.info("LOGIN SUCCESS user={} ip={}",
                clean(event.getAuthentication().getName()),
                ipFrom(event.getAuthentication())
                );
    }

    private String ipFrom(Authentication auth) {
        return auth.getDetails() instanceof WebAuthenticationDetails d ? d.getRemoteAddress(): "desconocida";
    }

    private String clean (String value){
        return value == null ? "null": value.replaceAll("[\\r\\n]", "_");
    }
}
