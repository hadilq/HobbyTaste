package ir.asparsa.hobbytaste.ui.fragment.content;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.core.util.UiUtil;
import ir.asparsa.android.ui.fragment.dialog.LoadingProgressDialogFragment;
import ir.asparsa.android.ui.fragment.dialog.ProgressDialogFragment;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.common.net.dto.StoreProto;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.BuildConfig;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.core.retrofit.ProgressRequestBody;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.net.BannerService;
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
 * Created by hadi
 * on 1/15/2017 AD.
 */
public class AddBannerContentFragment extends BaseContentFragment {

    private static final String BUNDLE_KEY_STORE = "BUNDLE_KEY_STORE";
    private static final String BUNDLE_KEY_BITMAP_FILE_PATH = "BUNDLE_KEY_BITMAP_FILE_PATH";
    public static final String BUNDLE_KEY_DIALOG_RESULT_EVENT = "BUNDLE_KEY_DIALOG_RESULT_EVENT";
    private static final String BUNDLE_KEY_BANNER = "BUNDLE_KEY_BANNER";
    private static final int PICK_IMAGE_REQUEST = 23445;

    @Inject
    BannerService mBannerService;
    @Inject
    StoresManager mStoresManager;

    @BindView(R.id.banner)
    ImageView mBannerImageView;
    @BindView(R.id.hint)
    TextView mHintTextView;
    @BindView(R.id.controller)
    DialogControlLayout mController;

    private String mFilePath;
    private BannerModel mBanner;
    private ProgressDialogFragment mProgressDialog;
    private LoadingProgressDialogFragment mLoadingProgressDialog;
    private final CompositeSubscription mSubscription = new CompositeSubscription();
    private Handler mHandler;
    private Runnable mShowProgressDialogRunnable;

    public static AddBannerContentFragment instantiate(
            AddStoreContentFragment.StoreSaveResultEvent event,
            StoreModel store
    ) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_STORE, store);
        bundle.putParcelable(BUNDLE_KEY_DIALOG_RESULT_EVENT, event);

        AddBannerContentFragment fragment = new AddBannerContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
        mHandler = new Handler();

        mShowProgressDialogRunnable = new Runnable() {
            @Override public void run() {
                if (mProgressDialog != null) {
                    mProgressDialog.show(getFragmentManager());
                }
            }
        };
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.add_banner_content_fragment, container, false);
        ButterKnife.bind(this, view);

        final StoreModel store = getArguments().getParcelable(BUNDLE_KEY_STORE);
        if (store == null) {
            return view;
        }

        mBannerImageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        mFilePath = getArguments().getString(BUNDLE_KEY_BITMAP_FILE_PATH);
        if (mFilePath != null) {
            mBannerImageView.setImageBitmap(BitmapFactory.decodeFile(
                    new File(mFilePath).getAbsolutePath(),
                    new BitmapFactory.Options()));
            mHintTextView.setText(getString(R.string.new_store_banner_successfully_prepared));
        }

        mBanner = getArguments().getParcelable(BUNDLE_KEY_BANNER);
        if (mBanner != null) {
            store.getBanners().remove(mBanner);
            mHintTextView.setText(getString(R.string.new_store_banner_successfully_sent));
        }

        if (store.getBanners().size() >= 2) {
            mController.setCommitText(getString(R.string.finish));
            mHintTextView.setText(getString(R.string.new_store_banner_choose_banner));
        }
        if (store.getBanners().size() <= 8) {
            mController.setNeutralText(getString(R.string.next));
        }

        mController.setCancelText(getString(R.string.send));
        mController.arrange();
        mController.setOnControlListener(getControllerListener(store));

        return view;
    }

    @Override public void onDestroyView() {
        mSubscription.clear();
        super.onDestroyView();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        tryToDeleteFile();
    }

    @Override public boolean hasHomeAsUp() {
        return true;
    }

    @Override public BackState onBackPressed() {
        final StoreModel store = getArguments().getParcelable(BUNDLE_KEY_STORE);
        if (store == null) {
            return super.onBackPressed();
        }
        if (mBanner != null) {
            store.getBanners().remove(mBanner);
        }
        return super.onBackPressed();
    }

    private DialogControlLayout.OnControlListener getControllerListener(final StoreModel store) {
        return new DialogControlLayout.OnControlListener() {
            @Override public void onCommit() {
                // Finish adding new store
                if (mBanner == null) {
                    mHintTextView.setText(R.string.new_store_banner_is_not_sent);
                    return;
                }
                mHintTextView.setText("");
                store.getBanners().add(mBanner);
                mSubscription.add(mStoresManager.saveStore(store, getStoreSaveObserver()));
                showProgressDialog(R.string.new_store_saving);
            }

            @Override public void onNeutral() {
                // Adding more banner
                if (mBanner == null) {
                    mHintTextView.setText(R.string.new_store_banner_is_not_sent);
                    return;
                }
                mHintTextView.setText("");
                store.getBanners().add(mBanner);
                AddStoreContentFragment.StoreSaveResultEvent
                        event = getArguments().getParcelable(BUNDLE_KEY_DIALOG_RESULT_EVENT);
                NavigationUtil
                        .startContentFragment(getFragmentManager(), AddBannerContentFragment.instantiate(event, store));
            }

            @Override public void onCancel() {
                // Send selected banner to the server
                if (mFilePath == null) {
                    mHintTextView.setText(R.string.new_store_banner_is_empty);
                    return;
                }
                mHintTextView.setText("");
                if (mBanner != null) {
                    store.getBanners().remove(mBanner);
                    mBanner = null;
                }
                File file = new File(mFilePath);
                ProgressRequestBody requestFile = new ProgressRequestBody(file)
                        .registerObserver(getProgressObserver());
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                mSubscription.add(mBannerService.handleFileUpload(body)
                                                .subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(getFileUploadObserver()));
                showLoadingProgressDialog(R.string.new_store_uploading);
            }
        };
    }

    private Observer<StoreModel> getStoreSaveObserver() {
        return new Observer<StoreModel>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.e(AddBannerContentFragment.class, "Cannot save store", e);
                mHintTextView.setText(e.getLocalizedMessage());
                dismissProgressDialog();
            }

            @Override public void onNext(StoreModel storeModel) {
                L.i(AddBannerContentFragment.class, "Store is saved" + storeModel);
                sendEvent(storeModel);
                dismissProgressDialog();
            }
        };
    }

    private void sendEvent(StoreModel storeModel) {
        AddStoreContentFragment.StoreSaveResultEvent event = getArguments()
                .getParcelable(AddStoreContentFragment.BUNDLE_KEY_DIALOG_RESULT_EVENT);
        event.setStoreModel(storeModel);

        UiUtil.invokeEventReceiver(event, getFragmentManager(), true);
    }

    private Action1<Integer> getProgressObserver() {
        return new Action1<Integer>() {
            @Override public void call(Integer percentage) {
                if (mLoadingProgressDialog != null) {
                    mLoadingProgressDialog.setProgress(percentage);
                }
            }
        };
    }

    private Observer<StoreProto.Banner> getFileUploadObserver() {
        return new Observer<StoreProto.Banner>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
                L.e(AddBannerContentFragment.class, "Banner is not uploaded", e);
                mHintTextView.setText(e.getLocalizedMessage());
                dismissLoadingProgressDialog();
            }

            @Override public void onNext(StoreProto.Banner bannerDto) {
                mBanner = new BannerModel(bannerDto.getMainUrl(), bannerDto.getThumbnailUrl());
                getArguments().putParcelable(BUNDLE_KEY_BANNER, mBanner);
                L.i(AddBannerContentFragment.class, "Banner is uploaded " + mBanner);
                mHintTextView.setText(getString(R.string.new_store_banner_successfully_sent));
                dismissLoadingProgressDialog();
            }
        };
    }

    @Override public void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        L.i(getClass(), "onActivityResult gets called");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null &&
            data.getData() != null) {
            prepareBanner(data.getData());
        }
    }

    private void prepareBanner(Uri uri) {
        L.i(getClass(), "Starting to prepare banner");

        try {
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
            mBannerImageView.setImageBitmap(bitmap);
            if (bitmap.getByteCount() > BuildConfig.MAX_FILE_SIZE) {
                mHintTextView.setText(getString(R.string.new_store_banner_error_max_size));
                return;
            }

            Observable.create(new Observable.OnSubscribe<Void>() {
                @Override public void call(Subscriber<? super Void> subscriber) {
                    try {
                        L.i(getClass(), "Starting to prepare banner asynchronously");
                        tryToDeleteFile();
                        String filePath = getContext().getCacheDir().getAbsolutePath() + "file_" +
                                          System.currentTimeMillis();
                        FileOutputStream out = new FileOutputStream(filePath);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.close();

                        mFilePath = filePath;
                        getArguments().putString(BUNDLE_KEY_BITMAP_FILE_PATH, mFilePath);
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
                              mHintTextView.setText(getString(R.string.new_store_banner_successfully_prepared));
                              dismissProgressDialog();
                          }
                      }, new Action1<Throwable>() {
                          @Override public void call(Throwable throwable) {
                              L.i(getClass(), "Preparing is finished with error", throwable);
                              mHintTextView.setText(getString(R.string.new_store_banner_error_prepared));
                              dismissProgressDialog();
                          }
                      });
            mHintTextView.setText("");
            showProgressDialog(R.string.new_store_preparing);

        } catch (IOException e) {
            L.w(getClass(), "Cannot prepare banner", e);
        }
    }

    private void tryToDeleteFile() {
        if (mFilePath != null) {
            new File(mFilePath).delete();
            mFilePath = null;
        }
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.title_add_banner);
    }

    private void showProgressDialog(@StringRes final int message) {
        dismissProgressDialog();

        mProgressDialog = ProgressDialogFragment.newInstance(getString(message));
        mHandler.post(mShowProgressDialogRunnable);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mHandler.removeCallbacks(mShowProgressDialogRunnable);
            if (mProgressDialog.isAdded()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
    }

    private void showLoadingProgressDialog(@StringRes final int message) {
        dismissLoadingProgressDialog();

        mLoadingProgressDialog = LoadingProgressDialogFragment.newInstance(getString(message));
        mLoadingProgressDialog.show(getFragmentManager());
    }

    private void dismissLoadingProgressDialog() {
        if (mLoadingProgressDialog != null) {
            if (mLoadingProgressDialog.isAdded()) {
                mLoadingProgressDialog.dismiss();
            }
            mLoadingProgressDialog = null;
        }
    }
}
