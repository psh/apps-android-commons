package fr.free.nrw.commons.nearby.fragments

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import fr.free.nrw.commons.bookmarks.locations.BookmarkLocationsDao
import fr.free.nrw.commons.nearby.Place
import fr.free.nrw.commons.nearby.placeAdapterDelegate
import fr.free.nrw.commons.upload.categories.BaseDelegateAdapter

class PlaceAdapter(
    activity: Activity,
    bookmarkLocationsDao: BookmarkLocationsDao,
    onPlaceClicked: ((Place) -> Unit)? = null,
    onBookmarkClicked: (Place, Boolean) -> Unit,
    commonPlaceClickActions: CommonPlaceClickActions,
    inAppCameraLocationPermissionLauncher: ActivityResultLauncher<Array<String>>
) :
    BaseDelegateAdapter<Place>(
        placeAdapterDelegate(
            bookmarkLocationsDao,
            onPlaceClicked,
            commonPlaceClickActions.onCameraClicked(activity),
            commonPlaceClickActions.onCameraLongPressed(activity),
            commonPlaceClickActions.onGalleryClicked(activity),
            commonPlaceClickActions.onGalleryLongPressed(activity),
            onBookmarkClicked,
            commonPlaceClickActions.onBookmarkLongPressed(activity),
            commonPlaceClickActions.onOverflowClicked(activity),
            commonPlaceClickActions.onOverflowLongPressed(activity),
            commonPlaceClickActions.onDirectionsClicked(activity),
            commonPlaceClickActions.onDirectionsLongPressed(activity),
            inAppCameraLocationPermissionLauncher
        ),
        areItemsTheSame = {oldItem, newItem -> oldItem.wikiDataEntityId == newItem.wikiDataEntityId }
    )
