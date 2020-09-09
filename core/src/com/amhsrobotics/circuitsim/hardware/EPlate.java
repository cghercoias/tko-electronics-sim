package com.amhsrobotics.circuitsim.hardware;

import com.amhsrobotics.circuitsim.Constants;
import com.amhsrobotics.circuitsim.gui.CircuitGUIManager;
import com.amhsrobotics.circuitsim.utility.Box;
import com.amhsrobotics.circuitsim.utility.Tools;
import com.amhsrobotics.circuitsim.utility.camera.ClippedCameraController;
import com.amhsrobotics.circuitsim.wiring.CableManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import me.rohanbansal.ricochet.tools.ModifiedShapeRenderer;

import java.util.ArrayList;

public class EPlate extends Hardware {

    private Box box;
    private TextField.TextFieldStyle textFieldStyle;

    private ArrayList<Hardware> hardwareOnPlate = new ArrayList<>();
    private ResizeNode[] nodes = new ResizeNode[8];

    private ResizeNode prevHandle;
    private float createTime = 0f;


    public EPlate(Vector2 pos) {
        super(pos, HardwareType.EPLATE);

        textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = Constants.SKIN.getDrawable("textbox_02");
        textFieldStyle.cursor = Constants.SKIN.getDrawable("textbox_cursor_02");
        textFieldStyle.font = Constants.FONT_SMALL;
        textFieldStyle.fontColor = Color.BLACK;
    }

    public void init() {
        box = new Box(getPosition().x, getPosition().y, 300, 300);
        initNodes();
    }

    private void initNodes() {
        for(int x = 0; x < 8; x++) {
            nodes[x] = new ResizeNode(box.getResizePointAtIndex(x).x, box.getResizePointAtIndex(x).y, ResizeNode.nodeMap.get(x));
        }
    }


    @Override
    public void update(SpriteBatch batch, ModifiedShapeRenderer renderer, ClippedCameraController camera) {
        super.update(batch, renderer, camera);

        if(createTime < 2) {
            createTime += Gdx.graphics.getDeltaTime();
        }

        renderer.setColor(193/255f, 211/255f, 200/255f, 0.5f);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.roundedRect(box.getX(), box.getY(), box.getWidth(), box.getHeight(), 5);
        renderer.end();

        Vector2 vec = Tools.mouseScreenToWorld(camera);

        if(box.contains(vec.x, vec.y)) {
            drawHover(renderer);

            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                HardwareManager.currentHardware = this;
                CableManager.currentCable = null;
                populateProperties();
                CircuitGUIManager.propertiesBox.show();
            }
        }

        if (HardwareManager.currentHardware == this) {
            drawHover(renderer);
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            for(ResizeNode c : nodes) {
                c.draw(renderer, camera);
            }
            renderer.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                HardwareManager.currentHardware = null;
                CircuitGUIManager.propertiesBox.hide();
            }


            for(int x = 0; x < nodes.length; x++) {
                if(createTime >= 1.8f) {
                    if(Gdx.input.isTouched()) {
                        if(nodes[x].contains(vec)) {
                            setSelectedNode(x);
                            nodes[x].movePosition(camera, box);
                        }
                    }
                }

                if(!nodes[x].isSelected()) {
                    nodes[x].updateIdlePos(box);
                }
            }
        }
    }

    private void setSelectedNode(int index) {
        for(int i = 0; i < nodes.length; i++) {
            if(i == index) {
                nodes[i].setSelected(true);
            } else {
                nodes[i].setSelected(false);
            }
        }
    }

    @Override
    public void populateProperties() {
        CircuitGUIManager.propertiesBox.clearTable();
        CircuitGUIManager.propertiesBox.addElement(new Label("E-Plate", CircuitGUIManager.propertiesBox.LABEL), true, 2);
        CircuitGUIManager.propertiesBox.addElement(new Label("Width", CircuitGUIManager.propertiesBox.LABEL), true, 1);
        TextField width = new TextField(box.getWidth() + "", textFieldStyle);
        width.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        CircuitGUIManager.propertiesBox.addElement(width, false, 1);
        CircuitGUIManager.propertiesBox.addElement(new Label("Height", CircuitGUIManager.propertiesBox.LABEL), true, 1);
        TextField height = new TextField(box.getHeight() + "", textFieldStyle);
        height.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        CircuitGUIManager.propertiesBox.addElement(height, false, 1);

    }

    @Override
    public void drawHover(ModifiedShapeRenderer renderer) {
        renderer.setColor(255/255f, 255/255f, 255/255f, 0.2f);

        Gdx.gl.glLineWidth(5);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.roundedRect(box.getX() - 5, box.getY() - 5, box.getWidth() + 10, box.getHeight() + 10, 5);
        renderer.end();
        Gdx.gl.glLineWidth(1);
    }
}