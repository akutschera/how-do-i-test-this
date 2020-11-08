package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * Created by Andreas Kutschera.
 */
@SpringBootTest
@Import({ TestChannelBinderConfiguration.class})
@ActiveProfiles("consumer")
public class ConsumerBeanTest {

    @Autowired
    InputDestination inputDestination;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger consumerBeanLogger;

    @BeforeEach
    void setupTestLogger() {
        consumerBeanLogger = (Logger) LoggerFactory.getLogger( ConsumerBean.class);

        listAppender = new ListAppender<>();
        listAppender.start();

        consumerBeanLogger.addAppender( listAppender );
    }

    @AfterEach
    void clearTestLogger() {
        consumerBeanLogger.detachAndStopAllAppenders();
    }

    @Test
    @DisplayName( "How to send a simple JSON string" )
    void testJsonString() {
        inputDestination.send( MessageBuilder.withPayload( "{ \"name\": \"foo\" }" ).build() );

        Assertions.assertThat( listAppender.list)
                  .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                  .containsExactly( Tuple.tuple( "Received: foo", Level.INFO));
    }

    @Test
    @DisplayName("how to send an object")
    void testObject() {
        var payload = new Person();
        payload.setName( "martha" );

        inputDestination.send( MessageBuilder.withPayload( payload ).build() );

        Assertions.assertThat( listAppender.list)
                  .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                  .containsExactly( Tuple.tuple( "Received: martha", Level.INFO));
    }

    @Test
    @DisplayName("how to send a map (Map.toString does not put quotes around the string keys)")
    void testMap() throws JsonProcessingException {
        var payload = Map.of("name", "billy");
        var val = new ObjectMapper().writeValueAsString( payload );

        inputDestination.send( MessageBuilder.withPayload( val ).build() );

        Assertions.assertThat( listAppender.list)
                  .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                  .containsExactly( Tuple.tuple( "Received: billy", Level.INFO));
    }

    @Test
    @DisplayName("how to use a JSONObject for wrapping a map")
    void jsonObjectMap() {
        var payload = new JSONObject( Map.of( "name", "billy" ) );

        inputDestination.send( MessageBuilder.withPayload( payload ).build() );

        Assertions.assertThat( listAppender.list)
                  .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                  .containsExactly( Tuple.tuple( "Received: billy", Level.INFO));
    }

}
