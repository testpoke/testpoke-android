package com.testpoke.core.content;

import android.content.Context;
import com.testpoke.core.activation.ActivationToken;
import com.testpoke.core.activation.StatesActivator;

/*
 * Created by Jansel Valentin on 5/3/14.
 */
public class Activator implements com.testpoke.core.activation.Activator {
    @Override
    public void activate(Context app, ActivationToken token) {
        StatesActivator.checkToken(token);
    }
}
