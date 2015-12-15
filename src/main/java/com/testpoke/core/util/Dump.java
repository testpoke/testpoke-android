package com.testpoke.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/*
 * Created by Jansel Valentin on 5/24/14.
 */

public final class Dump {
    public static final void printStackTraceCause(Throwable tr){
        tr.printStackTrace();
    }

    public static String getStacktraceLastThrowableCause( Throwable tr ){

        Writer swriter = new StringWriter();
        PrintWriter writer = new PrintWriter(swriter);

        for (; ;) {
            if( null != tr.getCause() ){
                tr = tr.getCause();
                continue;
            }
            tr.printStackTrace(writer);
            break;
        }

        String result = swriter.toString();
        writer.close();
        return result;
    }

}
