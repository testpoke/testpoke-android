package com.testpoke.core.content;

import android.content.Context;
import com.testpoke.core.Injectable;

/*
 * Created by Jansel Valentin on 5/3/14.
 */
public final class PersistenceProvider {

    private static PersistenceProvider INSTANCE;

    private Context androidContext;
    private PersistenceContext persistenceContext;
    private DatabaseHandler handler;
    private SQLiteOpenHelper openHelper;
    private PersistenceResolver resolver;

    private PersistenceProvider(Context androidContext) {
        this.androidContext = androidContext;
        initialize();
    }

    public static PersistenceProvider getDefault(Context androidContext) {
        if (null != INSTANCE)
            return INSTANCE;
        synchronized (PersistenceProvider.class) {
            if (null == INSTANCE)
                INSTANCE = new PersistenceProvider(androidContext);
        }
        return INSTANCE;
    }

    private void initialize() {
        persistenceContext = new PersistenceContext();
    }

    public Context getAndroidContext() {
        return androidContext;
    }

    PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }

    DatabaseHandler getHandler() {
        return null == handler ? handler = new DatabaseOpenHandler(persistenceContext, androidContext) : handler;
    }

    SQLiteOpenHelper getOpenHelper() {
        return null == openHelper ? openHelper = new SQLiteOpenHelper(this) : openHelper;
    }

    void setResolver(Injectable<PersistenceResolver> injectable) {
        this.resolver = IdentityStandardResolverInjector.getResolverImp( injectable ).get();
    }

    public PersistenceResolver getResolver() {
        if( null == resolver )
            IdentityStandardResolverInjector.injectResolver(this);
        return resolver;
    }
}
