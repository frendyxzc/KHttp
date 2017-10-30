package vip.frendy.khttp.model;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by frendy on 2017/3/29.
 */

public class UploadProgressInterceptor implements Interceptor {
    private CountingRequestBody.Listener progressListener;

    public UploadProgressInterceptor(CountingRequestBody.Listener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        if (originalRequest.body() == null) {
            return chain.proceed(originalRequest);
        }

        Request progressRequest = originalRequest.newBuilder()
                .method(originalRequest.method(),
                        new CountingRequestBody(originalRequest.body(), progressListener))
                .build();

        return chain.proceed(progressRequest);
    }
}
