package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by Andreas Kutschera.
 */
@Configuration
@Profile( {"consumer", "all"} )
public class ConsumerBean {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger( ConsumerBean.class );

    @Bean
    public Consumer<Person> consumeAndLogPersonEvent() {
        return person -> log.info( "Received: " + person );
    }

}
