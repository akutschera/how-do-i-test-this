package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

/**
 * Created by Andreas Kutschera.
 */
@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.cloud.stream.kafka.bindings.convertPersonToAddress-in-0.consumer.ack-mode=BATCH"
})
@EmbeddedKafka(topics = { "person-in", "address-out" })
@ActiveProfiles("kafka")
public class EmbeddedKafkaTest {

    @Autowired
    EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    KafkaTemplate<byte[], byte[]> template;
    private Consumer<byte[], String> consumer;

    @AfterEach
    void shutdown() {
        consumer.close();
    }

    @Test
    @DisplayName("How to test this with a Kafka Broker")
    void withKafka() throws IOException {
        template.send( "person-in", "{ \"name\": \"foo\" }".getBytes( StandardCharsets.UTF_8 ) );

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps( "testT", "true", embeddedKafka );
        DefaultKafkaConsumerFactory<byte[], String> cf = new DefaultKafkaConsumerFactory<>(
                consumerProps );
        consumer = cf.createConsumer();
        embeddedKafka.consumeFromAllEmbeddedTopics( consumer );

        ConsumerRecord<byte[], String> received = KafkaTestUtils.getSingleRecord( consumer, "address-out", 2_000L );
        var payload = received.value();

        var address = new ObjectMapper().readValue( payload, Address.class );

        assertThat( address ).usingRecursiveComparison().isEqualTo( new Address( "foo" ) );
    }

}
