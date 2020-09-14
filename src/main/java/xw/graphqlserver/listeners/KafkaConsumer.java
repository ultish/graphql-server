package xw.graphqlserver.listeners;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "datastream", clientIdPrefix =
        "xwClientId", id = "xwId")
    public void listenGroupFoo(String message) {

        System.out.println("[" + Thread.currentThread()
            .getId() + "] Received " +
            "Message: " + message);
    }
}
