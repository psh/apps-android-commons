package fr.free.nrw.commons.upload.queue;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class UploadQueueModule {
    @Binds
    public abstract UploadQueueContract.Presenter providePresenter(UploadQueuePresenter presenter);
}
