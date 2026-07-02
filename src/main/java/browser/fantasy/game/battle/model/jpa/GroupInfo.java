package browser.fantasy.game.battle.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "group_info")
@Data
public class GroupInfo {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "node_id", nullable = false)
  private Node node;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "unit_type", nullable = false)
  private UnitType unitType;

  @Column(name = "count")
  private Integer count;

  @Enumerated(EnumType.STRING)
  @Column(name = "owner", length = 50)
  private UnitOwner owner;
}
