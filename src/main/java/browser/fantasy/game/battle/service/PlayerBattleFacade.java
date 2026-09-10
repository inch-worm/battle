package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.PathDto;
import browser.fantasy.game.battle.PlayerBattleInfoDto;
import browser.fantasy.game.battle.UnitPlacementRequest;
import browser.fantasy.game.battle.UnplacedGroupDto;
import browser.fantasy.game.battle.mapper.PlayerBattleInfoMapper;
import browser.fantasy.game.battle.model.jpa.*;
import browser.fantasy.game.battle.model.repository.GroupInfoRepository;
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
  private final PlayerBattleInfoMapper playerBattleInfoMapper;
  private final GroupInfoRepository groupInfoRepository;

  public PlayerBattleFacade(
      PlayerBattleService playerBattleService,
      PlayerBattleMovementService playerBattleMovementService,
      PlayerBattleCombatService playerBattleCombatService,
      PlayerBattleInfoMapper playerBattleInfoMapper,
      GroupInfoRepository groupInfoRepository) {
    this.playerBattleService = playerBattleService;
    this.playerBattleMovementService = playerBattleMovementService;
    this.playerBattleCombatService = playerBattleCombatService;
    this.playerBattleInfoMapper = playerBattleInfoMapper;
    this.groupInfoRepository = groupInfoRepository;
  }

  public PlayerBattleInfoDto getCurrentPlayerBattleInfoDto(String playerId) {
    PlayerBattleInfo playerBattleInfo = playerBattleService.getCurrentPlayerBattleInfo(playerId);
    return mapPlayerBattleInfo(playerBattleInfo);
  }

  @Transactional
  public PlayerBattleInfoDto playerBattlePathNextTurn(
      String playerId, UnitPlacementRequest unitPlacementRequest) {
    PlayerBattleInfo playerBattleInfo = playerBattleService.getCurrentPlayerBattleInfo(playerId);
    List<Path> paths = playerBattleInfo.getPaths();
    for (Path path : paths) {
      Map<UUID, Node> nodesById =
          path.getNodes().stream().collect(Collectors.toMap(Node::getId, Function.identity()));
      Map<UUID, UUID> previousNodeIdsByNodeId =
          path.getEdges().stream()
              .collect(
                  Collectors.toMap(
                      edge -> edge.getFromNode().getId(), edge -> edge.getToNode().getId()));
      Map<UUID, UUID> nextNodeIdsByNodeId =
          path.getEdges().stream()
              .collect(
                  Collectors.toMap(
                      edge -> edge.getToNode().getId(), edge -> edge.getFromNode().getId()));

      playerBattleCombatService.resolveFightOutcomes(path, previousNodeIdsByNodeId, nodesById);

      playerBattleMovementService.moveEnemyGroups(path, previousNodeIdsByNodeId, nodesById);
      playerBattleMovementService.movePlayerGroups(path, nextNodeIdsByNodeId, nodesById);
    }

    Map<UUID, Node> allNodesById =
        paths.stream()
            .flatMap(playerBattlePathInfo -> playerBattlePathInfo.getNodes().stream())
            .collect(Collectors.toMap(Node::getId, Function.identity()));
    playerBattleMovementService.placePlayerUnits(allNodesById, unitPlacementRequest);
    playerBattleInfo.setTurnCount(playerBattleInfo.getTurnCount() + 1);
    return mapPlayerBattleInfo(playerBattleInfo);
  }

  private PlayerBattleInfoDto mapPlayerBattleInfo(PlayerBattleInfo playerBattleInfo) {
    List<PathDto> pathDtos =
        playerBattleInfo.getPaths().stream().map(playerBattleInfoMapper::mapPathToPathDto).toList();
    List<UnplacedGroupDto> unplacedGroupDtos =
        groupInfoRepository.findByNodeIsNullAndOwner(UnitOwner.PLAYER).stream()
            .map(playerBattleInfoMapper::mapUnplacedGroupInfoToUnplacedGroupDto)
            .toList();

    return new PlayerBattleInfoDto()
        .withTurnCount(playerBattleInfo.getTurnCount())
        .withPathDtos(pathDtos)
        .withUnplacedGroupDtos(unplacedGroupDtos);
  }
}
