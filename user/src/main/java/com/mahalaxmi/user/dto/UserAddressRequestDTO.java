package com.mahalaxmi.user.dto;


import com.mahalaxmi.user.dto.types.IndianState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAddressRequestDTO {


    @NotNull(message = "Address Line cannot be null")
    @NotBlank(message = "Address Line cannot be blank")
    @NotEmpty(message = "Address Line cannot be Empty")
    private String addressLine;

    @NotNull(message = "City cannot be null")
    @NotBlank(message = "City cannot be blank")
    @NotEmpty(message = "City cannot be Empty")
    private String city;

    @NotNull(message = "State is required")
    private IndianState state;

    @Size(min=6, max=6, message = "Provide the correct pincode")
    private String pincode;

    private Boolean isDefault;

}




