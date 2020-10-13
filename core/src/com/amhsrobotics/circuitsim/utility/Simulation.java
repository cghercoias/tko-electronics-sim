package com.amhsrobotics.circuitsim.utility;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.Hardware;
import com.amhsrobotics.circuitsim.hardware.HardwareManager;
import com.amhsrobotics.circuitsim.hardware.devices.Battery;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import java.util.HashMap;

public class Simulation {
    private DelayedRemovalArray<Hardware> h;
    private DelayedRemovalArray<Cable> c;
    private HashMap<Object, String> error;
    private Battery battery;

    private boolean isRunning = false;

    public Simulation() {
        error = new HashMap<>();
    }

    public HashMap<Object, String> getErrors() {
        return error;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void simulate() {
        error.clear();
        isRunning = true;
        battery = null;

        h = HardwareManager.getHardware();

        for(Hardware ha : h) {
            if(ha.check() != null) {
              error.put(ha, ha.check());
            }
        }

        //CircuitGUIManager.popup.activateError("Simulation Not Implemented");
        isRunning = false;

        for(Object o : error.keySet()) {
            Gdx.app.log(error.get(o), "");
        }
        //return error;
    }
}
