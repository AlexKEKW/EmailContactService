package com.alexsantosportfolio.emailcontactservice.repository;

import com.alexsantosportfolio.emailcontactservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
