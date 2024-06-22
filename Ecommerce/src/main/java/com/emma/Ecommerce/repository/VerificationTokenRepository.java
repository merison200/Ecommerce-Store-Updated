package com.emma.Ecommerce.repository;

import com.emma.Ecommerce.model.LocalUser;
import com.emma.Ecommerce.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(LocalUser user);
    List<VerificationToken> findByUser_IdOrderByIdDesc(Long id);
}
