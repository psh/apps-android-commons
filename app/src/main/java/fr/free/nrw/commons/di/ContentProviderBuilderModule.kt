package fr.free.nrw.commons.di

import dagger.Module

/**
 * This Class Represents the Module for dependency injection (using dagger)
 * so, if a developer needs to add a new ContentProvider to the commons app
 * then that must be mentioned here to inject the dependencies
 */
@Module
@Suppress("unused")
abstract class ContentProviderBuilderModule {
}
