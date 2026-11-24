package com.cafepos.backend.repository;

import com.cafepos.backend.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByProductoId(Long productoId);
    void deleteByProductoId(Long productoId);
}