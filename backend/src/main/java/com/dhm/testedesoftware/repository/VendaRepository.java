package com.dhm.testedesoftware.repository;

import com.dhm.testedesoftware.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Hartur Sales Xavier <hartursalesxavier@gmail.com>
 * @date 21/09/2026
 * @brief Class VendaRepository
 */

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    java.util.List<com.dhm.testedesoftware.model.Venda> findByUsuarioIdOrderByIdDesc(Long usuarioId);
}
