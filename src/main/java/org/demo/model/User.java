package org.demo.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Cacheable
@Table(name = "users")
public class User extends PanacheEntity {
  @Column
  public String name;
  @Column
  public String email;
  @Column
  public String address;
  @Column
  public String phone;

  public static void merge(User original, User update) {
    original.name = update.name == null ? original.name : update.name;
    original.phone = update.phone == null ? original.phone : update.phone;
    original.email = update.email == null ? original.email : update.email;
    original.address = update.address == null ? original.address : update.address;
  }
}
