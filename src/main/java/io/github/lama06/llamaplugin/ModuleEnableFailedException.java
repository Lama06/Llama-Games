package io.github.lama06.llamaplugin;

public class ModuleEnableFailedException extends Exception {
    public ModuleEnableFailedException(String message) {
        super(message);
    }

    public ModuleEnableFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
