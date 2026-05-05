package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.PlayerBattlePathInfoDto;
import browser.fantasy.game.battle.mapper.PlayerBattlePathInfoMapper;
import browser.fantasy.game.battle.model.repository.PlayerBattlePathInfoRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PlayerBattleService {

  private final PlayerBattlePathInfoRepository playerBattlePathInfoRepository;
  private final PlayerBattlePathInfoMapper playerBattlePathInfoMapper;

  public PlayerBattleService(
      PlayerBattlePathInfoRepository playerBattlePathInfoRepository,
      PlayerBattlePathInfoMapper playerBattlePathInfoMapper) {
    this.playerBattlePathInfoRepository = playerBattlePathInfoRepository;
    this.playerBattlePathInfoMapper = playerBattlePathInfoMapper;
  }

  @Transactional
  public List<PlayerBattlePathInfoDto> getPlayerBattlePathInfoDtos(String playerId) {
    return playerBattlePathInfoRepository.findByPlayerId(UUID.fromString(playerId)).stream()
        .map(playerBattlePathInfoMapper::mapPlayerBattleInfoToPlayerBattleInfoDto)
        .toList();
  }
}
