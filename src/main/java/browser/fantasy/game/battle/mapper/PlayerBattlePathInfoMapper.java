package browser.fantasy.game.battle.mapper;

import browser.fantasy.game.battle.*;
import browser.fantasy.game.battle.model.jpa.Edge;
import browser.fantasy.game.battle.model.jpa.GroupInfo;
import browser.fantasy.game.battle.model.jpa.Node;
import browser.fantasy.game.battle.model.jpa.PlayerBattlePathInfo;
import java.util.Comparator;
import org.springframework.stereotype.Component;

@Component
public class PlayerBattlePathInfoMapper {

  public PlayerBattlePathInfoDto mapPlayerBattleInfoToPlayerBattleInfoDto(
      PlayerBattlePathInfo playerBattlePathInfo) {
    return new PlayerBattlePathInfoDto()
        .withNodeDtos(
            playerBattlePathInfo.getNodes().stream()
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
                                    .map(
                                        groupInfo ->
                                            new GroupInfoDto()
                                                .withUnitType(
                                                    groupInfo.getUnitType() == null
                                                        ? null
                                                        : groupInfo.getUnitType().name())
                                                .withCount(toLong(groupInfo.getCount()))
                                                .withOwner(
                                                    groupInfo.getOwner() == null
                                                        ? null
                                                        : groupInfo.getOwner().name()))
                                    .toList()))
                .toList())
        .withEdgeDtos(
            playerBattlePathInfo.getEdges().stream()
                .sorted(Comparator.comparing(Edge::getId))
                .map(
                    edge ->
                        new EdgeDto()
                            .withFromNodeId(edge.getFromNode().getId().toString())
                            .withToNodeId(edge.getToNode().getId().toString()))
                .toList());
  }

  private Long toLong(Integer value) {
    return value == null ? null : value.longValue();
  }
}
