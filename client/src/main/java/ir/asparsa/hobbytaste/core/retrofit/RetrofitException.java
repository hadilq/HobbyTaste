package ir.asparsa.hobbytaste.core.retrofit;

import android.content.Context;
import android.support.annotation.Nullable;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.common.net.dto.ResponseProto;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author hadi
 * @since 3/7/2017 AD.
 */
public class RetrofitException extends RuntimeException {

    @Inject
    Context mContext;

    public static RetrofitException httpError(
            String url,
            Response response,
            Retrofit retrofit
    ) {
        String message = response.code() + " " + response.message();
        return new RetrofitException(message, url, response, Kind.HTTP, null, retrofit);
    }

    public static RetrofitException networkError(IOException exception) {
        return new RetrofitException(exception.getMessage(), null, null, Kind.NETWORK, exception, null);
    }

    public static RetrofitException unexpectedError(Throwable exception) {
        return new RetrofitException(exception.getMessage(), null, null, Kind.UNEXPECTED, exception, null);
    }

    /**
     * Identifies the event kind which triggered a {@link RetrofitException}.
     */
    public enum Kind {
        /**
         * An {@link IOException} occurred while communicating to the server.
         */
        NETWORK,
        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    private final String url;
    private final Response response;
    private final Kind kind;
    private final Retrofit retrofit;

    RetrofitException(
            String message,
            String url,
            Response response,
            Kind kind,
            Throwable exception,
            Retrofit retrofit
    ) {
        super(message, exception);
        this.url = url;
        this.response = response;
        this.kind = kind;
        this.retrofit = retrofit;
        ApplicationLauncher.mainComponent().inject(this);
    }

    /**
     * The request URL which produced the error.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Response object containing status code, headers, body, etc.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * The event kind which triggered this error.
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * The Retrofit this request was executed on
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    @Override public String getLocalizedMessage() {
        switch (kind) {
            case HTTP:
                ResponseProto.Response errorDto = getErrorBody();
                if (errorDto != null) {
                    return errorDto.getLocalizedMessage();
                }
                break;
            case UNEXPECTED:
                if (getCause() != null) {
                    L.e(RetrofitException.class, "Unexpected error", getCause());
                }
//                throw this;
        }
        return mContext.getString(R.string.connection_error);
    }

    /**
     * HTTP response body converted to specified {@code type}. {@code null} if there is no
     * response.
     */
    @Nullable private ResponseProto.Response getErrorBody() {
        if (response == null || response.errorBody() == null) {
            return null;
        }
        try {
            return ResponseProto.Response.parseFrom(response.errorBody().bytes());
        } catch (IOException e) {
            L.w(getClass(), "Cannot convert to error dto", e);
        }
        return null;
    }
}

