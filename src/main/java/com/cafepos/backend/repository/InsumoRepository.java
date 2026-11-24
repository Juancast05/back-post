package com.cafepos.backend.repository;

import com.cafepos.backend.entity.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    List<Insumo> findByCafeIdAndActivoTrue(Long cafeId);

    @Query("SELECT i FROM Insumo i WHERE i.cafe.id = :cafeId AND i.activo = true AND i.stockActual <= i.stockMinimo")
    List<Insumo> findInsumosBajoStock(@Param("cafeId") Long cafeId);
}