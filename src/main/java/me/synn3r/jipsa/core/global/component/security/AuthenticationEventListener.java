package me.synn3r.jipsa.core.component.security;

import lombok.RequiredArgsConstructor;
import me.synn3r.jipsa.core.component.security.enumerations.AuthenticationFailureType;
import me.synn3r.jipsa.core.component.security.logging.AuthenticationFailureLogger;
import me.synn3r.jipsa.core.component.security.logging.AuthenticationSuccessLogger;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private final AuthenticationFailureLogger authenticationFailureLogger;
    private final AuthenticationSuccessLogger authenticationSuccessLogger;

    @EventListener
    public void authenticationSuccessEvent(AuthenticationSuccessEvent event) {
        authenticationSuccessLogger.saveAuthenticationSuccessHistory(
          (UserDetails) event.getAuthentication().getPrincipal());
    }

    @EventListener
    public void authenticationFailureBadCredentialsEvent(AuthenticationFailureServiceExceptionEvent event) {
        AuthenticationFailureType failureType = AuthenticationFailureType.valueOf(event.getException());
        authenticationFailureLogger.saveAuthenticationFailureHistory(event.getAuthentication().getName(), failureType);
    }

    @EventListener
    public void authenticationFailureBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent event) {
        AuthenticationFailureType failureType = AuthenticationFailureType.valueOf(event.getException());
        authenticationFailureLogger.saveAuthenticationFailureHistory(event.getAuthentication().getName(), failureType);
    }


}
