package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class AuthenticateRequestDto {
    private long hashCode;
    private String token;

    public AuthenticateRequestDto() {
    }

    public AuthenticateRequestDto(
            long hashCode,
            String token
    ) {
        this.hashCode = hashCode;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public long getHashCode() {
        return hashCode;
    }
}
