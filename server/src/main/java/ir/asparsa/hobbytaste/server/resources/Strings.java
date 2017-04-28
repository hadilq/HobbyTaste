package ir.asparsa.hobbytaste.server.resources;

/**
 * @author hadi
 * @since 3/7/2017 AD.
 */
public interface Strings {
    String DEFAULT_LOCALE = "fa";

    String COMMENT_NOT_FOUND = "exception.comment_not_found";
    String STORE_NOT_FOUND = "exception.store_not_found";
    String ACCOUNT_NOT_FOUND = "exception.account_not_found";
    String USERNAME_IS_EMPTY = "exception.username_is_empty";
    String USERNAME_IS_REPEATED= "exception.username_is_repeated";
    String JWT_TOKEN_NOT_VALID = "exception.jwt_token_not_valid";
    String NO_JWT_HEADER_FOUND = "exception.jwt_no_token_found";
    String EMPTY_FILE = "exception.empty_file";
    String CANNOT_STORE_FILE = "exception.cannot_store_file";
    String CANNOT_READ_STORED_FILES = "exception.cannot_read_stored_files";
    String CANNOT_READ_STORED_FILE = "exception.cannot_read_stored_file";
    String CANNOT_MOVE_FILE = "exception.cannot_move_file";
    String CANNOT_INIT_STORAGE = "exception.cannot_init_storage";
    String FILENAME_NOT_FOUND = "exception.filename_not_found";
    String SECURITY_DDOS = "exception.security_ddos";
    String UNKNOWN = "exception.unknown";
    String HASH_CODE_EXPIRED = "exception.hash_code_expired";
}
