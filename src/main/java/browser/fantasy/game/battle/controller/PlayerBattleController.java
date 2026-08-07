package browser.fantasy.game.battle.controller;

import browser.fantasy.game.battle.PlayerBattleInfoDto;
import browser.fantasy.game.battle.UnitPlacementRequest;
import browser.fantasy.game.battle.service.PlayerBattleFacade;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlayerBattleController {

  private final PlayerBattleFacade playerBattleFacade;

  public PlayerBattleController(PlayerBattleFacade playerBattleFacade) {
    this.playerBattleFacade = playerBattleFacade;
  }

  @GetMapping("/playerBattlePathInfoDtos/{playerId}")
  public PlayerBattleInfoDto getCurrentPlayerBattlePathInfoDtos(@PathVariable String playerId) {
    return playerBattleFacade.getPlayerBattlePathInfoDtos(playerId);
  }

  @PostMapping("/playerBattlePathNextTurn/{playerId}")
  public PlayerBattleInfoDto playerBattlePathNextTurn(
      @PathVariable String playerId, @RequestBody UnitPlacementRequest unitPlacementRequest) {
    return playerBattleFacade.playerBattlePathNextTurn(playerId, unitPlacementRequest);
  }
}
