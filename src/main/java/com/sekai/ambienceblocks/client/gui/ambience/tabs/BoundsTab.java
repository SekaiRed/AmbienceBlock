package com.sekai.ambienceblocks.client.gui.ambience.tabs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sekai.ambienceblocks.ambience.AmbienceData;
import com.sekai.ambienceblocks.ambience.bounds.*;
import com.sekai.ambienceblocks.ambience.util.AmbienceAxis;
import com.sekai.ambienceblocks.ambience.util.AmbienceWorldSpace;
import com.sekai.ambienceblocks.client.gui.ambience.AmbienceGUI;
import com.sekai.ambienceblocks.client.gui.widgets.CheckboxWidget;
import com.sekai.ambienceblocks.client.gui.widgets.TextInstance;
import com.sekai.ambienceblocks.util.BoundsUtil;
import com.sekai.ambienceblocks.util.ParsingUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BoundsTab extends AbstractTab {
    private int boundType;

    TextInstance textBounds = new TextInstance(getBaseX(), getRowY(0) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.type") + " :", font);
    Button buttonBounds = new Button(getNeighbourX(textBounds), getRowY(0) + getOffsetY(20), 60, 20, new TextComponent("no"), button -> {
        moveToNextBoundType();
    });
    /*guiRef.addButton(new Button(getNeighbourX(textBounds), getRowY(0) + getOffsetY(20), 60, 20, new TextComponent("no"), button -> {
        moveToNextBoundType();
    }));*/
    CheckboxWidget isGlobal = new CheckboxWidget(getNeighbourX(buttonBounds), getRowY(0), 20 + font.width("Global"), 20, "Global", false);
    CheckboxWidget isLocatable = new CheckboxWidget(getNeighbourX(buttonBounds), getRowY(0), 20 + font.width("Locatable"), 20, "Locatable", false);

    TextInstance textSphereRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.radius"), font);
    EditBox sphereRadius = new EditBox(font, getNeighbourX(textSphereRadius), getRowY(1), 40, 20, TextComponent.EMPTY);

    TextInstance textCylRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.radius"), font);
    EditBox cylRadius = new EditBox(font, getNeighbourX(textCylRadius), getRowY(1), 40, 20, TextComponent.EMPTY);
    TextInstance textCylLength = new TextInstance(getNeighbourX(cylRadius), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.length"), font);
    EditBox cylLength = new EditBox(font, getNeighbourX(textCylLength), getRowY(1), 40, 20, TextComponent.EMPTY);
    TextInstance textCylAxis = new TextInstance(getNeighbourX(cylLength), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.axis"), font);
    Button cylAxisButton = new Button(getNeighbourX(textCylAxis), getRowY(1) + getOffsetY(20), 20, 20, new TextComponent("X"), button -> {
        moveToNextAxisCylinder();
    });
    /*guiRef.addButton(new Button(getNeighbourX(textCylAxis), getRowY(1) + getOffsetY(20), 20, 20, new TextComponent("X"), button -> {
        moveToNextAxisCylinder();
    }));*/
    AmbienceAxis cylAxis;

    TextInstance textCapRadius = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.radius"), font);
    EditBox capRadius = new EditBox(font, getNeighbourX(textCapRadius), getRowY(1), 40, 20, TextComponent.EMPTY);
    TextInstance textCapLength = new TextInstance(getNeighbourX(capRadius), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.length"), font);
    EditBox capLength = new EditBox(font, getNeighbourX(textCapLength), getRowY(1), 40, 20, TextComponent.EMPTY);
    TextInstance textCapAxis = new TextInstance(getNeighbourX(capLength), getRowY(1) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.axis"), font);
    /*Button capAxisButton = guiRef.addButton(new Button(getNeighbourX(textCapAxis), getRowY(1) + getOffsetY(20), 20, 20, new TextComponent("X"), button -> {
        System.out.println(button.visible);
        System.out.println(button.active);
        System.out.println(this.isActive());
        moveToNextAxisCapsule();
    }));*/
    Button capAxisButton = new Button(getNeighbourX(textCapAxis), getRowY(1) + getOffsetY(20), 20, 20, new TextComponent("X"), button -> {
        moveToNextAxisCapsule();
    });
    AmbienceAxis capAxis;

    TextInstance textCubicX = new TextInstance(getBaseX(), getRowY(1) + getOffsetFontY(), 0xFFFFFF, "X", font);
    EditBox cubicX = new EditBox(font, getNeighbourX(textCubicX), getRowY(1), 40, 20, TextComponent.EMPTY);
    TextInstance textCubicY = new TextInstance(getNeighbourX(cubicX), getRowY(1) + getOffsetFontY(), 0xFFFFFF, "Y", font);
    EditBox cubicY = new EditBox(font, getNeighbourX(textCubicY), getRowY(1), 40, 20, TextComponent.EMPTY);
    TextInstance textCubicZ = new TextInstance(getNeighbourX(cubicY), getRowY(1) + getOffsetFontY(), 0xFFFFFF, "Z", font);
    EditBox cubicZ = new EditBox(font, getNeighbourX(textCubicZ), getRowY(1), 40, 20, TextComponent.EMPTY);

    TextInstance textOffset = new TextInstance(getBaseX(), getRowY(2) + getOffsetFontY(), 0xFFFFFF, I18n.get("ui.ambienceblocks.offset"), font);
    AmbienceWorldSpace offsetPos;
    Button offsetPosButton = new Button(getNeighbourX(textOffset), getRowY(2) + getOffsetY(20), 20, 20, new TextComponent("X"), button -> {
        offsetPos = offsetPos.next();
        button.setMessage(new TextComponent(offsetPos.getName()));
    });
    TextInstance textOffsetX = new TextInstance(getNeighbourX(offsetPosButton), getRowY(2) + getOffsetFontY(), 0xFFFFFF, "X", font);
    EditBox offsetX = new EditBox(font, getNeighbourX(textOffsetX), getRowY(2), 40, 20, TextComponent.EMPTY);
    TextInstance textOffsetY = new TextInstance(getNeighbourX(offsetX), getRowY(2) + getOffsetFontY(), 0xFFFFFF, "Y", font);
    EditBox offsetY = new EditBox(font, getNeighbourX(textOffsetY), getRowY(2), 40, 20, TextComponent.EMPTY);
    TextInstance textOffsetZ = new TextInstance(getNeighbourX(offsetY), getRowY(2) + getOffsetFontY(), 0xFFFFFF, "Z", font);
    EditBox offsetZ = new EditBox(font, getNeighbourX(textOffsetZ), getRowY(2), 40, 20, TextComponent.EMPTY);

    private List<AbstractWidget> sphereWidgets;// = new ArrayList<>();
    private List<AbstractWidget> cylinderWidgets;// = new ArrayList<>();
    private List<AbstractWidget> capsuleWidgets;
    private List<AbstractWidget> cubicWidgets;// = new ArrayList<>();
    private List<AbstractWidget> noneWidgets;// = new ArrayList<>();

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
        addWidget(isLocatable);

        sphereWidgets.add(textSphereRadius);
        sphereWidgets.add(sphereRadius);
        sphereRadius.setFilter(ParsingUtil.decimalNumberFilter);
        sphereRadius.setMaxLength(6);
        addWidget(sphereRadius);

        cylinderWidgets.add(textCylRadius);
        cylinderWidgets.add(cylRadius);
        cylRadius.setFilter(ParsingUtil.decimalNumberFilter);
        cylRadius.setMaxLength(6);
        addWidget(cylRadius);
        cylinderWidgets.add(textCylLength);
        cylinderWidgets.add(cylLength);
        cylLength.setFilter(ParsingUtil.decimalNumberFilter);
        cylLength.setMaxLength(6);
        addWidget(cylLength);
        cylinderWidgets.add(textCylAxis);
        cylinderWidgets.add(cylAxisButton);
        addButton(cylAxisButton);

        capsuleWidgets.add(textCapRadius);
        capsuleWidgets.add(capRadius);
        capRadius.setFilter(ParsingUtil.decimalNumberFilter);
        capRadius.setMaxLength(6);
        addWidget(capRadius);
        capsuleWidgets.add(textCapLength);
        capsuleWidgets.add(capLength);
        capLength.setFilter(ParsingUtil.decimalNumberFilter);
        capLength.setMaxLength(6);
        addWidget(capLength);
        capsuleWidgets.add(textCapAxis);
        capsuleWidgets.add(capAxisButton);
        addButton(capAxisButton);

        cubicWidgets.add(textCubicX);
        cubicWidgets.add(cubicX);
        cubicX.setFilter(ParsingUtil.decimalNumberFilter);
        cubicX.setMaxLength(6);
        addWidget(cubicX);
        cubicWidgets.add(textCubicY);
        cubicWidgets.add(cubicY);
        cubicY.setFilter(ParsingUtil.decimalNumberFilter);
        cubicY.setMaxLength(6);
        addWidget(cubicY);
        cubicWidgets.add(textCubicZ);
        cubicWidgets.add(cubicZ);
        cubicZ.setFilter(ParsingUtil.decimalNumberFilter);
        cubicZ.setMaxLength(6);
        addWidget(cubicZ);

        addButton(offsetPosButton);
        offsetX.setFilter(ParsingUtil.negativeDecimalNumberFilter);
        offsetX.setMaxLength(10);
        addWidget(offsetX);
        offsetY.setFilter(ParsingUtil.negativeDecimalNumberFilter);
        offsetY.setMaxLength(10);
        addWidget(offsetY);
        offsetZ.setFilter(ParsingUtil.negativeDecimalNumberFilter);
        offsetZ.setMaxLength(10);
        addWidget(offsetZ);

        resetBoundFields();
        setBoundType(0);

        resetShownFields();
    }

    @Override
    public void updateWidgetPosition() {
        textBounds.x = getBaseX(); textBounds.y = getRowY(0) + getOffsetFontY();
        buttonBounds.x = getNeighbourX(textBounds); buttonBounds.y = getRowY(0) + getOffsetY(20);

        isGlobal.x = getNeighbourX(buttonBounds); isGlobal.y = getRowY(0);
        isLocatable.x = getNeighbourX(isGlobal) + horizontalSeparation; isLocatable.y = getRowY(0);

        textSphereRadius.x = getBaseX(); textSphereRadius.y = getRowY(1) + getOffsetFontY();
        sphereRadius.x = getNeighbourX(textSphereRadius); sphereRadius.y = getRowY(1);

        textCylRadius.x = getBaseX(); textCylRadius.y = getRowY(1) + getOffsetFontY();
        cylRadius.x = getNeighbourX(textCylRadius); cylRadius.y = getRowY(1);
        textCylLength.x = getNeighbourX(cylRadius); textCylLength.y = getRowY(1) + getOffsetFontY();
        cylLength.x = getNeighbourX(textCylLength); cylLength.y = getRowY(1);
        textCylAxis.x = getNeighbourX(cylLength); textCylAxis.y = getRowY(1) + getOffsetFontY();
        cylAxisButton.x = getNeighbourX(textCylAxis); cylAxisButton.y = getRowY(1) + getOffsetY(20);

        textCapRadius.x = getBaseX(); textCapRadius.y = getRowY(1) + getOffsetFontY();
        capRadius.x = getNeighbourX(textCapRadius); capRadius.y = getRowY(1);
        textCapLength.x = getNeighbourX(capRadius); textCapLength.y = getRowY(1) + getOffsetFontY();
        capLength.x = getNeighbourX(textCapLength); capLength.y = getRowY(1);
        textCapAxis.x = getNeighbourX(capLength); textCapAxis.y = getRowY(1) + getOffsetFontY();
        capAxisButton.x = getNeighbourX(textCapAxis); capAxisButton.y = getRowY(1) + getOffsetY(20);

        textCubicX.x = getBaseX(); textCubicX.y = getRowY(1) + getOffsetFontY();
        cubicX.x = getNeighbourX(textCubicX); cubicX.y = getRowY(1);
        textCubicY.x = getNeighbourX(cubicX); textCubicY.y = getRowY(1) + getOffsetFontY();
        cubicY.x = getNeighbourX(textCubicY); cubicY.y = getRowY(1);
        textCubicZ.x = getNeighbourX(cubicY); textCubicZ.y = getRowY(1) + getOffsetFontY();
        cubicZ.x = getNeighbourX(textCubicZ); cubicZ.y = getRowY(1);

        textOffset.x = getBaseX(); textOffset.y = getRowY(2) + getOffsetFontY();
        offsetPosButton.x = getNeighbourX(textOffset); offsetPosButton.y = getRowY(2) + getOffsetY(20);
        textOffsetX.x = getNeighbourX(offsetPosButton); textOffsetX.y = getRowY(2) + getOffsetFontY();
        offsetX.x = getNeighbourX(textOffsetX); offsetX.y = getRowY(2);
        textOffsetY.x = getNeighbourX(offsetX); textOffsetY.y = getRowY(2) + getOffsetFontY();
        offsetY.x = getNeighbourX(textOffsetY); offsetY.y = getRowY(2);
        textOffsetZ.x = getNeighbourX(offsetY); textOffsetZ.y = getRowY(2) + getOffsetFontY();
        offsetZ.x = getNeighbourX(textOffsetZ); offsetZ.y = getRowY(2);
    }

    private void moveToNextAxisCapsule() {
        capAxis = capAxis.next();
        capAxisButton.setMessage(new TextComponent(capAxis.toString()));
    }

    private void moveToNextAxisCylinder() {
        cylAxis = cylAxis.next();
        cylAxisButton.setMessage(new TextComponent(cylAxis.toString()));
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        textBounds.render(matrix, mouseX, mouseY);
        buttonBounds.render(matrix, mouseX, mouseY, partialTicks);
        isGlobal.render(matrix, mouseX, mouseY, partialTicks);
        isLocatable.render(matrix, mouseX, mouseY, partialTicks);

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
        offsetPosButton.render(matrix, mouseX, mouseY, partialTicks);
        textOffsetX.render(matrix, mouseX, mouseY);
        offsetX.render(matrix, mouseX, mouseY, partialTicks);
        textOffsetY.render(matrix, mouseX, mouseY);
        offsetY.render(matrix, mouseX, mouseY, partialTicks);
        textOffsetZ.render(matrix, mouseX, mouseY);
        offsetZ.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(PoseStack matrix, int mouseX, int mouseY) {
        List<String> list = new ArrayList<String>();

        if (buttonBounds.isHoveredOrFocused())
        {
            if(boundType == SphereBounds.id) {
                list.add(ChatFormatting.RED + "Sphere Bounds");
                list.add(ChatFormatting.WHITE + "Sphere bounds that cover a radius all around it.");
                list.add(ChatFormatting.GRAY + "(If the sound isn't global, the volume will be scaled by the distance of the player from the source)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
            }

            if(boundType == CylinderBounds.id) {
                list.add(ChatFormatting.RED + "Cylinder Bounds");
                list.add(ChatFormatting.WHITE + "Cylinder bounds that cover a radius all around it except the height which can be changed.");
                list.add(ChatFormatting.GRAY + "(If the sound isn't global, the volume will be unaffected by height and only scaled by horizontal distance)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
            }

            if(boundType == CapsuleBounds.id) {
                list.add(ChatFormatting.RED + "Capsule Bounds");
                list.add(ChatFormatting.WHITE + "Capsule bounds that work almost exactly like the cylinder variant, it adds two sphere detection to each end.");
                list.add(ChatFormatting.GRAY + "(If the sound isn't global, the volume will be unaffected by height and only scaled by horizontal distance (and regular sphere stuff on each ends))");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
            }

            if(boundType == CubicBounds.id) {
                list.add(ChatFormatting.RED + "Cubic Bounds");
                list.add(ChatFormatting.WHITE + "Cubic bounds that cover a cubic area of origin by the value of x, y and z.");
                list.add(ChatFormatting.GRAY + "(If the sound isn't global, the volume will be scaled as if this was a Sphere Bounds which won't cover the whole area)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
            }

            if(boundType == NoneBounds.id) {
                list.add(ChatFormatting.RED + "None Bounds");
                list.add(ChatFormatting.WHITE + "Will always play as long as the tile is loaded on the client.");
                list.add(ChatFormatting.GRAY + "(The volume will not change with distance regardless of if the tile is set to be global)");
                drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
            }
        }

        if(isGlobal.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Global");
            list.add("If global is checked the volume won't scale with distance, otherwise it will.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }

        if(isLocatable.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Locatable");
            list.add("If locatable is checked the sound will play in a 3D space, otherwise it plays globally like menu sounds.");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
        }

        if(textOffset.isHoveredOrFocused()) {
            list.add(ChatFormatting.RED + "Offset");
            list.add("Shifts the origin of the bounds to a certain position.");
            list.add(ChatFormatting.GRAY + "(Useful for hiding the block)");
            drawHoveringText(matrix, list, mouseX + 3, mouseY + 3, width, height, width / 2);
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
    public void setFieldFromData(AmbienceData data) {
        //setCheckBoxChecked(isGlobal, data.isGlobal());
        isGlobal.setChecked(data.isGlobal());
        isLocatable.setChecked(data.isLocatable());

        offsetPos = data.getSpace();
        offsetPosButton.setMessage(new TextComponent(offsetPos.getName()));

        loadBoundType(data);

        Vec3 pos = data.getOffset();
        offsetX.setValue(String.valueOf(pos.x()));
        offsetY.setValue(String.valueOf(pos.y()));
        offsetZ.setValue(String.valueOf(pos.z()));
    }

    @Override
    public void setDataFromField(AmbienceData data) {
        data.setGlobal(isGlobal.isChecked());
        data.setLocatable(isLocatable.isChecked());

        data.setSpace(offsetPos);

        if(boundType == SphereBounds.id) {
            SphereBounds bounds = new SphereBounds();

            bounds.setRadius(ParsingUtil.tryParseDouble(sphereRadius.getValue()));

            data.setBounds(bounds);
        }

        if(boundType == CylinderBounds.id) {
            CylinderBounds bounds = new CylinderBounds();

            bounds.setRadius(ParsingUtil.tryParseDouble(cylRadius.getValue()));
            bounds.setLength(ParsingUtil.tryParseDouble(cylLength.getValue()));
            bounds.setAxis(cylAxis);

            data.setBounds(bounds);
        }

        if(boundType == CapsuleBounds.id) {
            CapsuleBounds bounds = new CapsuleBounds();

            bounds.setRadius(ParsingUtil.tryParseDouble(capRadius.getValue()));
            bounds.setLength(ParsingUtil.tryParseDouble(capLength.getValue()));
            bounds.setAxis(capAxis);

            data.setBounds(bounds);
        }

        if(boundType == CubicBounds.id) {
            CubicBounds bounds = new CubicBounds();

            bounds.setxSize(ParsingUtil.tryParseDouble(cubicX.getValue()));
            bounds.setySize(ParsingUtil.tryParseDouble(cubicY.getValue()));
            bounds.setzSize(ParsingUtil.tryParseDouble(cubicZ.getValue()));

            data.setBounds(bounds);
        }

        if(boundType == NoneBounds.id) {
            data.setBounds(new NoneBounds());
        }

        data.setOffset(new Vec3(
                ParsingUtil.tryParseDouble(offsetX.getValue()),
                ParsingUtil.tryParseDouble(offsetY.getValue()),
                ParsingUtil.tryParseDouble(offsetZ.getValue())
        ));

        //data.setOffset(new BlockPos(offsetX.getValue()));
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
            sphereRadius.setValue(String.valueOf(bounds.getRadius()));
        }
        if(data.getBounds() instanceof CylinderBounds) {
            CylinderBounds bounds = (CylinderBounds) data.getBounds();
            cylRadius.setValue(String.valueOf(bounds.getRadius()));
            cylLength.setValue(String.valueOf(bounds.getLength()));
            cylAxisButton.setMessage(new TextComponent(bounds.getAxis().toString()));
            cylAxis = bounds.getAxis();
        }
        if(data.getBounds() instanceof CapsuleBounds) {
            CapsuleBounds bounds = (CapsuleBounds) data.getBounds();
            capRadius.setValue(String.valueOf(bounds.getRadius()));
            capLength.setValue(String.valueOf(bounds.getLength()));
            capAxisButton.setMessage(new TextComponent(bounds.getAxis().toString()));
            capAxis = bounds.getAxis();
        }
        if(data.getBounds() instanceof CubicBounds) {
            CubicBounds bounds = (CubicBounds) data.getBounds();
            cubicX.setValue(String.valueOf(bounds.getxSize()));
            cubicY.setValue(String.valueOf(bounds.getySize()));
            cubicZ.setValue(String.valueOf(bounds.getzSize()));
        }
        resetShownFields();
    }

    public void setBoundType(int type) {
        boundType = type;
        buttonBounds.setMessage(new TextComponent(BoundsUtil.getBoundsFromType(boundType).getName()));
        updateBoundsField();
    }

    public void updateBoundsField() {
        resetShownFields();
        //System.out.println(BoundsUtil.getBoundsFromType(boundType).getName());
        //System.out.println(sphereWidgets.size());
        if(boundType == SphereBounds.id) for(AbstractWidget widget : sphereWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CylinderBounds.id) for(AbstractWidget widget : cylinderWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CapsuleBounds.id) for(AbstractWidget widget : capsuleWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == CubicBounds.id) for(AbstractWidget widget : cubicWidgets) { widget.active = true; widget.visible = true; }
        if(boundType == NoneBounds.id) for(AbstractWidget widget : noneWidgets) { widget.active = true; widget.visible = true; }
    }

    public void resetShownFields() {
        for(AbstractWidget widget : sphereWidgets) { widget.active = false; widget.visible = false; }
        for(AbstractWidget widget : cylinderWidgets) { widget.active = false; widget.visible = false; }
        for(AbstractWidget widget : capsuleWidgets) { widget.active = false; widget.visible = false; }
        for(AbstractWidget widget : cubicWidgets) { widget.active = false; widget.visible = false; }
        for(AbstractWidget widget : noneWidgets) { widget.active = false; widget.visible = false; }
    }

    public void resetBoundFields() {
        sphereRadius.setValue(String.valueOf(0));

        cylRadius.setValue(String.valueOf(0));
        cylLength.setValue(String.valueOf(0));
        cylAxis = AmbienceAxis.Y;
        cylAxisButton.setMessage(new TextComponent(cylAxis.toString()));

        capRadius.setValue(String.valueOf(0));
        capLength.setValue(String.valueOf(0));
        capAxis = AmbienceAxis.Y;
        capAxisButton.setMessage(new TextComponent(capAxis.toString()));

        cubicX.setValue(String.valueOf(0));
        cubicY.setValue(String.valueOf(0));
        cubicZ.setValue(String.valueOf(0));
    }
}
