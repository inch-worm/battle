package browser.fantasy.game.battle.controller;

import browser.fantasy.game.battle.Edge;
import browser.fantasy.game.battle.Node;
import browser.fantasy.game.battle.PlayerBattlePathInfoDto;
import browser.fantasy.game.battle.service.PlayerBattleService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerBattleController {

  private final PlayerBattleService playerBattleService;

  public PlayerBattleController(PlayerBattleService playerBattleService) {
    this.playerBattleService = playerBattleService;
  }

  @GetMapping("/playerBattlePathInfoDtos/{playerId}")
  public List<PlayerBattlePathInfoDto> getCurrentPlayerBattlePathInfoDtos(@PathVariable String playerId) {
    return playerBattleService.getPlayerBattlePathInfoDtos(playerId);
  }
}
