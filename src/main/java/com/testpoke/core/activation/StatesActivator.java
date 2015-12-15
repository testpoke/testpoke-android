package com.testpoke.core.activation;

import android.content.Context;
import com.testpoke.core.util.log.TP;

import java.lang.ref.WeakReference;


/*
 * Created by Jansel Valentin on 5/4/14.
 */
public final class StatesActivator {
    private static WeakReference<SelfContainedActivator> activators;

    static {
        activators = new WeakReference<SelfContainedActivator>(SelfContainedActivator.prepare());
    }

    public static void activate(Context app) {
        if (null == activators)
            return;

        SelfContainedActivator mActivators = activators.get();

        if (null == mActivators) {
            TP.d("Could not possible to get SelfContainedActivator instance");
            return;
        }
        ActivationToken token = new NaiveActivationToken();

            TP.i("Activating TestPoke modules...");

        for (Activator activator : mActivators) {
            activator.activate(app, token);
        }
        mActivators.clear();
        activators.clear();
        activators = null;
        token = null;
    }

    public static void checkToken(ActivationToken token) {
        if (!(token instanceof NaiveActivationToken))
            throw new IllegalArgumentException("Invalid Activation Token");
    }

    private static class NaiveActivationToken implements ActivationToken {
    }
}
