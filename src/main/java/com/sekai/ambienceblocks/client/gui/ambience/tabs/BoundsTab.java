package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Button;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Checkbox;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.TextField;
import com.sekai.ambienceblocks.client.gui.widgets.ambience.Widget;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.bounds.*;
import com.sekai.ambienceblocks.ambience.util.AmbienceAxis;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.util.BoundsUtil;
import com.sekai.ambienceblocks.util.ParsingUtil;
import com.sekai.ambienceblocks.util.Vector3d;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

//TODO URGENT :
public class BoundsTab extends AbstractTab {
    private int boundType;

    TextInstance textBounds = new TextInstance(getBaseX(), getRowY(0) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.type") + " :", font);
    Button buttonBounds = new Button(getNeighbourX(textBounds), getRowY(0) + getOffsetY(20), 60, 20, new TextComponentString("no"), button -> {
        moveToNextBoundType();
    });
    Checkbox isGlobal = new Checkbox(getNeighbourX(buttonBounds), getRowY(0), 20 + font.getStringWidth("Global"), 20, new TextComponentString("Global"), false);

    TextInstance textSphereRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.radius"), font);
    TextField sphereRadius = new TextField(font, getNeighbourX(textSphereRadius), getRowY(1), 40, 20, new TextComponentString(""));

    TextInstance textCylRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.radius"), font);
    TextField cylRadius = new TextField(font, getNeighbourX(textCylRadius), getRowY(1), 40, 20, new TextComponentTranslation(""));
    TextInstance textCylLength = new TextInstance(getNeighbourX(cylRadius), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.length"), font);
    TextField cylLength = new TextField(font, getNeighbourX(textCylLength), getRowY(1), 40, 20, new TextComponentTranslation(""));
    TextInstance textCylAxis = new TextInstance(getNeighbourX(cylLength), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.axis"), font);
    Button cylAxisButton = new Button(getNeighbourX(textCylAxis), getRowY(1) + getOffsetY(20), 20, 20, new TextComponentTranslation("X"), button -> {
        moveToNextAxisCylinder();
    });
    AmbienceAxis cylAxis;

    TextInstance textCapRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.radius"), font);
    TextField capRadius = new TextField(font, getNeighbourX(textCapRadius), getRowY(1), 40, 20, new TextComponentTranslation(""));
    TextInstance textCapLength = new TextInstance(getNeighbourX(capRadius), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.length"), font);
    TextField capLength = new TextField(font, getNeighbourX(textCapLength), getRowY(1), 40, 20, new TextComponentTranslation(""));
    TextInstance textCapAxis = new TextInstance(getNeighbourX(capLength), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.axis"), font);
    /*Button capAxisButton = guiRef.addButton(new Button(getNeighbourX(textCapAxis), getRowY(1) + getOffsetY(20), 20, 20, new StringTextComponent("X"), button -> {
        System.out.println(button.visible);
        System.out.println(button.active);
        System.out.println(this.isActive());
        moveToNextAxisCapsule();
    }));*/
    Button capAxisButton = new Button(getNeighbourX(textCapAxis), getRowY(1) + getOffsetY(20), 20, 20, new TextComponentTranslation("X"), button -> {
        moveToNextAxisCapsule();
    });
    AmbienceAxis capAxis;

    TextInstance textCubicX = new TextInstance(getBaseX(), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "X", font);
    TextField cubicX = new TextField(font, getNeighbourX(textCubicX), getRowY(1), 40, 20, new TextComponentTranslation(""));
    TextInstance textCubicY = new TextInstance(getNeighbourX(cubicX), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Y", font);
    TextField cubicY = new TextField(font, getNeighbourX(textCubicY), getRowY(1), 40, 20, new TextComponentTranslation(""));
    TextInstance textCubicZ = new TextInstance(getNeighbourX(cubicY), getRowY(1) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Z", font);
    TextField cubicZ = new TextField(font, getNeighbourX(textCubicZ), getRowY(1), 40, 20, new TextComponentTranslation(""));

    TextInstance textOffset = new TextInstance(getBaseX(), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, I18n.format("ui.ambienceblocks.offset"), font);
    AmbienceWorldSpace offsetPos;
    Button offsetPosButton = new Button(getNeighbourX(textOffset), getRowY(2) + getOffsetY(20), 20, 20, new TextComponentString("X"), button -> {
        offsetPos = offsetPos.next();
        button.setMessage(new TextComponentString(offsetPos.getName()));
    });
    TextInstance textOffsetX = new TextInstance(getNeighbourX(offsetPosButton), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "X", font);
    TextField offsetX = new TextField(font, getNeighbourX(textOffsetX), getRowY(2), 40, 20, new TextComponentTranslation(""));
    TextInstance textOffsetY = new TextInstance(getNeighbourX(offsetX), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Y", font);
    TextField offsetY = new TextField(font, getNeighbourX(textOffsetY), getRowY(2), 40, 20, new TextComponentTranslation(""));
    TextInstance textOffsetZ = new TextInstance(getNeighbourX(offsetY), getRowY(2) + getOffsetY(font.FONT_HEIGHT), 0xFFFFFF, "Z", font);
    TextField offsetZ = new TextField(font, getNeighbourX(textOffsetZ), getRowY(2), 40, 20, new TextComponentTranslation(""));

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

        //addButton(buttonBounds);
        addWidget(textBounds);
        addWidget(buttonBounds);
        addWidget(isGlobal);

        addWidget(textSphereRadius);
        sphereWidgets.add(textSphereRadius);
        sphereWidgets.add(sphereRadius);
        sphereRadius.setValidator(ParsingUtil.decimalNumberFilter);
        sphereRadius.setMaxStringLength(6);
        addWidget(sphereRadius);

        addWidget(textCylRadius);
        cylinderWidgets.add(textCylRadius);
        cylinderWidgets.add(cylRadius);
        cylRadius.setValidator(ParsingUtil.decimalNumberFilter);
        cylRadius.setMaxStringLength(6);
        addWidget(cylRadius);
        addWidget(textCylLength);
        cylinderWidgets.add(textCylLength);
        cylinderWidgets.add(cylLength);
        cylLength.setValidator(ParsingUtil.decimalNumberFilter);
        cylLength.setMaxStringLength(6);
        addWidget(cylLength);
        addWidget(textCylAxis);
        cylinderWidgets.add(textCylAxis);
        cylinderWidgets.add(cylAxisButton);
        addWidget(cylAxisButton);
        //addButton(cylAxisButton);

        addWidget(textCapRadius);
        capsuleWidgets.add(textCapRadius);
        capsuleWidgets.add(capRadius);
        capRadius.setValidator(ParsingUtil.decimalNumberFilter);
        capRadius.setMaxStringLength(6);
        addWidget(capRadius);
        addWidget(textCapLength);
        capsuleWidgets.add(textCapLength);
        capsuleWidgets.add(capLength);
        capLength.setValidator(ParsingUtil.decimalNumberFilter);
        capLength.setMaxStringLength(6);
        addWidget(capLength);
        addWidget(textCapAxis);
        capsuleWidgets.add(textCapAxis);
        capsuleWidgets.add(capAxisButton);
        addWidget(capAxisButton);

        addWidget(textCubicX);
        cubicWidgets.add(textCubicX);
        cubicWidgets.add(cubicX);
        cubicX.setValidator(ParsingUtil.decimalNumberFilter);
        cubicX.setMaxStringLength(6);
        addWidget(cubicX);
        addWidget(textCubicY);
        cubicWidgets.add(textCubicY);
        cubicWidgets.add(cubicY);
        cubicY.setValidator(ParsingUtil.decimalNumberFilter);
        cubicY.setMaxStringLength(6);
        addWidget(cubicY);
        addWidget(textCubicZ);
        cubicWidgets.add(textCubicZ);
        cubicWidgets.add(cubicZ);
        cubicZ.setValidator(ParsingUtil.decimalNumberFilter);
        cubicZ.setMaxStringLength(6);
        addWidget(cubicZ);

        addWidget(textOffset);
        addWidget(offsetPosButton);
        offsetX.setValidator(ParsingUtil.negativeDecimalNumberFilter);
        offsetX.setMaxStringLength(10);
        addWidget(textOffsetX);
        addWidget(offsetX);
        offsetY.setValidator(ParsingUtil.negativeDecimalNumberFilter);
        offsetY.setMaxStringLength(10);
        addWidget(textOffsetY);
        addWidget(offsetY);
        offsetZ.setValidator(ParsingUtil.negativeDecimalNumberFilter);
        offsetZ.setMaxStringLength(10);
        addWidget(textOffsetZ);
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
        sphereRadius.forceUpdatePosition();

        textCylRadius.x = getBaseX(); textCylRadius.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cylRadius.x = getNeighbourX(textCylRadius); cylRadius.y = getRowY(1);
        cylRadius.forceUpdatePosition();
        textCylLength.x = getNeighbourX(cylRadius); textCylLength.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cylLength.x = getNeighbourX(textCylLength); cylLength.y = getRowY(1);
        cylLength.forceUpdatePosition();
        textCylAxis.x = getNeighbourX(cylLength); textCylAxis.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cylAxisButton.x = getNeighbourX(textCylAxis); cylAxisButton.y = getRowY(1) + getOffsetY(20);

        textCapRadius.x = getBaseX(); textCapRadius.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        capRadius.x = getNeighbourX(textCapRadius); capRadius.y = getRowY(1);
        capRadius.forceUpdatePosition();
        textCapLength.x = getNeighbourX(capRadius); textCapLength.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        capLength.x = getNeighbourX(textCapLength); capLength.y = getRowY(1);
        capLength.forceUpdatePosition();
        textCapAxis.x = getNeighbourX(capLength); textCapAxis.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        capAxisButton.x = getNeighbourX(textCapAxis); capAxisButton.y = getRowY(1) + getOffsetY(20);

        textCubicX.x = getBaseX(); textCubicX.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cubicX.x = getNeighbourX(textCubicX); cubicX.y = getRowY(1);
        cubicX.forceUpdatePosition();
        textCubicY.x = getNeighbourX(cubicX); textCubicY.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cubicY.x = getNeighbourX(textCubicY); cubicY.y = getRowY(1);
        cubicY.forceUpdatePosition();
        textCubicZ.x = getNeighbourX(cubicY); textCubicZ.y = getRowY(1) + getOffsetY(font.FONT_HEIGHT);
        cubicZ.x = getNeighbourX(textCubicZ); cubicZ.y = getRowY(1);
        cubicZ.forceUpdatePosition();

        textOffset.x = getBaseX(); textOffset.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        offsetPosButton.x = getNeighbourX(textOffset); offsetPosButton.y = getRowY(2) + getOffsetY(20);
        textOffsetX.x = getNeighbourX(offsetPosButton); textOffsetX.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        offsetX.x = getNeighbourX(textOffsetX); offsetX.y = getRowY(2);
        offsetX.forceUpdatePosition();
        textOffsetY.x = getNeighbourX(offsetX); textOffsetY.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        offsetY.x = getNeighbourX(textOffsetY); offsetY.y = getRowY(2);
        offsetY.forceUpdatePosition();
        textOffsetZ.x = getNeighbourX(offsetY); textOffsetZ.y = getRowY(2) + getOffsetY(font.FONT_HEIGHT);
        offsetZ.x = getNeighbourX(textOffsetZ); offsetZ.y = getRowY(2);
        offsetZ.forceUpdatePosition();
    }

    private void moveToNextAxisCapsule() {
        capAxis = capAxis.next();
        capAxisButton.setMessage(new TextComponentString(capAxis.toString()));
    }

    private void moveToNextAxisCylinder() {
        cylAxis = cylAxis.next();
        cylAxisButton.setMessage(new TextComponentString(cylAxis.toString()));
    }

    @Override
    public void render(int mouseX, int mouseY) {
        /*textBounds.render(mouseX, mouseY);
        buttonBounds.render(mouseX, mouseY, partialTicks);
        isGlobal.render(mouseX, mouseY, partialTicks);

        textSphereRadius.render(mouseX, mouseY);
        sphereRadius.render(mouseX, mouseY, partialTicks);

        textCylRadius.render(mouseX, mouseY);
        cylRadius.render(mouseX, mouseY, partialTicks);
        textCylLength.render(mouseX, mouseY);
        cylLength.render(mouseX, mouseY, partialTicks);
        textCylAxis.render(mouseX, mouseY);
        cylAxisButton.render(mouseX, mouseY, partialTicks);

        textCapRadius.render(mouseX, mouseY);
        capRadius.render(mouseX, mouseY, partialTicks);
        textCapLength.render(mouseX, mouseY);
        capLength.render(mouseX, mouseY, partialTicks);
        textCapAxis.render(mouseX, mouseY);
        capAxisButton.render(mouseX, mouseY, partialTicks);

        textCubicX.render(mouseX, mouseY);
        cubicX.render(mouseX, mouseY, partialTicks);
        textCubicY.render(mouseX, mouseY);
        cubicY.render(mouseX, mouseY, partialTicks);
        textCubicZ.render(mouseX, mouseY);
        cubicZ.render(mouseX, mouseY, partialTicks);

        textOffset.render(mouseX, mouseY);
        offsetPosButton.render(mouseX, mouseY, partialTicks);
        textOffsetX.render(mouseX, mouseY);
        offsetX.render(mouseX, mouseY, partialTicks);
        textOffsetY.render(mouseX, mouseY);
        offsetY.render(mouseX, mouseY, partialTicks);
        textOffsetZ.render(mouseX, mouseY);
        offsetZ.render(mouseX, mouseY, partialTicks);*/
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if (buttonBounds.isHovered())
        {
            if(boundType == SphereBounds.id) {
                list.add(TextFormatting.RED + "Sphere Bounds");
                list.add(TextFormatting.WHITE + "Sphere bounds that cover a radius all around it.");
                list.add(TextFormatting.GRAY + "(If the sound isn't global, the volume will be scaled by the distance of the player from the source)");
                drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
            }

            if(boundType == CylinderBounds.id) {
                list.add(TextFormatting.RED + "Cylinder Bounds");
                list.add(TextFormatting.WHITE + "Cylinder bounds that cover a radius all around it except the height which can be changed.");
                list.add(TextFormatting.GRAY + "(If the sound isn't global, the volume will be unaffected by height and only scaled by horizontal distance)");
                drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
            }

            if(boundType == CapsuleBounds.id) {
                list.add(TextFormatting.RED + "Capsule Bounds");
                list.add(TextFormatting.WHITE + "Capsule bounds that work almost exactly like the cylinder variant, it adds two sphere detection to each end.");
                list.add(TextFormatting.GRAY + "(If the sound isn't global, the volume will be unaffected by height and only scaled by horizontal distance (and regular sphere stuff on each ends))");
                drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
            }

            if(boundType == CubicBounds.id) {
                list.add(TextFormatting.RED + "Cubic Bounds");
                list.add(TextFormatting.WHITE + "Cubic bounds that cover a cubic area of origin by the value of x, y and z.");
                list.add(TextFormatting.GRAY + "(If the sound isn't global, the volume will be scaled as if this was a Sphere Bounds which won't cover the whole area)");
                drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
            }

            if(boundType == NoneBounds.id) {
                list.add(TextFormatting.RED + "None Bounds");
                list.add(TextFormatting.WHITE + "Will always play as long as the tile is loaded on the client.");
                list.add(TextFormatting.GRAY + "(The volume will not change with distance regardless of if the tile is set to be global)");
                drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
            }
        }

        if(isGlobal.isHovered()) {
            list.add(TextFormatting.RED + "Global");
            list.add("If global is checked the volume won't scale with distance, otherwise it will.");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }

        if(textOffset.isHovered()) {
            list.add(TextFormatting.RED + "Offset");
            list.add("Shifts the origin of the bounds to a certain position.");
            list.add(TextFormatting.GRAY + "(Useful for hiding the block)");
            drawHoveringText(list, mouseX + 3, mouseY + 3, width, height, width / 2, font);
        }
    }

    @Override
    public void tick() {
        /*sphereRadius.tick();

        cylRadius.tick();
        cylLength.tick();

        cubicX.tick();
        cubicY.tick();
        cubicZ.tick();*/
    }

    @Override
    public void setFieldFromData(AmbienceData data) {
        //setCheckBoxChecked(isGlobal, data.isGlobal());
        isGlobal.setChecked(data.isGlobal());

        offsetPos = data.getSpace();
        offsetPosButton.setMessage(new TextComponentString(offsetPos.getName()));

        loadBoundType(data);

        Vector3d pos = data.getOffset();
        offsetX.setText(String.valueOf(pos.getX()));
        offsetY.setText(String.valueOf(pos.getY()));
        offsetZ.setText(String.valueOf(pos.getZ()));
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setGlobal(isGlobal.isChecked());

        data.setSpace(offsetPos);

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

    public void loadBoundType(AmbienceData data) {
        setBoundType(data.getBounds().getID());
        if(data.getBounds() instanceof SphereBounds) {
            SphereBounds bounds = (SphereBounds) data.getBounds();
            sphereRadius.setText(String.valueOf(bounds.getRadius()));
        }
        if(data.getBounds() instanceof CylinderBounds) {
            CylinderBounds bounds = (CylinderBounds) data.getBounds();
            cylRadius.setText(String.valueOf(bounds.getRadius()));
            cylLength.setText(String.valueOf(bounds.getLength()));
            cylAxisButton.setMessage(new TextComponentString(bounds.getAxis().toString()));
            cylAxis = bounds.getAxis();
        }
        if(data.getBounds() instanceof CapsuleBounds) {
            CapsuleBounds bounds = (CapsuleBounds) data.getBounds();
            capRadius.setText(String.valueOf(bounds.getRadius()));
            capLength.setText(String.valueOf(bounds.getLength()));
            capAxisButton.setMessage(new TextComponentString(bounds.getAxis().toString()));
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
        buttonBounds.setMessage(new TextComponentTranslation(BoundsUtil.getBoundsFromType(boundType).getName()));
        updateBoundsField();
    }

    public void updateBoundsField() {
        resetShownFields();
        //System.out.println(BoundsUtil.getBoundsFromType(boundType).getName());
        //System.out.println(sphereWidgets.size());
        /*if(boundType == SphereBounds.id) for(Widget widget : sphereWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CylinderBounds.id) for(Widget widget : cylinderWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CapsuleBounds.id) for(Widget widget : capsuleWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CubicBounds.id) for(Widget widget : cubicWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == NoneBounds.id) for(Widget widget : noneWidgets) { widget.active = true; widget.visible = true; }*/
        switch (boundType) {
            case SphereBounds.id: setWidgetBounds(sphereWidgets, true); break;
            case CylinderBounds.id: setWidgetBounds(cylinderWidgets, true); break;
            case CapsuleBounds.id: setWidgetBounds(capsuleWidgets, true); break;
            case CubicBounds.id: setWidgetBounds(cubicWidgets, true); break;
            case NoneBounds.id: setWidgetBounds(noneWidgets, true); break;
        }
    }

    public void resetShownFields() {
        setWidgetBounds(sphereWidgets, false);
        setWidgetBounds(cylinderWidgets, false);
        setWidgetBounds(capsuleWidgets, false);
        setWidgetBounds(cubicWidgets, false);
        setWidgetBounds(noneWidgets, false);
        /*for(Widget widget : sphereWidgets) { widget.active = false; widget.visible = false; }
        for(Widget widget : cylinderWidgets) { widget.active = false; widget.visible = false; }
        for(Widget widget : capsuleWidgets) { widget.active = false; widget.visible = false; }
        for(Widget widget : cubicWidgets) { widget.active = false; widget.visible = false; }
        for(Widget widget : noneWidgets) { widget.active = false; widget.visible = false; }*/
    }

    private void setWidgetBounds(List<Widget> widgets, boolean state) {
        for(Widget widget : widgets) {
            widget.active = state;
            widget.setVisible(state);
            //widget.visible = false;
        }
    }

    public void resetBoundFields() {
        sphereRadius.setText(String.valueOf(0));

        cylRadius.setText(String.valueOf(0));
        cylLength.setText(String.valueOf(0));
        cylAxis = AmbienceAxis.Y;
        cylAxisButton.setMessage(new TextComponentTranslation(cylAxis.toString()));

        capRadius.setText(String.valueOf(0));
        capLength.setText(String.valueOf(0));
        capAxis = AmbienceAxis.Y;
        capAxisButton.setMessage(new TextComponentTranslation(capAxis.toString()));

        cubicX.setText(String.valueOf(0));
        cubicY.setText(String.valueOf(0));
        cubicZ.setText(String.valueOf(0));
    }
}
