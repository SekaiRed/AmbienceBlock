package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.tileentity.AmbienceTileEntityData;
import com.sekai.ambienceblocks.tileentity.ambiencetilebounds.*;
import com.sekai.ambienceblocks.tileentity.util.AmbienceAxis;
import com.sekai.ambienceblocks.util.BoundsUtil;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class BoundsTab extends AbstractTab {
    private int boundType;

    TextInstance textBounds = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.type") + " :", font);
    Button buttonBounds = guiRef.addButton(new Button(getNeighbourX(textBounds), getRowY(0) + getOffsetY(20), 60, 20, new StringTextComponent("no"), button -> {
        moveToNextBoundType();
    }));
    CheckboxWidget isGlobal = new CheckboxWidget(getNeighbourX(buttonBounds), getRowY(0), 20 + font.getStringWidth("Global"), 20, "Global", false);

    TextInstance textSphereRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.radius"), font);
    TextFieldWidget sphereRadius = new TextFieldWidget(font, getNeighbourX(textSphereRadius), getRowY(1), 40, 20, new StringTextComponent(""));

    TextInstance textCylRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.radius"), font);
    TextFieldWidget cylRadius = new TextFieldWidget(font, getNeighbourX(textCylRadius), getRowY(1), 40, 20, new StringTextComponent(""));
    TextInstance textCylLength = new TextInstance(getNeighbourX(cylRadius), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.length"), font);
    TextFieldWidget cylLength = new TextFieldWidget(font, getNeighbourX(textCylLength), getRowY(1), 40, 20, new StringTextComponent(""));
    TextInstance textCylAxis = new TextInstance(getNeighbourX(cylLength), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.axis"), font);
    Button cylAxisButton = guiRef.addButton(new Button(getNeighbourX(textCylAxis), getRowY(1) + getOffsetY(20), 20, 20, new StringTextComponent("X"), button -> {
        moveToNextAxisCylinder();
    }));
    AmbienceAxis cylAxis;

    TextInstance textCapRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.radius"), font);
    TextFieldWidget capRadius = new TextFieldWidget(font, getNeighbourX(textCapRadius), getRowY(1), 40, 20, new StringTextComponent(""));
    TextInstance textCapLength = new TextInstance(getNeighbourX(capRadius), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.length"), font);
    TextFieldWidget capLength = new TextFieldWidget(font, getNeighbourX(textCapLength), getRowY(1), 40, 20, new StringTextComponent(""));
    TextInstance textCapAxis = new TextInstance(getNeighbourX(capLength), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.axis"), font);
    /*Button capAxisButton = guiRef.addButton(new Button(getNeighbourX(textCapAxis), getRowY(1) + getOffsetY(20), 20, 20, new StringTextComponent("X"), button -> {
        System.out.println(button.visible);
        System.out.println(button.active);
        System.out.println(this.isActive());
        moveToNextAxisCapsule();
    }));*/
    Button capAxisButton = new Button(getNeighbourX(textCapAxis), getRowY(1) + getOffsetY(20), 20, 20, new StringTextComponent("X"), button -> {
        System.out.println(button.visible);
        System.out.println(button.active);
        System.out.println(this.isActive());
        moveToNextAxisCapsule();
    });
    AmbienceAxis capAxis;

    TextInstance textCubicX = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "X", font);
    TextFieldWidget cubicX = new TextFieldWidget(font, getNeighbourX(textCubicX), getRowY(1), 40, 20, new StringTextComponent(""));
    TextInstance textCubicY = new TextInstance(getNeighbourX(cubicX), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Y", font);
    TextFieldWidget cubicY = new TextFieldWidget(font, getNeighbourX(textCubicY), getRowY(1), 40, 20, new StringTextComponent(""));
    TextInstance textCubicZ = new TextInstance(getNeighbourX(cubicY), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Z", font);
    TextFieldWidget cubicZ = new TextFieldWidget(font, getNeighbourX(textCubicZ), getRowY(1), 40, 20, new StringTextComponent(""));

    TextInstance textOffset = new TextInstance(getBaseX(), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.offset") + " :", font);
    TextInstance textOffsetX = new TextInstance(getNeighbourX(textOffset), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "X", font);
    TextFieldWidget offsetX = new TextFieldWidget(font, getNeighbourX(textOffsetX), getRowY(2), 40, 20, new StringTextComponent(""));
    TextInstance textOffsetY = new TextInstance(getNeighbourX(offsetX), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Y", font);
    TextFieldWidget offsetY = new TextFieldWidget(font, getNeighbourX(textOffsetY), getRowY(2), 40, 20, new StringTextComponent(""));
    TextInstance textOffsetZ = new TextInstance(getNeighbourX(offsetY), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Z", font);
    TextFieldWidget offsetZ = new TextFieldWidget(font, getNeighbourX(textOffsetZ), getRowY(2), 40, 20, new StringTextComponent(""));

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
    public String getShortName() {
        return "Bnds";
    }

    @Override
    public void initialInit() {
        sphereWidgets = new ArrayList<>();
        cylinderWidgets = new ArrayList<>();
        capsuleWidgets = new ArrayList<>();
        cubicWidgets = new ArrayList<>();
        noneWidgets = new ArrayList<>();

        addButton(buttonBounds);
        addWidget(isGlobal);

        sphereWidgets.add(textSphereRadius);
        sphereWidgets.add(sphereRadius);
        sphereRadius.setValidator(ParsingUtil.decimalNumberFilter);
        sphereRadius.setMaxStringLength(6);
        addWidget(sphereRadius);

        cylinderWidgets.add(textCylRadius);
        cylinderWidgets.add(cylRadius);
        cylRadius.setValidator(ParsingUtil.decimalNumberFilter);
        cylRadius.setMaxStringLength(6);
        addWidget(cylRadius);
        cylinderWidgets.add(textCylLength);
        cylinderWidgets.add(cylLength);
        cylLength.setValidator(ParsingUtil.decimalNumberFilter);
        cylLength.setMaxStringLength(6);
        addWidget(cylLength);
        cylinderWidgets.add(textCylAxis);
        cylinderWidgets.add(cylAxisButton);
        addButton(cylAxisButton);

        capsuleWidgets.add(textCapRadius);
        capsuleWidgets.add(capRadius);
        capRadius.setValidator(ParsingUtil.decimalNumberFilter);
        capRadius.setMaxStringLength(6);
        addWidget(capRadius);
        capsuleWidgets.add(textCapLength);
        capsuleWidgets.add(capLength);
        capLength.setValidator(ParsingUtil.decimalNumberFilter);
        capLength.setMaxStringLength(6);
        addWidget(capLength);
        capsuleWidgets.add(textCapAxis);
        capsuleWidgets.add(capAxisButton);
        addButton(capAxisButton);

        cubicWidgets.add(textCubicX);
        cubicWidgets.add(cubicX);
        cubicX.setValidator(ParsingUtil.decimalNumberFilter);
        cubicX.setMaxStringLength(6);
        addWidget(cubicX);
        cubicWidgets.add(textCubicY);
        cubicWidgets.add(cubicY);
        cubicY.setValidator(ParsingUtil.decimalNumberFilter);
        cubicY.setMaxStringLength(6);
        addWidget(cubicY);
        cubicWidgets.add(textCubicZ);
        cubicWidgets.add(cubicZ);
        cubicZ.setValidator(ParsingUtil.decimalNumberFilter);
        cubicZ.setMaxStringLength(6);
        addWidget(cubicZ);

        offsetX.setValidator(ParsingUtil.negativeDecimalNumberFilter);
        offsetX.setMaxStringLength(8);
        addWidget(offsetX);
        offsetY.setValidator(ParsingUtil.negativeDecimalNumberFilter);
        offsetY.setMaxStringLength(8);
        addWidget(offsetY);
        offsetZ.setValidator(ParsingUtil.negativeDecimalNumberFilter);
        offsetZ.setMaxStringLength(8);
        addWidget(offsetZ);

        resetBoundFields();
        setBoundType(0);

        resetShownFields();
    }

    @Override
    public void updateWidgetPosition() {
        textBounds.x = getBaseX(); textBounds.y = getRowY(0) + getOffsetY(font.FONT_HEIGHT);
        buttonBounds.x = getNeighbourX(textBounds); buttonBounds.y = getRowY(0) + getOffsetY(20);

        isGlobal.x = getNeighbourX(buttonBounds); isGlobal.y = getRowY(0);

        textSphereRadius.x = getBaseX(); textSphereRadius.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        sphereRadius.x = getNeighbourX(textSphereRadius); sphereRadius.y = getRowY(1);

        textCylRadius.x = getBaseX(); textCylRadius.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cylRadius.x = getNeighbourX(textCylRadius); cylRadius.y = getRowY(1);
        textCylLength.x = getNeighbourX(cylRadius); textCylLength.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cylLength.x = getNeighbourX(textCylLength); cylLength.y = getRowY(1);
        textCylAxis.x = getNeighbourX(cylLength); textCylAxis.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cylAxisButton.x = getNeighbourX(textCylAxis); cylAxisButton.y = getRowY(1) + getOffsetY(20);

        textCapRadius.x = getBaseX(); textCapRadius.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        capRadius.x = getNeighbourX(textCapRadius); capRadius.y = getRowY(1);
        textCapLength.x = getNeighbourX(capRadius); textCapLength.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        capLength.x = getNeighbourX(textCapLength); capLength.y = getRowY(1);
        textCapAxis.x = getNeighbourX(capLength); textCapAxis.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        capAxisButton.x = getNeighbourX(textCapAxis); capAxisButton.y = getRowY(1) + getOffsetY(20);

        textCubicX.x = getBaseX(); textCubicX.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cubicX.x = getNeighbourX(textCubicX); cubicX.y = getRowY(1);
        textCubicY.x = getNeighbourX(cubicX); textCubicY.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cubicY.x = getNeighbourX(textCubicY); cubicY.y = getRowY(1);
        textCubicZ.x = getNeighbourX(cubicY); textCubicZ.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cubicZ.x = getNeighbourX(textCubicZ); cubicZ.y = getRowY(1);

        textOffset.x = getBaseX(); textOffset.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        textOffsetX.x = getNeighbourX(textOffset); textOffsetX.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        offsetX.x = getNeighbourX(textOffsetX); offsetX.y = getRowY(2);
        textOffsetY.x = getNeighbourX(offsetX); textOffsetY.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        offsetY.x = getNeighbourX(textOffsetY); offsetY.y = getRowY(2);
        textOffsetZ.x = getNeighbourX(offsetY); textOffsetZ.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        offsetZ.x = getNeighbourX(textOffsetZ); offsetZ.y = getRowY(2);
    }

    private void moveToNextAxisCapsule() {
        capAxis = capAxis.next();
        capAxisButton.setMessage(new StringTextComponent(capAxis.toString()));
    }

    private void moveToNextAxisCylinder() {
        cylAxis = cylAxis.next();
        cylAxisButton.setMessage(new StringTextComponent(cylAxis.toString()));
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        textBounds.render(matrix, mouseX, mouseY);
        buttonBounds.render(matrix, mouseX, mouseY, partialTicks);
        isGlobal.render(matrix, mouseX, mouseY, partialTicks);

        textSphereRadius.render(matrix, mouseX, mouseY);
        sphereRadius.render(matrix, mouseX, mouseY, partialTicks);

        textCylRadius.render(matrix, mouseX, mouseY);
        cylRadius.render(matrix, mouseX, mouseY, partialTicks);
        textCylLength.render(matrix, mouseX, mouseY);
        cylLength.render(matrix, mouseX, mouseY, partialTicks);
        textCylAxis.render(matrix, mouseX, mouseY);
        cylAxisButton.render(matrix, mouseX, mouseY, partialTicks);

        textCapRadius.render(matrix, mouseX, mouseY);
        capRadius.render(matrix, mouseX, mouseY, partialTicks);
        textCapLength.render(matrix, mouseX, mouseY);
        capLength.render(matrix, mouseX, mouseY, partialTicks);
        textCapAxis.render(matrix, mouseX, mouseY);
        capAxisButton.render(matrix, mouseX, mouseY, partialTicks);

        textCubicX.render(matrix, mouseX, mouseY);
        cubicX.render(matrix, mouseX, mouseY, partialTicks);
        textCubicY.render(matrix, mouseX, mouseY);
        cubicY.render(matrix, mouseX, mouseY, partialTicks);
        textCubicZ.render(matrix, mouseX, mouseY);
        cubicZ.render(matrix, mouseX, mouseY, partialTicks);

        textOffset.render(matrix, mouseX, mouseY);
        textOffsetX.render(matrix, mouseX, mouseY);
        offsetX.render(matrix, mouseX, mouseY, partialTicks);
        textOffsetY.render(matrix, mouseX, mouseY);
        offsetY.render(matrix, mouseX, mouseY, partialTicks);
        textOffsetZ.render(matrix, mouseX, mouseY);
        offsetZ.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(MatrixStack matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if (buttonBounds.isHovered())
        {
            if(boundType == SphereBounds.id) {
                list.add(TextFormatting.RED + "Sphere Bounds");
                list.add(TextFormatting.WHITE + "Sphere bounds that cover a radius all around it.");
                list.add(TextFormatting.GRAY + "(If the sound isn't global, the volume will be scaled by the distance of the player from the source)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
            }

            if(boundType == CylinderBounds.id) {
                list.add(TextFormatting.RED + "Cylinder Bounds");
                list.add(TextFormatting.WHITE + "Cylinder bounds that cover a radius all around it except the height which can be changed.");
                list.add(TextFormatting.GRAY + "(If the sound isn't global, the volume will be unaffected by height and only scaled by horizontal distance)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
            }

            if(boundType == CapsuleBounds.id) {
                list.add(TextFormatting.RED + "Capsule Bounds");
                list.add(TextFormatting.WHITE + "Capsule bounds that work almost exactly like the cylinder variant, it adds two sphere detection to each end.");
                list.add(TextFormatting.GRAY + "(If the sound isn't global, the volume will be unaffected by height and only scaled by horizontal distance (and regular sphere stuff on each ends))");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
            }

            if(boundType == CubicBounds.id) {
                list.add(TextFormatting.RED + "Cubic Bounds");
                list.add(TextFormatting.WHITE + "Cubic bounds that cover a cubic area of origin by the value of x, y and z.");
                list.add(TextFormatting.GRAY + "(If the sound isn't global, the volume will be scaled as if this was a Sphere Bounds which won't cover the whole area)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
            }

            if(boundType == NoneBounds.id) {
                list.add(TextFormatting.RED + "None Bounds");
                list.add(TextFormatting.WHITE + "Will always play as long as the tile is loaded on the client.");
                list.add(TextFormatting.GRAY + "(The volume will not change with distance regardless of if the tile is set to be global)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
            }
        }

        if(isGlobal.isHovered()) {
            list.add(TextFormatting.RED + "Global");
            list.add("If global is checked the volume won't scale with distance, otherwise it will.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }

        if(textOffset.isHovered()) {
            list.add(TextFormatting.RED + "Offset");
            list.add("Shifts the origin of the bounds to a certain position.");
            list.add(TextFormatting.GRAY + "(Useful for hiding the block)");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
    }

    @Override
    public void tick() {
        sphereRadius.tick();

        cylRadius.tick();
        cylLength.tick();

        cubicX.tick();
        cubicY.tick();
        cubicZ.tick();
    }

    @Override
    public void setFieldFromData(AmbienceTileEntityData data) {
        //setCheckBoxChecked(isGlobal, data.isGlobal());
        isGlobal.setChecked(data.isGlobal());

        loadBoundType(data);

        Vector3d pos = data.getOffset();
        offsetX.setText(String.valueOf(pos.getX()));
        offsetY.setText(String.valueOf(pos.getY()));
        offsetZ.setText(String.valueOf(pos.getZ()));
    }

    @Override
    public void setDataFromField(AmbienceTileEntityData data) {
        data.setGlobal(isGlobal.isChecked());

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

        data.setOffset(new Vector3d(
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
        resetShownFields();
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
            cylAxisButton.setMessage(new StringTextComponent(bounds.getAxis().toString()));
            cylAxis = bounds.getAxis();
        }
        if(data.getBounds() instanceof CapsuleBounds) {
            CapsuleBounds bounds = (CapsuleBounds) data.getBounds();
            capRadius.setText(String.valueOf(bounds.getRadius()));
            capLength.setText(String.valueOf(bounds.getLength()));
            capAxisButton.setMessage(new StringTextComponent(bounds.getAxis().toString()));
            capAxis = bounds.getAxis();
        }
        if(data.getBounds() instanceof CubicBounds) {
            CubicBounds bounds = (CubicBounds) data.getBounds();
            cubicX.setText(String.valueOf(bounds.getxSize()));
            cubicY.setText(String.valueOf(bounds.getySize()));
            cubicZ.setText(String.valueOf(bounds.getzSize()));
        }
        resetShownFields();
    }

    public void setBoundType(int type) {
        boundType = type;
        buttonBounds.setMessage(new StringTextComponent(BoundsUtil.getBoundsFromType(boundType).getName()));
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
        cylAxis = AmbienceAxis.Y;
        cylAxisButton.setMessage(new StringTextComponent(cylAxis.toString()));

        capRadius.setText(String.valueOf(0));
        capLength.setText(String.valueOf(0));
        capAxis = AmbienceAxis.Y;
        capAxisButton.setMessage(new StringTextComponent(capAxis.toString()));

        cubicX.setText(String.valueOf(0));
        cubicY.setText(String.valueOf(0));
        cubicZ.setText(String.valueOf(0));
    }
}
