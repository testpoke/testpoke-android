package com.testpoke.core.analytics;

import com.testpoke.core.Injectable;
import com.testpoke.core.settings._KInjector;

/**
 * Created by Jansel Valentin on 5/31/2014.
 */

public abstract class _K {
    private static _K k;

    protected _K() {
    }
    public static _K k() {
        return k;
    }

    public final static void i(Injectable<? extends _K> injector) {
        if (null != k)
            return;
        k = _KInjector.getInjector(injector).get();
    }

    public abstract String getViewClass(String name);
}
