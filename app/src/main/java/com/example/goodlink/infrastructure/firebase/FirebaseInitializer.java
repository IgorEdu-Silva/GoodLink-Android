// src/main/java/com/example/goodlink/infrastructure/firebase/FirebaseInitializer.java
package com.example.goodlink.infrastructure.firebase;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

public final class FirebaseInitializer {
    private FirebaseInitializer() {}

    public static void init(Application app) {
        FirebaseApp.initializeApp(app);
        FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(
                        PlayIntegrityAppCheckProviderFactory.getInstance());
    }
}