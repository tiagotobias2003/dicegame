package com.lunaltas.dicegame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.lunaltas.dicegame.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("select u from User u where u.email like :email")  
  User findByEmail(@Param("email") String email);

}
