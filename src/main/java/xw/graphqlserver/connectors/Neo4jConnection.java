package xw.graphqlserver.connectors;

import org.neo4j.driver.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Neo4jConnection {

    private Driver driver;

    @Value(value = "${neo4j.properties.url}")
    private String url;
    @Value(value = "${neo4j.properties.username}")
    private String username;
    @Value(value = "${neo4j.properties.password}")
    private String password;

    public Neo4jConnection() {
        System.out.println("Init me");
    }

    private Driver getDriver() {
        if (this.driver == null) {
            this.driver = GraphDatabase.driver(url, AuthTokens.basic(
                username,
                password
            ));
        }
        return driver;
    }

    public List<Record> execute() {
        try (Session session = getDriver().session()) {
            return session.writeTransaction(new TransactionWork<List<Record>>() {
                @Override
                public List<Record> execute(Transaction tx) {
                    Result result = tx.run("MATCH (t:TrackedTask) RETURN t");

                    return result.list();
                }
            });
        }
    }
}
