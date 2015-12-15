package com.testpoke.core.content;

import android.content.Context;
import com.testpoke.core.Injectable;
import com.testpoke.core.content.policy.AutoPolicy;
import com.testpoke.core.content.policy.DowngradePolicy;
import com.testpoke.core.content.policy.UpgradePolicy;
import com.testpoke.core.util.Objects;

/*
 * Created by Jansel Valentin on 5/3/14.
 */
final class IdentityStandardPolicyInjector {


    static Injectable<DowngradePolicy<Context>> getDowngradeVersionImp(Injectable<DowngradePolicy<Context>> injectable) {
        Objects.requireNonNull(injectable, "Version Configuration Error");
        if (injectable instanceof IdentityDowngradeVersionInjectable)
            return injectable;
        return null;
    }


    static Injectable<UpgradePolicy<Context>> getUpgradeVersionImp(Injectable<UpgradePolicy<Context>> injectable) {
        Objects.requireNonNull(injectable, "Version Configuration Error");
        if (injectable instanceof IdentityUpgradeVersionInjectable)
            return injectable;
        return null;
    }



    static void injectAvailablePolicies(PersistenceContext context) {
        context.setDowngradePolicy(IdentityDowngradeVersionInjectable.INJECTOR);
        context.setUpgradePolicy(IdentityUpgradeVersionInjectable.INJECTOR);
    }


    private static final class IdentityDowngradeVersionInjectable implements Injectable<DowngradePolicy<Context>> {
        static final Injectable<DowngradePolicy<Context>> INJECTOR = new IdentityDowngradeVersionInjectable();
        @Override
        public DowngradePolicy<Context> get() {
            return PolicyHolder.defaultPolicy;
        }
    }


    private static final class IdentityUpgradeVersionInjectable implements Injectable<UpgradePolicy<Context>> {
        static final Injectable<UpgradePolicy<Context>> INJECTOR = new IdentityUpgradeVersionInjectable();

        @Override
        public UpgradePolicy<Context> get() {
            return PolicyHolder.defaultPolicy;
        }
    }

    private static class PolicyHolder{
        static final AutoPolicy defaultPolicy = new AutoPolicy();
    }
}
