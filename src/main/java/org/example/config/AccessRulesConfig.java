package org.example.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.security.access-rules")
@Getter
public class AccessRulesConfig {

    private List<Rule> rules = new ArrayList<>();

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    @Getter
    @Setter
    public static class Rule {
        private String pattern;
        private String access;
        private List<HttpMethod> methods;

        public AuthorizationManager<RequestAuthorizationContext> getAuthorizationManager() {
            return (authentication, requestContext) -> {
                if ("permitAll".equals(access)) {
                    return new AuthorizationDecision(true);
                }
                if ("denyAll".equals(access)) {
                    return new AuthorizationDecision(false);
                }
                if ("authenticated".equals(access)) {
                    return new AuthorizationDecision(
                            authentication.get() != null && authentication.get().isAuthenticated()
                    );
                }
                if (access != null && access.startsWith("hasRole(") && access.endsWith(")")) {
                    String role = access.substring(8, access.length() - 1)
                            .replace("'", "").replace("\"", "");
                    boolean granted = authentication.get() != null
                            && authentication.get().getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals(role));
                    return new AuthorizationDecision(granted);
                }
                if (access != null && access.startsWith("hasAnyRole(") && access.endsWith(")")) {
                    String rolesStr = access.substring(11, access.length() - 1);
                    String[] roles = rolesStr.split(",");
                    boolean granted = authentication.get() != null
                            && authentication.get().getAuthorities().stream()
                            .anyMatch(a -> {
                                for (String role : roles) {
                                    if (a.getAuthority().equals(role.trim().replace("'", "").replace("\"", ""))) {
                                        return true;
                                    }
                                }
                                return false;
                            });
                    return new AuthorizationDecision(granted);
                }
                return new AuthorizationDecision(false);
            };
        }
    }

}