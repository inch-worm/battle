package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.model.jpa.GroupInfo;
import browser.fantasy.game.battle.model.jpa.Node;
import browser.fantasy.game.battle.model.jpa.PlayerBattlePathInfo;
import browser.fantasy.game.battle.model.jpa.UnitOwner;
import browser.fantasy.game.battle.model.repository.GroupInfoRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class PlayerBattleCombatService {

  private final GroupInfoRepository groupInfoRepository;

  public PlayerBattleCombatService(GroupInfoRepository groupInfoRepository) {
    this.groupInfoRepository = groupInfoRepository;
  }

  public void resolveFightOutcomes(
      PlayerBattlePathInfo playerBattlePathInfo,
      Map<UUID, UUID> previousNodeIdsByNodeId,
      Map<UUID, Node> nodesById) {
    Map<UUID, GroupAttack> groupAttacksPerNodeEnemy = new HashMap<>();
    playerBattlePathInfo
        .getNodes()
        .forEach(
            node ->
                node.getGroupInfos().stream()
                    .filter(groupInfo -> UnitOwner.ENEMY.equals(groupInfo.getOwner()))
                    .forEach(
                        groupInfo -> {
                          UUID currentNodeId = node.getId();
                          UUID destinationNodeId = previousNodeIdsByNodeId.get(currentNodeId);
                          if (destinationNodeId != null) {
                            Node destinationNode = nodesById.get(destinationNodeId);
                            if (groupAttacksPerNodeEnemy.containsKey(currentNodeId)) {
                              groupAttacksPerNodeEnemy.get(currentNodeId).attackers.add(groupInfo);
                            } else {
                              List<GroupInfo> attackers = List.of(groupInfo);
                              List<GroupInfo> defenders =
                                  destinationNode.getGroupInfos().stream()
                                      .filter(gi -> UnitOwner.PLAYER.equals(gi.getOwner()))
                                      .toList();
                              if (!CollectionUtils.isEmpty(defenders)) {
                                groupAttacksPerNodeEnemy.put(
                                    currentNodeId,
                                    new GroupAttack(attackers, node, defenders, destinationNode));
                              }
                            }
                          }
                        }));

    groupAttacksPerNodeEnemy.values().forEach(this::resolveFightOutcome);
  }

  private void resolveFightOutcome(GroupAttack groupAttack) {
    int attackersAttack = groupAttack.attackers().stream().mapToInt(a -> a.getCount() * a.getUnitType().getAttack()).sum();
    int defendersAttack = groupAttack.defenders().stream().mapToInt(d -> d.getCount() * d.getUnitType().getAttack()).sum();

    List<GroupInfo> defenderCasualties = new ArrayList<>();
    for (GroupInfo d : groupAttack.defenders().stream()
            .sorted(Comparator.comparing((d) -> d.getUnitType().getOrderInFight()))
            .toList()) {
      attackersAttack = attackersAttack - d.getCount() * d.getUnitType().getHp();
      if (attackersAttack >= 0) {
        defenderCasualties.add(d);
      } else {
        d.setCount(-attackersAttack/d.getUnitType().getHp());
        break;
      }
    }

    List<GroupInfo> attackersCasualties = new ArrayList<>();
    for (GroupInfo a :
        groupAttack.attackers().stream()
            .sorted(Comparator.comparing((a) -> a.getUnitType().getOrderInFight()))
            .toList()) {
      defendersAttack = defendersAttack - a.getCount() * a.getUnitType().getHp();
      if (defendersAttack >= 0) {
        attackersCasualties.add(a);
      } else {
        a.setCount(-defendersAttack/a.getUnitType().getHp());
        break;
      }
    }

    defenderCasualties.forEach(
        dc -> {
          groupAttack.defendersNode().getGroupInfos().remove(dc);
          groupInfoRepository.deleteById(dc.getId());
        });
    attackersCasualties.forEach(
        ac -> {
          groupAttack.attackersNode().getGroupInfos().remove(ac);
          groupInfoRepository.deleteById(ac.getId());
        });
  }

  private record GroupAttack(
      List<GroupInfo> attackers,
      Node attackersNode,
      List<GroupInfo> defenders,
      Node defendersNode) {}
}
