package ir.asparsa.hobbytaste.ui.list.data;

import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.model.BannerModel;

import java.util.List;

/**
 * Created by hadi on 12/12/2016 AD.
 */
public class GalleryData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.gallery;

    private List<BannerModel> banners;

    public GalleryData(List<BannerModel> banners) {
        this.banners = banners;
    }

    public List<BannerModel> getBanners() {
        return banners;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }
}
