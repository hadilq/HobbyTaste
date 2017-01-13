package ir.asparsa.hobbytaste.ui.list.holder;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.ui.list.HorizontalSpaceItemDecoration;
import ir.asparsa.hobbytaste.ui.list.data.GalleryData;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hadi on 12/12/2016 AD.
 */
public class GalleryViewHolder extends BaseViewHolder<GalleryData> {

    private final Adapter mAdapter;

    @BindView(R.id.gallery_recycler)
    RecyclerView mRecyclerView;

    public GalleryViewHolder(
            View itemView,
            Observer<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ButterKnife.bind(this, itemView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);

        mAdapter = new Adapter();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                                itemView.getContext().getResources().getDisplayMetrics())));
    }

    @Override
    public void onBindView(GalleryData data) {
        mAdapter.banners = data.getBanners();
        mAdapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter {

        private List<BannerModel> banners = new ArrayList<>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(
                ViewGroup parent,
                int viewType
        ) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
        }

        @Override
        public void onBindViewHolder(
                RecyclerView.ViewHolder holder,
                int position
        ) {
            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).onBindView(banners.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return banners.size();
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.shot;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.shot)
        ImageView mShot;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(final BannerModel banner) {
            boolean imageSet = false;
            if (TextUtils.isEmpty(banner.getThumbnailUrl())) {
                if (TextUtils.isEmpty(banner.getMainUrl())) {
                    itemView.setVisibility(View.GONE);
                } else {
                    Picasso.with(itemView.getContext())
                           .load(banner.getMainUrl())
                           .into(mShot);
                    imageSet = true;
                }
            } else {
                Picasso.with(itemView.getContext())
                       .load(banner.getThumbnailUrl())
                       .into(mShot);
                imageSet = true;
            }

            if (imageSet) {
                mShot.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        if (mObserver != null) {
                            mObserver.onNext(new OnScreenshotClick(banner, mShot.getDrawable()));
                        }
                    }
                });
            }
        }
    }

    public static class OnScreenshotClick implements BaseRecyclerFragment.Event {
        private BannerModel data;
        private Drawable drawable;

        OnScreenshotClick(
                BannerModel data,
                Drawable drawable
        ) {
            this.data = data;
            this.drawable = drawable;
        }

        public BannerModel getData() {
            return data;
        }

        public Drawable getDrawable() {
            return drawable;
        }
    }

}
