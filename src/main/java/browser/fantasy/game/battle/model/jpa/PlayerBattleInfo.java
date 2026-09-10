package browser.fantasy.game.battle.model.jpa;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Entity
@Data
@Table(name = "player_battle_info")
public class PlayerBattleInfo {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "player_id", nullable = false)
  private Player player;

  @Column(name = "turn_count")
  private Long turnCount;

  @Column(name = "player_hp")
  private Long playerHp;

  @OneToMany(mappedBy = "playerBattleInfo", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Path> paths = new ArrayList<>();
}
