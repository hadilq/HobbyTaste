package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class FeedbackDto {
    private String body;
    private String crashThrowableName;
    private String crashMessage;

    public FeedbackDto() {
    }

    public FeedbackDto(
            String body,
            String crashThrowableName,
            String crashMessage
    ) {
        this.body = body;
        this.crashThrowableName = crashThrowableName;
        this.crashMessage = crashMessage;
    }

    public String getBody() {
        return body;
    }

    public String getCrashThrowableName() {
        return crashThrowableName;
    }

    public String getCrashMessage() {
        return crashMessage;
    }
}
