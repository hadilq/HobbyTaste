package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class AuthenticateDto {
    private String token;
    private String username;

    public AuthenticateDto() {
    }

    public AuthenticateDto(
            String token,
            String username
    ) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}
