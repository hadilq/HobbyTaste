package ir.asparsa.android.core.async;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public interface ErrorCallback<T> {
    void onError(T error);
}
