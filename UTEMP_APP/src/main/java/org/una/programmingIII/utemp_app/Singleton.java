package org.una.programmingIII.utemp_app;

import lombok.Getter;
import lombok.Setter;
import org.una.programmingIII.utemp_app.manager.ScreenManager;

@Setter
@Getter
public class Singleton {
    private static Singleton instance;

    private ScreenManager screenManager;

    private Singleton() {
    }

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

}
