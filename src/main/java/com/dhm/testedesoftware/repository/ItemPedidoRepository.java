package com.dhm.testedesoftware.repository;

import com.dhm.testedesoftware.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Hartur Sales Xavier <hartursalesxavier@gmail.com>
 * @date 21/09/2026
 * @brief Class ItemPedidoRepository
 */

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}
