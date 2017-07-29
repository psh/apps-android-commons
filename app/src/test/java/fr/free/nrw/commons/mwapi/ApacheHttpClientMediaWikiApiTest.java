package fr.free.nrw.commons.mwapi;

import android.os.Build;
import android.support.annotation.NonNull;

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
@Config(constants = BuildConfig.class, sdk = 21)
public class ApacheHttpClientMediaWikiApiTest {
    private MediaWikiApi testObject;
    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        testObject = new ApacheHttpClientMediaWikiApi("http://" + server.getHostName() + ":" + server.getPort() + "/");
    }

    @After
    public void teardown() throws IOException {
        server.shutdown();
    }

    @Test
    public void authCookiesAreHandled() {
        assertEquals("", testObject.getAuthCookie());

        testObject.setAuthCookie("cookie=chocolate-chip");

        assertEquals("cookie=chocolate-chip", testObject.getAuthCookie());
    }

    @Test
    public void simpleLoginWithWrongPassword() throws Exception {
        String payload = "<tokens logintoken=\"baz\" />";
        server.enqueue(new MockResponse().setBody(apiBatchBody(query(payload))));
        server.enqueue(new MockResponse().setBody(apiBody("<clientlogin status=\"FAIL\" message=\"Incorrect password entered.&#10;Please try again.\" messagecode=\"wrongpassword\" />")));

        String result = testObject.login("foo", "bar");

        RecordedRequest loginTokenRequest = assertBasicRequestParameters(server, "POST");
        Map<String, String> body = parseBody(loginTokenRequest.getBody().readUtf8());
        assertEquals("xml", body.get("format"));
        assertEquals("query", body.get("action"));
        assertEquals("login", body.get("type"));
        assertEquals("tokens", body.get("meta"));

        RecordedRequest loginRequest = assertBasicRequestParameters(server, "POST");
        body = parseBody(loginRequest.getBody().readUtf8());
        assertEquals("1", body.get("rememberMe"));
        assertEquals("foo", body.get("username"));
        assertEquals("bar", body.get("password"));
        assertEquals("baz", body.get("logintoken"));
        assertEquals("https://commons.wikimedia.org", body.get("loginreturnurl"));
        assertEquals("xml", body.get("format"));

        assertEquals("wrongpassword", result);
    }

    @Test
    public void simpleLogin() throws Exception {
        server.enqueue(new MockResponse().setBody(apiBatchBody(query("<tokens logintoken=\"baz\" />"))));
        server.enqueue(new MockResponse().setBody(apiBody("<clientlogin status=\"PASS\" username=\"foo\" />")));

        String result = testObject.login("foo", "bar");

        RecordedRequest loginTokenRequest = assertBasicRequestParameters(server, "POST");
        Map<String, String> body = parseBody(loginTokenRequest.getBody().readUtf8());
        assertEquals("xml", body.get("format"));
        assertEquals("query", body.get("action"));
        assertEquals("login", body.get("type"));
        assertEquals("tokens", body.get("meta"));

        RecordedRequest loginRequest = assertBasicRequestParameters(server, "POST");
        body = parseBody(loginRequest.getBody().readUtf8());
        assertEquals("1", body.get("rememberMe"));
        assertEquals("foo", body.get("username"));
        assertEquals("bar", body.get("password"));
        assertEquals("baz", body.get("logintoken"));
        assertEquals("https://commons.wikimedia.org", body.get("loginreturnurl"));
        assertEquals("xml", body.get("format"));

        assertEquals("PASS", result);
    }

    @Test
    public void twoFactorLogin() throws Exception {
        server.enqueue(new MockResponse().setBody(apiBatchBody(query("<tokens logintoken=\"baz\" />"))));
        server.enqueue(new MockResponse().setBody(apiBody("<clientlogin status=\"PASS\" username=\"foo\" />")));

        String result = testObject.login("foo", "bar", "2fa");

        RecordedRequest loginTokenRequest = assertBasicRequestParameters(server, "POST");
        Map<String, String> body = parseBody(loginTokenRequest.getBody().readUtf8());
        assertEquals("xml", body.get("format"));
        assertEquals("query", body.get("action"));
        assertEquals("login", body.get("type"));
        assertEquals("tokens", body.get("meta"));

        RecordedRequest loginRequest = assertBasicRequestParameters(server, "POST");
        body = parseBody(loginRequest.getBody().readUtf8());
        assertEquals("1", body.get("rememberMe"));
        assertEquals("foo", body.get("username"));
        assertEquals("bar", body.get("password"));
        assertEquals("baz", body.get("logintoken"));
        assertEquals("1", body.get("logincontinue"));
        assertEquals("2fa", body.get("OATHToken"));
        assertEquals("xml", body.get("format"));

        assertEquals("PASS", result);
    }

    @Test
    public void validateLoginForLoggedInUser() throws Exception {
        server.enqueue(new MockResponse().setBody(apiBody(query("<userinfo id=\"10\" name=\"foo\"/>"))));

        boolean result = testObject.validateLogin();

        RecordedRequest loginTokenRequest = assertBasicRequestParameters(server, "GET");
        Map<String, String> body = parseQueryParams(loginTokenRequest);
        assertEquals("xml", body.get("format"));
        assertEquals("query", body.get("action"));
        assertEquals("userinfo", body.get("meta"));

        assertTrue(result);
    }

    @Test
    public void validateLoginForLoggedOutUser() throws Exception {
        server.enqueue(new MockResponse().setBody(apiBody(query("<userinfo id=\"0\" name=\"foo\"/>"))));

        boolean result = testObject.validateLogin();

        RecordedRequest loginTokenRequest = assertBasicRequestParameters(server, "GET");
        Map<String, String> params = parseQueryParams(loginTokenRequest);
        assertEquals("xml", params.get("format"));
        assertEquals("query", params.get("action"));
        assertEquals("userinfo", params.get("meta"));

        assertFalse(result);
    }

    @Test
    public void editToken() throws Exception {
        server.enqueue(new MockResponse().setBody(apiBody("<tokens edittoken=\"baz\" />")));

        String result = testObject.getEditToken();

        RecordedRequest loginTokenRequest = assertBasicRequestParameters(server, "GET");
        Map<String, String> params = parseQueryParams(loginTokenRequest);
        assertEquals("xml", params.get("format"));
        assertEquals("tokens", params.get("action"));
        assertEquals("edit", params.get("type"));

        assertEquals("baz", result);
    }

    @Test
    public void fileExistsWithName_FileNotFound() throws Exception {
        server.enqueue(new MockResponse().setBody(apiBatchBody(query("<normalized><n from=\"File:foo\" to=\"File:Foo\" /></normalized><pages><page _idx=\"-1\" ns=\"6\" title=\"File:Foo\" missing=\"\" imagerepository=\"\" /></pages>"))));

        boolean result = testObject.fileExistsWithName("foo");

        RecordedRequest request = assertBasicRequestParameters(server, "GET");
        Map<String, String> params = parseQueryParams(request);
        assertEquals("xml", params.get("format"));
        assertEquals("query", params.get("action"));
        assertEquals("imageinfo", params.get("prop"));
        assertEquals("File:foo", params.get("titles"));

        assertFalse(result);
    }

    @Test
    public void edit() throws Exception {
        server.enqueue(new MockResponse().setBody(apiBody("<edit result=\"foo\" />")));

        String result = testObject.edit("token-1", "the content", "a file has no name", "summary");

        RecordedRequest editRequest = assertBasicRequestParameters(server, "POST");
        Map<String, String> body = parseBody(editRequest.getBody().readUtf8());
        assertEquals("a file has no name", body.get("title"));
        assertEquals("token-1", body.get("token"));
        assertEquals("the content", body.get("text"));
        assertEquals("summary", body.get("summary"));

        assertEquals("foo", result);
    }

    @Test
    public void findThumbnailByFilename() throws Exception {
        server.enqueue(new MockResponse().setBody(apiBatchBody(query("<pages><page><imageinfo><ii thumburl=\"bar\"/></imageinfo></page></pages>"))));

        String result = testObject.findThumbnailByFilename("foo");

        RecordedRequest request = assertBasicRequestParameters(server, "GET");
        Map<String, String> params = parseQueryParams(request);
        assertEquals("xml", params.get("format"));
        assertEquals("query", params.get("action"));
        assertEquals("imageinfo", params.get("prop"));
        assertEquals("foo", params.get("titles"));
        assertEquals("url", params.get("iiprop"));
        assertEquals("640", params.get("iiurlwidth"));

        assertEquals("bar", result);
    }

    @NonNull
    private String query(String payload) {
        return "<query>" + payload + "</query>";
    }

    @NonNull
    private String apiBody(String payload) {
        return "<?xml version=\"1.0\"?><api>" + payload + "</api>";
    }

    @NonNull
    private String apiBatchBody(String payload) {
        return "<?xml version=\"1.0\"?><api batchcomplete=\"\">" + payload + "</api>";
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
