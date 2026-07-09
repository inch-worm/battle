package browser.fantasy.game.battle.model.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Entity
@Data
@Table(name = "node")
public class Node {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "path_id", nullable = false)
  private PlayerBattlePathInfo path;

  @Column(name = "x_coordinate")
  private Integer xCoordinate;

  @Column(name = "y_coordinate")
  private Integer yCoordinate;

  @OneToMany(mappedBy = "node", cascade = CascadeType.ALL)
  private List<GroupInfo> groupInfos = new ArrayList<>();

  @Column(name = "is_root", nullable = false)
  private boolean isRoot = false;
}
