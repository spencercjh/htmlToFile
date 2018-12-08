package top.spencercjh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * @author 欧阳洁
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //如果我们要指定一个绝对路径的文件夹（如 H:/myimgs/ ），则只需要使用 addResourceLocations 指定即可。
        //registry.addResourceHandler("/myimgs/**").addResourceLocations("file:H:/myimgs/");
        //无缓存静态资源，开发时候调成0
        final int maxAge = 0;
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/").setCacheControl(CacheControl.maxAge(maxAge, TimeUnit.MINUTES));
        registry.addResourceHandler("/output/**").addResourceLocations("classpath:/output/").setCacheControl(CacheControl.maxAge(maxAge, TimeUnit.MINUTES));

        super.addResourceHandlers(registry);
    }
}
