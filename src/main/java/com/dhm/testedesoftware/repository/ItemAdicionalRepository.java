package com.dhm.testedesoftware.repository;

import com.dhm.testedesoftware.model.ItemAdicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Hartur Sales Xavier <hartursalesxavier@gmail.com>
 * @date 21/09/2026
 * @brief Class ItemAdicionalRepository
 */

@Repository
public interface ItemAdicionalRepository extends JpaRepository<ItemAdicional, Long> {
}
