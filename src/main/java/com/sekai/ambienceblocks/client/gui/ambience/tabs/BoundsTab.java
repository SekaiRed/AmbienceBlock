package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.*;
import com.sekai.ambienceblocks.util.BoundsUtil;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class BoundsTab extends AbstractTab {
    private int boundType;

    TextInstance textBounds;
    Button buttonBounds;

    TextInstance textSphereRadius;
    TextFieldWidget sphereRadius;

    TextInstance textCylRadius;
    TextFieldWidget cylRadius;
    TextInstance textCylLength;
    TextFieldWidget cylLength;
    TextInstance textCylAxis;
    Button cylAxisButton;
    BoundsAxis cylAxis;

    TextInstance textCapRadius;
    TextFieldWidget capRadius;
    TextInstance textCapLength;
    TextFieldWidget capLength;
    TextInstance textCapAxis;
    Button capAxisButton;
    BoundsAxis capAxis;

    TextInstance textCubicX;
    TextFieldWidget cubicX;
    TextInstance textCubicY;
    TextFieldWidget cubicY;
    TextInstance textCubicZ;
    TextFieldWidget cubicZ;

    TextInstance textOffset;
    TextInstance textOffsetX;
    TextFieldWidget offsetX;
    TextInstance textOffsetY;
    TextFieldWidget offsetY;
    TextInstance textOffsetZ;
    TextFieldWidget offsetZ;

    private List<Widget> sphereWidgets;// = new ArrayList<>();
    private List<Widget> cylinderWidgets;// = new ArrayList<>();
    private List<Widget> capsuleWidgets;
    private List<Widget> cubicWidgets;// = new ArrayList<>();
    private List<Widget> noneWidgets;// = new ArrayList<>();

    public BoundsTab(AmbienceGUI guiRef) {
        super(guiRef);
    }

    @Override
    public String getName() {
        return "Bounds";
    }

    @Override
    public void init() {
        sphereWidgets = new ArrayList<>();
        cylinderWidgets = new ArrayList<>();
        capsuleWidgets = new ArrayList<>();
        cubicWidgets = new ArrayList<>();
        noneWidgets = new ArrayList<>();

        textBounds = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.type") + " :", font);
        addWidget(buttonBounds = guiRef.addButton(new Button(getNeighbourX(textBounds), getRowY(0) + getOffsetY(20), 60, 20, "no", button -> {
            moveToNextBoundType();
        })));

        textSphereRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.radius"), font);
        sphereWidgets.add(textSphereRadius);
        addWidget(sphereRadius = new TextFieldWidget(font, getNeighbourX(textSphereRadius), getRowY(1), 40, 20, "name"));
        sphereWidgets.add(sphereRadius);
        sphereRadius.setValidator(ParsingUtil.decimalNumberFilter);
        sphereRadius.setMaxStringLength(6);

        textCylRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.radius"), font);
        cylinderWidgets.add(textCylRadius);
        addWidget(cylRadius = new TextFieldWidget(font, getNeighbourX(textCylRadius), getRowY(1), 40, 20, "name"));
        cylinderWidgets.add(cylRadius);
        cylRadius.setValidator(ParsingUtil.decimalNumberFilter);
        cylRadius.setMaxStringLength(6);
        textCylLength = new TextInstance(getNeighbourX(cylRadius), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.length"), font);
        cylinderWidgets.add(textCylLength);
        addWidget(cylLength = new TextFieldWidget(font, getNeighbourX(textCylLength), getRowY(1), 40, 20, "name"));
        cylinderWidgets.add(cylLength);
        cylLength.setValidator(ParsingUtil.decimalNumberFilter);
        cylLength.setMaxStringLength(6);
        textCylAxis = new TextInstance(getNeighbourX(cylLength), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.axis"), font);
        cylinderWidgets.add(textCylAxis);
        addWidget(cylAxisButton = guiRef.addButton(new Button(getNeighbourX(textCylAxis), getRowY(1) + getOffsetY(20), 20, 20, "X", button -> {
            moveToNextAxisCylinder();
        })));
        cylinderWidgets.add(cylAxisButton);

        textCapRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.radius"), font);
        capsuleWidgets.add(textCapRadius);
        addWidget(capRadius = new TextFieldWidget(font, getNeighbourX(textCapRadius), getRowY(1), 40, 20, "name"));
        capsuleWidgets.add(capRadius);
        capRadius.setValidator(ParsingUtil.decimalNumberFilter);
        capRadius.setMaxStringLength(6);
        textCapLength = new TextInstance(getNeighbourX(capRadius), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.length"), font);
        capsuleWidgets.add(textCapLength);
        addWidget(capLength = new TextFieldWidget(font, getNeighbourX(textCapLength), getRowY(1), 40, 20, "name"));
        capsuleWidgets.add(capLength);
        capLength.setValidator(ParsingUtil.decimalNumberFilter);
        capLength.setMaxStringLength(6);
        textCapAxis = new TextInstance(getNeighbourX(capLength), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.axis"), font);
        capsuleWidgets.add(textCapAxis);
        addWidget(capAxisButton = guiRef.addButton(new Button(getNeighbourX(textCapAxis), getRowY(1) + getOffsetY(20), 20, 20, "X", button -> {
            moveToNextAxisCapsule();
        })));
        capsuleWidgets.add(capAxisButton);

        cubicWidgets.add(textCubicX = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "X", font));
        addWidget(cubicX = new TextFieldWidget(font, getNeighbourX(textCubicX), getRowY(1), 40, 20, "name"));
        cubicWidgets.add(cubicX);
        cubicX.setValidator(ParsingUtil.decimalNumberFilter);
        cubicX.setMaxStringLength(6);
        cubicWidgets.add(textCubicY = new TextInstance(getNeighbourX(cubicX), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Y", font));
        addWidget(cubicY = new TextFieldWidget(font, getNeighbourX(textCubicY), getRowY(1), 40, 20, "name"));
        cubicWidgets.add(cubicY);
        cubicY.setValidator(ParsingUtil.decimalNumberFilter);
        cubicY.setMaxStringLength(6);
        cubicWidgets.add(textCubicZ = new TextInstance(getNeighbourX(cubicY), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Z", font));
        addWidget(cubicZ = new TextFieldWidget(font, getNeighbourX(textCubicZ), getRowY(1), 40, 20, "name"));
        cubicWidgets.add(cubicZ);
        cubicZ.setValidator(ParsingUtil.decimalNumberFilter);
        cubicZ.setMaxStringLength(6);

        textOffset = new TextInstance(getBaseX(), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.offset") + " :", font);
        textOffsetX = new TextInstance(getNeighbourX(textOffset), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "X", font);
        addWidget(offsetX = new TextFieldWidget(font, getNeighbourX(textOffsetX), getRowY(2), 40, 20, "name"));
        offsetX.setValidator(ParsingUtil.negativeDecimalNumberFilter);
        offsetX.setMaxStringLength(8);
        textOffsetY = new TextInstance(getNeighbourX(offsetX), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Y", font);
        addWidget(offsetY = new TextFieldWidget(font, getNeighbourX(textOffsetY), getRowY(2), 40, 20, "name"));
        offsetY.setValidator(ParsingUtil.negativeDecimalNumberFilter);
        offsetY.setMaxStringLength(8);
        textOffsetZ = new TextInstance(getNeighbourX(offsetY), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Z", font);
        addWidget(offsetZ = new TextFieldWidget(font, getNeighbourX(textOffsetZ), getRowY(2), 40, 20, "name"));
        offsetZ.setValidator(ParsingUtil.negativeDecimalNumberFilter);
        offsetZ.setMaxStringLength(8);

        resetBoundFields();
        setBoundType(0);
    }

    private void moveToNextAxisCapsule() {
        capAxis = capAxis.next();
        capAxisButton.setMessage(capAxis.toString());
    }

    private void moveToNextAxisCylinder() {
        cylAxis = cylAxis.next();
        cylAxisButton.setMessage(cylAxis.toString());
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        textBounds.render();
        buttonBounds.render(mouseX, mouseY, partialTicks);

        textSphereRadius.render();
        sphereRadius.render(mouseX, mouseY, partialTicks);

        textCylRadius.render();
        cylRadius.render(mouseX, mouseY, partialTicks);
        textCylLength.render();
        cylLength.render(mouseX, mouseY, partialTicks);
        textCylAxis.render();
        cylAxisButton.render(mouseX, mouseY, partialTicks);

        textCapRadius.render();
        capRadius.render(mouseX, mouseY, partialTicks);
        textCapLength.render();
        capLength.render(mouseX, mouseY, partialTicks);
        textCapAxis.render();
        capAxisButton.render(mouseX, mouseY, partialTicks);

        textCubicX.render();
        cubicX.render(mouseX, mouseY, partialTicks);
        textCubicY.render();
        cubicY.render(mouseX, mouseY, partialTicks);
        textCubicZ.render();
        cubicZ.render(mouseX, mouseY, partialTicks);

        textOffset.render();
        textOffsetX.render();
        offsetX.render(mouseX, mouseY, partialTicks);
        textOffsetY.render();
        offsetY.render(mouseX, mouseY, partialTicks);
        textOffsetZ.render();
        offsetZ.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        sphereRadius.tick();

        cylRadius.tick();
        cylLength.tick();
    }

    @Override
    public void setFieldFromData(AmbienceTileEntityData data) {
        loadBoundType(data);

        Vec3d pos = data.getOffset();
        offsetX.setText(String.valueOf(pos.getX()));
        offsetY.setText(String.valueOf(pos.getY()));
        offsetZ.setText(String.valueOf(pos.getZ()));
    }

    @Override
    public void setDataFromField(AmbienceTileEntityData data) {
        /*AbstractBounds bounds = BoundsUtil.getBoundsFromType(boundType);
        data.setBounds(BoundsUtil.getBoundsFromType(boundType));*/
        if(boundType == SphereBounds.id) {
            SphereBounds bounds = new SphereBounds();

            bounds.setRadius(ParsingUtil.tryParseDouble(sphereRadius.getText()));

            data.setBounds(bounds);
        }

        if(boundType == CylinderBounds.id) {
            CylinderBounds bounds = new CylinderBounds();

            bounds.setRadius(ParsingUtil.tryParseDouble(cylRadius.getText()));
            bounds.setLength(ParsingUtil.tryParseDouble(cylLength.getText()));
            bounds.setAxis(cylAxis);

            data.setBounds(bounds);
        }

        if(boundType == CapsuleBounds.id) {
            CapsuleBounds bounds = new CapsuleBounds();

            bounds.setRadius(ParsingUtil.tryParseDouble(capRadius.getText()));
            bounds.setLength(ParsingUtil.tryParseDouble(capLength.getText()));
            bounds.setAxis(capAxis);

            data.setBounds(bounds);
        }

        if(boundType == CubicBounds.id) {
            CubicBounds bounds = new CubicBounds();

            bounds.setxSize(ParsingUtil.tryParseDouble(cubicX.getText()));
            bounds.setySize(ParsingUtil.tryParseDouble(cubicY.getText()));
            bounds.setzSize(ParsingUtil.tryParseDouble(cubicZ.getText()));

            data.setBounds(bounds);
        }

        if(boundType == NoneBounds.id) {
            data.setBounds(new NoneBounds());
        }

        data.setOffset(new Vec3d(
                ParsingUtil.tryParseDouble(offsetX.getText()),
                ParsingUtil.tryParseDouble(offsetY.getText()),
                ParsingUtil.tryParseDouble(offsetZ.getText())
        ));

        //data.setOffset(new BlockPos(offsetX.getText()));
    }

    @Override
    public void onActivate() {
        //refresh boundType widgets
        updateBoundsField();
    }

    @Override
    public void onDeactivate() {

    }

    public void moveToNextBoundType() {
        if(boundType == BoundsUtil.lastBoundType)
        {
            setBoundType(0);
        }
        else
        {
            setBoundType(boundType + 1);
        }
    }

    public void loadBoundType(AmbienceTileEntityData data) {
        setBoundType(data.getBounds().getID());
        if(data.getBounds() instanceof SphereBounds) {
            SphereBounds bounds = (SphereBounds) data.getBounds();
            sphereRadius.setText(String.valueOf(bounds.getRadius()));
        }
        if(data.getBounds() instanceof CylinderBounds) {
            CylinderBounds bounds = (CylinderBounds) data.getBounds();
            cylRadius.setText(String.valueOf(bounds.getRadius()));
            cylLength.setText(String.valueOf(bounds.getLength()));
            cylAxisButton.setMessage(bounds.getAxis().toString());
            cylAxis = bounds.getAxis();
        }
        if(data.getBounds() instanceof CapsuleBounds) {
            CapsuleBounds bounds = (CapsuleBounds) data.getBounds();
            capRadius.setText(String.valueOf(bounds.getRadius()));
            capLength.setText(String.valueOf(bounds.getLength()));
            capAxisButton.setMessage(bounds.getAxis().toString());
            capAxis = bounds.getAxis();
        }
        if(data.getBounds() instanceof CubicBounds) {
            CubicBounds bounds = (CubicBounds) data.getBounds();
            cubicX.setText(String.valueOf(bounds.getxSize()));
            cubicY.setText(String.valueOf(bounds.getySize()));
            cubicZ.setText(String.valueOf(bounds.getzSize()));
        }
    }

    public void setBoundType(int type) {
        boundType = type;
        buttonBounds.setMessage(BoundsUtil.getBoundsFromType(boundType).getName());
        updateBoundsField();
    }

    public void updateBoundsField() {
        resetShownFields();
        //System.out.println(BoundsUtil.getBoundsFromType(boundType).getName());
        //System.out.println(sphereWidgets.size());
        if(boundType == SphereBounds.id) for(Widget widget : sphereWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CylinderBounds.id) for(Widget widget : cylinderWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CapsuleBounds.id) for(Widget widget : capsuleWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CubicBounds.id) for(Widget widget : cubicWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == NoneBounds.id) for(Widget widget : noneWidgets) { widget.active = true; widget.visible = true; }
    }

    public void resetShownFields() {
        for(Widget widget : sphereWidgets) { widget.active = false; widget.visible = false; }
        for(Widget widget : cylinderWidgets) { widget.active = false; widget.visible = false; }
        for(Widget widget : capsuleWidgets) { widget.active = false; widget.visible = false; }
        for(Widget widget : cubicWidgets) { widget.active = false; widget.visible = false; }
        for(Widget widget : noneWidgets) { widget.active = false; widget.visible = false; }
    }

    public void resetBoundFields() {
        sphereRadius.setText(String.valueOf(0));

        cylRadius.setText(String.valueOf(0));
        cylLength.setText(String.valueOf(0));
        cylAxis = BoundsAxis.Y;
        cylAxisButton.setMessage(cylAxis.toString());

        capRadius.setText(String.valueOf(0));
        capLength.setText(String.valueOf(0));
        capAxis = BoundsAxis.Y;
        capAxisButton.setMessage(capAxis.toString());

        cubicX.setText(String.valueOf(0));
        cubicY.setText(String.valueOf(0));
        cubicZ.setText(String.valueOf(0));
    }
}
