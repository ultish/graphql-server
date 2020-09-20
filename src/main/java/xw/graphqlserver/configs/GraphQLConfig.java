package xw.graphqlserver.configs;

import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.*;
import lombok.SneakyThrows;
import org.neo4j.driver.*;
import org.neo4j.graphql.SchemaBuilder;
import org.neo4j.graphql.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xw.graphqlserver.neo4j.CypherDataFetcher;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
public class GraphQLConfig {

    @Autowired
    private CypherDataFetcher cypherQueryDataFetcher;
    private String schema;

    @SneakyThrows
    @PostConstruct
    public void init() {
        URL url = Resources.getResource("schema.graphqls");
        schema = Resources.toString(url, UTF_8);
    }

    @Bean
    Translator translator() {
        return new Translator(SchemaBuilder.buildSchema(schema));
    }

    @Bean("graphql-driver")
    Driver driver() {
        Config config = Config.builder().withLogging(Logging.slf4j()).build();
        return GraphDatabase.driver(
            "bolt://localhost:7687",
            AuthTokens.basic("neo4j", "test"),
            config
        );
    }

    @Bean
    GraphQL graphQL() {
        GraphQLSchema graphQLSchema = buildSchema();
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    /**
     * This requires a bit of explanation.
     * <p>
     * First, realize that the SchemaBuilder.buildSchema call is using the
     * neo4j-graphql-java library
     * (https://github.com/neo4j-graphql/neo4j-graphql-java), which is mostly
     * about auto-generating cypher queries based
     * on a GraphQL schema. But one of the things it does when you use it to
     * build a schema is create a whole mess of CRUD
     * queries and mutators based on the domain types in your GraphQL schema
     * - basically all of the operations it knows
     * how to auto-generate cypher queries for.
     * <p>
     * We want the queries and mutators that it generates, but we also need
     * to customize it a bit - namely we need to
     * set all of the DataFetchers for top-level queries and mutations to our
     * fancy CypherDataFetcher which uses
     * neo4j-graphql-java's GraphQL to Cypher translator to resolve the
     * operations neo4j-graphql-java defines with the
     * queries that neo4j-graphql-java is able to build.
     * <p>
     * Hence this buildSchema method works by creating neo4j-graphql-java's
     * augmented schema, looking at it for all
     * the top-level queries and mutations, and then creating a new schema
     * based on it but with the CypherDataFetchers
     * plugged in for the top queries and mutations, and with any other
     * custom DataFetchers needed for properties in
     * the schema.
     */
    private GraphQLSchema buildSchema() {
        GraphQLSchema neoGeneratedGraphQLSchema = SchemaBuilder.buildSchema(
            schema);

        //        Map<String, DataFetcher> queryDataFetchers = new HashMap<>();
        //        for (GraphQLType queryType : neoGeneratedGraphQLSchema
        //            .getQueryType()
        //            .getChildren()) {
        //            queryDataFetchers.put(
        //                queryType.getName(),
        //                cypherQueryDataFetcher
        //            );
        //        }
        //        Map<String, DataFetcher> mutationDataFetchers = new
        //        HashMap<>();
        //        for (GraphQLType mutationType :
        //            neoGeneratedGraphQLSchema.getMutationType()
        //                .getChildren()) {
        //            mutationDataFetchers.put(
        //                mutationType.getName(),
        //                cypherQueryDataFetcher
        //            );
        //        }

        Map<String, DataFetcher<?>> queryDataFetchers =
            neoGeneratedGraphQLSchema.getQueryType()
                .getFieldDefinitions()
                .stream()
                .collect(
                    Collectors.toMap(
                        GraphQLFieldDefinition::getName,
                        (x -> cypherQueryDataFetcher)
                    ));

        Map<String, DataFetcher<?>> mutationDataFetchers =
            neoGeneratedGraphQLSchema.getQueryType()
                .getFieldDefinitions()
                .stream()
                .collect(Collectors.toMap(
                    GraphQLFieldDefinition::getName,
                    (x) -> cypherQueryDataFetcher
                ));

        GraphQLCodeRegistry customCodeRegistry =
            GraphQLCodeRegistry.newCodeRegistry(neoGeneratedGraphQLSchema.getCodeRegistry())
                .dataFetchers("Query", queryDataFetchers)
                .dataFetchers("Mutation", mutationDataFetchers)
                .dataFetcher(
                    FieldCoordinates.coordinates("Hunt", "name"),
                    huntNameDataFetcher()
                )
                .build();

        return GraphQLSchema.newSchema(neoGeneratedGraphQLSchema)
            .codeRegistry(customCodeRegistry)
            .build();
    }

    /**
     * This is just a simple example of plugging in a custom data fetcher for
     * a property that's already been resolved
     * by a cypher query. Our "source" here is a Map that was part of the
     * return value of our original cypher query,
     * and the "name" field in it is what would have been returned by the
     * default PropertyDataFetcher, but by creating
     * our own DataFetcher and inserting it into our schema-building we can
     * get custom logic to happen when resolving
     * any property of our GraphQL schema.
     */
    @Bean
    DataFetcher huntNameDataFetcher() {
        return dataFetchingEnvironment -> {
            String name =
                dataFetchingEnvironment.<Map<String, Object>>getSource()
                    .get("name")
                    .toString();
            return name.toLowerCase();
        };
    }
}
