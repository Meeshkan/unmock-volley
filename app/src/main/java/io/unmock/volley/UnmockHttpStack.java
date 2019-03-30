package io.unmock.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.Request;
import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.HttpResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.unmock.core.UnmockOptions;
import io.unmock.okhttp.UnmockInterceptor;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

class UnmockHttpStack extends BaseHttpStack {

    final private @NotNull OkHttpClient unmockClient;
    final private @NotNull UnmockInterceptor interceptor;

    public UnmockHttpStack(UnmockOptions unmockOptions) throws IOException {
        this.interceptor = new UnmockInterceptor(unmockOptions);
        this.unmockClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    @Override
    public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException,
            AuthFailureError {
        String url = request.getUrl();
        Map<String, String> headers = request.getHeaders();
        String method;
        switch(request.getMethod()) {
            case Request.Method.GET:
                method = "GET";
                break;
            case Request.Method.POST:
                method = "POST";
                break;
            case Request.Method.PUT:
                method = "PUT";
                break;
            case Request.Method.DELETE:
                method = "DELETE";
                break;
            default:
                throw new IOException("Unknown method "+request.getMethod());
        }
        Headers.Builder headersBuilder = new Headers.Builder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headersBuilder = headersBuilder.add(entry.getKey(), entry.getValue());
        }
        byte[] body = request.getBody();
        okhttp3.Request okhttpRequest = new okhttp3.Request.Builder()
                .url(url)
                .headers(headersBuilder.build())
                .method(method, request.getHeaders().get("Content-Type") == null ? null : RequestBody.create(MediaType.get(request.getHeaders().get("Content-Type")), body))
                .build();

        Response response = this.unmockClient.newCall(okhttpRequest).execute();

        final List<Header> responseHeaders = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : response.headers().toMultimap().entrySet()) {
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (i != 0) {
                    header.append(",");
                }
                header.append(entry.getValue().get(i));
            }
            responseHeaders.add(new Header(entry.getKey(), header.toString()));
        }

        return new HttpResponse(response.code(), responseHeaders, (int) response.body().contentLength(), response.body().byteStream());
    }
}