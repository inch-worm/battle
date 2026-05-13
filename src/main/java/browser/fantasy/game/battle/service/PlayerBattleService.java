package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.PlayerBattlePathInfoDto;
import browser.fantasy.game.battle.mapper.PlayerBattlePathInfoMapper;
import browser.fantasy.game.battle.model.jpa.GroupInfo;
import browser.fantasy.game.battle.model.jpa.Node;
import browser.fantasy.game.battle.model.jpa.PlayerBattlePathInfo;
import browser.fantasy.game.battle.model.jpa.UnitOwner;
import browser.fantasy.game.battle.model.repository.PlayerBattlePathInfoRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PlayerBattleService {

  private final PlayerBattlePathInfoRepository playerBattlePathInfoRepository;
  private final PlayerBattlePathInfoMapper playerBattlePathInfoMapper;

  public PlayerBattleService(
      PlayerBattlePathInfoRepository playerBattlePathInfoRepository,
      PlayerBattlePathInfoMapper playerBattlePathInfoMapper) {
    this.playerBattlePathInfoRepository = playerBattlePathInfoRepository;
    this.playerBattlePathInfoMapper = playerBattlePathInfoMapper;
  }

  @Transactional
  public List<PlayerBattlePathInfoDto> getPlayerBattlePathInfoDtos(String playerId) {
    return playerBattlePathInfoRepository.findByPlayerId(UUID.fromString(playerId)).stream()
        .map(playerBattlePathInfoMapper::mapPlayerBattleInfoToPlayerBattleInfoDto)
        .toList();
  }

  @Transactional
  public List<PlayerBattlePathInfoDto> playerBattlePathNextTurn(String playerId) {
    List<PlayerBattlePathInfo> pathInfos =
        playerBattlePathInfoRepository.findByPlayerId(UUID.fromString(playerId));

    pathInfos.forEach(this::moveUnitsForNextTurn);

    return pathInfos.stream()
        .map(playerBattlePathInfoMapper::mapPlayerBattleInfoToPlayerBattleInfoDto)
        .toList();
  }

  private void moveUnitsForNextTurn(PlayerBattlePathInfo pathInfo) {
    Map<UUID, Node> nodesById =
        pathInfo.getNodes().stream().collect(Collectors.toMap(Node::getId, Function.identity()));
    Map<UUID, UUID> previousNodeIdsByNodeId =
        pathInfo.getEdges().stream()
            .collect(
                Collectors.toMap(
                    edge -> edge.getFromNode().getId(), edge -> edge.getToNode().getId()));
    Map<UUID, UUID> nextNodeIdsByNodeId =
        pathInfo.getEdges().stream()
            .collect(
                Collectors.toMap(
                    edge -> edge.getToNode().getId(), edge -> edge.getFromNode().getId()));

    moveEnemyGroups(pathInfo, previousNodeIdsByNodeId, nodesById);
    movePlayerGroups(pathInfo, nextNodeIdsByNodeId, nodesById);
  }

  private void moveEnemyGroups(
      PlayerBattlePathInfo pathInfo,
      Map<UUID, UUID> previousNodeIdsByNodeId,
      Map<UUID, Node> nodesById) {
    List<GroupMove> groupMovesEnemy = new ArrayList<>();
    pathInfo
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
                                .noneMatch(gi -> UnitOwner.PLAYER.equals(gi.getOwner()))) {
                              groupMovesEnemy.add(new GroupMove(groupInfo, node, destinationNode));
                            }
                          }
                        }));

    groupMovesEnemy.forEach(this::moveGroup);
  }

  private void movePlayerGroups(
      PlayerBattlePathInfo pathInfo,
      Map<UUID, UUID> nextNodeIdsByNodeId,
      Map<UUID, Node> nodesById) {
    List<GroupMove> groupMovesPlayer = new ArrayList<>();
    pathInfo
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

    groupMovesPlayer.forEach(this::moveGroup);
  }

  private void moveGroup(GroupMove groupMove) {
    groupMove.fromNode().getGroupInfos().remove(groupMove.groupInfo());
    groupMove.groupInfo().setNode(groupMove.toNode());
    groupMove.toNode().getGroupInfos().add(groupMove.groupInfo());
  }

  private record GroupMove(GroupInfo groupInfo, Node fromNode, Node toNode) {}
}
