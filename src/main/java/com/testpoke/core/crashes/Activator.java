package com.testpoke.core.crashes;

import android.content.Context;
import com.testpoke.core.activation.ActivationToken;
import com.testpoke.core.activation.StatesActivator;

/*
 * Created by Jansel Valentin on 5/5/14.
 */
public final class Activator implements com.testpoke.core.activation.Activator {
    @Override
    public void activate(Context app, ActivationToken token) {
        StatesActivator.checkToken(token);

        UncaughtHandler<AfterCrashData> handler = new UncaughtHandler<AfterCrashData>(new FieldStatesProvider(app));
        handler.registerReceiver(new DefaultCrashDataReceiver(app));
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}
