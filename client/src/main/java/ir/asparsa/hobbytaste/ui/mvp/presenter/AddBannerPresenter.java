package ir.asparsa.hobbytaste.ui.mvp.presenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.common.net.dto.StoreProto;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.BuildConfig;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.core.retrofit.ProgressRequestBody;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.net.BannerService;
import ir.asparsa.hobbytaste.ui.fragment.content.AddBannerContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.AddStoreContentFragment;
import ir.asparsa.hobbytaste.ui.mvp.holder.AddBannerViewHolder;
import ir.asparsa.hobbytaste.ui.mvp.holder.FragmentHolder;
import okhttp3.MultipartBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author hadi
 * @since 4/23/2017 AD.
 */
public class AddBannerPresenter implements Presenter<AddBannerViewHolder> {
    public static final String BUNDLE_KEY_BITMAP_FILE_PATH = "BUNDLE_KEY_BITMAP_FILE_PATH";
    public static final String BUNDLE_KEY_BANNER = "BUNDLE_KEY_BANNER";

    private final FragmentHolder mFragment;
    private final CompositeSubscription mSubscription = new CompositeSubscription();

    @Inject
    BannerService mBannerService;
    @Inject
    StoresManager mStoresManager;

    private StoreModel mStore;
    private String mFilePath;
    private BannerModel mBanner;
    private AddBannerViewHolder mHolder;

    public AddBannerPresenter(FragmentHolder fragment) {
        ApplicationLauncher.mainComponent().inject(this);
        mFragment = fragment;
    }

    @Override public void bindView(@NonNull AddBannerViewHolder viewHolder) {
        mHolder = viewHolder;
        mStore = mFragment.getArguments().getParcelable(AddBannerContentFragment.BUNDLE_KEY_STORE);
        if (mStore == null) {
            throw new RuntimeException("Store cannot be null");
        }
        String filePath = mFragment.getArguments().getString(BUNDLE_KEY_BITMAP_FILE_PATH);
        if (filePath != null) {
            mFilePath = filePath;
        }
        BannerModel banner = mFragment.getArguments().getParcelable(BUNDLE_KEY_BANNER);
        if (banner != null) {
            mStore.getBanners().remove(banner);
            mBanner = banner;
        }
        mHolder.setupControllerButtons(
                mStore.getBanners().size() >= 2, mStore.getBanners().size() <= 8, getControllerListener());
        publish();
    }

    @Override public void unbindView() {
        mSubscription.clear();
        if (mHolder == null) {
            return;
        }
        mHolder.dismissProgressDialog();
        mHolder.dismissLoadingProgressDialog();
        mHolder = null;
    }

    @Override public void publish() {
        if (mHolder == null) {
            return;
        }
        if (mFilePath != null) {
            mHolder.setImageBitmap(mFilePath);
            mHolder.setHintText(mFragment.getString(R.string.new_store_banner_successfully_prepared));
            mHolder.dismissProgressDialog();
        }
        if (mBanner != null) {
            mHolder.setHintText(mFragment.getString(R.string.new_store_banner_successfully_sent));
            mHolder.dismissLoadingProgressDialog();
        }
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mFragment.onClick(AddBannerContentFragment.EVENT_KEY_CHOOSE_IMAGE, intent);
    }

    private DialogControlLayout.OnControlListener getControllerListener() {
        return new DialogControlLayout.OnControlListener() {
            @Override public void onCommit() {
                // Finish adding new store
                if (mBanner == null) {
                    mHolder.setHintText(R.string.new_store_banner_is_not_sent);
                    return;
                }
                mHolder.setHintText("");
                mStore.getBanners().add(mBanner);
                mSubscription.add(mStoresManager.saveStore(mStore, getStoreSaveObserver()));
                mHolder.showProgressDialog(R.string.new_store_saving, mFragment.getFragmentManager());
            }

            @Override public void onNeutral() {
                // Adding more banner
                if (mBanner == null) {
                    mHolder.setHintText(R.string.new_store_banner_is_not_sent);
                    return;
                }
                mHolder.setHintText("");
                mStore.getBanners().add(mBanner);
                AddStoreContentFragment.StoreSaveResultEvent
                        event = mFragment.getArguments()
                                         .getParcelable(AddBannerContentFragment.BUNDLE_KEY_DIALOG_RESULT_EVENT);
                mFragment.onClick(AddBannerContentFragment.EVENT_KEY_ADD_NEW_BANNER, event, mStore);
            }

            @Override public void onCancel() {
                // Send selected banner to the server
                if (mFilePath == null) {
                    mHolder.setHintText(R.string.new_store_banner_is_empty);
                    return;
                }
                mHolder.setHintText("");
                if (mBanner != null) {
                    mStore.getBanners().remove(mBanner);
                    mBanner = null;
                }
                File file = new File(mFilePath);
                ProgressRequestBody requestFile = new ProgressRequestBody(file)
                        .registerObserver(mHolder.getProgressObserver());
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                mSubscription.add(mBannerService.handleFileUpload(body)
                                                .subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(getFileUploadObserver()));
                mHolder.showLoadingProgressDialog(R.string.new_store_uploading, mFragment.getFragmentManager());
            }
        };
    }

    private Observer<StoreModel> getStoreSaveObserver() {
        return new Observer<StoreModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.e(AddBannerContentFragment.class, "Cannot save store", e);
                if (mHolder == null) {
                    return;
                }
                mHolder.setHintText(e.getLocalizedMessage());
                mHolder.dismissProgressDialog();
            }

            @Override public void onNext(StoreModel storeModel) {
                L.i(AddBannerContentFragment.class, "Store is saved" + storeModel);
                mFragment.onClick(AddBannerContentFragment.EVENT_KEY_SEND_STORE, mStore);
                if (mHolder == null) {
                    return;
                }
                mHolder.dismissProgressDialog();
            }
        };
    }


    private Observer<StoreProto.Banner> getFileUploadObserver() {
        return new Observer<StoreProto.Banner>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.e(AddBannerContentFragment.class, "Banner is not uploaded", e);
                if (mHolder == null) {
                    return;
                }
                mHolder.setHintText(e.getLocalizedMessage());
                mHolder.dismissLoadingProgressDialog();
            }

            @Override public void onNext(StoreProto.Banner bannerDto) {
                L.i(AddBannerContentFragment.class, "Banner is uploaded " + mBanner);
                mBanner = new BannerModel(bannerDto.getMainUrl(), bannerDto.getThumbnailUrl());
                mFragment.getArguments().putParcelable(BUNDLE_KEY_BANNER, mBanner);
                publish();
            }
        };
    }

    public void prepareBanner(Uri uri) {
        L.i(getClass(), "Starting to prepare banner");
        if (mHolder == null) {
            return;
        }

        try {
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(mFragment.getContext().getContentResolver(), uri);
            mHolder.setImageBitmap(bitmap);

            if (bitmap.getByteCount() > BuildConfig.MAX_FILE_SIZE) {
                mHolder.setHintText(mFragment.getContext().getString(R.string.new_store_banner_error_max_size));
                return;
            }

            final String filePath = mFragment.getContext().getCacheDir().getAbsolutePath() + "file_" +
                                    System.currentTimeMillis();
            mFilePath = null;
            Observable.create(new Observable.OnSubscribe<Void>() {
                @Override public void call(Subscriber<? super Void> subscriber) {
                    try {
                        L.i(getClass(), "Starting to prepare banner asynchronously");
                        tryToDeleteFile();

                        FileOutputStream out = new FileOutputStream(filePath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.close();

                        L.i(getClass(), "Preparing banner is finished");
                        subscriber.onNext(null);
                    } catch (IOException e) {
                        L.i(getClass(), "Preparing banner is finished with error", e);
                        subscriber.onError(e);
                    }
                }
            }).subscribeOn(Schedulers.newThread())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Action1<Void>() {
                          @Override public void call(Void aVoid) {
                              L.i(getClass(), "Successfully preparing is finished");
                              mFilePath = filePath;
                              mFragment.getArguments().putString(BUNDLE_KEY_BITMAP_FILE_PATH, mFilePath);
                              publish();
                          }
                      }, new Action1<Throwable>() {
                          @Override public void call(Throwable throwable) {
                              L.i(getClass(), "Preparing is finished with error", throwable);
                              if (mHolder == null) {
                                  return;
                              }
                              mHolder.setHintText(
                                      mFragment.getContext().getString(R.string.new_store_banner_error_prepared));
                              mHolder.dismissProgressDialog();
                          }
                      });
            mHolder.setHintText("");
            mHolder.showProgressDialog(R.string.new_store_preparing, mFragment.getFragmentManager());

        } catch (IOException e) {
            L.w(getClass(), "Cannot prepare banner", e);
        }
    }

    public void tryToDeleteFile() {
        if (mFilePath != null) {
            new File(mFilePath).delete();
            mFilePath = null;
        }
    }

    public void releaseBanner() {
        if (mBanner != null) {
            mStore.getBanners().remove(mBanner);
        }
    }
}
