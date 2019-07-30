package com.xukexiang.house;

import com.xukexiang.house.user.config.NewRuleConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
//@RibbonClient(name = "provider-service", configuration = NewRuleConfig.class)
public class ProviderApp
{
    public static void main( String[] args )
    {
        SpringApplication.run(ProviderApp.class,args);
    }
}
