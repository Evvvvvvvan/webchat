// src/main/java/com/web/webMould/config/ResourcesConfig.java
package com.webchat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 将磁盘目录 param.imgAddress 映射为 /upload/**
 * 兼容 Windows / Linux（自动把 \ 转 /，补齐 file: 前缀与末尾 /）
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    private String picAddress = "/data/webchat/upload";

    // 0=关闭  1=开启（默认开启）
    @Value("${param.enableResources:1}")
    private int enableResources;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (enableResources == 0) {
            return;
        }
        String location = normalizeToFileLocation(picAddress);
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }

    private String normalizeToFileLocation(String p) {
        if (p == null) p = "";
        // 统一斜杠
        p = p.replace("\\", "/");
        // 补前缀
        if (!p.startsWith("file:")) {
            p = "file:" + p;
        }
        // 补末尾 /
        if (!p.endsWith("/")) {
            p = p + "/";
        }
        return p;
    }
}
