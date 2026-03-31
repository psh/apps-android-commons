package fr.free.nrw.commons.contributions

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.free.nrw.commons.di.DefaultKvStore
import fr.free.nrw.commons.di.LanguageWikipediaWikiSite
import fr.free.nrw.commons.kvstore.JsonKvStore
import fr.free.nrw.commons.wikidata.model.WikiSite

/**
 * The Dagger Module for contributions-related providers
 */
@Module
@InstallIn(SingletonComponent::class)
class ContributionsProvidesModule {

    @Provides
    fun providesApplicationKvStore(
        @DefaultKvStore kvStore: JsonKvStore
    ): JsonKvStore {
        return kvStore
    }

    @Provides
    fun providesLanguageWikipediaSite(
        @LanguageWikipediaWikiSite languageWikipediaSite: WikiSite
    ): WikiSite {
        return languageWikipediaSite
    }
}