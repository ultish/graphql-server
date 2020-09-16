package xw.graphqlserver.graphql;

import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import kotlin.text.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import xw.graphqlserver.resolvers.ChargeCodeQueryResolver;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

// See GraphQLConfig instead
//@Component
public class GraphQLProvider {

    private GraphQL graphQL;

    @Autowired
    private ChargeCodeQueryResolver chargeCodeQueryResolver;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(
            typeRegistry,
            runtimeWiring
        );
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
            .type(newTypeWiring("Query")
                .dataFetcher(
                    "chargeCodes",
                    chargeCodeQueryResolver.getChargeCodes()
                ))
            .build();
    }
}