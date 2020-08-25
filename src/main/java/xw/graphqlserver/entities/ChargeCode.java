package xw.graphqlserver.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Audited
@Table(name = "chargecodes")
public class ChargeCode extends AbstractAudited {
    @Id
    @GeneratedValue(generator = "cc-sequence-generator")
    @GenericGenerator(name = "cc-sequence-generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name",
                value = "chargecodes_id_seq"),
            @org.hibernate.annotations.Parameter(name = "initial_value",
                value = "1"),
            @org.hibernate.annotations.Parameter(name = "increment_size",
                value = "1")
        })
    private Integer id;
    private String name;
    private String code;
    private String description;
    private boolean expired;
    private Date createdAt;
    private Date updatedAt;

    @ManyToMany(mappedBy = "chargeCodes")
    private List<TrackedTask> trackedTasks;
}
