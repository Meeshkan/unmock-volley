package io.unmock.volley;

import android.content.Context;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import io.unmock.core.UnmockOptions;

public class UnmockRequestQueue {

    // from https://stackoverflow.com/questions/16816600/getting-robolectric-to-work-with-volley
    public static RequestQueue newRequestQueueForTest(final Context context, final UnmockOptions unmockOptions) throws IOException {
        final File cacheDir = new File(context.getCacheDir(), "volley");

        final Network network = new BasicNetwork(new UnmockHttpStack(unmockOptions));

        final ResponseDelivery responseDelivery = new ExecutorDelivery(Executors.newSingleThreadExecutor());

        final RequestQueue queue =
                new RequestQueue(
                        new DiskBasedCache(cacheDir),
                        network,
                        4,
                        responseDelivery);

        queue.start();

        return queue;
    }

}
