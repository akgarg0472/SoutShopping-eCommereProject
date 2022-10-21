package com.akgarg.ecommerce.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws IOException {
        boolean hasUserRole = false;
        boolean hasAdminRole = false;
        boolean hasSellerRole = false;

        final Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();

        for (final GrantedAuthority grantedAuthority : grantedAuthorities) {
            if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
                hasUserRole = true;
                break;
            } else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                hasAdminRole = true;
                break;
            } else if (grantedAuthority.getAuthority().equals("ROLE_SELLER")) {
                hasSellerRole = true;
                break;
            }
        }

        if (hasUserRole) {
            redirectStrategy.sendRedirect(request, response, "/user/dashboard");
        } else if (hasAdminRole) {
            redirectStrategy.sendRedirect(request, response, "/admin/dashboard");
        } else if (hasSellerRole) {
            redirectStrategy.sendRedirect(request, response, "/seller/dashboard");
        } else {
            throw new IllegalStateException("Invalid role received for user");
        }
    }

}