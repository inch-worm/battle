package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.Edge;
import browser.fantasy.game.battle.Node;
import browser.fantasy.game.battle.PlayerBattlePathInfoDto;
import browser.fantasy.game.battle.UnitsInfo;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PlayerBattleService {

  public List<PlayerBattlePathInfoDto> getPlayerBattlePathInfoDtos(String playerId) {
    PlayerBattlePathInfoDto pathA =
        new PlayerBattlePathInfoDto()
            .withNodes(
                List.of(
                    new Node("A1", 1L, 2L, List.of(new UnitsInfo("infantry", 10L, "player"))),
                    new Node(
                        "A2",
                        2L,
                        3L,
                        List.of(
                            new UnitsInfo("archer", 5L, "enemy"),
                            new UnitsInfo("infantry", 3L, "enemy"))),
                    new Node("A3", 1L, 4L, List.of())))
            .withEdges(List.of(new Edge("A1", "A2"), new Edge("A2", "A3")));

    PlayerBattlePathInfoDto pathB =
        new PlayerBattlePathInfoDto()
            .withNodes(
                List.of(
                    new Node("B1", 5L, 1L, List.of(new UnitsInfo("cavalry", 7L, "player"))),
                    new Node("B2", 4L, 2L, List.of()),
                    new Node("B3", 6L, 3L, List.of(new UnitsInfo("archer", 12L, "enemy"))),
                    new Node("B4", 5L, 4L, List.of())))
            .withEdges(List.of(new Edge("B1", "B2"), new Edge("B2", "B3"), new Edge("B3", "B4")));

    PlayerBattlePathInfoDto pathC =
        new PlayerBattlePathInfoDto()
            .withNodes(
                List.of(
                    new Node("C1", 9L, 0L, List.of()),
                    new Node("C2", 10L, 1L, List.of(new UnitsInfo("infantry", 20L, "enemy"))),
                    new Node("C3", 8L, 2L, List.of()),
                    new Node(
                        "C4",
                        10L,
                        3L,
                        List.of(
                            new UnitsInfo("cavalry", 4L, "player"),
                            new UnitsInfo("archer", 6L, "player"))),
                    new Node("C5", 9L, 4L, List.of())))
            .withEdges(
                List.of(
                    new Edge("C1", "C2"),
                    new Edge("C2", "C3"),
                    new Edge("C3", "C4"),
                    new Edge("C4", "C5")));

    return List.of(pathA, pathB, pathC);
  }
}
