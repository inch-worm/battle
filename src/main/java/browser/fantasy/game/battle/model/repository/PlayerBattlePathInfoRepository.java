package browser.fantasy.game.battle.model.repository;

import browser.fantasy.game.battle.model.jpa.PlayerBattlePathInfo;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerBattlePathInfoRepository extends JpaRepository<PlayerBattlePathInfo, UUID> {

  List<PlayerBattlePathInfo> findByPlayerId(UUID playerId);
}
