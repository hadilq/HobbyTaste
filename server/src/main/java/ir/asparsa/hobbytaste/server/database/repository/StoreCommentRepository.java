package ir.asparsa.hobbytaste.server.database.repository;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */

import ir.asparsa.hobbytaste.server.database.model.CommentModel;
import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreCommentRepository extends JpaRepository<CommentModel, Long> {

    Page<CommentModel> findByStore(
            StoreModel model,
            Pageable pageable
    );

    Optional<CommentModel> findById(Long id);

}