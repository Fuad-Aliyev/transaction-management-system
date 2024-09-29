package com.khantech.gaming.tms.service.impl;

import com.khantech.gaming.tms.exception.UserNotFoundException;
import com.khantech.gaming.tms.model.User;
import com.khantech.gaming.tms.repository.UserRepository;
import com.khantech.gaming.tms.service.UserService;
import com.khantech.gaming.tms.util.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.khantech.gaming.tms.util.LogMessages.USER_NOT_FOUND_LOG;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User fetchUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(USER_NOT_FOUND_LOG, userId);
                    return new UserNotFoundException(BusinessException.UserNotFoundException, userId);
                });
    }
}
