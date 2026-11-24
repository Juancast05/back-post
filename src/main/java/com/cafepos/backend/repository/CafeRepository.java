package com.cafepos.backend.repository;

import com.cafepos.backend.entity.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
    Optional<Cafe> findByNit(String nit);
    boolean existsByNit(String nit);
}