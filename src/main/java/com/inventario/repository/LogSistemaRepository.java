package com.inventario.repository;

import com.inventario.model.LogSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogSistemaRepository extends JpaRepository<LogSistema, Long> {
    List<LogSistema> findAllByOrderByDataHoraDesc();
}