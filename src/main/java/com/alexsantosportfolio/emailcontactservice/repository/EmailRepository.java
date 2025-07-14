package com.alexsantosportfolio.emailcontactservice.repository;

import com.alexsantosportfolio.emailcontactservice.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailEntity, Long> {
}
