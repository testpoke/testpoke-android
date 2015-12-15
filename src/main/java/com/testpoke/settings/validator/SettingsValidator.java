package com.testpoke.settings.validator;

import com.testpoke.settings.ConfigSettings;
import com.testpoke.settings.ConfigSettingsException;

/*
 * Created by Jansel Valentin on 4/17/14.
 */
public interface SettingsValidator<T extends ConfigSettings> {
    boolean validate(T setting) throws ConfigSettingsException;
}
