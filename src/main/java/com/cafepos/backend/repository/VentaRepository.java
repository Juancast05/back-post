package com.cafepos.backend.repository;

import com.cafepos.backend.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByCafeIdOrderByFechaDesc(Long cafeId);
    List<Venta> findByCafeIdAndFechaBetween(Long cafeId, LocalDateTime inicio, LocalDateTime fin);
    long countByCafeId(Long cafeId);
}