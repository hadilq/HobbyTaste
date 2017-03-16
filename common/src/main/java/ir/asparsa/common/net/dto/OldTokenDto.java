package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class OldTokenDto {
    private String token;

    public OldTokenDto(
            String token
    ) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
