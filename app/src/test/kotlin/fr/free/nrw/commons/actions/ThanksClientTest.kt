package fr.free.nrw.commons.actions

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import fr.free.nrw.commons.CommonsApplication
import fr.free.nrw.commons.utils.UserAgentProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.robolectric.RobolectricTestRunner
import org.wikipedia.csrf.CsrfTokenClient
import org.wikipedia.dataclient.Service

@RunWith(RobolectricTestRunner::class)
@PrepareForTest(CommonsApplication::class)
class ThanksClientTest {
    @Mock
    private lateinit var csrfTokenClient: CsrfTokenClient
    @Mock
    private lateinit var service: Service

    private lateinit var thanksClient: ThanksClient
    private lateinit var mockedApplication: MockedStatic<CommonsApplication>

    /**
     * initial setup, test environment
     */
    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockedApplication = Mockito.mockStatic(CommonsApplication::class.java)
        thanksClient = ThanksClient(csrfTokenClient, service, object : UserAgentProvider {
            override fun get(): String = "test"
        })
    }

    /**
     * Test thanks
     */
    @Test
    fun testThanks() {
        `when`(csrfTokenClient.tokenBlocking).thenReturn("test")
        thanksClient.thank(1L)
        verify(service).thank(ArgumentMatchers.anyString(), ArgumentMatchers.any(), eq("test"), eq("test"))
    }
}