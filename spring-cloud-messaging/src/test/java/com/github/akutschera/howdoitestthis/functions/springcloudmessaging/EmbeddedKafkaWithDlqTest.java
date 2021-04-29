package com.github.akutschera.howdoitestthis.functions.springcloudmessaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Map;

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
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@EmbeddedKafka(topics = { "person-in", "dead-out", "address-out" })
@ActiveProfiles("kafka-with-dlq")
public class EmbeddedKafkaWithDlqTest {

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
    @DisplayName("How to test that an error sends the message to a DLQ")
    void withKafka() {
        var nonConvertableMessage = "{ \"fail\": \"me\" }";
        template.send( "person-in", nonConvertableMessage.getBytes( StandardCharsets.UTF_8 ) );

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps( "testT", "true", embeddedKafka );
        DefaultKafkaConsumerFactory<byte[], String> cf = new DefaultKafkaConsumerFactory<>(
                consumerProps );
        consumer = cf.createConsumer();
        embeddedKafka.consumeFromAllEmbeddedTopics( consumer );

        var thisMayTakeAWhile = 5_000L;
        ConsumerRecord<byte[], String> received = KafkaTestUtils.getSingleRecord( consumer, "dead-out", thisMayTakeAWhile );
        var dlqPayload = received.value();

        assertThat( dlqPayload ).isEqualTo( nonConvertableMessage );
    }

}
