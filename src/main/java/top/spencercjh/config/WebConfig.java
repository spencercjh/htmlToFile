package top.spencercjh.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.spencercjh.utils.OsInfo;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 欧阳洁
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final Environment environment;

    @Autowired
    public WebConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        final int maxAge = 0;
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/").
                setCacheControl(CacheControl.maxAge(maxAge, TimeUnit.MINUTES));
        registry.addResourceHandler("/output/**").addResourceLocations("classpath:/output/").
                setCacheControl(CacheControl.maxAge(maxAge, TimeUnit.MINUTES));
        registry.addResourceHandler("/image/**").addResourceLocations("file:" +
                (OsInfo.isWindows() ? environment.getProperty("windows-output-image-path") :
                        environment.getProperty("linux-output-image-path")));
        registry.addResourceHandler("/pdf/**").addResourceLocations("file:" +
                (OsInfo.isWindows() ? environment.getProperty("windows-output-pdf-path") :
                        environment.getProperty("linux-output-pdf-path")));
    }

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(Charset.forName(String.valueOf(StandardCharsets.UTF_8)));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(responseBodyConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }
}
