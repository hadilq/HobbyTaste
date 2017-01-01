package ir.asparsa.hobbytaste.server.database.repository;

import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.model.CommentLikeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hadi on 12/30/2016 AD.
 */
public interface CommentLikeRepository extends JpaRepository<CommentLikeModel, Long> {
    List<CommentLikeModel> findByAccount(AccountModel account);
}
