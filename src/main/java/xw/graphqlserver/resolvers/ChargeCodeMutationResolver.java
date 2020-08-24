package xw.graphqlserver.resolvers;

import graphql.GraphQLException;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xw.graphqlserver.dao.ChargeCodeDAO;
import xw.graphqlserver.entities.ChargeCode;

@Component
@Transactional
public class ChargeCodeMutationResolver implements GraphQLMutationResolver {

    @Autowired
    ChargeCodeDAO chargeCodeDAO;

    public ChargeCode updateChargeCode(
        Integer id, String name, String code,
        Boolean expired, String description
    ) {

        ChargeCode cc = chargeCodeDAO.find(id);
        if (cc != null) {
            if (name != null) {
                cc.setName(name);
            }
            if (code != null) {
                cc.setCode(code);
            }
            if (description != null) {
                cc.setDescription(description);
            }
            if (expired != null) {
                cc.setExpired(expired);
            }

            return chargeCodeDAO.save(cc);

        } else {
            throw new GraphQLException("Invalid ChargeCode ID: " + id);
        }
    }
}
