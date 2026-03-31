package fr.free.nrw.commons.customselector.ui.selector

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * View Model Factory.
 */
class CustomSelectorViewModelFactory
    @Inject
    constructor(
        @ApplicationContext val context: Context,
        val imageFileLoader: ImageFileLoader,
    ) : ViewModelProvider.Factory {
        override fun <CustomSelectorViewModel : ViewModel> create(modelClass: Class<CustomSelectorViewModel>): CustomSelectorViewModel =
            CustomSelectorViewModel(context, imageFileLoader) as CustomSelectorViewModel
    }
