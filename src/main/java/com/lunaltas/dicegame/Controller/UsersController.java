package com.lunaltas.dicegame.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.lunaltas.dicegame.domain.User;
import com.lunaltas.dicegame.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UsersController {

  @Autowired
  private UserService userService;

  @GetMapping("/index") // lista de usuários
  public String index(ModelMap model) {
    model.addAttribute("users", userService.findAll());
    int size = userService.findAll().size();
    model.addAttribute("size", size);
    return "/users/index";
  }

  @GetMapping("/new") // novo usuário
  public String newUser(ModelMap model) {
    model.addAttribute("user", new User());
    return "/users/new";
  }

  @PostMapping("/create") // salvar novo usuário
  public String create(@Valid User user, BindingResult result, RedirectAttributes redirectAttributes) {
    // Validação customizada para senha
    if (user.getPassword() != null && (user.getPassword().length() < 8 || user.getPassword().length() > 20)) {
      result.rejectValue("password", "error.password", "A senha deve ter entre 8 e 20 caracteres.");
    }
    
    if (result.hasErrors()) {
			return "/users/new";
		}
    userService.save(user);
    redirectAttributes.addFlashAttribute("success", "Usuário salvo com sucesso");
    return "redirect:/users/show/" + user.getId();
  }

  @GetMapping("/show/{id}") // detalhes do usuário
  public String show(@PathVariable Long id, ModelMap model) {
    model.addAttribute("user", userService.findById(id));
    return "/users/show";
  }

  @GetMapping("/edit/{id}") // editar usuário
  public String edit(@PathVariable Long id, ModelMap model) {
    model.addAttribute("user", userService.findById(id));
    return "/users/edit";
  }

  @PutMapping("/update/{id}") // atualizar usuário - put+++
  public String update(@PathVariable Long id, @Valid User user, BindingResult result, RedirectAttributes redirectAttributes) {
    // Validação customizada para senha (apenas se foi informada uma nova senha)
    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
      // Verifica se não é uma senha já criptografada (BCrypt sempre começa com $2a$ ou $2b$)
      if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
        if (user.getPassword().length() < 8 || user.getPassword().length() > 20) {
          result.rejectValue("password", "error.password", "A senha deve ter entre 8 e 20 caracteres.");
        }
      }
    }
    
    if (result.hasErrors()) {
			return "/users/edit";
		}
    userService.update(user);
    redirectAttributes.addFlashAttribute("success", "Usuário atualizado com sucesso");
    return "redirect:/users/show/" + user.getId();
  }

  @DeleteMapping("/delete/{id}") // deletar usuário - delete
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    if (userService.hasBets(id)) {
      redirectAttributes.addFlashAttribute("error", "Usuário não pode ser deletado porque tem Bets associadas");
      return "redirect:/users/show/" + id;
    }
    else {
      userService.delete(id);
      redirectAttributes.addFlashAttribute("success", "Usuário deletado com sucesso");
      return "redirect:/users/index";
    }
  }
}
