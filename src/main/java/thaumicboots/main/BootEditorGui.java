package thaumicboots.main;

import com.cleanroommc.modularui.ModularUI;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IIcon;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.*;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.factory.SimpleGuiFactory;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.*;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.sizer.Box;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SliderWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextEditorWidget;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import scala.swing.Applet;
import thaumicboots.api.ThaumicBootsAPI;
import thaumicboots.main.utils.VersionInfo;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class BootEditorGui implements IGuiHolder<GuiData> {

    private static final SimpleGuiFactory GUI = new SimpleGuiFactory("mui:boot_editor", BootEditorGui::new);

    @Override
    public ModularScreen createScreen(GuiData data, ModularPanel mainPanel) {
        return new ModularScreen(ModularUI.ID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(GuiData data, PanelSyncManager syncManager, UISettings settings) {
        ItemStack itemStack = data.getPlayer().getCurrentArmor(0);

        syncManager.syncValue("display_item", GenericSyncValue.forItem(() -> itemStack, null));
        syncManager.syncValue("speed_boost", new DoubleSyncValue(
            () -> itemStack.stackTagCompound.getDouble("speed"),
            speed -> itemStack.stackTagCompound.setDouble("speed", Math.round(speed * 100) / 100.0)
            )
        );
        syncManager.syncValue("jump_boost", new DoubleSyncValue(
            () -> itemStack.stackTagCompound.getDouble("jump"),
            jump -> itemStack.stackTagCompound.setDouble("jump", Math.round(jump * 100) / 100.0)
            )
        );
        syncManager.syncValue("charge", new DoubleSyncValue(() -> itemStack.stackTagCompound.getDouble("charge")));

        return ModularPanel.defaultPanel("boot_editor")
            .child(Flow.col()
                .crossAxisAlignment(Alignment.CrossAxis.START)
                .childPadding(4)
                .margin(7)
                .child(IKey.str("Modulation Control").asWidget())
                .child(Flow.row()
                    .childPadding(0)
                    .height(32)
                    .child(new ItemDisplayWidget()
                        .syncHandler("display_item")
                        .size(32,32)
                    )
                    .child(new SliderWidget()
                        .syncHandler("speed_boost")
                        .bounds(0, 1)
                        .expanded()
                        .background(new Rectangle().color(0xFF0000)) // wanna do a custom texture later
                    )
                    .child(new TextFieldWidget()
                        .syncHandler("speed_boost")
                        .size(32,32)
                        .setTextAlignment(Alignment.CENTER)
                    )
                )
                .child(Flow.row()
                    .childPadding(0)
                    .height(32)
                    .child(new ItemDisplayWidget()
                        .syncHandler("display_item")
                        .size(32,32)
                    )
                    .child(new SliderWidget()
                        .syncHandler("jump_boost")
                        .bounds(0, 1)
                        .expanded()
                        .background(new Rectangle().color(0xFF0000)) // wanna do a custom texture later
                    )
                    .child(new TextFieldWidget()
                        .syncHandler("jump_boost")
                        .size(32,32)
                        .setTextAlignment(Alignment.CENTER)
                    )
                )
            );
    }

    public static class Command extends CommandBase {

        @Override
        public String getCommandName() {
            return "bootEditor";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/bootEditor";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (sender instanceof EntityPlayerMP entityPlayerMP && entityPlayerMP.capabilities.isCreativeMode) {
                GUI.open(entityPlayerMP);
            } else {
                throw new CommandException("Player must be creative mode!");
            }
        }
    }
}