package com.integrador.rocket.roadmap.controller;

import com.auth0.jwt.JWT;
import com.integrador.rocket.roadmap.infra.security.TokenService;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.users.dto.UserAuthentication;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RememberMeTest {
    @Test void loginUsesSessionCookieByDefaultAndPersistentCookieWhenRemembered() {
        for (boolean remember : new boolean[]{false, true}) {
            var user = mock(User.class);
            var manager = mock(AuthenticationManager.class);
            var tokens = mock(TokenService.class);
            var controller = new AuthenticationController();
            ReflectionTestUtils.setField(controller, "authenticationManager", manager);
            ReflectionTestUtils.setField(controller, "tokenService", tokens);
            when(manager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(user, null, List.of()));
            var lifetime = remember ? Duration.ofDays(7) : Duration.ofHours(2);
            when(tokens.generarToken(user, lifetime)).thenReturn("test-token");
            var response = new MockHttpServletResponse();
            controller.iniciarSesion(new UserAuthentication("user@example.com", "password", remember), new MockHttpServletRequest(), response);
            String cookie = response.getHeader("Set-Cookie");
            assertNotNull(cookie);
            assertTrue(cookie.contains("HttpOnly"));
            assertTrue(cookie.contains("Secure"));
            if (remember) assertTrue(cookie.contains("Max-Age=604800"));
            else assertFalse(cookie.contains("Max-Age"));
            verify(tokens).generarToken(user, lifetime);
        }
    }

    @Test void tokenExpiryMatchesTheSelectedLifetime() {
        var service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", "test-secret-for-remember-me");
        var user = mock(User.class);
        when(user.getEmail()).thenReturn("user@example.com");
        for (var lifetime : List.of(Duration.ofHours(2), Duration.ofDays(7))) {
            var before = Instant.now().plus(lifetime).minusSeconds(2);
            var token = service.generarToken(user, lifetime);
            var expires = JWT.decode(token).getExpiresAtAsInstant();
            assertTrue(expires.isAfter(before));
            assertTrue(expires.isBefore(Instant.now().plus(lifetime).plusSeconds(2)));
            assertEquals("user@example.com", service.getSubject(token));
        }
    }
}
