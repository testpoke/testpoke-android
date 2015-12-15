package com.testpoke.core.analytics;

import android.content.Context;
import com.testpoke.core.schedule.Recurrent;
import com.testpoke.core.schedule.Task;
import com.testpoke.core.util.Tasks;
import com.testpoke.core.util.log.TP;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jansel Valentin on 12/25/14.
 */
final class MemoryMonitor extends Task implements Recurrent {


    private static final long RECURRENT_INTERVAL = 1000;

    private static final int MAX_WORKER_IDLE_EXECUTIONS = 10; // 6 request/min

    private static final float MEGABYTES = 1024 * 1024;

    private final Runtime runtime = Runtime.getRuntime();
    private int currentExecution = 1;

    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private Context context;

    private List<float[]> collected = new ArrayList<float[]>();


    public MemoryMonitor(Context context){
        this.context = context;
    }


    @Override
    public int getId() {
        return Tasks.MEMORY_MONITOR;
    }

    @Override
    protected void performTask() {
        float[] stat = dumpUsage();

        collected.add(stat);

        if( MAX_WORKER_IDLE_EXECUTIONS == currentExecution++ ){
            List<float[]> copy = new ArrayList<float[]>(collected);

            executor.execute(new MemoryPostWorker(copy));

            collected.clear();
            currentExecution = 1;
        }
    }

    @Override
    public long getRecurrentInterval() {
        return RECURRENT_INTERVAL;
    }

    private float[] dumpUsage() {
        return new float[]{
                runtime.maxMemory() / MEGABYTES,
                runtime.freeMemory() / MEGABYTES,
                runtime.totalMemory() / MEGABYTES
        };
    }


    private final class MemoryPostWorker implements Runnable{

        private final int MAX_RETRY_INTENT = 2;
        private int currentRetry = 1;
        private boolean isDone;

        private List<float[]> stats;

        MemoryPostWorker(List<float[]> stats ){
            this.stats = stats;

        }

        @Override
        public void run() {
//            if (NetworkHelper.isNetworkReady(context) && NetworkHelper.isWifi(context)) {
                while (MAX_RETRY_INTENT != currentRetry && !isDone ) {
                    isDone = true;
                    for( float[] stat : stats ){
                        TP.e("max: " + stat[0] + ", free: " + stat[1] + ", total: " + stat[2]);
                    }
//                    boolean isRetrying = false;
//                    try {
//                        HttpResponse response = null;//HttpRequest.executeHttpPost(endpoint, json.getBytes(charset), a);
//                        int status = response.getStatusLine().getStatusCode();
//                        if (200 == status) {
//                            /**
//                             * Memory stat was sent, cleaning not active session.
//                             */
//                            isDone = true;
//                        } else {
//                            ML.w("Error sending memory stat, attempt #" + (++currentRetry) + ",retrying in 2 seconds");
//                        }
//
//                    } catch (Exception ex) {
//                        isRetrying = true;
//                        ML.w("Error sending memory stat, attempt #" + (++currentRetry) + ",retrying in 2 seconds");
//                    }
//                    if (isRetrying) {
//                        try {
//                            TimeUnit.SECONDS.sleep(2);
//                        } catch (InterruptedException ex) {
//                            Dump.printStackTraceCause(ex);
//                        }
//                    }
                }
//            } else {
//                ML.w("Skip send memory stat, device is not connected to network or not connected to wifi");
//            }
        }
    }

}
