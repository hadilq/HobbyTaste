package ir.asparsa.hobbytaste.core.retrofit;

import ir.asparsa.android.core.logger.L;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ProgressRequestBody extends RequestBody {
    private File mFile;

    private Subject<Integer, Integer> mOnUploadProgressSubject = new SerializedSubject<>(
            PublishSubject.<Integer>create());

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public ProgressRequestBody(
            final File file
    ) {
        mFile = file;
    }

    public ProgressRequestBody registerObserver(Action1<Integer> observer) {
        mOnUploadProgressSubject
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        L.w(ProgressRequestBody.class, "Error happened", throwable);
                    }
                });
        return this;
    }

    @Override
    public MediaType contentType() {
        // i want to upload only images
        return MediaType.parse("image/*");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = contentLength();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        try {
            int read;
            int percentage = 0;
            int lastPercentage = 0;
            while ((read = in.read(buffer)) != -1) {

                // update progress on UI thread
                percentage = (int) (100 * uploaded / fileLength);
                if (percentage % 10 == 0 && percentage != lastPercentage) {
                    mOnUploadProgressSubject.onNext(percentage);
                    lastPercentage = percentage;
                }

                uploaded += read;
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
            mOnUploadProgressSubject.onCompleted();
        }
    }
}
