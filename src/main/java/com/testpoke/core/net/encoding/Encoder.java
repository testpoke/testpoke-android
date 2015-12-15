package com.testpoke.core.net.encoding;

import java.io.IOException;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
public interface Encoder<TReturn,TParam> {
    TReturn encode(TParam param) throws IOException;
}
