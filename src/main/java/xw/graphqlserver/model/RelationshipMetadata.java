package xw.graphqlserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

// Taken from legacy-server
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelationshipMetadata {
    List<Map<String, Object>> data;

}
