package xw.graphqlserver.model;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Taken from legacy-server
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMetadata {
    String type;
    String revType;
    String id;
    String entityName;
    int rev;
    RelationshipMetadata relationships;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof KafkaMetadata))
            return false;
        KafkaMetadata that = (KafkaMetadata) o;
        return Objects.equal(id, that.id) &&
            Objects.equal(entityName, that.entityName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, entityName);
    }
}
