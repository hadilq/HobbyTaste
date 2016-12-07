package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class AuthenticateDto {
    private String token;

    public AuthenticateDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
