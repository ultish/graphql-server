package xw.graphqlserver.controller;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import lombok.SneakyThrows;
import org.neo4j.driver.Driver;
import org.neo4j.graphql.Cypher;
import org.neo4j.graphql.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xw.graphqlserver.model.GraphQLRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static graphql.ExecutionInput.newExecutionInput;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Controller that implements a GraphQL API by using the graphql-java library
 * as the main "driver" of resolution, but
 * where all of the top-level queries and mutations are set up to be resolved
 * by the CypherDataFetcher, which uses
 * the neo4j-graphql-java library to generate the cypher queries used to
 * implement these queries and mutations.
 * <p>
 * This is actually a pretty cool implementation. It means we're getting
 * magic where a GraphQL query can be
 * transformed into a single Cypher query, and for simple data fetching cases
 * and simple CRUD operations this means
 * you pretty much don't have to implement anything except defining new types
 * in the GraphQL schema. And even less
 * simple cases can work just by editing the GraphQL schema if you're
 * willing/able to write the cypher for them with
 * neo4j-graphql-java's @cypher directive in the GraphQL schema.
 * <p>
 * But unlike the implementation in the LegacyGraphQLController, this one
 * plays nice with graphql-java's universe, and
 * in particular its algorithm for using DataFetchers to return the data. The
 * top-level queries are resolved with cypher
 * which in many cases will return a full object graph that you can return
 * as-is. But since we still go through the
 * DataFetcher framework to resolve each field of each object in that graph
 * it means we have a place where we can easily
 * plug in custom logic to do whatever we need in the resolution of a
 * particular property, so we have the ability to
 * implement business rules in code on top of that property that transform it
 * (or auth rules that may strip it out),
 * or format it in a way that's difficult in cypher, or even add a property
 * to our GraphQL schema that isn't resolved by
 * Neo4j at all and has a totally different DataFetcher implementation. We've
 * got default neo4j-graphql-java magic
 * combined with flexibility offered by the DataFetcher framework. Pretty sweet!
 */
@RestController("neo-graphql-controller-using-graphql-java")
public class GraphQLController {

    private static final Pattern pattern = Pattern.compile(
        "(?s)^.*\\s+AS\\s+(.*)\\s*$");

    @Autowired
    GraphQL graphQL;

    @Autowired
    Translator translator;

    @Autowired
    @Qualifier("graphql-driver")
    Driver driver;

    @PostMapping("/graphql")
    @SneakyThrows
    public Map<String, Object> graphQLAPI(@RequestBody GraphQLRequest request) {

        // We use a special case to allow GraphQL's introspection queries. In
        // particular this gets used by GraphiQL to
        // implement its autocomplete feature. We need to do this as a
        // special case because the translator will puke on
        // GraphQL queries for introspection types.
        // The contains checks here are a bit cheesy and overbroad but maybe
        // good enough? Also if the user sent in an
        // introspection and a non-introspection query at the same time we
        // won't handle it properly. We can fix that if it
        // ever matters, but it doesn't seem critically important.
        if (request.getQuery().contains("__schema") || request.getQuery()
            .contains("__type")) {

            Map<String, Object> result = new HashMap<>();
            result.put("data", graphQL.execute(request.getQuery()).getData());
            return result;
        }

        // Amazing magic occurs here! Each GraphQL query (somewhat
        // confusingly, the request.getQuery() string can represent
        // multiple queries to run in the same batch) is translated into a
        // single Cypher query. This means that instead
        // of having to make a lot of database queries to fetch individual
        // node properties, each top-level GraphQL query
        // only executes one cypher query. Bow down before the mighty
        // Translator.
        //
        // We run the translator here in the controller because its required
        // argument is the raw GraphQL query string and
        // that's no longer visible when you get to DataFetchers. The trick
        // we're doing is generating all the cypher queries
        // here and putting them in a context object that gets passed to the
        // DataFetchers, with each query under a key that
        // the CypherDataFetcher can use to find the right Cypher query to
        // execute.
        List<Cypher> queries = translator.translate(request.getQuery());

        Map<String, Cypher> context = queries.stream()
            .collect(toMap(this::extractQueryKey, identity()));
        ExecutionInput.Builder input =
            newExecutionInput().query(request.getQuery())
                .context(context);

        Map<String, Object> result = new HashMap<>();

        ExecutionResult executionResult = graphQL.execute(input);

        if (!executionResult.getErrors().isEmpty()) {
            // TODO generate graphQL error using this data
            for (GraphQLError error : executionResult.getErrors()) {
                System.err.println(error.getMessage());
            }
        }

        result.put("data", executionResult.getData());
        return result;
    }

    private String extractQueryKey(Cypher query) {
        Matcher matcher = pattern.matcher(query.getQuery());
        return matcher.matches() ? matcher.group(1) : null;
    }
}
