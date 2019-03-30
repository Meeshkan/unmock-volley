package io.unmock.volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;

import io.unmock.core.UnmockOptions;

@RunWith(RobolectricTestRunner.class)
public class UnmockRequestQueueTest {

    class BodyHolder {
        public String body;
    }

    @Test
    public void basicRequestWorks() throws IOException, JSONException, InterruptedException {
        final RequestQueue queue = UnmockRequestQueue.newRequestQueueForTest(RuntimeEnvironment.application, new UnmockOptions.Builder().build());
        String url = "https://www.behance.net/v2/projects";
        final BodyHolder body = new BodyHolder();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        body.body = response;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        queue.add(stringRequest);
        Thread.sleep(5000); // hack, is there a better way to do this?
        JSONObject json = new JSONObject(body.body);
        JSONArray projects = json.getJSONArray("projects");
        // smoke test, will fail if ID not present
        projects.getJSONObject(0).getInt("id");
    }
}