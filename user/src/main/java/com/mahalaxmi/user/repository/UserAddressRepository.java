package com.mahalaxmi.user.repository;


import com.mahalaxmi.user.entity.UserAddress;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress,Long> {

    @Override
    <S extends UserAddress> S save(S entity);

    @Modifying
    @Transactional
    @Query("UPDATE UserAddress ua SET ua.isDefault = false WHERE ua.userId = :userId")
    void updateUserAddressDefault(Long userId);

    List<UserAddress> findByUserId(Long userId);

}
