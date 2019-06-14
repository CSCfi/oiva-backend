package fi.minedu.oiva.backend.core.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;
import fi.minedu.oiva.backend.core.spring.handler.CompletionStageReturnValueHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private TypeResolver typeResolver;

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("**/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("**/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
    }

    @Bean
    public Docket swaggerConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("oiva")
                .apiInfo(apiInfo())
                .select()
                .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(Timestamp.class, Long.class)
                .alternateTypeRules(buildTypeRules());
    }

    private AlternateTypeRule[] buildTypeRules() {
        return new AlternateTypeRule[]{
                newRule(typeResolver.resolve(CompletableFuture.class,
                        typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                        typeResolver.resolve(WildcardType.class)),
                newRule(typeResolver.resolve(CompletableFuture.class,
                        typeResolver.resolve(HttpEntity.class, WildcardType.class)),
                        typeResolver.resolve(WildcardType.class)),
                newRule(typeResolver.resolve(CompletableFuture.class,
                        typeResolver.resolve(Collection.class, WildcardType.class)),
                        typeResolver.resolve(List.class, WildcardType.class)),
                newRule(typeResolver.resolve(CompletableFuture.class,
                        typeResolver.resolve(Page.class, WildcardType.class)),
                        typeResolver.resolve(Page.class, WildcardType.class)),
                newRule(typeResolver.resolve(CompletableFuture.class,
                        typeResolver.resolve(WildcardType.class)),
                        typeResolver.resolve(WildcardType.class)),
                newRule(typeResolver.resolve(CompletionStage.class,
                        typeResolver.resolve(WildcardType.class)),
                        typeResolver.resolve(WildcardType.class))
        };
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Oiva API",
                "Opetushallinnon ohjaus- ja säätelypalvelun API",
                "2.0",
                "",
                new Contact("", "", ""),
                "",
                "",
                Collections.EMPTY_LIST
        );
    }

    @Bean
    public HandlerMethodReturnValueHandler completionStageReturnValueHandler() {
        return new CompletionStageReturnValueHandler();
    }
}
