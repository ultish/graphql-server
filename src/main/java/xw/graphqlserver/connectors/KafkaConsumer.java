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

                } else {
                    if (entry.getValue() instanceof Number) {
                        nodeProperties.put(entry.getKey(), entry.getValue());
                    } else {
                        String val = null;
                        if (entry.getValue() != null) {
                            val = "'" + entry.getValue().toString() + "'";
                        }
                        nodeProperties.put(entry.getKey(), val);
                    }
                }
            });

            // TODO a pretty crude Cypher query that will create or update a
            //  Node of label: entityName, and attach properties to it via
            //  the 'nodeProperties' we gathered above. It will then go
            //  through all the relationships detected for the entity and
            //  generate MERGE statements that will create the destination
            //  Node if it doesn't exist, then create a un-directed
            //  relationship to it. This should be enhanced to have different
            //  relationship Types as it's now only using a highly inventive
            //  relationship Type called... "USES".

            // TODO as noted above this does NOT perform DELETEs yet

            StringBuilder sb = new StringBuilder();
            sb.append("MERGE (n:" + meta.entityNameForNode() + " { id: " + meta.getId() + " }) ");
            sb.append("\nON CREATE SET ");
            String nodeProps = nodeProperties.entrySet().stream()
                .map(e -> "n." + e.getKey() + " = " + e.getValue())
                .collect(Collectors.joining(", "));
            sb.append(nodeProps);
            sb.append("\nON MATCH SET ");
            sb.append(nodeProps);

            String rels = relationshipsForNode.entrySet()
                .stream()
                .map(e -> {
                    String destNode = e.getKey().replaceAll("[.]", "_");
                    int relIdx = 0;
                    List<String> innerCyphers = new ArrayList<>();
                    for (String relId : e.getValue()) {
                        String d = destNode + (relIdx++);
                        String result = "\nMERGE (" + d + ":" + destNode +
                            " { " +
                            "id: " + relId + " }) ";
                        result += "\nMERGE (n)-[:USES]-(" + d + ") ";

                        innerCyphers.add(result);
                    }

                    return innerCyphers.stream().collect(Collectors.joining());

                })
                .collect(Collectors.joining());
            sb.append(rels);

            sb.append("\nRETURN n");

            System.out.println("Execute to Neo4j:\n" + sb.toString());

            List<Record> stream = neo4j.execute(sb.toString());

            stream.forEach(r -> {
                // "n" as that's the return value of the cypher query above
                System.out.println("id: " + r.get("n").asMap());

            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("[" + Thread.currentThread()
            .getId() + "] Received " +
            "Message: " + message);

    }
}
