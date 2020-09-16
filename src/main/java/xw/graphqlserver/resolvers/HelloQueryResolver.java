package xw.graphqlserver.resolvers;

import org.springframework.stereotype.Component;

@Component
public class HelloQueryResolver /*implements GraphQLQueryResolver */ {

    public String hello() {
        return "Hello Server";
    }

}
