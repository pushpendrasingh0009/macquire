package com.macquire.rmg.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.macquire.rmg.search.model.AuthorityName;
import com.macquire.rmg.search.model.entity.Authority;

public interface AuthorityRepository  extends JpaRepository<Authority, Long> {
	Authority findByName(AuthorityName name);
}
