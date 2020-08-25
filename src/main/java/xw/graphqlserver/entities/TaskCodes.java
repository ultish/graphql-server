package xw.graphqlserver.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Not used at the moment
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//@Entity
//@IdClass(TaskCodesPK.class)
//@Table(name = "taskcodes")
public class TaskCodes extends AbstractAudited {

    @Id
    @ManyToOne
    @JoinColumn(name = "chargecodeId", referencedColumnName = "id")
    private ChargeCode chargeCode;

    @Id
    @ManyToOne
    @JoinColumn(name = "trackedtaskId", referencedColumnName = "id")
    private TrackedTask trackedTask;
}
