package xw.graphqlserver.dao;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import xw.graphqlserver.entities.TrackedTask;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Component
@NoArgsConstructor
public class TrackedTaskDAO {

    @PersistenceContext
    private EntityManager em;

    public TrackedTask find(Integer id) {
        return em.createQuery("select tt from TrackedTask tt where tt" +
            ".id = :id", TrackedTask.class)
            .setParameter("id", id)
            .getSingleResult();
    }

    public List<TrackedTask> findAll() {
        return em.createQuery("select tt from TrackedTask tt").getResultList();

    }

    public TrackedTask save(@NotNull TrackedTask tt) {
        if (tt.getId() == null) {
            tt.setCreatedAt(new Date());
        }
        tt.setUpdatedAt(new Date());
        em.persist(tt);
        return tt;
    }
}
