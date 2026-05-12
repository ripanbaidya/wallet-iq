package online.walletiq.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * Configuration for email template rendering.
 * Sets up a dedicated Thymeleaf template resolver and engine for email
 * templates to keep them isolated from the web view template configuration.
 */
@Configuration
public class MailConfig {

    /**
     * Resolves HTML email templates from the classpath.
     */
    @Bean(name = "emailTemplateResolver")
    public ITemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

        resolver.setPrefix("templates/mail/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setOrder(1);
        resolver.setCheckExistence(true);

        return resolver;
    }

    /**
     * Thymeleaf template engine used for processing email templates.
     */
    @Bean(name = "emailTemplateEngine")
    public SpringTemplateEngine emailTemplateEngine(
        @Qualifier("emailTemplateResolver") ITemplateResolver resolver
    ) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addTemplateResolver(resolver);
        return engine;
    }
}