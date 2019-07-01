package com.macquire.rmg.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.macquire.rmg.auth.model.AuthorityName;
import com.macquire.rmg.auth.model.entity.Authority;

public interface AuthorityRepository  extends JpaRepository<Authority, Long> {
	Authority findByName(AuthorityName name);
}
