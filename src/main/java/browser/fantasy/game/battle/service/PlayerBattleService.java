package browser.fantasy.game.battle.service;

import browser.fantasy.game.battle.Edge;
import browser.fantasy.game.battle.Node;
import browser.fantasy.game.battle.PlayerBattleInfoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerBattleService {

    public PlayerBattleInfoDto createPlayerBattleInfo(String playerId) {
       return new PlayerBattleInfoDto()
                .withNodes(List.of(new Node("1", 1L, 1L), new Node("2", 400L, 200L), new Node("3", 300L, 500L)))
                .withEdges(List.of(new Edge("1", "2")));
    }

}
