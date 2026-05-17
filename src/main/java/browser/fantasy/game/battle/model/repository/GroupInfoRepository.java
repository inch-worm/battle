package browser.fantasy.game.battle.model.repository;

import browser.fantasy.game.battle.model.jpa.GroupInfo;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupInfoRepository extends JpaRepository<GroupInfo, UUID> {}
