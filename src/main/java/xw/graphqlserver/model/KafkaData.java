package xw.graphqlserver.model;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Taken from legacy-server
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaData {
    public KafkaMetadata meta;
    public Object data;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof KafkaData))
            return false;
        KafkaData kafkaData = (KafkaData) o;
        return Objects.equal(meta, kafkaData.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(meta);
    }
}