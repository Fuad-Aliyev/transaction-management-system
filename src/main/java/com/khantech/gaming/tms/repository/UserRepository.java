package com.khantech.gaming.tms.repository;

import com.khantech.gaming.tms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
