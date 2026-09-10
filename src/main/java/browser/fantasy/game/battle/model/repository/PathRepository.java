package browser.fantasy.game.battle.model.repository;

import browser.fantasy.game.battle.model.jpa.Path;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PathRepository extends JpaRepository<Path, UUID> {

  List<Path> findByPlayerBattleInfoId(UUID playerId);
}
