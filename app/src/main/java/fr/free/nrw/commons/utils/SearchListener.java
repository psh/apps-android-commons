package fr.free.nrw.commons.utils;


import android.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.SearchView;

public abstract class SearchListener implements OnQueryTextListener,
    SearchView.OnQueryTextListener {

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
