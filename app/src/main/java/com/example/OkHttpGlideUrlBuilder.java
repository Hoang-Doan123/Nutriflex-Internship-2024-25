package com.example;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

import okhttp3.Call;

public class OkHttpGlideUrlBuilder implements ModelLoader<GlideUrl, InputStream> {
    private final Call.Factory factory;

    public OkHttpGlideUrlBuilder(Call.Factory factory) {
        this.factory = factory;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull GlideUrl glideUrl, int width, int height, @NonNull Options options) {
        return new LoadData<>(glideUrl, new OkHttpStreamFetcher(factory, glideUrl));
    }

    @Override
    public boolean handles(@NonNull GlideUrl url) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private final Call.Factory client;

        public Factory(Call.Factory client) {
            this.client = client;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new OkHttpGlideUrlBuilder(client);
        }

        @Override
        public void teardown() {
        }
    }
}
