package xw.graphqlserver.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskCodesPK {
    private Integer chargecodeId;
    private Integer trackedtaskId;
}
