package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.model.repository.GroupInfoRepository;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class PlayerBattleEnemiesSpawnService {

  private final GroupInfoRepository groupInfoRepository;

  public PlayerBattleEnemiesSpawnService(GroupInfoRepository groupInfoRepository) {
    this.groupInfoRepository = groupInfoRepository;
  }

  public void spawnEnemyGroups() {
    //TODO
  }
}
