package com.lunaltas.dicegame.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.AuthorityUtils;

import com.lunaltas.dicegame.dao.UserDao;
import com.lunaltas.dicegame.domain.User;
import com.lunaltas.dicegame.repository.UserRepository;

@Service @Transactional(readOnly = false)
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao dao;

	@Autowired
	private UserRepository repository;

	@Autowired
	@Lazy
	private PasswordEncoder passwordEncoder;

	@Override
	public void save(User user) {
		// Criptografa a senha antes de salvar
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		dao.save(user);		
	}

	@Override
	public void update(User user) {
		// Se a senha foi alterada, criptografa antes de atualizar
		User existingUser = dao.findById(user.getId());
		if (existingUser != null && user.getPassword() != null && 
		    !user.getPassword().isEmpty() && 
		    !user.getPassword().equals(existingUser.getPassword())) {
			// Verifica se a senha já está criptografada (começa com $2a$ ou $2b$)
			if (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
			}
		}
		dao.update(user);		
	}

	@Override
	public void delete(Long id) {
		dao.delete(id);		
	}

	@Override @Transactional(readOnly = true)
	public User findById(Long id) {
		
		return dao.findById(id);
	}

	@Override @Transactional(readOnly = true)
	public List<User> findAll() {
		
		return dao.findAll();
	}

  @Override
  public boolean hasBets(Long id) {
    User user = findById(id);
    if (user == null) {
      return false;
    }
    return user.getBets().size() > 0;
  }


  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      User user = repository.findByEmail(username);
      
      if (user == null) {
        throw new UsernameNotFoundException("Usuário não encontrado: " + username);
      }
      
      return new org.springframework.security.core.userdetails.User(
          user.getEmail(),
          user.getPassword(),
          AuthorityUtils.createAuthorityList("ROLE_" + user.getRole())
        );
      }
}
