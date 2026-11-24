package com.cafepos.backend.repository;

import com.cafepos.backend.entity.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByInsumoIdOrderByFechaDesc(Long insumoId);
    List<MovimientoInventario> findByInsumoCafeIdOrderByFechaDesc(Long cafeId);
}