package com.testpoke.core.analytics;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import com.testpoke.core.content.$_V;
import com.testpoke.core.content.PersistenceProvider;
import com.testpoke.core.content.PersistenceResolver;
import com.testpoke.core.schedule.SchedulerService;
import com.testpoke.core.schedule.event.TaskEvent;
import com.testpoke.core.util.Tasks;
import com.testpoke.core.util.log.TP;

/*
 * Created by Jansel Valentin on 6/9/2014.
 */
public final class CloseOldestSession extends TaskEvent {

    public CloseOldestSession(SchedulerService scheduler, Context context) {
        super(scheduler, context);
    }

    @Override
    public int getId() {
        return Tasks.CLOSE_OLDEST_SESSION;
    }

    @Override
    protected void performTask() {
        Latest oldest = Latest.load(getContext());

        if (TextUtils.isEmpty(oldest.uuid) || !oldest.handled )
            return;

        TP.i("TestPoke is closing oldest unclosed session "+oldest.uuid);

        PersistenceProvider provider = PersistenceProvider.getDefault(getContext());
        PersistenceResolver resolver = provider.getResolver();

        ContentValues values = new ContentValues();
        values.put("end_reason", OCReason.REMOVED_FROM_RECENTS);
        values.put("end", oldest.time);
        values.put("handled", oldest.handled ? 1 : 0);

        resolver.update($_V.V1.s, values, "uuid='" + oldest.uuid + "' AND end IS NULL AND end_reason IS NULL", null);
        values.clear();
    }
}
