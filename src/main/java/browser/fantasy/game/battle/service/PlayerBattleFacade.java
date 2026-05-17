package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.PlayerBattlePathInfoDto;
import browser.fantasy.game.battle.mapper.PlayerBattlePathInfoMapper;
import browser.fantasy.game.battle.model.jpa.Node;
import browser.fantasy.game.battle.model.jpa.PlayerBattlePathInfo;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PlayerBattleFacade {

  private final PlayerBattleService playerBattleService;
  private final PlayerBattleMovementService playerBattleMovementService;
  private final PlayerBattleCombatService playerBattleCombatService;
  private final PlayerBattlePathInfoMapper playerBattlePathInfoMapper;

  public PlayerBattleFacade(
      PlayerBattleService playerBattleService,
      PlayerBattleMovementService playerBattleMovementService,
      PlayerBattleCombatService playerBattleCombatService,
      PlayerBattlePathInfoMapper playerBattlePathInfoMapper) {
    this.playerBattleService = playerBattleService;
    this.playerBattleMovementService = playerBattleMovementService;
    this.playerBattleCombatService = playerBattleCombatService;
    this.playerBattlePathInfoMapper = playerBattlePathInfoMapper;
  }

  public List<PlayerBattlePathInfoDto> getPlayerBattlePathInfoDtos(String playerId) {
    return playerBattleService.getPlayerBattlePathInfos(playerId).stream()
        .map(playerBattlePathInfoMapper::mapPlayerBattleInfoToPlayerBattleInfoDto)
        .toList();
  }

  @Transactional
  public List<PlayerBattlePathInfoDto> playerBattlePathNextTurn(String playerId) {
    List<PlayerBattlePathInfo> playerBattlePathInfos =
        playerBattleService.getPlayerBattlePathInfos(playerId);
    for (PlayerBattlePathInfo playerBattlePathInfo : playerBattlePathInfos) {
      Map<UUID, Node> nodesById =
          playerBattlePathInfo.getNodes().stream()
              .collect(Collectors.toMap(Node::getId, Function.identity()));
      Map<UUID, UUID> previousNodeIdsByNodeId =
          playerBattlePathInfo.getEdges().stream()
              .collect(
                  Collectors.toMap(
                      edge -> edge.getFromNode().getId(), edge -> edge.getToNode().getId()));
      Map<UUID, UUID> nextNodeIdsByNodeId =
          playerBattlePathInfo.getEdges().stream()
              .collect(
                  Collectors.toMap(
                      edge -> edge.getToNode().getId(), edge -> edge.getFromNode().getId()));

      playerBattleCombatService.resolveFightOutcomes(
          playerBattlePathInfo, previousNodeIdsByNodeId, nodesById);

      playerBattleMovementService.moveEnemyGroups(
          playerBattlePathInfo, previousNodeIdsByNodeId, nodesById);
      playerBattleMovementService.movePlayerGroups(
          playerBattlePathInfo, nextNodeIdsByNodeId, nodesById);
    }

    return playerBattlePathInfos.stream()
        .map(playerBattlePathInfoMapper::mapPlayerBattleInfoToPlayerBattleInfoDto)
        .toList();
  }
}
