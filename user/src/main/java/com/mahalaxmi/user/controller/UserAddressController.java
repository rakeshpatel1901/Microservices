package com.mahalaxmi.user.controller;

import com.mahalaxmi.user.dto.UserAddressRequestDTO;
import com.mahalaxmi.user.dto.UserAddressResponseDTO;
import com.mahalaxmi.user.service.UserAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class UserAddressController {
    @Autowired
    UserAddressService userAddressService;
    @PostMapping("")
    public ResponseEntity<String> addNewAddress(@Valid @RequestBody UserAddressRequestDTO userAddressRequestDTO){
        String res = userAddressService.newUserAddress(userAddressRequestDTO);
        return ResponseEntity.ok(res);
    }
    @GetMapping("")
    public ResponseEntity<List<UserAddressResponseDTO>> getAllAddressOfUser(){
        List<UserAddressResponseDTO> result = userAddressService.getAllAddressofUser();
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{addressId}")
    public ResponseEntity<UserAddressResponseDTO> getAddressOfUserById(@PathVariable("addressId") Long userAddressId){
        UserAddressResponseDTO result = userAddressService.getAddressOfUserById(userAddressId);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/{addressId}")
    public ResponseEntity<String> updateAddressById(@PathVariable("addressId") Long userAddressId, @Valid @RequestBody UserAddressRequestDTO userAddressRequestDTO){
        String msg = userAddressService.updateAddressById(userAddressId,userAddressRequestDTO);
        return ResponseEntity.ok(msg);
    }
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddressById(@PathVariable("addressId") Long userAddressId){
        userAddressService.deleteAddressById(userAddressId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PatchMapping("/{addressId}/set-default")
    public ResponseEntity<String> setDefaultAddress(@PathVariable("addressId")Long userAddressId){
        userAddressService.setDefaultAddress(userAddressId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}


