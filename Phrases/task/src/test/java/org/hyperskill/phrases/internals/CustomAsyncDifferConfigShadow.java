package org.hyperskill.phrases.internals;

import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.AsyncDifferConfig;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.util.concurrent.Executor;

// version 1.1
@Implements(AsyncDifferConfig.class)
public class CustomAsyncDifferConfigShadow {

    public static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable r) {
            handler.post(r);
        }
    }
    Executor mainExecutor;

    @Implementation
    public Executor getBackgroundThreadExecutor() {
        if(mainExecutor == null) {
            mainExecutor = new MainThreadExecutor();
        }
        return mainExecutor;
    }
}
