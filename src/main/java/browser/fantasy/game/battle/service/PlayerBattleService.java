package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.model.jpa.PlayerBattleInfo;
import browser.fantasy.game.battle.model.repository.PlayerBattleInfoRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PlayerBattleService {

  private final PlayerBattleInfoRepository playerBattleInfoRepository;

  public PlayerBattleService(PlayerBattleInfoRepository playerBattleInfoRepository) {
    this.playerBattleInfoRepository = playerBattleInfoRepository;
  }

  public PlayerBattleInfo getCurrentPlayerBattleInfo(String playerId) {
    return playerBattleInfoRepository.findByPlayerId(UUID.fromString(playerId)).get(0);
  }
}
