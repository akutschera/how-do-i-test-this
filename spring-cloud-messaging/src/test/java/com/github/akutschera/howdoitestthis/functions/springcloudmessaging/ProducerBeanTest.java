package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Created by Andreas Kutschera.
 */
@SpringBootTest
@Import({ TestChannelBinderConfiguration.class})
@ActiveProfiles("producer")
public class ProducerBeanTest {

    @Autowired
    private OutputDestination output;

    @Autowired
    private ProducerBean producerBean;

    @Test
    @DisplayName( "How to assert a simple JSON string" )
    void testJsonString() {
        producerBean.sendCity( "foo" );
        var payload = output.receive().getPayload();

        assertThat( payload ).isEqualTo( "{\"city\":\"foo\"}".getBytes() );
    }

    @Test
    @DisplayName("how to assert an object")
    void testObject() throws IOException {
        producerBean.sendCity( "foo" );
        var payload = output.receive().getPayload();

        var address = new ObjectMapper().readValue( payload, Address.class );

        assertThat( address ).isEqualToComparingFieldByField( new Address( "foo" ) );
    }

    @Test
    @DisplayName("how to assert a map")
    void testMap() throws IOException {
        producerBean.sendCity( "foo" );
        var payload = output.receive().getPayload();

        var address = new ObjectMapper().readValue( payload, Map.class );

        assertThat( address ).containsEntry( "city", "foo" );
    }

}
