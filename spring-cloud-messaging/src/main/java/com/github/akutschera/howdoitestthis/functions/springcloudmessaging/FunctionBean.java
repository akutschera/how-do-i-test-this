package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by Andreas Kutschera.
 */
@Configuration
@Profile( {"function", "kafka", "kafka-with-dlq", "all"} )
public class FunctionBean {
    @Bean
    public Function<Person, Address> convertPersonToAddress() {
        return person -> new Address(person.getName());
    }
}
