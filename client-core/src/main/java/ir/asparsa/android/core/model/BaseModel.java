package ir.asparsa.android.core.model;

import android.os.Parcelable;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public abstract class BaseModel<T> implements Parcelable {

    public abstract T getId();

    public abstract void setId(T id);
}
