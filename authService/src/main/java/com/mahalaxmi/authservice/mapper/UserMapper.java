package com.mahalaxmi.authservice.mapper;


import com.mahalaxmi.authservice.dto.UserDetailResponseDto;
import com.mahalaxmi.authservice.dto.UserRequestDTO;
import com.mahalaxmi.authservice.dto.UserResponseDTO;
import com.mahalaxmi.authservice.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface UserMapper {

        // create User Mapping
        @Mapping(source="role" , target="role" , defaultValue="user")
        Users toUserEntity(UserRequestDTO userRequestDTO);

        UserRequestDTO toUserDTO(Users users);

        UserDetailResponseDto toUserDetailResponseDto(Users user);

        UserResponseDTO toUserResponseDTO(Users users);
        List<UserResponseDTO> toUserResponseDTOList(List<Users> users);



}
