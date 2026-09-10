package browser.fantasy.game.battle.model.repository;

import browser.fantasy.game.battle.model.jpa.PlayerBattleInfo;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerBattleInfoRepository extends JpaRepository<PlayerBattleInfo, UUID> {

  List<PlayerBattleInfo> findByPlayerId(UUID playerId);
}
