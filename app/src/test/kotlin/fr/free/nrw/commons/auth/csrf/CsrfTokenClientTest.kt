package fr.free.nrw.commons.auth.csrf

import com.google.gson.stream.MalformedJsonException
import fr.free.nrw.commons.MockWebServerTest
import fr.free.nrw.commons.auth.SessionManager
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isA
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.wikipedia.dataclient.WikiSite
import org.wikipedia.dataclient.mwapi.MwException
import org.wikipedia.dataclient.okhttp.HttpStatusException

class CsrfTokenClientTest : MockWebServerTest() {
    private val wikiSite = WikiSite("test.wikipedia.org")
    private val cb = mock(CsrfTokenClient.Callback::class.java)
    private val sessionManager = mock(SessionManager::class.java)
    private lateinit var tokenInterface: CsrfTokenInterface
    private lateinit var subject: CsrfTokenClient


    @Before
    override fun setUp() {
        super.setUp()
        tokenInterface = service(CsrfTokenInterface::class.java)
        subject = CsrfTokenClient(wikiSite, tokenInterface, sessionManager)
    }


    @Test
    @Throws(Throwable::class)
    fun testRequestSuccess() {
        val expected = "b6f7bd58c013ab30735cb19ecc0aa08258122cba+\\"
        enqueueFromFile("csrf_token.json")

        subject.request(cb)
        server().takeRequest()

        verify(cb).success(eq(expected))
        verify(cb, never()).failure(any(Throwable::class.java))
    }

    @Test
    @Throws(Throwable::class)
    fun testRequestResponseApiError() {
        enqueueFromFile("api_error.json")

        subject.request(cb)
        server().takeRequest()

        verify(cb, never()).success(any(String::class.java))
        verify(cb).failure(isA(MwException::class.java))
    }

    @Test
    @Throws(Throwable::class)
    fun testRequestResponseFailure() {
        enqueue404()

        subject.request(cb)
        server().takeRequest()

        verify(cb, never()).success(any(String::class.java))
        verify(cb).failure(isA(HttpStatusException::class.java))
    }

    @Test
    @Throws(Throwable::class)
    fun testRequestResponseMalformed() {
        enqueueMalformed()

        subject.request(cb)
        server().takeRequest()

        verify(cb, never()).success(any(String::class.java))
        verify(cb).failure(isA(MalformedJsonException::class.java))
    }

}
