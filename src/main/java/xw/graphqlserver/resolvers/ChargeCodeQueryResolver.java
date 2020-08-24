package xw.graphqlserver.resolvers;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xw.graphqlserver.dao.ChargeCodeDAO;
import xw.graphqlserver.entities.ChargeCode;

import java.util.List;

@Component
public class ChargeCodeQueryResolver implements GraphQLQueryResolver {

    @Autowired
    ChargeCodeDAO chargeCodeDAO;

    public List<ChargeCode> chargeCodes() {
        return chargeCodeDAO.findAll();
    }
}
