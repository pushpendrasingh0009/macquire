package com.macquire.rmg.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.macquire.rmg.search.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
