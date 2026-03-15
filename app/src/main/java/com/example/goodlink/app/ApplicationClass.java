package com.example.goodlink.app;

import android.app.Application;
import android.content.Context;

import com.example.goodlink.core.domain.settings.FontSettingsGateway;
import com.example.goodlink.core.domain.usecase.GetFontSizeUseCase;
import com.example.goodlink.infrastructure.firebase.FirebaseInitializer;
import com.example.goodlink.infrastructure.session.FontSettingsRepositoryImplementation;
import com.example.goodlink.infrastructure.ui.font.FontScaleApplier;

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseInitializer.init(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        float size = 14f;

        try {
            FontSettingsGateway gateway = new FontSettingsRepositoryImplementation(base);
            size = new GetFontSizeUseCase(gateway).execute();
        } catch (Exception ignored) {

        }

        super.attachBaseContext(FontScaleApplier.wrap(base, size));
    }

}