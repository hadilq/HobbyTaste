package ir.asparsa.hobbytaste.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.asparsa.hobbytaste.core.model.BaseModel;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@DatabaseTable(tableName = "stores")
public class StoreModel extends BaseModel implements Parcelable {
    public interface Columns {
        String ID = "id";
        String LON = "lon";
        String LAT = "lat";
        String TITLE = "title";
    }

    @DatabaseField(columnName = Columns.ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = Columns.LON, canBeNull = false)
    private Double lon;
    @DatabaseField(columnName = Columns.LAT, canBeNull = false)
    private Double lat;

    @DatabaseField(columnName = Columns.TITLE)
    private String title;

    public StoreModel() {
    }

    public int getId() {
        return id;
    }

    public Double getLon() {
        return lon;
    }

    public Double getLat() {
        return lat;
    }

    public String getTitle() {
        return title;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeValue(this.lon);
        dest.writeValue(this.lat);
        dest.writeString(this.title);
    }

    protected StoreModel(Parcel in) {
        this.id = in.readInt();
        this.lon = (Double) in.readValue(Double.class.getClassLoader());
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.title = in.readString();
    }

    public static final Creator<StoreModel> CREATOR = new Creator<StoreModel>() {
        @Override public StoreModel createFromParcel(Parcel source) {
            return new StoreModel(source);
        }

        @Override public StoreModel[] newArray(int size) {
            return new StoreModel[size];
        }
    };
}
