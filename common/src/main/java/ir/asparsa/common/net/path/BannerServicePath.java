package ir.asparsa.common.net.path;

/**
 * @author hadi
 * @since 12/31/2016 AD.
 */
public interface BannerServicePath {
    String SERVICE = "banner";
    String TMP = "/tmp/{filename}";
    String IMAGE = "/image/{filename:.+}";
}
