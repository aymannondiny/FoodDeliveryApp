package com.fooddelivery.model;

import java.time.LocalDateTime;

/**
 * Represents any system user: customer, restaurant owner, or delivery rider.
 */
public class User {

    public enum Role { CUSTOMER, RESTAURANT_OWNER, RIDER, ADMIN }

    private String id;
    private String name;
    private String email;
    private String passwordHash;   // Store hashed password (SHA-256 hex)
    private String phone;
    private Role   role;
    private Address defaultAddress;
    private boolean active;
    private LocalDateTime createdAt;

    public User() { this.active = true; }

    public User(String id, String name, String email, String passwordHash,
                String phone, Role role) {
        this();
        this.id           = id;
        this.name         = name;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.phone        = phone;
        this.role         = role;
        this.createdAt    = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return name + " <" + email + "> [" + role + "]";
    }

    // ── Getters & Setters ───────────────────────────────────────────────────
    public String  getId()                        { return id; }
    public void    setId(String id)               { this.id = id; }
    public String  getName()                      { return name; }
    public void    setName(String name)           { this.name = name; }
    public String  getEmail()                     { return email; }
    public void    setEmail(String email)         { this.email = email; }
    public String  getPasswordHash()              { return passwordHash; }
    public void    setPasswordHash(String ph)     { this.passwordHash = ph; }
    public String  getPhone()                     { return phone; }
    public void    setPhone(String phone)         { this.phone = phone; }
    public Role    getRole()                      { return role; }
    public void    setRole(Role role)             { this.role = role; }
    public Address getDefaultAddress()            { return defaultAddress; }
    public void    setDefaultAddress(Address a)   { this.defaultAddress = a; }
    public boolean isActive()                     { return active; }
    public void    setActive(boolean active)      { this.active = active; }
    public LocalDateTime getCreatedAt()           { return createdAt; }
    public void    setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
}
