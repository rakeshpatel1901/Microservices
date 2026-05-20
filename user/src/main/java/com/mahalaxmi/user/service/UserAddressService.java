package com.mahalaxmi.user.service;

import com.mahalaxmi.user.dto.UserAddressRequestDTO;
import com.mahalaxmi.user.dto.UserAddressResponseDTO;

import java.util.List;

public interface UserAddressService {
    public String newUserAddress(UserAddressRequestDTO userAddressRequestDTO);
    public List<UserAddressResponseDTO> getAllAddressofUser();
    public UserAddressResponseDTO getAddressOfUserById(Long userAddressId);
    public String updateAddressById(Long userAddressId, UserAddressRequestDTO userAddressRequestDTO);
    public String deleteAddressById(Long userAddressId);
    public String setDefaultAddress(Long userAddressId);
}
