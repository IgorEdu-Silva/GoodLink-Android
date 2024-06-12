package com.example.goodlink.Utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyboardUtils {

    public interface KeyboardVisibilityListener {
        void onKeyboardVisibilityChanged(boolean isVisible, int keyboardHeight);
    }

    public static void setKeyboardVisibilityListener(Activity activity, final KeyboardVisibilityListener listener) {
        final View activityRootView = activity.findViewById(android.R.id.content);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean wasOpened;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);

                int screenHeight = activityRootView.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);

                boolean isOpen = heightDifference > screenHeight / 3;

                if (isOpen == wasOpened) {
                    return;
                }

                wasOpened = isOpen;
                if (isOpen) {
                    listener.onKeyboardVisibilityChanged(true, heightDifference);
                } else {
                    listener.onKeyboardVisibilityChanged(false, 0);
                }
            }
        });
    }
}
