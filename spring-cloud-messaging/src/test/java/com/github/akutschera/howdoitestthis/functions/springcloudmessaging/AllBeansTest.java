package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
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
@ActiveProfiles("all")
public class AllBeansTest {

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private InputDestination inputDestination;

    @Autowired
    private ProducerBean producerBean;

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
    @DisplayName( "How to send input" )
    void testConsumer() {
        inputDestination.send( MessageBuilder.withPayload( "{ \"name\": \"foo\" }" ).build() );

        Assertions.assertThat( listAppender.list)
                  .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                  .containsExactly( Tuple.tuple( "Received: foo", Level.INFO));
    }

    @Test
    @DisplayName( "How to test input AND output" )
    void testFunction() throws IOException {
        inputDestination.send( MessageBuilder.withPayload( "{ \"name\": \"foo\" }" ).build(), "throughput-topic" );

        var payload = outputDestination.receive().getPayload();

        var address = new ObjectMapper().readValue( payload, Address.class );

        assertThat( address ).isEqualToComparingFieldByField( new Address( "foo" ) );
    }

    @Test
    @DisplayName( "How to assert output" )
    void testProducer() {
        producerBean.sendCity( "foo" );
        var payload = outputDestination.receive(0L, "produceTopic").getPayload();

        assertThat( payload ).isEqualTo( "{\"city\":\"foo\"}".getBytes() );
    }

}
