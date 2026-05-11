package browser.fantasy.game.battle.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.http.HttpStatus.OK;

import browser.fantasy.game.battle.AbstractIntegrationTest;
import browser.fantasy.game.battle.EdgeDto;
import browser.fantasy.game.battle.GroupInfoDto;
import browser.fantasy.game.battle.NodeDto;
import browser.fantasy.game.battle.PlayerBattlePathInfoDto;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlayerBattleControllerIntegrationTest extends AbstractIntegrationTest {

  private static final String EXISTING_PLAYER_ID = "11111111-1111-1111-1111-111111111111";

  @LocalServerPort private int port;

  private RestClient restClient;

  @BeforeEach
  void setUp() {
    restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
  }

  @Test
  @Order(1)
  void shouldReturnPlayerBattlePathInfoDtosForExistingPlayer() {
    ResponseEntity<PlayerBattlePathInfoDto[]> response =
        restClient
            .get()
            .uri("/playerBattlePathInfoDtos/{playerId}", EXISTING_PLAYER_ID)
            .retrieve()
            .toEntity(PlayerBattlePathInfoDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(OK);

    List<PlayerBattlePathInfoDto> pathInfos = Arrays.asList(response.getBody());
    assertThat(pathInfos).hasSize(3);

    PlayerBattlePathInfoDto pathA =
        pathInfos.stream()
            .filter(pathInfo -> pathInfo.getNodeDtos().size() == 3)
            .findFirst()
            .orElseThrow();

    assertThat(pathA.getNodeDtos())
        .extracting(NodeDto::getId)
        .containsExactly(
            "aaaaaaaa-0000-0000-0000-000000000001",
            "aaaaaaaa-0000-0000-0000-000000000002",
            "aaaaaaaa-0000-0000-0000-000000000003");
    assertThat(pathA.getNodeDtos())
        .extracting(NodeDto::getxCoordinate, NodeDto::getyCoordinate)
        .containsExactly(tuple(1L, 2L), tuple(2L, 3L), tuple(1L, 4L));
    assertThat(pathA.getNodeDtos().get(0).getGroupInfoDtos())
        .extracting(GroupInfoDto::getUnitType, GroupInfoDto::getCount, GroupInfoDto::getOwner)
        .containsExactly(tuple("INFANTRY", 10L, "PLAYER"));
    assertThat(pathA.getNodeDtos().get(1).getGroupInfoDtos())
        .extracting(GroupInfoDto::getUnitType, GroupInfoDto::getCount, GroupInfoDto::getOwner)
        .containsExactly(tuple("ARCHER", 5L, "ENEMY"), tuple("INFANTRY", 3L, "ENEMY"));
    assertThat(pathA.getEdgeDtos())
        .extracting(EdgeDto::getFromNodeId, EdgeDto::getToNodeId)
        .containsExactly(
            tuple("aaaaaaaa-0000-0000-0000-000000000001", "aaaaaaaa-0000-0000-0000-000000000002"),
            tuple("aaaaaaaa-0000-0000-0000-000000000002", "aaaaaaaa-0000-0000-0000-000000000003"));
  }

  @Test
  @Order(2)
  void shouldReturnEmptyListWhenPlayerHasNoBattlePathInfoDtos() {
    ResponseEntity<PlayerBattlePathInfoDto[]> response =
        restClient
            .get()
            .uri("/playerBattlePathInfoDtos/{playerId}", "22222222-2222-2222-2222-222222222222")
            .retrieve()
            .toEntity(PlayerBattlePathInfoDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isEmpty();
  }

  @Test
  @Order(3)
  void shouldMoveAllUnitsOneNodeAndPersistTurnResult() {
    ResponseEntity<PlayerBattlePathInfoDto[]> nextTurnResponse =
        restClient
            .post()
            .uri("/playerBattlePathNextTurn/{playerId}", EXISTING_PLAYER_ID)
            .retrieve()
            .toEntity(PlayerBattlePathInfoDto[].class);

    assertThat(nextTurnResponse.getStatusCode()).isEqualTo(OK);

    ResponseEntity<PlayerBattlePathInfoDto[]> currentStateResponse =
        restClient
            .get()
            .uri("/playerBattlePathInfoDtos/{playerId}", EXISTING_PLAYER_ID)
            .retrieve()
            .toEntity(PlayerBattlePathInfoDto[].class);

    assertThat(currentStateResponse.getStatusCode()).isEqualTo(OK);
    assertPathAUnitsWereMoved(Arrays.asList(nextTurnResponse.getBody()));
    assertPathAUnitsWereMoved(Arrays.asList(currentStateResponse.getBody()));
  }

  private void assertPathAUnitsWereMoved(List<PlayerBattlePathInfoDto> pathInfos) {
    PlayerBattlePathInfoDto pathA =
        pathInfos.stream()
            .filter(pathInfo -> pathInfo.getNodeDtos().size() == 3)
            .findFirst()
            .orElseThrow();

    assertThat(nodeById(pathA, "aaaaaaaa-0000-0000-0000-000000000001").getGroupInfoDtos())
        .extracting(GroupInfoDto::getUnitType, GroupInfoDto::getCount, GroupInfoDto::getOwner)
        .containsExactly(tuple("ARCHER", 5L, "ENEMY"), tuple("INFANTRY", 3L, "ENEMY"));
    assertThat(nodeById(pathA, "aaaaaaaa-0000-0000-0000-000000000002").getGroupInfoDtos())
        .extracting(GroupInfoDto::getUnitType, GroupInfoDto::getCount, GroupInfoDto::getOwner)
        .containsExactly(tuple("INFANTRY", 10L, "PLAYER"));
    assertThat(nodeById(pathA, "aaaaaaaa-0000-0000-0000-000000000003").getGroupInfoDtos())
        .isEmpty();
  }

  private NodeDto nodeById(PlayerBattlePathInfoDto pathInfo, String nodeId) {
    return pathInfo.getNodeDtos().stream()
        .filter(nodeDto -> nodeDto.getId().equals(nodeId))
        .findFirst()
        .orElseThrow();
  }
}
