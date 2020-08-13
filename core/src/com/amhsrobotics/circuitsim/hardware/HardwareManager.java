package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.hardware.devices.*;
import com.amhsrobotics.circuitsim.utility.DeviceUtil;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.wiring.Cable;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.HashMap;

public class HardwareManager {

    public static Hardware currentHardware = null;
    public static boolean movingObject = false;

    private static DelayedRemovalArray<Hardware> hardwares = new DelayedRemovalArray<>();
    private static DelayedRemovalArray<Hardware> temp;

    public static void update(ModifiedShapeRenderer renderer, SpriteBatch batch, ClippedCameraController cam) {
        for(Hardware h : hardwares) {
            h.update(batch, renderer, cam);
        }
    }

    public static DelayedRemovalArray<Hardware> getHardware() {
        return hardwares;
    }

    public static void moveToFront(Hardware hardware) {
        temp = new DelayedRemovalArray<>();
        for(int i = 0; i < hardwares.size; i++) {
            if(hardwares.get(i).getHardwareID() != hardware.getHardwareID()) {
                temp.add(hardwares.get(i));
            }
        }

        temp.add(hardware);
        hardwares = temp;

    }

    public static HashMap<Hardware, Integer> wireHoveringHardware(Vector2 vec) {

        //GET IF WIRE IS CLICKED ON HARDWARE

        for(Hardware h : hardwares) {
            if(h.getTotalConnectors() != 0) {
                for(int x = 0; x < h.getTotalConnectors(); x++) {
                    if(h.getConnector(x).getBoundingRectangle().contains(vec.x, vec.y)) {
                        int finalX = x;
                        return new HashMap<Hardware, Integer>() {{
                            put(h, finalX);
                        }};
                    }
                }
            }
        }
        return null;
    }

    public static void removeCableFromHardware(Cable cable, Hardware hardware) {
        if(hardware != null) {
            hardware.clearConnection(cable);
        }
    }

    public static Hardware addHardware(float startX, float startY, HardwareType type) {
        CircuitGUIManager.propertiesBox.show();

        Hardware temp;

        switch(type) {
            case PDP:
                temp = new PowerDistributionPanel(new Vector2(startX, startY), type, false);
                break;
            case VRM:
                temp = new VoltageRegulatorModule(new Vector2(startX, startY), type, false);
                break;
            case PCM:
                temp = new PneumaticsControlModule(new Vector2(startX, startY), type, false);
                break;
            case DOUBLESANDCRAB:
                temp = new SandCrab(new Vector2(startX, startY), type, false);
                break;
            case TRIPLESANDCRAB:
                temp = new SandCrab(new Vector2(startX, startY), type, false);
                break;
            case ROBORIO:
                temp = new RoboRio(new Vector2(startX, startY), type, false);
                break;
            case TALON:
                temp = new Talon(new Vector2(startX, startY), type, false);
                break;
            case SPARK:
                temp = new Spark(new Vector2(startX, startY), type, false);
                break;
            case FALCON:
                temp = new Falcon(new Vector2(startX, startY), type, false);
                break;
            case MOTOR775:
                temp = new Motor775(new Vector2(startX, startY), type, false);
                break;
            case NEO:
                temp = new NEO(new Vector2(startX, startY), type, false);
                break;
            case BREAKER:
                temp = new Breaker(new Vector2(startX, startY), type, false);
                break;
            case BATTERY:
                temp = new Battery(new Vector2(startX, startY), type, false);
                break;
            default:
                temp = new SandCrab(new Vector2(startX, startY), type, false);
                break;
        }

        if(type == HardwareType.PDP) {
            Hardware breaker = new Breaker(new Vector2(startX + temp.getDim().x - 200, startY), HardwareType.BREAKER);
            hardwares.add(breaker);
            Cable c = new Cable(new Vector2(startX + temp.getDim().x - 300, startY + 250), DeviceUtil.getNewHardwareID());
            Cable c2 = new Cable(new Vector2(startX + temp.getDim().x - 300, startY - 250), DeviceUtil.getNewHardwareID());
            c.setGauge(4);
            c2.setGauge(4);
            CableManager.addCable(c);
            CableManager.addCable(c2);
            breaker.attachWire(c, 0, true);
            breaker.attachWire(c2, 1, true);
            temp.attachWire(c, 42, false);
            temp.attachWire(c2, 43, false);

        }

        currentHardware = temp;
        CableManager.currentCable = null;
        hardwares.add(temp);

        return temp;
    }



    public static void removeHardware(Hardware ha) {
        hardwares.removeValue(ha, true);
    }
}
