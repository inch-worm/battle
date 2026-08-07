package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.PathDto;
import browser.fantasy.game.battle.PlayerBattleInfoDto;
import browser.fantasy.game.battle.UnitPlacementRequest;
import browser.fantasy.game.battle.UnplacedGroupDto;
import browser.fantasy.game.battle.mapper.PlayerBattlePathInfoMapper;
import browser.fantasy.game.battle.model.jpa.GroupInfo;
import browser.fantasy.game.battle.model.jpa.Node;
import browser.fantasy.game.battle.model.jpa.PlayerBattlePathInfo;
import browser.fantasy.game.battle.model.jpa.UnitOwner;
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
  private final PlayerBattlePathInfoMapper playerBattlePathInfoMapper;
  private final GroupInfoRepository groupInfoRepository;

  public PlayerBattleFacade(
      PlayerBattleService playerBattleService,
      PlayerBattleMovementService playerBattleMovementService,
      PlayerBattleCombatService playerBattleCombatService,
      PlayerBattlePathInfoMapper playerBattlePathInfoMapper,
      GroupInfoRepository groupInfoRepository) {
    this.playerBattleService = playerBattleService;
    this.playerBattleMovementService = playerBattleMovementService;
    this.playerBattleCombatService = playerBattleCombatService;
    this.playerBattlePathInfoMapper = playerBattlePathInfoMapper;
    this.groupInfoRepository = groupInfoRepository;
  }

  public PlayerBattleInfoDto getPlayerBattlePathInfoDtos(String playerId) {
    List<PlayerBattlePathInfo> playerBattlePathInfos =
        playerBattleService.getPlayerBattlePathInfos(playerId);
    return mapPlayerBattleInfo(playerBattlePathInfos);
  }

  @Transactional
  public PlayerBattleInfoDto playerBattlePathNextTurn(
      String playerId, UnitPlacementRequest unitPlacementRequest) {
    List<PlayerBattlePathInfo> playerBattlePathInfos =
        playerBattleService.getPlayerBattlePathInfos(playerId);
    Map<UUID, Node> allNodesById =
        playerBattlePathInfos.stream()
            .flatMap(playerBattlePathInfo -> playerBattlePathInfo.getNodes().stream())
            .collect(Collectors.toMap(Node::getId, Function.identity()));

    playerBattleMovementService.placePlayerUnits(allNodesById, unitPlacementRequest);

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

    return mapPlayerBattleInfo(playerBattlePathInfos);
  }

  private PlayerBattleInfoDto mapPlayerBattleInfo(
      List<PlayerBattlePathInfo> playerBattlePathInfos) {
    List<PathDto> pathDtos =
        playerBattlePathInfos.stream()
            .map(playerBattlePathInfoMapper::mapPlayerBattleInfoToPlayerBattleInfoDto)
            .toList();
    List<UnplacedGroupDto> unplacedGroupDtos =
        groupInfoRepository.findByNodeIsNullAndOwner(UnitOwner.PLAYER).stream()
            .map(this::mapUnplacedGroup)
            .toList();

    return new PlayerBattleInfoDto()
        .withPathDtos(pathDtos)
        .withUnplacedGroupDtos(unplacedGroupDtos);
  }

  private UnplacedGroupDto mapUnplacedGroup(GroupInfo groupInfo) {
    return new UnplacedGroupDto()
        .withGroupInfoDto(playerBattlePathInfoMapper.mapGroupInfo(groupInfo))
        .withCount(groupInfo.getCount() == null ? null : groupInfo.getCount().longValue());
  }
}
