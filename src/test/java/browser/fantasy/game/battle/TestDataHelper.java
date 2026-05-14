package browser.fantasy.game.battle;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestDataHelper {

  private final JdbcTemplate jdbcTemplate;

  public TestDataHelper(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void cleanDatabase() {
    jdbcTemplate.execute(
        """
        truncate table
          player_source_of_resource,
          player_resource,
          source_of_resource_cost,
          source_of_resource,
          resource,
          group_info,
          edge,
          node,
          player_battle_path_info,
          player
        restart identity cascade
        """);
  }

  public void insertPlayer(String id, String name, String password) {
    jdbcTemplate.update(
        "insert into player (id, name, password) values (?::uuid, ?, ?)", id, name, password);
  }

  public void insertPath(String id, String playerId, String name) {
    jdbcTemplate.update(
        "insert into player_battle_path_info (id, player_id, name) values (?::uuid, ?::uuid, ?)",
        id,
        playerId,
        name);
  }

  public void insertNode(String id, String pathId, int xCoordinate, int yCoordinate) {
    jdbcTemplate.update(
        "insert into node (id, path_id, x_coordinate, y_coordinate) values (?::uuid, ?::uuid, ?, ?)",
        id,
        pathId,
        xCoordinate,
        yCoordinate);
  }

  public void insertGroupInfo(String id, String nodeId, String unitType, int count, String owner) {
    jdbcTemplate.update(
        "insert into group_info (id, node_id, unit_type, count, owner) values (?::uuid, ?::uuid, ?, ?, ?)",
        id,
        nodeId,
        unitType,
        count,
        owner);
  }

  public void insertEdge(String id, String pathId, String fromNodeId, String toNodeId) {
    jdbcTemplate.update(
        "insert into edge (id, path_id, from_node_id, to_node_id) values (?::uuid, ?::uuid, ?::uuid, ?::uuid)",
        id,
        pathId,
        fromNodeId,
        toNodeId);
  }
}
