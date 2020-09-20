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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        //        if (!result.hasNext()) {
        //            return isList ? Collections.emptyList() : Collections
        //            .emptyMap();
        //        } else if (isSingle) {
        //            // this generates key/value pairs for the 1st object in
        //            the
        //            // result set
        //            return result.single()
        //                .asMap()
        //                .entrySet()
        //                .stream()
        //                .findFirst()
        //                .orElseThrow(() -> new RuntimeException("nope"))
        //                .getValue();
        //        } else if (isList) {
        //            List<Object> collect = result.stream()
        //                .map(x -> x.asMap())
        //                .map(Map::entrySet)
        //                .map(x -> x.stream().findFirst())
        //                .filter(x -> x.isPresent())
        //                .map(x -> x.get().getValue())
        //                .collect(Collectors.toList());
        //
        //            return collect;
        //        }

        // Case 1: The result set is empty, so we need to return an empty
        // object (list or map) depending on the result
        // type of the query being executed.
        if (!result.hasNext()) {
            if (resultType == ResultType.LIST) {
                return new ArrayList<>();
            } else {
                return new HashMap<>();
            }
        }
        // Case 2: The result set is not empty and the result type is a
        // single object. Return that object.
        else if (resultType == ResultType.SINGLE) {
            // Sorry this line got a bit crazy. At the moment I'm not seeing
            // a simpler way... The cypher query returns the
            // values mapped to the key of their property name (the same
            // thing as the data fetcher's segment path). But as
            // the graphql-java framework isn't expecting DataFetchers to
            // return the object mapped by its property name, it's
            // expect to get the object and map it to the property name
            // itself, so we awkwardly have to reach into the query's
            // result map - it is fair to assume a map with only one property
            // because that is what the query will produce -
            // and return just the value.
            return result.single()
                .asMap()
                .entrySet()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("nope"))
                .getValue();
        } else if (resultType == ResultType.LIST) {
            List<Object> resultList = new ArrayList<>();
            while (result.hasNext()) {
                Map<String, Object> currentMap = result.next().asMap();
                // All non-empty responses will be a map with a single key.
                // When there are multiple results each call
                // to result.next().asMap() will produce a map with the same
                // key name mapped to a different object. We just need
                // the values for each result in a list so we just pull the
                // one value out of each result to add to our
                // resultList.

                resultList.add(currentMap.entrySet()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("nope"))
                    .getValue());
            }
            return resultList;
        } else {
            // Not sure how/why this would ever happen. See the comment below
            // in getResultType.
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