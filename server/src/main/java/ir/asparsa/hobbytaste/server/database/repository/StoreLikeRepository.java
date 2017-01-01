package ir.asparsa.hobbytaste.server.database.repository;

import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.model.StoreLikeModel;
import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by hadi on 12/30/2016 AD.
 */
public interface StoreLikeRepository extends JpaRepository<StoreLikeModel, Long> {
    List<StoreLikeModel> findByAccount(AccountModel account);

    Optional<StoreLikeModel> findByAccountAndStore(
            AccountModel account,
            StoreModel store
    );
}
