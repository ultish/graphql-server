package xw.graphqlserver.listeners;

import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
public class PostUpdateListener implements PostUpdateEventListener {

    @Override
    public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
        System.out.println("Hello??");
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {

        return false;
    }
}
