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
    Map<UUID, UUID> nextNodeIdsByNodeId =
        pathInfo.getEdges().stream()
            .collect(
                Collectors.toMap(
                    edge -> edge.getFromNode().getId(), edge -> edge.getToNode().getId()));
    Map<UUID, UUID> previousNodeIdsByNodeId =
        pathInfo.getEdges().stream()
            .collect(
                Collectors.toMap(
                    edge -> edge.getToNode().getId(), edge -> edge.getFromNode().getId()));

    List<GroupMove> groupMoves = new ArrayList<>();
    for (Node node : pathInfo.getNodes()) {
      for (GroupInfo groupInfo : node.getGroupInfos()) {
        UUID destinationNodeId =
            getDestinationNodeId(groupInfo, nextNodeIdsByNodeId, previousNodeIdsByNodeId);
        if (destinationNodeId != null) {
          groupMoves.add(new GroupMove(groupInfo, node, nodesById.get(destinationNodeId)));
        }
      }
    }

    groupMoves.forEach(this::moveGroup);
  }

  private UUID getDestinationNodeId(
      GroupInfo groupInfo,
      Map<UUID, UUID> nextNodeIdsByNodeId,
      Map<UUID, UUID> previousNodeIdsByNodeId) {
    UUID currentNodeId = groupInfo.getNode().getId();
    if (groupInfo.getOwner() == UnitOwner.ENEMY) {
      return nextNodeIdsByNodeId.get(currentNodeId);
    }
    if (groupInfo.getOwner() == UnitOwner.PLAYER) {
      return previousNodeIdsByNodeId.get(currentNodeId);
    }
    return null;
  }

  private void moveGroup(GroupMove groupMove) {
    if (groupMove.toNode() == null || groupMove.fromNode().equals(groupMove.toNode())) {
      return;
    }

    groupMove.fromNode().getGroupInfos().remove(groupMove.groupInfo());
    groupMove.groupInfo().setNode(groupMove.toNode());
    if (!groupMove.toNode().getGroupInfos().contains(groupMove.groupInfo())) {
      groupMove.toNode().getGroupInfos().add(groupMove.groupInfo());
    }
  }

  private record GroupMove(GroupInfo groupInfo, Node fromNode, Node toNode) {}
}
