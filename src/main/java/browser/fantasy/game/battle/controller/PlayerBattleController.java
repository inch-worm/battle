package browser.fantasy.game.battle.controller;

import browser.fantasy.game.battle.Edge;
import browser.fantasy.game.battle.Node;
import browser.fantasy.game.battle.PlayerBattleInfoDto;
import browser.fantasy.game.battle.service.PlayerBattleService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerBattleController {

  private final PlayerBattleService playerResourceService;

  public PlayerBattleController(PlayerBattleService playerBattleService) {
    this.playerResourceService = playerBattleService;
  }

  @GetMapping("/playerBattleInfo/{playerId}")
  public PlayerBattleInfoDto getCurrentPlayerBattleInfo(@PathVariable String playerId) {
    return new PlayerBattleInfoDto()
        .withNodes(List.of(new Node("1", 1L, 1L), new Node("2", 400L, 200L), new Node("3", 300L, 500L)))
        .withEdges(List.of(new Edge("1", "2")));
  }
}
