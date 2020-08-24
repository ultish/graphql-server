package xw.graphqlserver.resolvers;

import graphql.GraphQLException;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xw.graphqlserver.dao.ChargeCodeDAO;
import xw.graphqlserver.dao.TrackedTaskDAO;
import xw.graphqlserver.entities.ChargeCode;
import xw.graphqlserver.entities.TrackedTask;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class TrackedTaskMutationResolver implements GraphQLMutationResolver {

    @Autowired
    TrackedTaskDAO trackedTaskDAO;

    @Autowired
    ChargeCodeDAO chargeCodeDAO;

    public TrackedTask createTrackedTask(List<Integer> chargeCodeIds) {
        TrackedTask tt = new TrackedTask();
        if (chargeCodeIds != null) {
            List<ChargeCode> chargeCodes = chargeCodeDAO.findAll()
                .stream()
                .filter(cc -> chargeCodeIds.contains(cc.getId()))
                .collect(
                    Collectors.toList());

            tt.setChargeCodes(chargeCodes);
        }

        return trackedTaskDAO.save(tt);
    }

    public TrackedTask updateTrackedTask(
        Integer id, String notes,
        List<Integer> chargeCodeIds
    ) {

        TrackedTask tt = trackedTaskDAO.find(id);
        if (tt != null) {
            if (notes != null) {
                tt.setNotes(notes);
            }
            if (chargeCodeIds != null) {
                List<ChargeCode> chargeCodes = chargeCodeDAO.findAll()
                    .stream()
                    .filter(cc -> chargeCodeIds.contains(cc.getId()))
                    .collect(
                        Collectors.toList());

                tt.setChargeCodes(chargeCodes);
            }

            return trackedTaskDAO.save(tt);
        } else {
            throw new GraphQLException("Invalid TrackedTask ID: " + id);
        }
    }
}
