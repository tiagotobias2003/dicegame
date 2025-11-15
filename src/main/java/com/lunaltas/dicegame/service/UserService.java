package com.lunaltas.dicegame.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.lunaltas.dicegame.domain.User;

public interface UserService extends UserDetailsService {

	void save(User user);
	
	void update(User user);
	
	void delete(Long id);
	
	User findById(Long id);
	
	List<User> findAll();

  boolean hasBets(Long id);
	
}