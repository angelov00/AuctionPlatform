package com.springproject.auctionplatform.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email!")
    private String email;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,}$", message = "Invalid phone number!")
    private String phone;
}
