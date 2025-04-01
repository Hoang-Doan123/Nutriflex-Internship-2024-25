package com.example;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.Preconditions;

import java.io.InputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpStreamFetcher implements DataFetcher<InputStream> {
    private final Call.Factory factory;
    private final GlideUrl url;
    private InputStream stream;
    private Call call;

    public OkHttpStreamFetcher(Call.Factory factory, GlideUrl url) {
        this.factory = factory;
        this.url = url;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        Request request = new Request.Builder().url(url.toStringUrl()).build();
        call = factory.newCall(request);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                stream = Preconditions.checkNotNull(response.body()).byteStream();
                callback.onDataReady(stream);
            } else {
                callback.onLoadFailed(new Exception("Request failed with code: " + response.code()));
            }
        } catch (Exception e) {
            callback.onLoadFailed(e);
        }
    }

    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (Exception e) {
            Log.i("OkHttpStreamFetcherCleanUpError", Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public void cancel() {
        Call localCall = call;
        if (localCall != null) {
            localCall.cancel();
        }
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
