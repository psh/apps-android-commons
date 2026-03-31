package fr.free.nrw.commons.utils

import javax.inject.Inject
import javax.inject.Singleton

fun interface TimeProvider {
    fun currentTimeMillis(): Long
}

@Singleton
class SystemTimeProvider @Inject constructor() : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
