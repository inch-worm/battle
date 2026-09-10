package browser.fantasy.game.battle.mapper;

import browser.fantasy.game.battle.*;
import browser.fantasy.game.battle.model.jpa.Edge;
import browser.fantasy.game.battle.model.jpa.GroupInfo;
import browser.fantasy.game.battle.model.jpa.Node;
import browser.fantasy.game.battle.model.jpa.Path;
import browser.fantasy.game.battle.model.jpa.UnitType;
import java.util.Comparator;
import org.springframework.stereotype.Component;

@Component
public class PlayerBattleInfoMapper {

  public PathDto mapPathToPathDto(Path path) {
    return new PathDto()
        .withNodeDtos(
            path.getNodes().stream()
                .sorted(Comparator.comparing(Node::getId))
                .map(
                    node ->
                        new NodeDto()
                            .withId(node.getId().toString())
                            .withxCoordinate(toLong(node.getXCoordinate()))
                            .withyCoordinate(toLong(node.getYCoordinate()))
                            .withGroupInfoDtos(
                                node.getGroupInfos().stream()
                                    .sorted(Comparator.comparing(GroupInfo::getId))
                                    .map(this::mapGroupInfo)
                                    .toList()))
                .toList())
        .withEdgeDtos(
            path.getEdges().stream()
                .sorted(Comparator.comparing(Edge::getId))
                .map(
                    edge ->
                        new EdgeDto()
                            .withFromNodeId(edge.getFromNode().getId().toString())
                            .withToNodeId(edge.getToNode().getId().toString()))
                .toList());
  }

  public UnplacedGroupDto mapUnplacedGroupInfoToUnplacedGroupDto(GroupInfo groupInfo) {
    return new UnplacedGroupDto()
        .withGroupInfoDto(mapGroupInfo(groupInfo))
        .withCount(groupInfo.getCount() == null ? null : groupInfo.getCount().longValue());
  }

  public GroupInfoDto mapGroupInfo(GroupInfo groupInfo) {
    return new GroupInfoDto()
        .withId(groupInfo.getId().toString())
        .withUnitTypeDto(mapUnitType(groupInfo.getUnitType()))
        .withCount(toLong(groupInfo.getCount()))
        .withOwner(groupInfo.getOwner() == null ? null : groupInfo.getOwner().name());
  }

  private UnitTypeDto mapUnitType(UnitType unitType) {
    return unitType == null
        ? null
        : new UnitTypeDto()
            .withName(unitType.getName())
            .withHp(toLong(unitType.getHp()))
            .withAttack(toLong(unitType.getAttack()))
            .withOrderInFight(toLong(unitType.getOrderInFight()));
  }

  private Long toLong(Integer value) {
    return value == null ? null : value.longValue();
  }
}
