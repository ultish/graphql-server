package xw.graphqlserver.model;

import lombok.Data;

import java.util.Map;

@Data
public class GraphQLRequest {
    String query;
    String operationName;
    Map<String, Object> variables;
}