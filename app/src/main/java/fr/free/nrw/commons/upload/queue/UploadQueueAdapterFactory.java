package fr.free.nrw.commons.upload.queue;

import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.Collections;
import java.util.List;

import fr.free.nrw.commons.contributions.Contribution;

class UploadQueueAdapterFactory {

    public RVRendererAdapter<Contribution> create(List<Contribution> notifications) {
        RendererBuilder<Contribution> builder = new RendererBuilder<Contribution>()
                .bind(Contribution.class, new UploadRenderer());
        ListAdapteeCollection<Contribution> collection = new ListAdapteeCollection<>(
                notifications != null ? notifications : Collections.emptyList());
        return new RVRendererAdapter<>(builder, collection);
    }
}
