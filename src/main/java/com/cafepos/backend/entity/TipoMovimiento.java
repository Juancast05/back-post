package com.cafepos.backend.entity;

public enum TipoMovimiento {
    ENTRADA,       // Compra o abastecimiento a proveedores
    SALIDA_VENTA,  // Descuento automatico por venta en POS
    MERMA,         // Calibracion de molino, desperdicio o vencimiento
    AJUSTE         // Correccion tras conteo fisico
}