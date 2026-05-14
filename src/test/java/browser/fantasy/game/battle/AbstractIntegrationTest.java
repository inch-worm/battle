package browser.fantasy.game.battle;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("component-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

  @Autowired protected TestDataHelper testDataHelper;

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @AfterEach
  void cleanUpAfterEach() {
    testDataHelper.cleanDatabase();
  }
}
