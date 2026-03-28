package fr.free.nrw.commons.di

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable

@AndroidEntryPoint
abstract class CommonsDaggerSupportFragment : Fragment() {

    // Removed @JvmField to allow overriding
    protected open var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
