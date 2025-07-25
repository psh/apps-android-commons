package fr.free.nrw.commons.media;

import static fr.free.nrw.commons.utils.UrlUtilsKt.handleWebUrl;

import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.snackbar.Snackbar;
import fr.free.nrw.commons.CommonsApplication;
import fr.free.nrw.commons.Media;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.utils.ClipboardUtils;
import fr.free.nrw.commons.auth.SessionManager;
import fr.free.nrw.commons.bookmarks.models.Bookmark;
import fr.free.nrw.commons.bookmarks.pictures.BookmarkPicturesContentProvider;
import fr.free.nrw.commons.bookmarks.pictures.BookmarkPicturesDao;
import fr.free.nrw.commons.contributions.Contribution;
import fr.free.nrw.commons.contributions.MainActivity;
import fr.free.nrw.commons.databinding.FragmentMediaDetailPagerBinding;
import fr.free.nrw.commons.di.CommonsDaggerSupportFragment;
import fr.free.nrw.commons.mwapi.OkHttpJsonApiClient;
import fr.free.nrw.commons.profile.ProfileActivity;
import fr.free.nrw.commons.utils.DownloadUtils;
import fr.free.nrw.commons.utils.ImageUtils;
import fr.free.nrw.commons.utils.NetworkUtils;
import fr.free.nrw.commons.utils.ViewUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import timber.log.Timber;

public class MediaDetailPagerFragment extends CommonsDaggerSupportFragment implements ViewPager.OnPageChangeListener, MediaDetailFragment.Callback {

    @Inject BookmarkPicturesDao bookmarkDao;

    @Inject
    protected OkHttpJsonApiClient okHttpJsonApiClient;

    @Inject
    protected SessionManager sessionManager;

    private static CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FragmentMediaDetailPagerBinding binding;

    private boolean editable;
    private boolean isFeaturedImage;
    private boolean isWikipediaButtonDisplayed;
    MediaDetailAdapter adapter;
    private Bookmark bookmark;
    private MediaDetailProvider provider;
    private boolean isFromFeaturedRootFragment;
    private int position;

    /**
     * ProgressBar used to indicate the loading status of media items.
     */
    private ProgressBar imageProgressBar;

    private ArrayList<Integer> removedItems=new ArrayList<Integer>();

    public void clearRemoved(){
        removedItems.clear();
    }
    public ArrayList<Integer> getRemovedItems() {
        return removedItems;
    }


    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * This method will create a new instance of MediaDetailPagerFragment and the arguments will be
     * saved to a bundle which will be later available in the {@link #onCreate(Bundle)}
     * @param editable
     * @param isFeaturedImage
     * @return
     */
    public static MediaDetailPagerFragment newInstance(boolean editable, boolean isFeaturedImage) {
        MediaDetailPagerFragment mediaDetailPagerFragment = new MediaDetailPagerFragment();
        Bundle args = new Bundle();
        args.putBoolean("is_editable", editable);
        args.putBoolean("is_featured_image", isFeaturedImage);
        mediaDetailPagerFragment.setArguments(args);
        return mediaDetailPagerFragment;
    }

    public MediaDetailPagerFragment() {
        // Required empty public constructor
    };


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMediaDetailPagerBinding.inflate(inflater, container, false);
        binding.mediaDetailsPager.addOnPageChangeListener(this);
        // Initialize the ProgressBar by finding it in the layout
        imageProgressBar = binding.getRoot().findViewById(R.id.itemProgressBar);
        adapter = new MediaDetailAdapter(getChildFragmentManager());

        // ActionBar is now supported in both activities - if this crashes something is quite wrong
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else {
            throw new AssertionError("Action bar should not be null!");
        }

        // If fragment is associated with ProfileActivity, then hide the tabLayout
        if (getActivity() instanceof ProfileActivity) {
            ((ProfileActivity)getActivity()).setTabLayoutVisibility(false);
        }

        // Else if fragment is associated with MainActivity then hide that tab layout
        else if (getActivity() instanceof MainActivity) {
            ((MainActivity)getActivity()).hideTabs();
        }

        binding.mediaDetailsPager.setAdapter(adapter);

        if (savedInstanceState != null) {
            final int pageNumber = savedInstanceState.getInt("current-page");
            binding.mediaDetailsPager.setCurrentItem(pageNumber, false);
            getActivity().invalidateOptionsMenu();
        }
        adapter.notifyDataSetChanged();

        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current-page", binding.mediaDetailsPager.getCurrentItem());
        outState.putBoolean("editable", editable);
        outState.putBoolean("isFeaturedImage", isFeaturedImage);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            editable = savedInstanceState.getBoolean("editable", false);
            isFeaturedImage = savedInstanceState.getBoolean("isFeaturedImage", false);

        }
        setHasOptionsMenu(true);
        initProvider();
    }

    /**
     * initialise the provider, based on from where the fragment was started, as in from an activity
     * or a fragment
     */
    private void initProvider() {
        if (getParentFragment() instanceof MediaDetailProvider) {
            provider = (MediaDetailProvider) getParentFragment();
        } else if (getActivity() instanceof MediaDetailProvider) {
            provider = (MediaDetailProvider) getActivity();
        } else {
            throw new ClassCastException("Parent must implement MediaDetailProvider");
        }
    }

    public MediaDetailProvider getMediaDetailProvider() {
        return provider;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getActivity() == null) {
            Timber.d("Returning as activity is destroyed!");
            return true;
        }

        Media m = provider.getMediaAtPosition(binding.mediaDetailsPager.getCurrentItem());
        MediaDetailFragment mediaDetailFragment = this.adapter.getCurrentMediaDetailFragment();
        switch (item.getItemId()) {
            case R.id.menu_bookmark_current_image:
                boolean bookmarkExists = bookmarkDao.updateBookmark(bookmark);
                Snackbar snackbar = bookmarkExists ? Snackbar.make(getView(), R.string.add_bookmark, Snackbar.LENGTH_LONG) : Snackbar.make(getView(), R.string.remove_bookmark, Snackbar.LENGTH_LONG);
                snackbar.show();
                updateBookmarkState(item);
                return true;
            case R.id.menu_copy_link:
                String uri = m.getPageTitle().getCanonicalUri();
                ClipboardUtils.copy("shareLink", uri, requireContext());
                Timber.d("Copied share link to clipboard: %s", uri);
                Toast.makeText(requireContext(), getString(R.string.menu_link_copied),
                    Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_share_current_image:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, m.getDisplayTitle() + " \n" + m.getPageTitle().getCanonicalUri());
                startActivity(Intent.createChooser(shareIntent, "Share image via..."));

                //Add media detail to backstack when the share button is clicked
                //So that when the share is cancelled or completed the media detail page is on top
                // of back stack fixing:https://github.com/commons-app/apps-android-commons/issues/2296
                FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                if (supportFragmentManager.getBackStackEntryCount() < 2) {
                    supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(MediaDetailPagerFragment.class.getName())
                        .commit();
                    supportFragmentManager.executePendingTransactions();
                }
                return true;
            case R.id.menu_browser_current_image:
                // View in browser
                handleWebUrl(requireContext(), Uri.parse(m.getPageTitle().getMobileUri()));
                return true;
            case R.id.menu_download_current_image:
                // Download
                if (!NetworkUtils.isInternetConnectionEstablished(getActivity())) {
                    ViewUtil.showShortSnackbar(getView(), R.string.no_internet);
                    return false;
                }
                DownloadUtils.downloadMedia(getActivity(), m);
                return true;
            case R.id.menu_set_as_wallpaper:
                // Set wallpaper
                setWallpaper(m);
                return true;
            case R.id.menu_set_as_avatar:
                // Set avatar
                setAvatar(m);
                return true;
            case R.id.menu_view_user_page:
                if (m != null && m.getUser() != null) {
                    ProfileActivity.startYourself(getActivity(), m.getUser(),
                        !Objects.equals(sessionManager.getUserName(), m.getUser()));
                }
                return true;
            case R.id.menu_view_report:
                showReportDialog(m);
            case R.id.menu_view_set_white_background:
                if (mediaDetailFragment != null) {
                    mediaDetailFragment.onImageBackgroundChanged(ContextCompat.getColor(getContext(), R.color.white));
                }
                return true;
            case R.id.menu_view_set_black_background:
                if (mediaDetailFragment != null) {
                    mediaDetailFragment.onImageBackgroundChanged(ContextCompat.getColor(getContext(), R.color.black));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showReportDialog(final Media media) {
        if (media == null) {
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        final String[] values = requireContext().getResources()
            .getStringArray(R.array.report_violation_options);
        builder.setTitle(R.string.report_violation);
        builder.setItems(R.array.report_violation_options, (dialog, which) -> {
            sendReportEmail(media, values[which]);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {});
        builder.setCancelable(false);
        builder.show();
    }

    private void sendReportEmail(final Media media, final String type) {
        final String technicalInfo = getTechInfo(media, type);

        final Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO);
        feedbackIntent.setType("message/rfc822");
        feedbackIntent.setData(Uri.parse("mailto:"));
        feedbackIntent.putExtra(Intent.EXTRA_EMAIL,
            new String[]{CommonsApplication.REPORT_EMAIL});
        feedbackIntent.putExtra(Intent.EXTRA_SUBJECT,
            CommonsApplication.REPORT_EMAIL_SUBJECT);
        feedbackIntent.putExtra(Intent.EXTRA_TEXT, technicalInfo);
        try {
            startActivity(feedbackIntent);
        } catch (final ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.no_email_client, Toast.LENGTH_SHORT).show();
        }
    }

    private String getTechInfo(final Media media, final String type) {
        final StringBuilder builder = new StringBuilder();

        builder.append("Report type: ")
            .append(type)
            .append("\n\n");

        builder.append("Image that you want to report: ")
            .append(media.getImageUrl())
            .append("\n\n");

        builder.append("User that you want to report: ")
            .append(media.getUser())
            .append("\n\n");

        if (sessionManager.getUserName() != null) {
            builder.append("Your username: ")
                .append(sessionManager.getUserName())
                .append("\n\n");
        }

        builder.append("Violation reason: ")
            .append("\n");

        builder.append("----------------------------------------------")
            .append("\n")
            .append("(please write reason here)")
            .append("\n")
            .append("----------------------------------------------")
            .append("\n\n")
            .append("Thank you for your report! Our team will investigate as soon as possible.")
            .append("\n")
            .append("Please note that images also have a `Nominate for deletion` button.");

        return builder.toString();
    }

    /**
     * Set the media as the device's wallpaper if the imageUrl is not null
     * Fails silently if setting the wallpaper fails
     * @param media
     */
    private void setWallpaper(Media media) {
        if (media.getImageUrl() == null || media.getImageUrl().isEmpty()) {
            Timber.d("Media URL not present");
            return;
        }
        ImageUtils.setWallpaperFromImageUrl(getActivity(), Uri.parse(media.getImageUrl()));
    }

    /**
     * Set the media as user's leaderboard avatar
     * @param media
     */
    private void setAvatar(Media media) {
        if (media.getImageUrl() == null || media.getImageUrl().isEmpty()) {
            Timber.d("Media URL not present");
            return;
        }
        ImageUtils.setAvatarFromImageUrl(getActivity(), media.getImageUrl(),
            Objects.requireNonNull(sessionManager.getCurrentAccount()).name,
            okHttpJsonApiClient, compositeDisposable);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!editable) { // Disable menu options for editable views
            menu.clear(); // see http://stackoverflow.com/a/8495697/17865
            inflater.inflate(R.menu.fragment_image_detail, menu);
            if (binding.mediaDetailsPager != null) {
                MediaDetailProvider provider = getMediaDetailProvider();
                if(provider == null) {
                    return;
                }
                final int position;
                if (isFromFeaturedRootFragment) {
                    position = this.position;
                } else {
                    position = binding.mediaDetailsPager.getCurrentItem();
                }

                Media m = provider.getMediaAtPosition(position);
                if (m != null) {
                    // Enable default set of actions, then re-enable different set of actions only if it is a failed contrib
                    menu.findItem(R.id.menu_browser_current_image).setEnabled(true).setVisible(true);
                    menu.findItem(R.id.menu_copy_link).setEnabled(true).setVisible(true);
                    menu.findItem(R.id.menu_share_current_image).setEnabled(true).setVisible(true);
                    menu.findItem(R.id.menu_download_current_image).setEnabled(true).setVisible(true);
                    menu.findItem(R.id.menu_bookmark_current_image).setEnabled(true).setVisible(true);
                    menu.findItem(R.id.menu_set_as_wallpaper).setEnabled(true).setVisible(true);
                    if (m.getUser() != null) {
                        menu.findItem(R.id.menu_view_user_page).setEnabled(true).setVisible(true);
                    }

                    try {
                        URL mediaUrl = new URL(m.getImageUrl());
                        this.handleBackgroundColorMenuItems(
                            () -> BitmapFactory.decodeStream(mediaUrl.openConnection().getInputStream()),
                            menu
                        );
                    } catch (Exception e) {
                        Timber.e("Cant detect media transparency");
                    }

                    // Initialize bookmark object
                    bookmark = new Bookmark(
                            m.getFilename(),
                            m.getAuthorOrUser(),
                            BookmarkPicturesContentProvider.uriForName(m.getFilename())
                    );
                    updateBookmarkState(menu.findItem(R.id.menu_bookmark_current_image));
                    final Integer contributionState = provider.getContributionStateAt(position);
                    if (contributionState != null) {
                        switch (contributionState) {
                            case Contribution.STATE_FAILED:
                            case Contribution.STATE_IN_PROGRESS:
                            case Contribution.STATE_QUEUED:
                                menu.findItem(R.id.menu_browser_current_image).setEnabled(false)
                                        .setVisible(false);
                                menu.findItem(R.id.menu_copy_link).setEnabled(false)
                                    .setVisible(false);
                                menu.findItem(R.id.menu_share_current_image).setEnabled(false)
                                        .setVisible(false);
                                menu.findItem(R.id.menu_download_current_image).setEnabled(false)
                                        .setVisible(false);
                                menu.findItem(R.id.menu_bookmark_current_image).setEnabled(false)
                                        .setVisible(false);
                                menu.findItem(R.id.menu_set_as_wallpaper).setEnabled(false)
                                        .setVisible(false);
                                break;
                            case Contribution.STATE_COMPLETED:
                                // Default set of menu items works fine. Treat same as regular media object
                                break;
                        }
                    }
                } else {
                    menu.findItem(R.id.menu_browser_current_image).setEnabled(false)
                            .setVisible(false);
                    menu.findItem(R.id.menu_copy_link).setEnabled(false)
                        .setVisible(false);
                    menu.findItem(R.id.menu_share_current_image).setEnabled(false)
                            .setVisible(false);
                    menu.findItem(R.id.menu_download_current_image).setEnabled(false)
                            .setVisible(false);
                    menu.findItem(R.id.menu_bookmark_current_image).setEnabled(false)
                            .setVisible(false);
                    menu.findItem(R.id.menu_set_as_wallpaper).setEnabled(false)
                            .setVisible(false);
                }

                if (!sessionManager.isUserLoggedIn()) {
                    menu.findItem(R.id.menu_set_as_avatar).setVisible(false);
                }

            }
        }
    }

    /**
     * Decide wether or not we should display the background color menu items
     * We display them if the image is transparent
     * @param getBitmap
     * @param menu
     */
    private void handleBackgroundColorMenuItems(Callable<Bitmap> getBitmap, Menu menu) {
        Observable.fromCallable(
                getBitmap
            ).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(image -> {
                if (image.hasAlpha()) {
                    menu.findItem(R.id.menu_view_set_white_background).setVisible(true).setEnabled(true);
                    menu.findItem(R.id.menu_view_set_black_background).setVisible(true).setEnabled(true);
                }
            });
    }

    private void updateBookmarkState(MenuItem item) {
        boolean isBookmarked = bookmarkDao.findBookmark(bookmark);
        if(isBookmarked) {
            if(removedItems.contains(binding.mediaDetailsPager.getCurrentItem())) {
                removedItems.remove(new Integer(binding.mediaDetailsPager.getCurrentItem()));
            }
        }
        else {
            if(!removedItems.contains(binding.mediaDetailsPager.getCurrentItem())) {
                removedItems.add(binding.mediaDetailsPager.getCurrentItem());
            }
        }
        int icon = isBookmarked ? R.drawable.menu_ic_round_star_filled_24px : R.drawable.menu_ic_round_star_border_24px;
        item.setIcon(icon);
    }

    public void showImage(int i, boolean isWikipediaButtonDisplayed) {
        this.isWikipediaButtonDisplayed = isWikipediaButtonDisplayed;
        setViewPagerCurrentItem(i);
    }

    public void showImage(int i) {
        setViewPagerCurrentItem(i);
    }

    /**
     * This function waits for the item to load then sets the item to current item
     * @param position current item that to be shown
     */
    private void setViewPagerCurrentItem(int position) {

        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Show the ProgressBar while waiting for the item to load
                imageProgressBar.setVisibility(View.VISIBLE);
                // Check if the adapter has enough items loaded
                if(adapter.getCount() > position){
                    // Set the current item in the ViewPager
                    binding.mediaDetailsPager.setCurrentItem(position, false);
                    // Hide the ProgressBar once the item is loaded
                    imageProgressBar.setVisibility(View.GONE);
                } else {
                    // If the item is not ready yet, post the Runnable again
                    handler.post(this);
                }
            }
        };
        // Start the Runnable
        handler.post(runnable);
    }

    /**
     * The method notify the viewpager that number of items have changed.
     */
    public void notifyDataSetChanged(){
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        if(getActivity() == null) {
            Timber.d("Returning as activity is destroyed!");
            return;
        }

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPageSelected(int i) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    public void onDataSetChanged() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Called after the media is nominated for deletion
     *
     * @param index item position that has been nominated
     */
    @Override
    public void nominatingForDeletion(int index) {
      provider.refreshNominatedMedia(index);
    }

    public interface MediaDetailProvider {
        Media getMediaAtPosition(int i);

        int getTotalMediaCount();

        Integer getContributionStateAt(int position);

        // Reload media detail fragment once media is nominated
        void refreshNominatedMedia(int index);
    }

    //FragmentStatePagerAdapter allows user to swipe across collection of images (no. of images undetermined)
    private class MediaDetailAdapter extends FragmentStatePagerAdapter {

        /**
         * Keeps track of the current displayed fragment.
         */
        private Fragment mCurrentFragment;

        public MediaDetailAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                // See bug https://code.google.com/p/android/issues/detail?id=27526
                if(getActivity() == null) {
                    Timber.d("Skipping getItem. Returning as activity is destroyed!");
                    return null;
                }
                binding.mediaDetailsPager.postDelayed(() -> getActivity().invalidateOptionsMenu(), 5);
            }
            if (isFromFeaturedRootFragment) {
                return MediaDetailFragment.forMedia(position+i, editable, isFeaturedImage, isWikipediaButtonDisplayed);
            } else {
                return MediaDetailFragment.forMedia(i, editable, isFeaturedImage, isWikipediaButtonDisplayed);
            }
        }

        @Override
        public int getCount() {
            if (getActivity() == null) {
                Timber.d("Skipping getCount. Returning as activity is destroyed!");
                return 0;
            }
            return provider.getTotalMediaCount();
        }

        /**
         * Get the currently displayed fragment.
         * @return
         */
        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        /**
         * If current fragment is of type MediaDetailFragment, return it, otherwise return null.
         * @return MediaDetailFragment
         */
        public MediaDetailFragment getCurrentMediaDetailFragment() {
            if (mCurrentFragment instanceof MediaDetailFragment) {
                return (MediaDetailFragment) mCurrentFragment;
            }

            return null;
        }

        /**
         * Called to inform the adapter of which item is currently considered to be the "primary",
         * that is the one show to the user as the current page.
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void setPrimaryItem(@NonNull final ViewGroup container, final int position,
            @NonNull final Object object) {
            // Update the current fragment if changed
            if(getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment)object);
            }
            super.setPrimaryItem(container, position, object);
        }
    }
}
