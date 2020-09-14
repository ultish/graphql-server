package xw.graphqlserver.configs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer,
        String>>
    kafkaListenerContainerFactory() {

        // TODO this has no concurrency. We should be able to exploit
        //  concurrency at the message producer and consumer. TBD when
        //  productionising this

        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class
        );
        props.put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class
        );
        return props;
    }
    //
    //
    //    @Bean
    //    public ConsumerFactory<String, String> consumerFactory() {
    //        Map<String, Object> props = new HashMap<>();
    //        props.put(
    //            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
    //            bootstrapAddress
    //        );
    //        props.put(
    //            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
    //            StringDeserializer.class
    //        );
    //        props.put(
    //            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    //            StringDeserializer.class
    //        );
    //        return new DefaultKafkaConsumerFactory<>(props);
    //    }
    //
    //    @Bean
    //    public ConcurrentKafkaListenerContainerFactory<String, String>
    //    kafkaListenerContainerFactory() {
    //
    //        ConcurrentKafkaListenerContainerFactory<String, String> factory =
    //            new ConcurrentKafkaListenerContainerFactory<>();
    //        factory.setConsumerFactory(consumerFactory());
    //        return factory;
    //    }
}
