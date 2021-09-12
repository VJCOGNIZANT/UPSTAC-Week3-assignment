package org.upgrad.upstac.config.api;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.upgrad.upstac.users.roles.UserRole;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static com.google.common.base.Predicates.or;
import static com.google.common.collect.ImmutableList.of;
import static java.util.Collections.singletonList;
import static org.upgrad.upstac.users.roles.UserRole.*;
import static springfox.documentation.builders.PathSelectors.ant;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;
import static springfox.documentation.spi.service.contexts.SecurityContext.builder;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(pathsToBeDocumented())
                .build()
                .securitySchemes(getSecuritySchemes())
                .securityContexts(getSecurityContexts());
    }

    private List<SecurityScheme> getSecuritySchemes() {
        return of(new ApiKey("Authorization", "Authorization", "header"));
    }

    private List<SecurityContext> getSecurityContexts() {
        SecurityContext context = builder()
                .securityReferences(getSecurityReferences())
                .forPaths(pathsToBeSecured())
                .build();

        return singletonList(context);
    }

    private List<SecurityReference> getSecurityReferences() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{
                new AuthorizationScope(getScopeFor(DOCTOR), "Doctors"),
                new AuthorizationScope(getScopeFor(TESTER), "Testers"),
                new AuthorizationScope(getScopeFor(GOVERNMENT_AUTHORITY), "Government Authority"),
                new AuthorizationScope(getScopeFor(USER), "Registered users")
        };

        return singletonList(new SecurityReference("Authorization", authorizationScopes));
    }

    String getScopeFor(UserRole role) {
        return role.name();
    }

    private Predicate<String> pathsToBeDocumented() {
        return or(
                ant("/auth/**"),
                ant("/documents/**"),
                pathsToBeSecured()
        );
    }

    private Predicate<String> pathsToBeSecured() {
        return or(
                ant("/api/testrequests/**"),
                ant("/api/government/**"),
                ant("/api/consultations/**"),
                ant("/users/**"),
                ant("/api/labrequests/**")

        );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Upgrad UPSTAC System")
                .description("UPSTAC Track APIs")
                .contact("Upgrad")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0.0")
                .build();
    }
}