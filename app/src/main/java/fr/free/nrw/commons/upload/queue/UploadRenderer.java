package fr.free.nrw.commons.upload.queue;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pedrogomez.renderers.Renderer;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.free.nrw.commons.R;
import fr.free.nrw.commons.contributions.Contribution;

public class UploadRenderer extends Renderer<Contribution> {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.sub_title)
    TextView subTitle;
    @BindView(R.id.status_icon)
    ImageView statusIcon;
    @BindView(R.id.preview_image)
    SimpleDraweeView previewImage;

    @Override
    protected View inflate(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return layoutInflater.inflate(R.layout.upload_queue_card, viewGroup, false);
    }

    @Override
    protected void setUpView(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected void hookListeners(View view) {

    }

    @Override
    public void render() {
        Contribution content = getContent();
        title.setText(content.getDisplayTitle());
        subTitle.setText(String.valueOf(content.getDateCreated()));
        statusIcon.setVisibility(content.getState() == Contribution.STATE_FAILED ? View.VISIBLE : View.INVISIBLE);
        previewImage.setImageURI(content.getLocalUri());

        Log.e("Commons", "@@@ Uri = "+content.getLocalUri());
    }
}
