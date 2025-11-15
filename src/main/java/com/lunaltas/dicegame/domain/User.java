package com.lunaltas.dicegame.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@SuppressWarnings("serial")
@Entity
@Table(name = "users")
public class User extends AbstractEntity<Long> {


  @NotBlank(message = "O nome é obrigatório.")
  @Column(name = "name", nullable = false, length = 60)
	private String name;

  @NotBlank(message = "O email é obrigatório.")
  @Email(message = "O email deve ser válido.")
  @Column(name = "email", nullable = false, length = 60)
	private String email;

  @NotBlank(message = "A senha é obrigatória.")
  @Column(name = "password", nullable = false)
	private String password;

  @NotBlank(message = "O papel é obrigatório.")
  @Column(name = "role", nullable = false)
	private String role = "USER";

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  @OneToMany(mappedBy = "user")
  private List<Bet> bets;

  public List<Bet> getBets() {
    return bets;
  }
}
