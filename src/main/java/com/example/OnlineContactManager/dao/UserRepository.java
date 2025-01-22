package com.example.OnlineContactManager.dao;

import com.example.OnlineContactManager.models.Contact;
import com.example.OnlineContactManager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Integer> {



    @Query("SELECT u FROM User u WHERE u.email = :email")
   public User getUserByUsername(@Param("email") String email);


}
