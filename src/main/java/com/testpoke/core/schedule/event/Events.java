package com.testpoke.core.schedule.event;

import android.content.Context;
import com.testpoke.core.schedule.SchedulerService;
import com.testpoke.core.util.Dump;
import com.testpoke.core.util.log.TP;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/*
 * Created by Jansel Valentin on 5/23/14.
 */
public final class Events {

    public static <T extends TaskEvent> T prepare(Class<T> event, SchedulerService scheduler, Context context ) {
        if (null == event)
            return null;
        T instance = null;
        try {
            Constructor<T> constructor = event.getConstructor(SchedulerService.class, Context.class);
            instance = constructor.newInstance(scheduler, context);

        } catch (NoSuchMethodException ex) {
            TP.e("Imposible fire " + event + " task event");
            Dump.printStackTraceCause(ex);
        } catch (IllegalAccessException ex) {
            TP.e("Imposible fire " + event + " task event");
            Dump.printStackTraceCause(ex);
        } catch (InstantiationException ex) {
            TP.e("Imposible fire " + event + " task event");
            Dump.printStackTraceCause(ex);
        } catch (InvocationTargetException ex) {
            TP.e("Imposible fire " + event + " task event");
            Dump.printStackTraceCause(ex);
        }
        return instance;
    }
}
