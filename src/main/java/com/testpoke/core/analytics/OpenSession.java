package com.testpoke.core.analytics;

import android.content.ContentValues;
import android.content.Context;
import com.testpoke.TestPoke;
import com.testpoke.core.content.$_V;
import com.testpoke.core.content.PersistenceProvider;
import com.testpoke.core.content.PersistenceResolver;
import com.testpoke.core.ia.Constants;
import com.testpoke.core.schedule.SchedulerService;
import com.testpoke.core.schedule.event.TaskEvent;
import com.testpoke.core.util.Tasks;
import com.testpoke.core.util.log.TP;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public final class OpenSession extends TaskEvent implements SessionImp.StateReporter {

    private int startReason = OCReason.FOREGROUND;

    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public OpenSession(SchedulerService scheduler, Context context) {
        super(scheduler, context);
    }

    @Override
    public int getId() {
        return Tasks.OPEN_SESSION;
    }

    @Override
    protected void performTask() {
        SessionImp active = (SessionImp) P.p().getActive(getContext());
        if (null == active)
            return;

        TP.d("TestPoke is opening new session");

        PersistenceResolver resolver = PersistenceProvider.getDefault(getContext()).getResolver();

        String openTime;
        try{
            openTime = format.format(active.getOpenTime());
        }catch( Exception ex){
            openTime = active.getOpenTime().toString();
        }

        ContentValues values = new ContentValues();
        values.put("uuid", IA.k().uuid());
        values.put("start", openTime);
        values.put("start_reason", startReason);
        values.put("handled", TestPoke.getSettings().getOptions().isSessionAutoHandled() ? 1 : 0);
        values.put("zone", TimeZone.getDefault().getID());
        values.put(Constants._ade677c68, IA.k()._ade677c68());
        values.put(Constants._ba8868af2, IA.k()._ba8868af2());

        resolver.updateOrInsert($_V.V1.s, Constants._ade677c68,values,"uuid='" + IA.k().uuid() + "'",null);
        values.clear();

        active.reportState(this);
    }

    public int getStartReason() {
        return startReason;
    }

    public void setStartReason( int startReason) {
        this.startReason = startReason;
    }

    @Override
    public int reportedState() {
        return null == IA.k()._ade677c68() ? SessionImp.STATE_WEAK : SessionImp.STATE_OPEN;
    }
}


