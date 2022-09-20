package com.bmc.mii.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = {"com.bmc.mii.*"})
@PropertySource("classpath:DCconfig.properties")
public class ConfigFile {

    @Autowired
    private Environment env;

    @Bean
    public ConfigurationValue getConfigurationValue() {
        return new ConfigurationValue(
                env.getProperty("remedy.server"),
                env.getProperty("remedy.username"),
                env.getProperty("remedy.password"),
                env.getProperty("remedy.port"),
                env.getProperty("remedy.middleform.CHG"),
                env.getProperty("remedy.middleform.Kontrak"),
                env.getProperty("remedy.middleform.WOI"),
                env.getProperty("remedy.middleform.SRM"),
                env.getProperty("documentum.credentials"),
                env.getProperty("documentum.endpoint"),
                env.getProperty("documentum.linkfile"));
    }
}
