package ir.asparsa.common.net.path;

/**
 * Created by hadi on 1/1/2017 AD.
 */
public interface UserServicePath {
    String SERVICE = "user";
    String AUTHENTICATE = "/authenticate/{hashCode}";
    String USERNAME = "/username/{hashCode}";
}
