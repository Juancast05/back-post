package com.cafepos.backend.repository;

import com.cafepos.backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCafeIdAndDisponibleTrue(Long cafeId);
    List<Producto> findByCafeIdAndCategoria(Long cafeId, String categoria);
}