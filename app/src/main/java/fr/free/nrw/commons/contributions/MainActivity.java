package fr.free.nrw.commons.contributions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.ExistingWorkPolicy;
import fr.free.nrw.commons.databinding.MainBinding;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.WelcomeActivity;
import fr.free.nrw.commons.auth.SessionManager;
import fr.free.nrw.commons.bookmarks.BookmarkFragment;
import fr.free.nrw.commons.explore.ExploreFragment;
import fr.free.nrw.commons.kvstore.JsonKvStore;
import fr.free.nrw.commons.location.LocationServiceManager;
import fr.free.nrw.commons.media.MediaDetailPagerFragment;
import fr.free.nrw.commons.navtab.MoreBottomSheetFragment;
import fr.free.nrw.commons.navtab.MoreBottomSheetLoggedOutFragment;
import fr.free.nrw.commons.navtab.NavTab;
import fr.free.nrw.commons.navtab.NavTabLayout;
import fr.free.nrw.commons.navtab.NavTabLoggedOut;
import fr.free.nrw.commons.nearby.Place;
import fr.free.nrw.commons.nearby.fragments.NearbyParentFragment;
import fr.free.nrw.commons.nearby.fragments.NearbyParentFragment.NearbyParentFragmentInstanceReadyCallback;
import fr.free.nrw.commons.notification.NotificationActivity;
import fr.free.nrw.commons.notification.NotificationController;
import fr.free.nrw.commons.quiz.QuizChecker;
import fr.free.nrw.commons.settings.SettingsFragment;
import fr.free.nrw.commons.theme.BaseActivity;
import fr.free.nrw.commons.upload.UploadProgressActivity;
import fr.free.nrw.commons.upload.worker.WorkRequestHelper;
import fr.free.nrw.commons.utils.PermissionUtils;
import fr.free.nrw.commons.utils.ViewUtilWrapper;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

public class MainActivity extends BaseActivity
    implements FragmentManager.OnBackStackChangedListener {

    @Inject
    SessionManager sessionManager;
    @Inject
    ContributionController controller;
    @Inject
    ContributionDao contributionDao;

    private ContributionsFragment contributionsFragment;
    private NearbyParentFragment nearbyParentFragment;
    private ExploreFragment exploreFragment;
    private BookmarkFragment bookmarkFragment;
    public ActiveFragment activeFragment;
    private MediaDetailPagerFragment mediaDetailPagerFragment;
    private NavTabLayout.OnNavigationItemSelectedListener navListener;

    @Inject
    public LocationServiceManager locationManager;
    @Inject
    NotificationController notificationController;
    @Inject
    QuizChecker quizChecker;
    @Inject
    @Named("default_preferences")
    public
    JsonKvStore applicationKvStore;
    @Inject
    ViewUtilWrapper viewUtilWrapper;

    public Menu menu;

    public MainBinding binding;

    NavTabLayout tabLayout;


    /**
     * Consumers should be simply using this method to use this activity.
     *
     * @param context A Context of the application package implementing this class.
     */
    public static void startYourself(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (activeFragment == ActiveFragment.CONTRIBUTIONS) {
            if (!contributionsFragment.backButtonClicked()) {
                return false;
            }
        } else {
            onBackPressed();
            showTabs();
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarBinding.toolbar);
        tabLayout = binding.fragmentMainNavTabLayout;
        loadLocale();

        binding.toolbarBinding.toolbar.setNavigationOnClickListener(view -> {
            onSupportNavigateUp();
        });
        /*
        "first_edit_depict" is a key for getting information about opening the depiction editor
        screen for the first time after opening the app.

        Getting true by the key means the depiction editor screen is opened for the first time
        after opening the app.
        Getting false by the key means the depiction editor screen is not opened for the first time
        after opening the app.
         */
        applicationKvStore.putBoolean("first_edit_depict", true);
        if (applicationKvStore.getBoolean("login_skipped") == true) {
            setTitle(getString(R.string.navigation_item_explore));
            setUpLoggedOutPager();
        } else {
            if (applicationKvStore.getBoolean("firstrun", true)) {
                applicationKvStore.putBoolean("hasAlreadyLaunchedBigMultiupload", false);
                applicationKvStore.putBoolean("hasAlreadyLaunchedCategoriesDialog", false);
            }
            if (savedInstanceState == null) {
                //starting a fresh fragment.
                // Open Last opened screen if it is Contributions or Nearby, otherwise Contributions
                if (applicationKvStore.getBoolean("last_opened_nearby")) {
                    setTitle(getString(R.string.nearby_fragment));
                    showNearby();
                    loadFragment(NearbyParentFragment.newInstance(), false);
                } else {
                    setTitle(getString(R.string.contributions_fragment));
                    loadFragment(ContributionsFragment.newInstance(), false);
                }
            }
            setUpPager();
            /**
             * Ask the user for media location access just after login
             * so that location in the EXIF metadata of the images shared by the user
             * is retained on devices running Android 10 or above
             */
//            if (VERSION.SDK_INT >= VERSION_CODES.Q) {
//                ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, 0);
//                PermissionUtils.checkPermissionsAndPerformAction(
//                    this,
//                    () -> {},
//                    R.string.media_location_permission_denied,
//                    R.string.add_location_manually,
//                    permission.ACCESS_MEDIA_LOCATION);
//            }
            checkAndResumeStuckUploads();
        }
    }

    public void setSelectedItemId(int id) {
        binding.fragmentMainNavTabLayout.setSelectedItemId(id);
    }

    private void setUpPager() {
        binding.fragmentMainNavTabLayout.setOnNavigationItemSelectedListener(
            navListener = (item) -> {
                if (!item.getTitle().equals(getString(R.string.more))) {
                    // do not change title for more fragment
                    setTitle(item.getTitle());
                }
                // set last_opened_nearby true if item is nearby screen else set false
                applicationKvStore.putBoolean("last_opened_nearby",
                    item.getTitle().equals(getString(R.string.nearby_fragment)));
                final Fragment fragment = NavTab.of(item.getOrder()).newInstance();
                return loadFragment(fragment, true);
            });
    }

    private void setUpLoggedOutPager() {
        loadFragment(ExploreFragment.newInstance(), false);
        binding.fragmentMainNavTabLayout.setOnNavigationItemSelectedListener(item -> {
            if (!item.getTitle().equals(getString(R.string.more))) {
                // do not change title for more fragment
                setTitle(item.getTitle());
            }
            Fragment fragment = NavTabLoggedOut.of(item.getOrder()).newInstance();
            return loadFragment(fragment, true);
        });
    }

    private boolean loadFragment(Fragment fragment, boolean showBottom) {
        //showBottom so that we do not show the bottom tray again when constructing
        //from the saved instance state.

        freeUpFragments();

        if (fragment instanceof ContributionsFragment) {
            if (activeFragment == ActiveFragment.CONTRIBUTIONS) {
                // scroll to top if already on the Contributions tab
                contributionsFragment.scrollToTop();
                return true;
            }
            contributionsFragment = (ContributionsFragment) fragment;
            activeFragment = ActiveFragment.CONTRIBUTIONS;
        } else if (fragment instanceof NearbyParentFragment) {
            if (activeFragment == ActiveFragment.NEARBY) { // Do nothing if same tab
                return true;
            }
            nearbyParentFragment = (NearbyParentFragment) fragment;
            activeFragment = ActiveFragment.NEARBY;
        } else if (fragment instanceof ExploreFragment) {
            if (activeFragment == ActiveFragment.EXPLORE) { // Do nothing if same tab
                return true;
            }
            exploreFragment = (ExploreFragment) fragment;
            activeFragment = ActiveFragment.EXPLORE;
        } else if (fragment instanceof BookmarkFragment) {
            if (activeFragment == ActiveFragment.BOOKMARK) { // Do nothing if same tab
                return true;
            }
            bookmarkFragment = (BookmarkFragment) fragment;
            activeFragment = ActiveFragment.BOOKMARK;
        } else if (fragment == null && showBottom) {
            if (applicationKvStore.getBoolean("login_skipped")
                == true) { // If logged out, more sheet is different
                MoreBottomSheetLoggedOutFragment bottomSheet = new MoreBottomSheetLoggedOutFragment();
                bottomSheet.show(getSupportFragmentManager(),
                    "MoreBottomSheetLoggedOut");
            } else {
                MoreBottomSheetFragment bottomSheet = new MoreBottomSheetFragment();
                bottomSheet.show(getSupportFragmentManager(),
                    "MoreBottomSheet");
            }
        }

        if (fragment != null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
            return true;
        }
        return false;
    }

    /**
     * loadFragment() overload that supports passing extras to fragments
     **/
    private boolean loadFragment(Fragment fragment, boolean showBottom, Bundle args) {
        if (fragment != null && args != null) {
            fragment.setArguments(args);
        }

        return loadFragment(fragment, showBottom);
    }

    /**
     * Old implementation of loadFragment() was causing memory leaks, due to MainActivity holding
     * references to cleared fragments. This function frees up all fragment references.
     * <p>
     * Called in loadFragment() before doing the actual loading.
     **/
    public void freeUpFragments() {
        // free all fragments except contributionsFragment because several tests depend on it.
        // hence, contributionsFragment is probably still a leak
        nearbyParentFragment = null;
        exploreFragment = null;
        bookmarkFragment = null;
    }

    public void hideTabs() {
        binding.fragmentMainNavTabLayout.setVisibility(View.GONE);
    }

    public void showTabs() {
        binding.fragmentMainNavTabLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Adds number of uploads next to tab text "Contributions" then it will look like "Contributions
     * (NUMBER)"
     *
     * @param uploadCount
     */
    public void setNumOfUploads(int uploadCount) {
        if (activeFragment == ActiveFragment.CONTRIBUTIONS) {
            setTitle(getResources().getString(R.string.contributions_fragment) + " " + (
                !(uploadCount == 0) ?
                    getResources()
                        .getQuantityString(R.plurals.contributions_subtitle,
                            uploadCount, uploadCount)
                    : getString(R.string.contributions_subtitle_zero)));
        }
    }

    /**
     * Resume the uploads that got stuck because of the app being killed or the device being
     * rebooted.
     * <p>
     * When the app is terminated or the device is restarted, contributions remain in the
     * 'STATE_IN_PROGRESS' state. This status persists and doesn't change during these events. So,
     * retrieving contributions labeled as 'STATE_IN_PROGRESS' from the database will provide the
     * list of uploads that appear as stuck on opening the app again
     */
    @SuppressLint("CheckResult")
    private void checkAndResumeStuckUploads() {
        List<Contribution> stuckUploads = contributionDao.getContribution(
                Collections.singletonList(Contribution.STATE_IN_PROGRESS))
            .subscribeOn(Schedulers.io())
            .blockingGet();
        Timber.d("Resuming " + stuckUploads.size() + " uploads...");
        if (!stuckUploads.isEmpty()) {
            for (Contribution contribution : stuckUploads) {
                contribution.setState(Contribution.STATE_QUEUED);
                contribution.setDateUploadStarted(Calendar.getInstance().getTime());
                Completable.fromAction(() -> contributionDao.saveSynchronous(contribution))
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            }
            WorkRequestHelper.Companion.makeOneTimeWorkRequest(
                this, ExistingWorkPolicy.APPEND_OR_REPLACE);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //quizChecker.initQuizCheck(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("viewPagerCurrentItem", binding.pager.getCurrentItem());
        outState.putString("activeFragment", activeFragment.name());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String activeFragmentName = savedInstanceState.getString("activeFragment");
        if (activeFragmentName != null) {
            restoreActiveFragment(activeFragmentName);
        }
    }

    private void restoreActiveFragment(@NonNull String fragmentName) {
        if (fragmentName.equals(ActiveFragment.CONTRIBUTIONS.name())) {
            setTitle(getString(R.string.contributions_fragment));
            loadFragment(ContributionsFragment.newInstance(), false);
        } else if (fragmentName.equals(ActiveFragment.NEARBY.name())) {
            setTitle(getString(R.string.nearby_fragment));
            loadFragment(NearbyParentFragment.newInstance(), false);
        } else if (fragmentName.equals(ActiveFragment.EXPLORE.name())) {
            setTitle(getString(R.string.navigation_item_explore));
            loadFragment(ExploreFragment.newInstance(), false);
        } else if (fragmentName.equals(ActiveFragment.BOOKMARK.name())) {
            setTitle(getString(R.string.bookmarks));
            loadFragment(BookmarkFragment.newInstance(), false);
        }
    }

    @Override
    public void onBackPressed() {
        if (contributionsFragment != null && activeFragment == ActiveFragment.CONTRIBUTIONS) {
            // Means that contribution fragment is visible
            if (!contributionsFragment.backButtonClicked()) {//If this one does not wan't to handle
                // the back press, let the activity do so
                super.onBackPressed();
            }
        } else if (nearbyParentFragment != null && activeFragment == ActiveFragment.NEARBY) {
            // Means that nearby fragment is visible
            /* If function nearbyParentFragment.backButtonClick() returns false, it means that the bottomsheet is
              not expanded. So if the back button is pressed, then go back to the Contributions tab */
            if (!nearbyParentFragment.backButtonClicked()) {
                getSupportFragmentManager().beginTransaction().remove(nearbyParentFragment)
                    .commit();
                setSelectedItemId(NavTab.CONTRIBUTIONS.code());
            }
        } else if (exploreFragment != null && activeFragment == ActiveFragment.EXPLORE) {
            // Means that explore fragment is visible
            if (!exploreFragment.onBackPressed()) {
                if (applicationKvStore.getBoolean("login_skipped")) {
                    super.onBackPressed();
                } else {
                    setSelectedItemId(NavTab.CONTRIBUTIONS.code());
                }
            }
        } else if (bookmarkFragment != null && activeFragment == ActiveFragment.BOOKMARK) {
            // Means that bookmark fragment is visible
            bookmarkFragment.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackStackChanged() {
        //initBackButton();
    }

    /**
     * Retry all failed uploads as soon as the user returns to the app
     */
    @SuppressLint("CheckResult")
    private void retryAllFailedUploads() {
        contributionDao.
            getContribution(Collections.singletonList(Contribution.STATE_FAILED))
            .subscribeOn(Schedulers.io())
            .subscribe(failedUploads -> {
                for (Contribution contribution : failedUploads) {
                    contributionsFragment.retryUpload(contribution);
                }
            });
    }

    /**
     * Handles item selection in the options menu. This method is called when a user interacts with
     * the options menu in the Top Bar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_tab:
                startActivity(new Intent(this, UploadProgressActivity.class));
                return true;
            case R.id.notifications:
                // Starts notification activity on click to notification icon
                NotificationActivity.Companion.startYourself(this, "unread");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void centerMapToPlace(Place place) {
        setSelectedItemId(NavTab.NEARBY.code());
        nearbyParentFragment.setNearbyParentFragmentInstanceReadyCallback(
            new NearbyParentFragmentInstanceReadyCallback() {
                @Override
                public void onReady() {
                    nearbyParentFragment.centerMapToPlace(place);
                }
            });
    }

    /**
     * Launch the Explore fragment from Nearby fragment. This method is called when a user clicks
     * the 'Show in Explore' option in the 3-dots menu in Nearby.
     *
     * @param zoom      current zoom of Nearby map
     * @param latitude  current latitude of Nearby map
     * @param longitude current longitude of Nearby map
     **/
    public void loadExploreMapFromNearby(double zoom, double latitude, double longitude) {
        Bundle bundle = new Bundle();
        bundle.putDouble("prev_zoom", zoom);
        bundle.putDouble("prev_latitude", latitude);
        bundle.putDouble("prev_longitude", longitude);

        loadFragment(ExploreFragment.newInstance(), false, bundle);
        setSelectedItemId(NavTab.EXPLORE.code());
    }

    /**
     * Launch the Nearby fragment from Explore fragment. This method is called when a user clicks
     * the 'Show in Nearby' option in the 3-dots menu in Explore.
     *
     * @param zoom      current zoom of Explore map
     * @param latitude  current latitude of Explore map
     * @param longitude current longitude of Explore map
     **/
    public void loadNearbyMapFromExplore(double zoom, double latitude, double longitude) {
        Bundle bundle = new Bundle();
        bundle.putDouble("prev_zoom", zoom);
        bundle.putDouble("prev_latitude", latitude);
        bundle.putDouble("prev_longitude", longitude);

        loadFragment(NearbyParentFragment.newInstance(), false, bundle);
        setSelectedItemId(NavTab.NEARBY.code());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ((applicationKvStore.getBoolean("firstrun", true)) &&
            (!applicationKvStore.getBoolean("login_skipped"))) {
            defaultKvStore.putBoolean("inAppCameraFirstRun", true);
            WelcomeActivity.startYourself(this);
        }

        retryAllFailedUploads();
    }

    @Override
    protected void onDestroy() {
        quizChecker.cleanup();
        locationManager.unregisterLocationManager();
        // Remove ourself from hashmap to prevent memory leaks
        locationManager = null;
        super.onDestroy();
    }

    /**
     * Public method to show nearby from the reference of this.
     */
    public void showNearby() {
        binding.fragmentMainNavTabLayout.setSelectedItemId(NavTab.NEARBY.code());
    }

    public enum ActiveFragment {
        CONTRIBUTIONS,
        NEARBY,
        EXPLORE,
        BOOKMARK,
        MORE
    }

    /**
     * Load default language in onCreate from SharedPreferences
     */
    private void loadLocale() {
        final SharedPreferences preferences = getSharedPreferences("Settings",
            Activity.MODE_PRIVATE);
        final String language = preferences.getString("language", "");
        final SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setLocale(this, language);
    }

    public NavTabLayout.OnNavigationItemSelectedListener getNavListener() {
        return navListener;
    }
}
