package browser.fantasy.game.battle.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "unit_type")
public class UnitType {

  @Id
  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "hp", nullable = false)
  private Integer hp;

  @Column(name = "attack", nullable = false)
  private Integer attack;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getHp() {
    return hp;
  }

  public void setHp(Integer hp) {
    this.hp = hp;
  }

  public Integer getAttack() {
    return attack;
  }

  public void setAttack(Integer attack) {
    this.attack = attack;
  }
}
