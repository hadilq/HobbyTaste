package ir.asparsa.hobbytaste.server.database.repository;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */

import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<StoreModel, Long> {
    Optional<StoreModel> findById(Long id);
}