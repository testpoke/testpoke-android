package com.testpoke.core.settings.validator;

import com.testpoke.settings.$Settings$Holder;
import com.testpoke.settings.ConfigSettings;
import com.testpoke.settings.ConfigSettingsException;
import com.testpoke.settings.validator.SettingsValidator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by Jansel Valentin on 4/17/14.
 */
/*package*/ class SettingsValidatorAgregator implements SettingsValidator {
    static {
        $Settings$Holder.setDefaultValidator(new SettingsValidatorAgregator());
    }

    private List<WeakReference<SettingsValidator<? extends ConfigSettings>>> validators = new ArrayList<WeakReference<SettingsValidator<? extends ConfigSettings>>>();

    private SettingsValidatorAgregator() {
        registerValidators();
    }

    private void registerValidators() {
        validators.add(new WeakReference<SettingsValidator<? extends ConfigSettings>>(new TokenValidator()));
    }


    @Override
    public boolean validate(ConfigSettings setting) throws ConfigSettingsException {
        if (setting.getOptions().isDisabled())
            return false;

        boolean validated = true;
        for (int i = 0; validators.size() > i; ++i) {

            WeakReference<SettingsValidator<? extends ConfigSettings>> validatorRef;
            validatorRef = validators.get(i);


            SettingsValidator validator;
            if (null != validatorRef & null != (validator = validatorRef.get())) {
                validated &= validator.validate(setting);
            }
        }
        return validated;
    }
}
