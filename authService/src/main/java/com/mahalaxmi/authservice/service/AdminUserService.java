package com.mahalaxmi.authservice.service;

import com.mahalaxmi.authservice.dto.AdminUserDTO;
import org.springframework.data.domain.Page;

public interface AdminUserService {
    public Page<AdminUserDTO> getUsers(String search, int page, int size);
    public long getTotalUserCount();
}
