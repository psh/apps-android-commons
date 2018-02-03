package fr.free.nrw.commons.upload.queue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.theme.NavigationBaseActivity;

public class UploadQueueActivity extends NavigationBaseActivity implements UploadQueueContract.View {

    @Inject
    UploadQueuePresenter presenter;
    @BindView(R.id.message_banner)
    TextView uploadBanner;

    public static void startYourself(Context context) {
        context.startActivity(new Intent(context, UploadQueueActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_queue);
        ButterKnife.bind(this);
        initDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.start(this);
    }

    @Override
    protected void onPause() {
        presenter.stop();
        super.onPause();
    }

    @Override
    public void showConnectionLost() {
        uploadBanner.setVisibility(View.VISIBLE);
        uploadBanner.setText(R.string.connection_lost);
    }

    @Override
    public void showNonWifi() {
        uploadBanner.setVisibility(View.VISIBLE);
        uploadBanner.setText(R.string.waiting_for_wifi);
    }

    @Override
    public void hideConnectionBanner() {
        uploadBanner.setVisibility(View.GONE);
    }
}
