package fr.free.nrw.commons.upload;

import android.net.Uri;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.reflect.Proxy;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public interface UploadView {
    // Dummy implementation of the view interface to allow us to have a 'null object pattern'
    // in the presenter and avoid constant NULL checking.
    UploadView DUMMY = (UploadView) Proxy.newProxyInstance(UploadView.class.getClassLoader(),
            new Class[]{UploadView.class}, (proxy, method, methodArgs) -> null);

    @Retention(SOURCE)
    @IntDef({TITLE_CARD, CATEGORIES, LICENSE})
    @interface UploadPage {}

    int TITLE_CARD = 0;
    int CATEGORIES = 1;
    int LICENSE = 2;

    void updateThumbnails(List<UploadModel.UploadItem> uploads);

    void setNextEnabled(boolean available);

    void setSubmitEnabled(boolean available);

    void setPreviousEnabled(boolean available);

    void setTopCardState(boolean state);

    void setBottomCardState(boolean state);

    void setBackground(Uri mediaUri);

    void setTopCardVisibility(boolean visible);

    void setBottomCardVisibility(@UploadPage int page);

    void updateBottomCardContent(int currentStep, int stepCount, UploadModel.UploadItem uploadItem);

    void updateLicenses(List<String> licenses, String selectedLicense);

    void updateLicenseSummary(String selectedLicense);

    void updateTopCardContent();

    void dismissKeyboard();
}