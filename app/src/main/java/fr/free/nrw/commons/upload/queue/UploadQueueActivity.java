package fr.free.nrw.commons.upload.queue;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pedrogomez.renderers.RVRendererAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.HandlerService;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.contributions.Contribution;
import fr.free.nrw.commons.theme.NavigationBaseActivity;
import fr.free.nrw.commons.upload.UploadService;

public class UploadQueueActivity extends NavigationBaseActivity implements UploadQueueContract.View {

    @Inject
    UploadQueuePresenter presenter;
    @BindView(R.id.message_banner)
    TextView uploadBanner;
    @BindView(R.id.upload_queue_list)
    RecyclerView recyclerView;

    private ServiceConnection uploadServiceConnection;

    public static void startYourself(Context context) {
        context.startActivity(new Intent(context, UploadQueueActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_queue);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initDrawer();
        bindService();
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
    protected void onDestroy() {
        unbindService(uploadServiceConnection);
        super.onDestroy();
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

    @Override
    public void displayContributions(List<Contribution> contributions) {
        UploadQueueAdapterFactory notificationAdapterFactory = new UploadQueueAdapterFactory();
        RVRendererAdapter<Contribution> adapter = notificationAdapterFactory.create(contributions);
        recyclerView.setAdapter(adapter);
    }

    private void bindService() {
        Intent uploadServiceIntent = new Intent(this, UploadService.class);
        uploadServiceIntent.setAction(UploadService.ACTION_START_SERVICE);
        startService(uploadServiceIntent);

        uploadServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                HandlerService handlerService = ((HandlerService.HandlerServiceLocalBinder) service).getService();
                presenter.setUploadService((UploadService) handlerService);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(uploadServiceIntent, uploadServiceConnection, Context.BIND_AUTO_CREATE);
    }

}
