package com.inventario.repository;

import com.inventario.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, String> {

    /**
     * Busca equipamentos por unidade (HC3 ou HC4)
     */
    List<Equipamento> findByUnidade(String unidade);

    /**
     * Busca por patrimônio do monitor
     */
    List<Equipamento> findByPatrimonioMonitor(String patrimonioMonitor);

    // Permite buscar um equipamento pelo Netname para fazer o "Merge" (Mesclagem)
    Optional<Equipamento> findByNetnameIgnoreCase(String netname);

}
