package com.macquire.rmg.auth.model.entity;

import javax.persistence.*;

import com.macquire.rmg.auth.model.AuthorityName;

import java.util.List;

@Entity
@Table(name = "ROLE")
public class Role {

    @Id
    @Column(name = "ID")
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "NAME", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityName name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthorityName getName() {
        return name;
    }

    public void setName(AuthorityName name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}