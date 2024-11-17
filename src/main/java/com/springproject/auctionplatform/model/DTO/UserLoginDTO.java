package com.springproject.auctionplatform.model.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserLoginDTO {

    @NotBlank(message = "Enter a valid username!")
    private String username;

    @NotBlank(message = "Enter a valid password!")
    private String password;
}
