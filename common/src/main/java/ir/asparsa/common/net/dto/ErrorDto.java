package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 3/7/2017 AD.
 */
public class ErrorDto extends ResponseDto {
    private String detailMessage;
    private String localizedMessage;

    public ErrorDto(
            int status,
            String detailMessage,
            String localizedMessage
    ) {
        super(status);
        this.detailMessage = detailMessage;
        this.localizedMessage = localizedMessage;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public String getLocalizedMessage() {
        return localizedMessage;
    }
}
