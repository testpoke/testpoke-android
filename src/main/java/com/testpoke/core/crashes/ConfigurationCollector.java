/*
 *  Copyright 2010 Emmanuel Astier & Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in netpliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.testpoke.core.crashes;

import android.content.Context;
import android.content.res.Configuration;
import android.util.SparseArray;
import com.testpoke.core.util.log.TP;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/*
 * Taken from ACRA
 */
/*package*/ final class ConfigurationCollector {

    private static final String SUFFIX_MASK = "_MASK";
    private static final String FIELD_SCREENLAYOUT = "screenLayout";
    private static final String FIELD_UIMODE = "uiMode";
    private static final String FIELD_MNC = "mnc";
    private static final String FIELD_MCC = "mcc";
    private static final String PREFIX_UI_MODE = "UI_MODE_";
    private static final String PREFIX_TOUCHSCREEN = "TOUCHSCREEN_";
    private static final String PREFIX_SCREENLAYOUT = "SCREENLAYOUT_";
    private static final String PREFIX_ORIENTATION = "ORIENTATION_";
    private static final String PREFIX_NAVIGATIONHIDDEN = "NAVIGATIONHIDDEN_";
    private static final String PREFIX_NAVIGATION = "NAVIGATION_";
    private static final String PREFIX_KEYBOARDHIDDEN = "KEYBOARDHIDDEN_";
    private static final String PREFIX_KEYBOARD = "KEYBOARD_";
    private static final String PREFIX_HARDKEYBOARDHIDDEN = "HARDKEYBOARDHIDDEN_";
    private static SparseArray<String> mHardKeyboardHiddenValues = new SparseArray<String>();
    private static SparseArray<String> mKeyboardValues = new SparseArray<String>();
    private static SparseArray<String> mKeyboardHiddenValues = new SparseArray<String>();
    private static SparseArray<String> mNavigationValues = new SparseArray<String>();
    private static SparseArray<String> mNavigationHiddenValues = new SparseArray<String>();
    private static SparseArray<String> mOrientationValues = new SparseArray<String>();
    private static SparseArray<String> mScreenLayoutValues = new SparseArray<String>();
    private static SparseArray<String> mTouchScreenValues = new SparseArray<String>();
    private static SparseArray<String> mUiModeValues = new SparseArray<String>();

    private static final HashMap<String, SparseArray<String>> mValueArrays = new HashMap<String, SparseArray<String>>();

    // Static init
    static {
        mValueArrays.put(PREFIX_HARDKEYBOARDHIDDEN, mHardKeyboardHiddenValues);
        mValueArrays.put(PREFIX_KEYBOARD, mKeyboardValues);
        mValueArrays.put(PREFIX_KEYBOARDHIDDEN, mKeyboardHiddenValues);
        mValueArrays.put(PREFIX_NAVIGATION, mNavigationValues);
        mValueArrays.put(PREFIX_NAVIGATIONHIDDEN, mNavigationHiddenValues);
        mValueArrays.put(PREFIX_ORIENTATION, mOrientationValues);
        mValueArrays.put(PREFIX_SCREENLAYOUT, mScreenLayoutValues);
        mValueArrays.put(PREFIX_TOUCHSCREEN, mTouchScreenValues);
        mValueArrays.put(PREFIX_UI_MODE, mUiModeValues);

        for (final Field f : Configuration.class.getFields()) {
            if (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())) {
                final String fieldName = f.getName();
                try {
                    if (fieldName.startsWith(PREFIX_HARDKEYBOARDHIDDEN)) {
                        mHardKeyboardHiddenValues.put(f.getInt(null), fieldName);
                    } else if (fieldName.startsWith(PREFIX_KEYBOARD)) {
                        mKeyboardValues.put(f.getInt(null), fieldName);
                    } else if (fieldName.startsWith(PREFIX_KEYBOARDHIDDEN)) {
                        mKeyboardHiddenValues.put(f.getInt(null), fieldName);
                    } else if (fieldName.startsWith(PREFIX_NAVIGATION)) {
                        mNavigationValues.put(f.getInt(null), fieldName);
                    } else if (fieldName.startsWith(PREFIX_NAVIGATIONHIDDEN)) {
                        mNavigationHiddenValues.put(f.getInt(null), fieldName);
                    } else if (fieldName.startsWith(PREFIX_ORIENTATION)) {
                        mOrientationValues.put(f.getInt(null), fieldName);
                    } else if (fieldName.startsWith(PREFIX_SCREENLAYOUT)) {
                        mScreenLayoutValues.put(f.getInt(null), fieldName);
                    } else if (fieldName.startsWith(PREFIX_TOUCHSCREEN)) {
                        mTouchScreenValues.put(f.getInt(null), fieldName);
                    } else if (fieldName.startsWith(PREFIX_UI_MODE)) {
                        mUiModeValues.put(f.getInt(null), fieldName);
                    }
                } catch (IllegalArgumentException e) {
                    TP.w("Error while inspecting device configuration: ", e);
                } catch (IllegalAccessException e) {
                    TP.w("Error while inspecting device configuration: ", e);
                }
            }
        }
    }

    /**
     * Use this method to generate a human readable String listing all values
     * from the provided Configuration instance.
     *
     * @param conf The Configuration to be described.
     * @return A String describing all the fields of the given Configuration,
     * with values replaced by constant names.
     */
    public static Map<String,String> toMap(Configuration conf) {

        final Map<String,String> config = new HashMap<String, String>();

        for (final Field f : conf.getClass().getFields()) {
            try {
                if (!Modifier.isStatic(f.getModifiers())) {
                    final String fieldName = f.getName();

                    if (f.getType().equals(int.class)) {
                        config.put(fieldName, getFieldValueName(conf, f));
                    } else if (f.get(conf) != null) {
                        config.put(fieldName, f.get(conf).toString());
                    }
                }
            } catch (IllegalArgumentException e) {
                TP.e("Error while inspecting device configuration: ", e);
            } catch (IllegalAccessException e) {
                TP.e("Error while inspecting device configuration: ", e);
            }
        }
        return config;
    }


    private static String getFieldValueName(Configuration conf, Field f) throws IllegalAccessException {
        final String fieldName = f.getName();
        if (fieldName.equals(FIELD_MCC) || fieldName.equals(FIELD_MNC)) {
            return Integer.toString(f.getInt(conf));
        } else if (fieldName.equals(FIELD_UIMODE)) {
            return activeFlags(mValueArrays.get(PREFIX_UI_MODE), f.getInt(conf));
        } else if (fieldName.equals(FIELD_SCREENLAYOUT)) {
            return activeFlags(mValueArrays.get(PREFIX_SCREENLAYOUT), f.getInt(conf));
        } else {
            final SparseArray<String> values = mValueArrays.get(fieldName.toUpperCase() + '_');
            if (values == null) {
                // Unknown field, return the raw int as String
                return Integer.toString(f.getInt(conf));
            }

            final String value = values.get(f.getInt(conf));
            if (value == null) {
                // Unknown value, return the raw int as String
                return Integer.toString(f.getInt(conf));
            }
            return value;
        }
    }


    private static String activeFlags(SparseArray<String> valueNames, int bitfield) {
        final StringBuilder result = new StringBuilder();

        // Look for masks, apply it an retrieve the masked value
        for (int i = 0; i < valueNames.size(); i++) {
            final int maskValue = valueNames.keyAt(i);
            if (valueNames.get(maskValue).endsWith(SUFFIX_MASK)) {
                final int value = bitfield & maskValue;
                if (value > 0) {
                    if (result.length() > 0) {
                        result.append('+');
                    }
                    result.append(valueNames.get(value));
                }
            }
        }
        return result.toString();
    }
    /**
     * Returns the current Configuration for this application.
     *
     * @param context Context for the application being reported.
     * @return A String representation of the current configuration for the application.
     */
    public static Map<String,String> collectConfiguration(Context context) {
        try {
            final Configuration crashConf = context.getResources().getConfiguration();
            return ConfigurationCollector.toMap(crashConf);
        } catch (RuntimeException e) {
            TP.w("Couldn't retrieve CrashConfiguration for : " + context.getPackageName(), e);
            return new HashMap<String, String>();
        }
    }
}
