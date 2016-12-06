package ir.asparsa.android.core.model;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public class ErrorModel extends Exception {

    public interface ErrorsCode {
        int EXCEPTION = -1;
        int NUMBER_IS_WRONG = -2;
        int INVOICE_CANNOT_BE_FOUND = -3;
        int PRICES_ARE_NOT_COMPLEMENTARY = -4;
        int VENDOR_CANNOT_BE_FOUND = -5;
        int TAG_CANNOT_BE_FOUND = -6;
    }

    private int code;
    private final String localizedMessage;


    public ErrorModel(int code, String message, String localizedMessage) {
        super(message);
        this.code = code;
        this.localizedMessage = localizedMessage;
    }

    public int getCode() {
        return code;
    }

    @Override public String getLocalizedMessage() {
        return localizedMessage;
    }
}
