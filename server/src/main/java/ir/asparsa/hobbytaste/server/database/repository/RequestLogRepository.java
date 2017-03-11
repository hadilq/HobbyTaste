package ir.asparsa.hobbytaste.server.database.repository;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */

import ir.asparsa.hobbytaste.server.database.model.RequestLogModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestLogRepository extends JpaRepository<RequestLogModel, Long> {

    List<RequestLogModel> findTop10ByAddressOrderByDatetimeAsc(String address);

}