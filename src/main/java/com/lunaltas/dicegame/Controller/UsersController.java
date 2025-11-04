package com.lunaltas.dicegame.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@Controller
@RequestMapping("/users")
public class UsersController {
  @GetMapping("/index") // lista de usuários
  public String index() {
    System.out.println("**********");
    System.out.println("********** Passou pelo index UsersController");
    System.out.println("**********");
    return "/users/index";
  }

  @GetMapping("/new") // novo usuário
  public String newUser() {
    System.out.println("**********");
    System.out.println("********** Passou pelo newUser UsersController");
    System.out.println("**********");
    return "/users/new";
  }

  @PostMapping("/create") // salvar novo usuário
  public String create() {
    System.out.println("**********");
    System.out.println("********** Passou pelo create UsersController");
    System.out.println("**********");
    return "redirect:/users/show/1";
  }

  @GetMapping("/show/{id}") // detalhes do usuário
  public String show(@PathVariable Long id) {
    System.out.println("**********");
    System.out.println("********** Passou pelo show - ID: " + id + " UsersController");
    System.out.println("**********");
    return "/users/show";
  }

  @GetMapping("/edit/{id}") // editar usuário
  public String edit(@PathVariable Long id) {
    System.out.println("**********");
    System.out.println("********** Passou pelo edit - ID: " + id + " UsersController");
    System.out.println("**********");
    return "/users/edit";
  }

  @PutMapping("/update/{id}") // atualizar usuário - put+++
  public String update(@PathVariable Long id) {
    System.out.println("**********");
    System.out.println("********** Passou pelo update - ID: " + id + " UsersController");
    System.out.println("**********");
    return "redirect:/users/show/1";
  }

  @DeleteMapping("/delete/{id}") // deletar usuário - delete
  public String delete(@PathVariable Long id) {
    System.out.println("**********");
    System.out.println("********** Passou pelo delete - ID: " + id + " UsersController");
    System.out.println("**********");
    return "redirect:/users/index";
  }
}
