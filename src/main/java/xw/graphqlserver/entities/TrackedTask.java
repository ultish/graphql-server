package xw.graphqlserver.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "trackedtasks")
public class TrackedTask {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(name = "sequence-generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @Parameter(name = "sequence_name",
                value = "trackedtasks_id_seq"),
            @Parameter(name = "initial_value", value = "1"),
            @Parameter(name = "increment_size", value = "1")
        })
    private Integer id;
    private String notes;
    @ManyToMany
    @JoinTable(name = "taskcodes", joinColumns = @JoinColumn(name =
        "trackedtaskId"),
        inverseJoinColumns = @JoinColumn(name = "chargecodeId")
    )
    private List<ChargeCode> chargeCodes;
    //    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trackedtaskId")
    //    private List<TimeBlock> timeBlocks;
    private Date createdAt;
    private Date updatedAt;
    private Boolean overtimeEnabled;

}



