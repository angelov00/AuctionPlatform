package com.springproject.auctionplatform.model.entity;

import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.model.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    private String avatarURL;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = AuctionCategory.class, fetch = FetchType.LAZY)
    private Set<AuctionCategory> preferredCategories = EnumSet.noneOf(AuctionCategory.class);

    @ManyToMany
    @JoinTable(
            name = "conversation_participants",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "conversation_id")
    )
    private Set<Conversation> conversations = new HashSet<>();

    @ManyToMany
    private Set<Auction> watchlist = new HashSet<>();

    public User(String username, String password, String firstName, String lastName,
                String email, String phone, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.roles = roles;
    }
}
