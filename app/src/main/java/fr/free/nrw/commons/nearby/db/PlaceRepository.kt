package fr.free.nrw.commons.nearby.db

import fr.free.nrw.commons.nearby.Place
import io.reactivex.Completable
import kotlinx.coroutines.rx2.rxCompletable
import javax.inject.Inject

class PlaceRepository @Inject constructor(
    private val dao: PlaceDao
) {
    fun save(place: Place): Completable =
        rxCompletable { dao.save(place.toEntity()) }

    fun getPlace(entity: String): Place? =
        dao.getPlace(entity)?.toDomain()

    fun fetchPlaces(latBegin: Double, lngBegin: Double, latEnd: Double, lngEnd: Double): List<Place> =
        dao.fetchPlaces(latBegin, lngBegin, latEnd, lngEnd).map { it.toDomain() }

    fun deleteAll(): Completable =
        rxCompletable { dao.deleteAll() }
}
