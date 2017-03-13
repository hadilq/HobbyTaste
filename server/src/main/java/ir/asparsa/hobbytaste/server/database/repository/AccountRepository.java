package ir.asparsa.hobbytaste.server.database.repository;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */

import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountModel, Long> {

    Optional<AccountModel> findByUsername(String username);

    Optional<AccountModel> findById(Long Id);

    Optional<AccountModel> findByHashCode(Long hashCode);

}