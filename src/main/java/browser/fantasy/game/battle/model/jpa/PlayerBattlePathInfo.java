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
@Table(name = "player_battle_path_info")
public class PlayerBattlePathInfo {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "player_id", nullable = false)
  private Player player;

  @Column(name = "name", nullable = false)
  private String name;

  @OneToMany(mappedBy = "path", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Node> nodes = new ArrayList<>();

  @OneToMany(mappedBy = "path", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Edge> edges = new ArrayList<>();
}
