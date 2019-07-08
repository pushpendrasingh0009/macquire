package com.macquire.rmg.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.macquire.rmg.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
