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
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

class PlayerBattleControllerIntegrationTest extends AbstractIntegrationTest {

  private static final String EXISTING_PLAYER_ID = "11111111-1111-1111-1111-111111111111";

  @LocalServerPort private int port;

  private RestClient restClient;

  @BeforeEach
  void setUp() {
    restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void shouldReturnPlayerBattlePathInfoDtosForExistingPlayer() {
    seedExistingPlayerBattlePaths();

    ResponseEntity<PlayerBattlePathInfoDto[]> response =
        restClient
            .get()
            .uri("/playerBattlePathInfoDtos/{playerId}", EXISTING_PLAYER_ID)
            .retrieve()
            .toEntity(PlayerBattlePathInfoDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(OK);

    List<PlayerBattlePathInfoDto> pathInfos = Arrays.asList(response.getBody());
    assertThat(pathInfos).hasSize(2);

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
  void shouldMoveUnblockedUnitsOneNodeAndPersistTurnResult() {
    seedExistingPlayerBattlePaths();

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
    assertPathAUnitsWereMovedAccordingToProductionRules(Arrays.asList(nextTurnResponse.getBody()));
    assertPathAUnitsWereMovedAccordingToProductionRules(
        Arrays.asList(currentStateResponse.getBody()));
  }

  private void assertPathAUnitsWereMovedAccordingToProductionRules(
      List<PlayerBattlePathInfoDto> pathInfos) {
    PlayerBattlePathInfoDto pathA =
        pathInfos.stream()
            .filter(pathInfo -> pathInfo.getNodeDtos().size() == 3)
            .findFirst()
            .orElseThrow();

    assertThat(nodeById(pathA, "aaaaaaaa-0000-0000-0000-000000000001").getGroupInfoDtos())
        .extracting(GroupInfoDto::getUnitType, GroupInfoDto::getCount, GroupInfoDto::getOwner)
        .containsExactly(tuple("INFANTRY", 10L, "PLAYER"));
    assertThat(nodeById(pathA, "aaaaaaaa-0000-0000-0000-000000000002").getGroupInfoDtos())
        .isEmpty();
    assertThat(nodeById(pathA, "aaaaaaaa-0000-0000-0000-000000000003").getGroupInfoDtos())
        .extracting(GroupInfoDto::getUnitType, GroupInfoDto::getCount, GroupInfoDto::getOwner)
        .containsExactly(tuple("ARCHER", 5L, "ENEMY"), tuple("INFANTRY", 3L, "ENEMY"));
  }

  private void seedExistingPlayerBattlePaths() {
    testDataHelper.insertPlayer(
        "11111111-1111-1111-1111-111111111111", "Test Player", "test password");

    testDataHelper.insertPath(
        "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", "11111111-1111-1111-1111-111111111111", "Path A");
    testDataHelper.insertNode(
        "aaaaaaaa-0000-0000-0000-000000000001", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", 1, 2);
    testDataHelper.insertNode(
        "aaaaaaaa-0000-0000-0000-000000000002", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", 2, 3);
    testDataHelper.insertNode(
        "aaaaaaaa-0000-0000-0000-000000000003", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", 1, 4);
    testDataHelper.insertGroupInfo(
        "aaaaaaaa-aaaa-aaaa-aaaa-000000000101",
        "aaaaaaaa-0000-0000-0000-000000000001",
        "INFANTRY",
        10,
        "PLAYER");
    testDataHelper.insertGroupInfo(
        "aaaaaaaa-aaaa-aaaa-aaaa-000000000102",
        "aaaaaaaa-0000-0000-0000-000000000002",
        "ARCHER",
        5,
        "ENEMY");
    testDataHelper.insertGroupInfo(
        "aaaaaaaa-aaaa-aaaa-aaaa-000000000103",
        "aaaaaaaa-0000-0000-0000-000000000002",
        "INFANTRY",
        3,
        "ENEMY");
    testDataHelper.insertEdge(
        "aaaaaaaa-aaaa-aaaa-aaaa-000000000201",
        "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
        "aaaaaaaa-0000-0000-0000-000000000001",
        "aaaaaaaa-0000-0000-0000-000000000002");
    testDataHelper.insertEdge(
        "aaaaaaaa-aaaa-aaaa-aaaa-000000000202",
        "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
        "aaaaaaaa-0000-0000-0000-000000000002",
        "aaaaaaaa-0000-0000-0000-000000000003");

    testDataHelper.insertPath(
        "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", "11111111-1111-1111-1111-111111111111", "Path B");
    testDataHelper.insertNode(
        "bbbbbbbb-0000-0000-0000-000000000001", "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", 5, 1);
    testDataHelper.insertNode(
        "bbbbbbbb-0000-0000-0000-000000000002", "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", 4, 2);
    testDataHelper.insertGroupInfo(
        "bbbbbbbb-bbbb-bbbb-bbbb-000000000101",
        "bbbbbbbb-0000-0000-0000-000000000001",
        "CAVALRY",
        7,
        "PLAYER");
    testDataHelper.insertEdge(
        "bbbbbbbb-bbbb-bbbb-bbbb-000000000201",
        "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
        "bbbbbbbb-0000-0000-0000-000000000001",
        "bbbbbbbb-0000-0000-0000-000000000002");
  }

  private NodeDto nodeById(PlayerBattlePathInfoDto pathInfo, String nodeId) {
    return pathInfo.getNodeDtos().stream()
        .filter(nodeDto -> nodeDto.getId().equals(nodeId))
        .findFirst()
        .orElseThrow();
  }
}
