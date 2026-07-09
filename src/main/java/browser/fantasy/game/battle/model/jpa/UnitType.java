package browser.fantasy.game.battle.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "unit_type")
public class UnitType {

  @Id
  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "hp", nullable = false)
  private Integer hp;

  @Column(name = "attack", nullable = false)
  private Integer attack;

  @Column(name = "order_in_fight", nullable = false)
  private Integer orderInFight;
}
