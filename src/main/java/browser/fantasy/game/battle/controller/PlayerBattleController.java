package browser.fantasy.game.battle.controller;

import browser.fantasy.game.battle.PlayerBattlePathInfoDto;
import browser.fantasy.game.battle.service.PlayerBattleFacade;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerBattleController {

  private final PlayerBattleFacade playerBattleFacade;

  public PlayerBattleController(PlayerBattleFacade playerBattleFacade) {
    this.playerBattleFacade = playerBattleFacade;
  }

  @GetMapping("/playerBattlePathInfoDtos/{playerId}")
  public List<PlayerBattlePathInfoDto> getCurrentPlayerBattlePathInfoDtos(
      @PathVariable String playerId) {
    return playerBattleFacade.getPlayerBattlePathInfoDtos(playerId);
  }

  @PostMapping("/playerBattlePathNextTurn/{playerId}")
  public List<PlayerBattlePathInfoDto> playerBattlePathNextTurn(@PathVariable String playerId) {
    return playerBattleFacade.playerBattlePathNextTurn(playerId);
  }
}
