package xw.graphqlserver.connectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.neo4j.driver.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import xw.graphqlserver.model.KafkaData;
import xw.graphqlserver.model.KafkaMetadata;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KafkaConsumer {
    @Autowired
    private Neo4jConnection neo4j;

    @KafkaListener(topics = "datastream", clientIdPrefix =
        "xwClientId", id = "xwId")
    public void listenGroupFoo(String message) {

        ObjectMapper mapper = new ObjectMapper();

        try {
            KafkaData kafkaData = mapper.readValue(message, KafkaData.class);
            System.out.println(kafkaData);

            KafkaMetadata meta = kafkaData.getMeta();
            List<Map<String, Object>> relationships =
                meta.getRelationships().getData();
            Map<String, Map<String, Object>> relMap = relationships.stream()
                .collect(Collectors.toMap(
                    x -> x.get("field").toString(),
                    Function.identity()
                ));

            Map<String, Object> data =
                (Map<String, Object>) kafkaData.getData();

            // TODO this listener is only dealing with CREATE/UPDATE cases
            //  would need to look at kafkaData.meta.revType to find DELETES too

            Map<String, Collection<String>> relationshipsForNode =
                new HashMap<>();

            Map<String, Object> nodeProperties = new HashMap<>();

            data.entrySet().forEach(entry -> {
                if (relMap.containsKey(entry.getKey())) {
                    // relationship
                    Map<String, Object> relData =
                        relMap.get(entry.getKey());
                    String destination = relData.get("destination").toString();
                    //                    System.out.println("To: " +
                    //                    destination + ": " + entry
                    //                        .getValue());

                    if (entry.getValue() instanceof Collection) {
                        Collection<Object> col = (Collection) entry.getValue();

                        List<String> relKeys = col.stream()
                            .map(o -> o.toString())
                            .collect(Collectors.toList());

                        relationshipsForNode.put
                            (destination, relKeys);
                    } else {
                        relationshipsForNode.put(
                            destination,
                            Arrays.asList(entry.getValue().toString())
                        );
                    }

                    // create relationship
                } else {
                    nodeProperties.put(entry.getKey(), entry.getValue());
                }
            });

            System.out.println("MERGE NODE");
            System.out.println(nodeProperties);
            System.out.println("With Relationships...");
            System.out.println(relationshipsForNode);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("[" + Thread.currentThread()
            .getId() + "] Received " +
            "Message: " + message);

        System.out.println("Execute to Neo4j");

        List<Record> stream = neo4j.execute();

        stream.forEach(r -> {
            //            System.out.println("\tRecord: " + r);

            System.out.println(r.get("t").asMap());
        });
    }
}
