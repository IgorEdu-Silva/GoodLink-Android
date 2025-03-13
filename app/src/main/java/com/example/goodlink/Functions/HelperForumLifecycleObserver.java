package com.example.goodlink.Functions;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class HelperForumLifecycleObserver implements LifecycleObserver {
    private boolean telaAtiva = true;

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onEnterForeground() {
        telaAtiva = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onEnterBackground() {
        telaAtiva = false;
    }

    public boolean isTelaAtiva() {
        return telaAtiva;
    }
}
