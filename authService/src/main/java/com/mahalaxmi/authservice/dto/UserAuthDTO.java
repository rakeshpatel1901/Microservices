package com.mahalaxmi.authservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {
    @NotNull(message="Email must not be null")
    @Email(message="Email is invalid!")
    private String email;
    @Size(min=6,max=15,message="Password length should be 6-15")
    private String password;
}
