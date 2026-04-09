package com.alexsantosportfolio.emailcontactservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public final class IpResolver {

    private static final Logger logger = LoggerFactory.getLogger(IpResolver.class);

    private IpResolver() {
        // Utility class - prevent instantiation
    }

    public static String resolve() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            logger.debug("X-Real-IP: {}", ip);
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            logger.debug("RemoteAddr: {}", ip);
        }

        // X-Forwarded-For pode conter múltiplos IPs (client, proxy1, proxy2)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        logger.debug("IP resolvido: {}", ip);
        return ip;
    }
}
