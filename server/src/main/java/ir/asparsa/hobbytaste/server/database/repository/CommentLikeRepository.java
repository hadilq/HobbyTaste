package ir.asparsa.hobbytaste.server.database.repository;

import ir.asparsa.common.database.model.Comment;
import ir.asparsa.common.database.model.CommentLike;
import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.model.CommentLikeModel;
import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hadi on 12/30/2016 AD.
 */
public interface CommentLikeRepository extends JpaRepository<CommentLikeModel, Long> {

    @Query(value = "SELECT l.* FROM " + CommentLike.TABLE_NAME + " l " +
                   "JOIN " + Comment.TABLE_NAME + " c " +
                   "ON l." + CommentLike.Columns.COMMENT + " = c." + Comment.Columns.ID + " " +
                   "WHERE l." + CommentLike.Columns.ACCOUNT + " = :account " +
                   "AND c." + Comment.Columns.STORE + " = :store", nativeQuery = true)
    List<CommentLikeModel> findByAccountAndStore(
            @Param("account") AccountModel account,
            @Param("store") StoreModel store
    );
}
