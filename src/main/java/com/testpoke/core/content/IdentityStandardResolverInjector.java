package com.testpoke.core.content;

import com.testpoke.core.Injectable;
import com.testpoke.core.util.Objects;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
final class IdentityStandardResolverInjector {
    private IdentityStandardResolverInjector() {
    }


    static Injectable<PersistenceResolver> getResolverImp(Injectable<PersistenceResolver> inj) {
        Objects.requireNonNull(inj, "Persistence Resolver Configuration Error");
        if (inj instanceof IdentityInjector)
            return inj;
        return null;
    }

    public static void injectResolver(PersistenceProvider provider) {
        IdentityInjector.INJECTOR.setProvider(provider);
        provider.setResolver(IdentityInjector.INJECTOR);
    }

    private static final class IdentityInjector implements Injectable<PersistenceResolver> {
        static final IdentityInjector INJECTOR = new IdentityInjector();
        private PersistenceProvider provider;

        private IdentityInjector(){}

        void setProvider( PersistenceProvider provider ){
            this.provider = provider;
        }

        @Override
        public PersistenceResolver get() {
            return new SQLPersistenceResolver(provider.getOpenHelper());
        }
    }
}
