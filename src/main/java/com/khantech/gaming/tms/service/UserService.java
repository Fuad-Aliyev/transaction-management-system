package com.khantech.gaming.tms.service;

import com.khantech.gaming.tms.model.User;

public interface UserService {
    User fetchUserById(Long userId);
}
