package com.testpoke.core.util;

/*
 * Created by Jansel Valentin on 4/22/14.
 */
public final class Objects {

    public static <T> T requireNonNull( T object ){
        if( null == object )
            throw new NullPointerException();
        return object;
    }


    public static <T> T requireNonNull( T object,String message ){
        if( null == object )
            throw new NullPointerException( message );
        return  object;
    }

    public static boolean equals( Object a, Object b ){
        return a == b || ( null != a && a.equals(b) );
    }
}
