package browser.fantasy.game.battle.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceException extends RuntimeException {

  private final String errorKey;

  public ServiceException(String message, String errorKey) {
    super(message);
    this.errorKey = errorKey;
  }
}
