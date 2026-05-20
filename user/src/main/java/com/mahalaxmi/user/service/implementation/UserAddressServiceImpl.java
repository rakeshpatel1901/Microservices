package com.mahalaxmi.user.service.implementation;

import com.mahalaxmi.user.dto.UserAddressRequestDTO;
import com.mahalaxmi.user.dto.UserAddressResponseDTO;
import com.mahalaxmi.user.entity.UserAddress;
import com.mahalaxmi.user.exception.InvalidDataException;
import com.mahalaxmi.user.externalservices.AuthClient;
import com.mahalaxmi.user.mapper.UserAddressMapper;
import com.mahalaxmi.user.repository.UserAddressRepository;
import com.mahalaxmi.user.service.UserAddressService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserAddressServiceImpl implements UserAddressService {

    private final AuthClient authClient;

    private final UserAddressRepository userAddressRepository;

    private final UserAddressMapper userAddressMapper;

    public UserAddressServiceImpl(AuthClient authClient,
                                  UserAddressRepository userAddressRepository,
                                  UserAddressMapper userAddressMapper){
        this.authClient = authClient;
        this.userAddressMapper = userAddressMapper;
        this.userAddressRepository = userAddressRepository;

    }

    @Transactional
    @Override
    public String newUserAddress(UserAddressRequestDTO userAddressRequestDTO) {
        Long userId = authClient.getUserId();
        UserAddress userAddress = userAddressMapper.toUserAddress(userAddressRequestDTO) ;
        if(Boolean.TRUE.equals(userAddress.getIsDefault())){
            userAddressRepository.updateUserAddressDefault(userId);
        }
        userAddress.setUserId(userId);
        UserAddress savedUserAddress = userAddressRepository.save(userAddress);
        if(savedUserAddress == null){
            return "Not able to save";
        }
        else{
            return "User Address Saved";
        }
    }

    @Override
    public List<UserAddressResponseDTO> getAllAddressofUser() {
        Long userId = authClient.getUserId();
        List<UserAddress> userAddressList = userAddressRepository.findByUserId(userId);
        return userAddressMapper.toUserAddressResponseDtoList(userAddressList);
    }

    @Override
    public UserAddressResponseDTO getAddressOfUserById(Long userAddressId) {

        UserAddress userAddress = userAddressRepository.findById(userAddressId)
                .orElseThrow(()-> new InvalidDataException("Address Id Provided is invalid"));

        return userAddressMapper.toUserAddressResponseDto(userAddress);

    }

    @Transactional
    @Override
    public String updateAddressById(Long userAddressId, UserAddressRequestDTO userAddressRequestDTO) {
        UserAddress userAddress = userAddressRepository.findById(userAddressId)
                .orElseThrow(()-> new InvalidDataException("Address Id Provided is invalid"));
        userAddress.setAddressLine(userAddressRequestDTO.getAddressLine());
        userAddress.setPincode(userAddressRequestDTO.getPincode());
        userAddress.setState(userAddressRequestDTO.getState());
        userAddress.setCity(userAddressRequestDTO.getCity());
        userAddress.setIsDefault(userAddressRequestDTO.getIsDefault());

        return "User Address has been Updated";

    }

    @Transactional
    @Override
    public String deleteAddressById(Long userAddressId) {
        UserAddress userAddress = userAddressRepository.findById(userAddressId)
                .orElseThrow(()-> new InvalidDataException("Address Id Provided is invalid"));
        userAddressRepository.deleteById(userAddressId);
        return "Record Deleted Successfully";
    }

    @Transactional
    @Override
    public String setDefaultAddress(Long userAddressId) {
        Long userId = authClient.getUserId();
        userAddressRepository.updateUserAddressDefault(userId);

        UserAddress userAddress = userAddressRepository.findById(userAddressId)
                .orElseThrow(()-> new InvalidDataException("Invalid Address Id "));
        userAddress.setIsDefault(true);
        return "Record has been updated";
    }

}
