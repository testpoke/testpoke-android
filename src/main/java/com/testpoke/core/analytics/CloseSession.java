package com.testpoke.core.analytics;

import android.content.ContentValues;
import android.content.Context;
import com.testpoke.core.content.$_V;
import com.testpoke.core.content.PersistenceProvider;
import com.testpoke.core.content.PersistenceResolver;
import com.testpoke.core.ia.Constants;
import com.testpoke.core.schedule.SchedulerService;
import com.testpoke.core.schedule.event.TaskEvent;
import com.testpoke.core.util.Tasks;
import com.testpoke.core.util.log.TP;

import java.text.SimpleDateFormat;

/**
 * Created by Jansel Valentin on 5/23/14.
 */
public final class CloseSession extends TaskEvent implements SessionImp.StateReporter{

    private int endReason = OCReason.FOREGROUND;

    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public CloseSession(SchedulerService scheduler, Context context) {
        super(scheduler, context);
    }

    @Override
    public int getId() {
        return Tasks.CLOSE_SESSION;
    }

    @Override
    protected void performTask() {

        SessionImp active = (SessionImp) P.p().getActive(getContext());
        if( null == active )
            return;

        if( SessionImp.STATE_NEW == active.getState() || SessionImp.STATE_CLOSED == active.getState() || active.wasCrashed()) {
            return;
        }

        TP.d("TestPoke is closing active session");

        if( null != IA.k() ){
            active.touchCloseTime();

            final String uuid = IA.k().uuid();

            PersistenceResolver resolver = PersistenceProvider.getDefault(getContext()).getResolver();

            ContentValues values = new ContentValues();
            values.put("end_reason", endReason);

            String closeTime;
            try{
                closeTime = format.format(active.getCloseTime());
            }catch ( Exception e){
                closeTime = active.getCloseTime().toString();
            }

            values.put("end", closeTime);

            resolver.updateOrInsert($_V.V1.s, Constants._ade677c68,values,"uuid='" + uuid + "'",null);

            values.clear();
            IA.k().reload();

            /**
             * Deprecated by issue #1
             */
//            tryToFireLogcatCrawler(uuid);
            dispatchSession();

        }
        active.reportState(this);
    }

    @Deprecated
    private void tryToFireLogcatCrawler(String uuid){

//        if(TestPoke.getSettings().getOptions().reportLogcatAlerts()){
//            LogcatCrawler crawler = LogcatCrawler.getInstance(getContext());
//            crawler.setReleatedSession(uuid);
//
//            getScheduler().scheduleImmediate( crawler );
//        }
    }

    void dispatchSession(){
        InlineThread it = InlineThread.getSynInstance(getContext());
        if( null != it && null != IA.k()._ade677c68() ) {
            it.getDispatcher().dispatch();
        }
    }

    void setEndReason(int endReason) {
        this.endReason = endReason;
    }


    @Override
    public int reportedState() {
        return SessionImp.STATE_CLOSED;
    }
}
