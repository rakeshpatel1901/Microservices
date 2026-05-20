package com.mahalaxmi.user.mapper;

import com.mahalaxmi.user.dto.UserAddressRequestDTO;
import com.mahalaxmi.user.dto.UserAddressResponseDTO;
import com.mahalaxmi.user.entity.UserAddress;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {


    UserAddress toUserAddress(UserAddressRequestDTO userAddressRequestDTO);


    UserAddressResponseDTO toUserAddressResponseDto(UserAddress userAddress);

    List<UserAddressResponseDTO> toUserAddressResponseDtoList(List<UserAddress> entityList);


}
