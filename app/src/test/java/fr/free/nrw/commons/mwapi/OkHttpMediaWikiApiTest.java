package fr.free.nrw.commons.mwapi;

import android.os.Build;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.free.nrw.commons.BuildConfig;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class OkHttpMediaWikiApiTest {

    private OkHttpMediaWikiApi testObject;
    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        testObject = new OkHttpMediaWikiApi("http://" + server.getHostName() + "/");
    }

    @After
    public void teardown() throws IOException {
        server.shutdown();
    }

    @Test
    public void loginToken() throws Exception {
        server.enqueue(new MockResponse().setBody("{\n" +
                "    \"batchcomplete\": \"\",\n" +
                "    \"query\": {\n" +
                "        \"tokens\": {\n" +
                "            \"logintoken\": \"baz\"\n" +
                "        }\n" +
                "    }\n" +
                "}"));

        String result = testObject.getLoginToken();

        RecordedRequest loginTokenRequest = assertBasicRequestParameters(server, "POST");
        Map<String, String> body = parseBody(loginTokenRequest.getBody().readUtf8());
        assertEquals("json", body.get("format"));
        assertEquals("query", body.get("action"));
        assertEquals("login", body.get("type"));
        assertEquals("tokens", body.get("meta"));

        assertEquals("baz", result);
    }

    private RecordedRequest assertBasicRequestParameters(MockWebServer server, String method) throws InterruptedException {
        RecordedRequest request = server.takeRequest();
        assertEquals("/", request.getRequestUrl().encodedPath());
        assertEquals(method, request.getMethod());
        assertEquals("Commons/" + BuildConfig.VERSION_NAME + " (https://mediawiki.org/wiki/Apps/Commons) Android/" + Build.VERSION.RELEASE, request.getHeader("User-Agent"));
        if ("POST".equals(method)) {
            assertEquals("application/x-www-form-urlencoded", request.getHeader("Content-Type"));
        }
        return request;
    }

    private Map<String, String> parseQueryParams(RecordedRequest request) {
        Map<String, String> result = new HashMap<>();
        HttpUrl url = request.getRequestUrl();
        Set<String> params = url.queryParameterNames();
        for (String name : params) {
            result.put(name, url.queryParameter(name));
        }
        return result;
    }

    private Map<String, String> parseBody(String body) throws UnsupportedEncodingException {
        String[] props = body.split("&");
        Map<String, String> result = new HashMap<>();
        for (String prop : props) {
            String[] pair = prop.split("=");
            result.put(pair[0], URLDecoder.decode(pair[1], "utf-8"));
        }
        return result;
    }
}
