package xw.graphqlserver.resolvers;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xw.graphqlserver.dao.TrackedTaskDAO;
import xw.graphqlserver.entities.TrackedTask;

import java.util.List;

@Component
public class TrackedTaskQueryResolver implements GraphQLQueryResolver {

    @Autowired
    TrackedTaskDAO trackedTaskDAO;

    public List<TrackedTask> trackedTasks() {
        return trackedTaskDAO.findAll();
    }
}
