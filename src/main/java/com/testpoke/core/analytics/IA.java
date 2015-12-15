package com.testpoke.core.analytics;

/*
 * Created by Jansel Valentin on 5/10/14.
 */

import com.testpoke.core.ia.$_U;

/*
 * InlineAccess
 */
public final class IA {
    /*
     * Unique instance
     */
    private static final IA ia = new IA();

    private String _ade677c68;

    private String _s7768b7aa;

    private String _ba8868af2;

    private String _ea8268b38;

    /*
     * uuid
     */
    private String uuid;

    IA() {
        uuid = $_U.$3();
    }

    public static IA k() {
        return ia;
    }

    public void reload() {
        uuid = $_U.$3();
    }


    public String uuid() {
        return uuid;
    }

    String _ade677c68() {
        return _ade677c68;
    }

    void _ade677c68(String _ade677c68) {
        this._ade677c68 = _ade677c68;
    }

    String _s7768b7aa() {
        return _s7768b7aa;
    }

    void _s7768b7aa(String _s7768b7aa) {
        this._s7768b7aa = _s7768b7aa;
    }

    String _ba8868af2() {
        return _ba8868af2;
    }

    void _ba8868af2(String _ba8868af2) {
        this._ba8868af2 = _ba8868af2;
    }

    void _ea8268b38(String _ea8268b38) {
        this._ea8268b38 = _ea8268b38;
    }

    String _ea8268b38(){
        return _ea8268b38;
    }

}
