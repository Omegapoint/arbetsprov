package se.omegapoint.config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;
import se.omegapoint.controllers.models.CalculationRequest;
import se.omegapoint.controllers.models.CalculationResult;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ReactiveKafkaConsumerTemplate<String, CalculationResult> reactiveKafkaConsumerTemplate() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // should be an environment variable instead
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "public-addition-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "se.omegapoint.controllers.models.CalculationResult");

        ReceiverOptions<String, CalculationResult> receiverOptions =
                ReceiverOptions.<String, CalculationResult>create(props)
                        .withValueDeserializer(new JsonDeserializer<>(CalculationResult.class));
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, CalculationRequest> reactiveKafkaProducerTemplate() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // should be an environment variable instead
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(JsonSerializer.TYPE_MAPPINGS, "se.omegapoint.controllers.models.CalculationRequest");

        SenderOptions<String, CalculationRequest> senderOptions = SenderOptions.create(props);

        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }
}