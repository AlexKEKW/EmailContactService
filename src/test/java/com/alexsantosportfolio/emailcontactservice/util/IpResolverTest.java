package com.alexsantosportfolio.emailcontactservice.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IpResolver — resolução de IP do cliente")
class IpResolverTest {

    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Deve resolver IP do header X-Forwarded-For")
    void deveResolverIpDoXForwardedFor() {
        mockRequest.addHeader("X-Forwarded-For", "192.168.1.1");

        String ip = IpResolver.resolve();

        assertThat(ip).isEqualTo("192.168.1.1");
    }

    @Test
    @DisplayName("Deve pegar primeiro IP quando X-Forwarded-For tem múltiplos (proxy chain)")
    void devePegarPrimeiroIpComMultiplosForwardedFor() {
        mockRequest.addHeader("X-Forwarded-For", "10.0.0.1, 10.0.0.2, 10.0.0.3");

        String ip = IpResolver.resolve();

        assertThat(ip).isEqualTo("10.0.0.1");
    }

    @Test
    @DisplayName("Deve usar X-Real-IP como fallback quando X-Forwarded-For é null")
    void deveUsarXRealIpComoFallback() {
        mockRequest.addHeader("X-Real-IP", "172.16.0.1");

        String ip = IpResolver.resolve();

        assertThat(ip).isEqualTo("172.16.0.1");
    }

    @Test
    @DisplayName("Deve usar RemoteAddr como último fallback")
    void deveUsarRemoteAddrComoUltimoFallback() {
        mockRequest.setRemoteAddr("127.0.0.1");

        String ip = IpResolver.resolve();

        assertThat(ip).isEqualTo("127.0.0.1");
    }

    @Test
    @DisplayName("Deve ignorar X-Forwarded-For com valor 'unknown' e usar X-Real-IP")
    void deveIgnorarForwardedForComValorUnknown() {
        mockRequest.addHeader("X-Forwarded-For", "unknown");
        mockRequest.addHeader("X-Real-IP", "172.16.0.1");

        String ip = IpResolver.resolve();

        assertThat(ip).isEqualTo("172.16.0.1");
    }

    @Test
    @DisplayName("Deve ignorar X-Real-IP com valor 'unknown' e usar RemoteAddr")
    void deveIgnorarXRealIpComValorUnknown() {
        mockRequest.addHeader("X-Forwarded-For", "unknown");
        mockRequest.addHeader("X-Real-IP", "unknown");
        mockRequest.setRemoteAddr("192.168.0.1");

        String ip = IpResolver.resolve();

        assertThat(ip).isEqualTo("192.168.0.1");
    }

    @Test
    @DisplayName("Deve resolver IP quando X-Forwarded-For está vazio")
    void deveResolverQuandoForwardedForVazio() {
        mockRequest.addHeader("X-Forwarded-For", "");
        mockRequest.setRemoteAddr("10.10.10.10");

        String ip = IpResolver.resolve();

        assertThat(ip).isEqualTo("10.10.10.10");
    }
}
