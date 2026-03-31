package fr.free.nrw.commons.wikidata

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Listener for wikidata edits
 */
@Singleton
class WikidataEditListenerImpl @Inject constructor() : WikidataEditListener() {
    /**
     * Fired when wikidata P18 edit is successful. If there's an active listener, then it is fired
     */
    override fun onSuccessfulWikidataEdit() {
        authenticationStateListener?.onWikidataEditSuccessful()
    }
}
