package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.UnitPlacementRequest;
import browser.fantasy.game.battle.model.ServiceException;
import browser.fantasy.game.battle.model.jpa.GroupInfo;
import browser.fantasy.game.battle.model.jpa.Node;
import browser.fantasy.game.battle.model.jpa.PlayerBattlePathInfo;
import browser.fantasy.game.battle.model.jpa.UnitOwner;
import browser.fantasy.game.battle.model.repository.GroupInfoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PlayerBattleMovementService {

  private final GroupInfoRepository groupInfoRepository;

  public PlayerBattleMovementService(GroupInfoRepository groupInfoRepository) {
    this.groupInfoRepository = groupInfoRepository;
  }

  public void moveEnemyGroups(
      PlayerBattlePathInfo playerBattlePathInfo,
      Map<UUID, UUID> previousNodeIdsByNodeId,
      Map<UUID, Node> nodesById) {
    List<GroupMove> groupMovesEnemy = new ArrayList<>();
    playerBattlePathInfo
        .getNodes()
        .forEach(
            node ->
                node.getGroupInfos().stream()
                    .filter(groupInfo -> UnitOwner.ENEMY.equals(groupInfo.getOwner()))
                    .forEach(
                        groupInfo -> {
                          UUID destinationNodeId =
                              previousNodeIdsByNodeId.get(groupInfo.getNode().getId());
                          if (destinationNodeId != null) {
                            Node destinationNode = nodesById.get(destinationNodeId);
                            if (destinationNode.getGroupInfos().stream()
                                    .noneMatch(gi -> UnitOwner.PLAYER.equals(gi.getOwner()))
                                && !destinationNode.isRoot()) {
                              groupMovesEnemy.add(new GroupMove(groupInfo, node, destinationNode));
                            }
                          }
                        }));

    groupMovesEnemy.forEach(this::groupMove);
  }

  public void movePlayerGroups(
      PlayerBattlePathInfo playerBattlePathInfo,
      Map<UUID, UUID> nextNodeIdsByNodeId,
      Map<UUID, Node> nodesById) {
    List<GroupMove> groupMovesPlayer = new ArrayList<>();
    playerBattlePathInfo
        .getNodes()
        .forEach(
            node ->
                node.getGroupInfos().stream()
                    .filter(groupInfo -> UnitOwner.PLAYER.equals(groupInfo.getOwner()))
                    .forEach(
                        groupInfo -> {
                          UUID destinationNodeId =
                              nextNodeIdsByNodeId.get(groupInfo.getNode().getId());
                          if (destinationNodeId != null) {
                            Node destinationNode = nodesById.get(destinationNodeId);
                            if (destinationNode.getGroupInfos().stream()
                                .noneMatch(gi -> UnitOwner.ENEMY.equals(gi.getOwner()))) {
                              groupMovesPlayer.add(new GroupMove(groupInfo, node, destinationNode));
                            }
                          }
                        }));

    groupMovesPlayer.forEach(this::groupMove);
  }

  private void groupMove(GroupMove groupMove) {
    groupMove.fromNode().getGroupInfos().remove(groupMove.groupInfo());
    groupMove.groupInfo().setNode(groupMove.toNode());
    groupMove.toNode().getGroupInfos().add(groupMove.groupInfo());
  }

  public void placePlayerUnits(
      Map<UUID, Node> nodesById, UnitPlacementRequest unitPlacementRequest) {

    unitPlacementRequest
        .getUnitPlacementDtos()
        .forEach(
            unitPlacementDto -> {
              GroupInfo groupInfo =
                  groupInfoRepository
                      .findById(UUID.fromString(unitPlacementDto.getUnplacedGroupInfoId()))
                      .orElseThrow(
                          () ->
                              new ServiceException(
                                  "Invalid unit placement id passed "
                                      + unitPlacementDto.getUnplacedGroupInfoId(),
                                  "IUPIP"));
              groupInfo.setNode(nodesById.get(UUID.fromString(unitPlacementDto.getNodeId())));
              groupInfoRepository.save(groupInfo);
            });
  }

  private record GroupMove(GroupInfo groupInfo, Node fromNode, Node toNode) {}
}
