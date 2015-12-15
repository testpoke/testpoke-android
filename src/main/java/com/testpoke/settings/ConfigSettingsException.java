package com.testpoke.settings;

/*
 * Created by Jansel Valentin on 4/17/14.
 */
public class ConfigSettingsException extends RuntimeException {

    public ConfigSettingsException() {
        super();
    }

    public ConfigSettingsException(String detailMessage) {
        super(detailMessage);
    }

    public ConfigSettingsException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ConfigSettingsException(Throwable throwable) {
        super(throwable);
    }

}
