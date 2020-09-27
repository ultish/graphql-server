package xw.graphqlserver.neo4j;

import graphql.schema.*;
import lombok.SneakyThrows;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.graphql.Cypher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data fetcher that resolves a property by generating a cypher query using
 * the neo4j-graphql-java library.
 * <p>
 * The GraphQLController comment has some good info about why this is cool.
 * <p>
 * This data fetcher only makes sense to be hooked into top-level queries and
 * mutations, because those are the only ones
 * neo4j-graphql-java is able to make cypher queries from. We are expecting
 * the cypher queries to be set on the context
 * and passed in, with the query appropriate to the data fetcher under a key
 * equal to the name/alias of the property
 * we're resolving in the GraphQL query.
 */
@Component
public class CypherDataFetcher implements DataFetcher<Object> {

    @Autowired
    @Qualifier("graphql-driver")
    Driver neoDriver;

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws
        Exception {
        Map<String, Cypher> cypherMap = dataFetchingEnvironment.<Map<String,
            Cypher>>getContext();
        // The segment name is the name of the property we're currently
        // returning in the overall query. This may be getting
        // a default name based on type or it may be aliased, but it doesn't
        // matter. Either way it is the key where we're
        // expecting to find the appropriate cypher query in the context.
        String cypherKey = dataFetchingEnvironment.getExecutionStepInfo()
            .getPath()
            .getSegmentName();

        Cypher cypher = cypherMap.get(cypherKey);
        Map<String, Object> paramMap = mergeRequestAndCypherParams(
            dataFetchingEnvironment.getArguments(),
            cypher
        );

        try (Session session = neoDriver.session()) {
            Result result = session.run(cypher.getQuery(), paramMap);
            return extractResultData(result, cypher);
        }
    }

    ;

    Map<String, Object> mergeRequestAndCypherParams(
        Map<String, Object> requestParams,
        Cypher query
    ) {
        // The generated cypher queries will use variables instead of
        // literals even for values that are literal in the
        // graphQL query. When it does this the variables it generated are in
        // the cypher object that comes back from the
        // translator. We need to make sure when we run the cypher query we
        // have all the variables that the translator
        // defined AND all the ones that were specified in the graphQL
        // request itself.
        Map<String, Object> queryVariables = new HashMap<>(query.getParams());
        if (requestParams != null) {
            queryVariables.putAll(requestParams);
        }
        return queryVariables;
    }

    @SneakyThrows
    Object extractResultData(Result result, Cypher query) {
        ResultType resultType = getResultType(query.getType());

        GraphQLType typeCheck = query.getType();
        if (query.getType() instanceof GraphQLNonNull) {
            typeCheck = ((GraphQLNonNull) query.getType()).getWrappedType();
        }

        boolean isList = typeCheck instanceof GraphQLList;
        boolean isSingle = typeCheck instanceof GraphQLObjectType;

        if (!result.hasNext()) {
            return isList ? Collections.emptyList() : Collections
                .emptyMap();
        } else if (isSingle) {
            // this generates key/value pairs for the 1st object in
            // the result set
            return result.single()
                .asMap()
                .entrySet()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("nope"))
                .getValue();
        } else if (isList) {
            List<Object> collect = result.stream()
                .map(x -> x.asMap())
                .map(Map::entrySet)
                .map(x -> x.stream().findFirst())
                .filter(x -> x.isPresent())
                .map(x -> x.get().getValue())
                .collect(Collectors.toList());

            return collect;
        } else {
            // TODO return error?
            return null;
        }
    }

    private ResultType getResultType(GraphQLType type) {
        // The type field in the Cypher object is nullable and defaults to
        // null, which implies that there may be a case
        // where we'd expect to get a null type here. We aren't sure what
        // that case actually is in practice though, so
        // although we defined this NONE value we don't really know how to
        // deal with a NONE in the code above. Seems like
        // something we may be able to figure out if and when we stumble
        // across an example.
        if (type == null) {
            return ResultType.NONE;
        } else if (type instanceof GraphQLList) {
            return ResultType.LIST;
        } else if (type instanceof GraphQLObjectType) {
            return ResultType.SINGLE;
        } else if (type instanceof GraphQLNonNull) {
            return getResultType(((GraphQLNonNull) type).getWrappedType());
        } else {
            // not really sure if this is correct
            return ResultType.SINGLE;
        }
    }

    private enum ResultType {
        NONE,
        SINGLE,
        LIST
    }
}