// package com.projet.toppatoo.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.web.header.HeaderWriter;

// @Configuration
// public class HeadersConfig {
    
//     @Bean
//     public HeaderWriter securityHeaders() {
//         return (request, response) -> {
//             // ✅ X-Content-Type-Options
//             response.setHeader("X-Content-Type-Options", "nosniff");
            
//             // ✅ X-Frame-Options
//             response.setHeader("X-Frame-Options", "DENY");
            
//             // ✅ X-XSS-Protection
//             response.setHeader("X-XSS-Protection", "1; mode=block");
            
//             // ✅ Content-Security-Policy
//             response.setHeader(
//                 "Content-Security-Policy",
//                 "default-src 'self'; " +
//                 "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net; " +
//                 "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
//                 "font-src 'self' https://fonts.gstatic.com; " +
//                 "img-src 'self' data: https:; " +
//                 "connect-src 'self' https://api.toppatoo.com; " +
//                 "frame-src 'self' https://meet.toppatoo.com"
//             );
            
//             // ✅ HSTS (pour HTTPS)
//             response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            
//             // ✅ Referrer-Policy
//             response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            
//             // ✅ Permissions-Policy
//             response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
//         };
//     }
// }