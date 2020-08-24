package xw.graphqlserver.dao;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import xw.graphqlserver.entities.ChargeCode;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Component
@NoArgsConstructor
public class ChargeCodeDAO {

    @PersistenceContext
    private EntityManager em;

    public ChargeCode find(Integer id) {
        return em.createQuery("select cc from ChargeCode cc where cc" +
            ".id = :id", ChargeCode.class)
            .setParameter("id", id)
            .getSingleResult();
    }

    public List<ChargeCode> findAll() {
        return em.createQuery("select cc from ChargeCode cc").getResultList();

    }

    public ChargeCode save(ChargeCode cc) {
        if (cc.getId() == null) {
            cc.setCreatedAt(new Date());
        }
        cc.setUpdatedAt(new Date());
        em.persist(cc);
        return cc;
    }
}
