package fr.free.nrw.commons.mwapi;

import org.junit.Test;

import fr.free.nrw.commons.BuildConfig;

import static org.junit.Assert.*;

public class JsonMediaWikiAppiTest {

    @Test
    public void captchTest() {
        JsonMediaWikiAppi api = new JsonMediaWikiAppi(BuildConfig.WIKIMEDIA_API_HOST);
        api.captcha();
    }
}