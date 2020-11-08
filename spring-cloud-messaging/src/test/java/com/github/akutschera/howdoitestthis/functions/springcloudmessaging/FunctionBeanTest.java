package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Created by Andreas Kutschera.
 */
@SpringBootTest
@Import({ TestChannelBinderConfiguration.class})
@ActiveProfiles("function")
public class FunctionBeanTest {

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private InputDestination inputDestination;

    @Test
    @DisplayName( "How to send a simple JSON string" )
    void testJsonString() throws IOException {
        inputDestination.send( MessageBuilder.withPayload( "{ \"name\": \"foo\" }" ).build(), "throughput-topic" );

        var payload = outputDestination.receive().getPayload();

        var address = new ObjectMapper().readValue( payload, Address.class );

        assertThat( address ).isEqualToComparingFieldByField( new Address( "foo" ) );
    }

    @Test
    @DisplayName("how to send an object")
    void testObject() throws IOException {
        var person = new Person();
        person.setName( "martha" );

        inputDestination.send( MessageBuilder.withPayload( person ).build(), "throughput-topic" );

        var payload = outputDestination.receive().getPayload();

        var address = new ObjectMapper().readValue( payload, Address.class );

        assertThat( address ).isEqualToComparingFieldByField( new Address( "martha" ) );
    }

    @Test
    @DisplayName("how to send a map (Map.toString does not put quotes around the string keys)")
    void testMap() throws IOException {
        var map = Map.of("name", "billy");
        var val = new ObjectMapper().writeValueAsString( map );

        inputDestination.send( MessageBuilder.withPayload( val ).build(), "throughput-topic" );

        var payload = outputDestination.receive().getPayload();

        var address = new ObjectMapper().readValue( payload, Map.class );

        assertThat( address ).containsEntry( "city", "billy" );
    }

    @Test
    @DisplayName("how to use a JSONObject for wrapping a map")
    void jsonObjectMap() throws IOException {
        var jsonObject = new JSONObject( Map.of( "name", "billy" ) );

        inputDestination.send( MessageBuilder.withPayload( jsonObject ).build(), "throughput-topic" );

        var payload = outputDestination.receive().getPayload();

        var address = new ObjectMapper().readValue( payload, Map.class );

        assertThat( address ).containsEntry( "city", "billy" );
    }

}
