package com.testpoke.core.content;

import android.content.Context;
import com.testpoke.core.Injectable;
import com.testpoke.core.content.policy.DowngradePolicy;
import com.testpoke.core.content.policy.UpgradePolicy;

/*
 * Created by Jansel Valentin on 5/3/14.
 */
final class PersistenceContext {

    private UpgradePolicy<Context> upgradePolicy;

    private DowngradePolicy<Context> downgradePolicy;

    private final int databaseVersion  = 20;

    private final String databaseName  = String.valueOf("tp.sqlite".hashCode());

    void setUpgradePolicy(Injectable<UpgradePolicy<Context>> injectable) {
        upgradePolicy = IdentityStandardPolicyInjector.getUpgradeVersionImp(injectable).get();
    }

    void setDowngradePolicy(Injectable<DowngradePolicy<Context>> injectable) {
        downgradePolicy = IdentityStandardPolicyInjector.getDowngradeVersionImp(injectable).get();
    }

    public DowngradePolicy<Context> getDowngradePolicy() {
        if( null == downgradePolicy )
            IdentityStandardPolicyInjector.injectAvailablePolicies(this);

        return downgradePolicy;
    }

    public UpgradePolicy<Context> getUpgradePolicy() {
        if( null == upgradePolicy )
            IdentityStandardPolicyInjector.injectAvailablePolicies(this);

        return upgradePolicy;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}

