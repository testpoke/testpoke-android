package com.testpoke.core.settings.validator;


import com.testpoke.settings.ConfigSettings;
import com.testpoke.settings.ConfigSettingsException;
import com.testpoke.settings.validator.SettingsValidator;

class TokenValidator implements SettingsValidator {

    private static final String TOKEN_REGEX = "(\\d|\\w){5,5}-(\\d|\\w){5,5}-(\\d|\\w){5,5}-(\\d|\\w){5,5}-(\\d|\\w){5,5}";

    private static final String EXCEPTION_CONFIG_MESSAGE = "Config Setting must not be null";
    private static final String EXCEPTION_CONFIG_TOKEN_MESSAGE = "API Token must not be null";

    @Override
    public boolean validate(ConfigSettings setting) throws ConfigSettingsException {
            return true;
//        Objects.requireNonNull(setting, EXCEPTION_CONFIG_MESSAGE);
//        Objects.requireNonNull(setting.getOptions(), EXCEPTION_CONFIG_MESSAGE);
//        Objects.requireNonNull(setting.getOptions().apiToken(), EXCEPTION_CONFIG_TOKEN_MESSAGE);
//
//        String token = setting.getOptions().apiToken();
//        Pattern pattern = Pattern.compile(TOKEN_REGEX, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher( token );
//
//        boolean validated = matcher.find();
//        if( !validated ){
//            throw new ConfigSettingsException( "Incorrect API Token "+token+", format must be 29 alphanumeric length including dashes, ie. 00000-00000-00000-00000-00000" );
//        }
//        return validated;
    }
}
