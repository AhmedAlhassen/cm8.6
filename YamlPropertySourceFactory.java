package com.camunda.engine.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {

        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();

        factory.setResources(resource.getResource());

        Properties properties = factory.getObject();

        return new PropertiesPropertySource(getNameForResource(resource.getResource()),properties != null ? properties : new Properties());

    }

    private static String getNameForResource(Resource resource) {
        String name = resource.getFilename();
        if (!StringUtils.hasText(name)) {
            name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
        }
        return name;
    }
}
