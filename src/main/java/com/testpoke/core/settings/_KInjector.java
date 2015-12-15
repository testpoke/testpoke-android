package com.testpoke.core.settings;

import com.testpoke.core.Injectable;
import com.testpoke.core.analytics._K;
import com.testpoke.core.util.Objects;

/*
 * Created by Jansel ValentinJansel R. Abreu (jrodr) on 5/31/2014.
 */
public final class _KInjector {

    static void propagate( _K k ){
        _K.i(new NaiveKInjector(k));
    }

    public static Injectable<? extends _K> getInjector( Injectable<? extends _K> injectable ){
        Objects.requireNonNull(injectable, "Not valid dependencies injector");
        if( injectable instanceof NaiveKInjector )
            return injectable;
        return null;
    }

    private static class NaiveKInjector implements Injectable<_K> {
        private _K k;
        private NaiveKInjector(_K k ){
            this.k = k;
        }
        @Override
        public _K get() {
            return k;
        }
    }
}
