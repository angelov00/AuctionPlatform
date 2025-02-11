package com.springproject.auctionplatform.model.DTO;

import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Conversation;
import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String avatarURL;

    private Set<AuctionCategory> preferredCategories = EnumSet.noneOf(AuctionCategory.class);

    public String getFullname() {
        return firstName + " " + lastName;
    }
}
