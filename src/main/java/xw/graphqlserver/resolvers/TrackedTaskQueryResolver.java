package xw.graphqlserver.resolvers;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;
import xw.graphqlserver.entities.TrackedTask;

import java.util.ArrayList;
import java.util.List;

@Component
public class TrackedTaskQueryResolver implements GraphQLQueryResolver {

    public List<TrackedTask> trackedTasks() {
        return new ArrayList<>();
    }
}
