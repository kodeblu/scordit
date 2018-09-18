package com.bencorp.scrab;

/**
 * Created by hp-pc on 9/16/2018.
 */

public class VersionControl {
    private static final VersionControl ourInstance = new VersionControl();
    public static final String SOFTWARE_VERSION = "1.0";
    public static VersionControl getInstance() {
        return ourInstance;
    }

    private VersionControl() {
    }
}
