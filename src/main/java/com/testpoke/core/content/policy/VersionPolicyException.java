package com.testpoke.core.content.policy;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
public class VersionPolicyException extends RuntimeException {
    public VersionPolicyException(String detailMessage) {
        super(detailMessage);
    }

    public VersionPolicyException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public VersionPolicyException(Throwable throwable) {
        super(throwable);
    }
}
