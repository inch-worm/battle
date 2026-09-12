package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.model.jpa.GroupInfo;
import browser.fantasy.game.battle.model.jpa.Node;
import browser.fantasy.game.battle.model.jpa.Path;
import browser.fantasy.game.battle.model.jpa.UnitOwner;
import browser.fantasy.game.battle.model.repository.GroupInfoRepository;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class PlayerBattleEnemiesSpawnService {

  private final GroupInfoRepository groupInfoRepository;

  public PlayerBattleEnemiesSpawnService(GroupInfoRepository groupInfoRepository) {
    this.groupInfoRepository = groupInfoRepository;
  }

  public void spawnEnemyGroups(List<Path> paths, Long turnCount) {
    List<Node> leafNodes =
        paths.stream().flatMap(path -> path.getNodes().stream()).filter(Node::isLeaf).toList();

    List<GroupInfo> enemiesToSpawn =
        groupInfoRepository.findByNodeIsNullAndOwnerAndTurnCountToAppear(
            UnitOwner.ENEMY, turnCount);

    enemiesToSpawn.forEach(
        enemyToSpawn -> {
          Node nodeToSpawn = randomNode(leafNodes);
          enemyToSpawn.setNode(nodeToSpawn);
          nodeToSpawn.getGroupInfos().add(enemyToSpawn);
          groupInfoRepository.save(enemyToSpawn);
        });
  }

  private Node randomNode(List<Node> nodes) {
    return nodes.get(ThreadLocalRandom.current().nextInt(nodes.size()));
  }
}
