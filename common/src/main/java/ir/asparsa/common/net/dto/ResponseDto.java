package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class ResponseDto {

    public interface STATUS {
        int SUCCEED = 0;
        int ERROR = 1;
    }

    private int status;

    public ResponseDto(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
