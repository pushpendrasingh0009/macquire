package com.macquire.rmg.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.macquire.rmg.auth.model.AuthorityName;
import com.macquire.rmg.auth.model.entity.Role;

public interface AuthorityRepository  extends JpaRepository<Role, Long> {
	Role findByName(AuthorityName name);
}
