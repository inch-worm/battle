package browser.fantasy.game.battle.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "edge")
public class Edge {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "path_id", nullable = false)
  private PlayerBattlePathInfo path;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "from_node_id", nullable = false)
  private Node fromNode;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "to_node_id", nullable = false)
  private Node toNode;

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

  public Node getFromNode() {
    return fromNode;
  }

  public void setFromNode(Node fromNode) {
    this.fromNode = fromNode;
  }

  public Node getToNode() {
    return toNode;
  }

  public void setToNode(Node toNode) {
    this.toNode = toNode;
  }
}
