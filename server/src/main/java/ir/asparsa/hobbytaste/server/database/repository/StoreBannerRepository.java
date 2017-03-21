package ir.asparsa.hobbytaste.server.database.repository;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */

import ir.asparsa.hobbytaste.server.database.model.BannerModel;
import ir.asparsa.hobbytaste.server.database.model.StoreModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreBannerRepository extends JpaRepository<BannerModel, Long> {

    Optional<List<BannerModel>> findByStore(StoreModel model);

    Optional<BannerModel> findByMainUrl(String mainUrl);

    Optional<BannerModel> findByThumbnailUrl(String thumbnailUrl);
}