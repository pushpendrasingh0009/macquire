package com.macquire.rmg.auth.model.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @Column(name = "ID")
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "USERNAME", length = 50, unique = true, nullable = false)
    @Size(min = 4, max = 50)
    private String username;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "USER_ROLE",
            joinColumns = {@JoinColumn(name = "USERS_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")})
    private List<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Role> getAuthorities() {
        return roles;
    }

    public void setAuthorities(List<Role> roles) {
        this.roles = roles;
    }

}