package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.model.jpa.PlayerBattlePathInfo;
import browser.fantasy.game.battle.model.repository.PlayerBattlePathInfoRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PlayerBattleService {

  private final PlayerBattlePathInfoRepository playerBattlePathInfoRepository;

  public PlayerBattleService(PlayerBattlePathInfoRepository playerBattlePathInfoRepository) {
    this.playerBattlePathInfoRepository = playerBattlePathInfoRepository;
  }

  public List<PlayerBattlePathInfo> getPlayerBattlePathInfos(String playerId) {
    return playerBattlePathInfoRepository.findByPlayerId(UUID.fromString(playerId));
  }
}
