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

@Entity
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

  @OneToMany(mappedBy = "node", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GroupInfo> groupInfos = new ArrayList<>();

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public PlayerBattlePathInfo getPath() {
    return path;
  }

  public void setPath(PlayerBattlePathInfo path) {
    this.path = path;
  }

  public Integer getXCoordinate() {
    return xCoordinate;
  }

  public void setXCoordinate(Integer xCoordinate) {
    this.xCoordinate = xCoordinate;
  }

  public Integer getYCoordinate() {
    return yCoordinate;
  }

  public void setYCoordinate(Integer yCoordinate) {
    this.yCoordinate = yCoordinate;
  }

  public List<GroupInfo> getGroupInfos() {
    return groupInfos;
  }

  public void setGroupInfos(List<GroupInfo> groupInfos) {
    this.groupInfos = groupInfos;
  }
}
