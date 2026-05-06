package thaumicboots.main;

import com.cleanroommc.modularui.ModularUI;
import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.factory.SimpleGuiFactory;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
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

        ModularPanel panel = ModularPanel.defaultPanel("boot_editor");
        return panel.child(Flow.col()
                        .crossAxisAlignment(Alignment.CrossAxis.START)
                        .sizeRel(1f)
                        .margin(7)
                        .child(IKey.str("Modulation Control").asWidget()))
                        .child(new Grid()
                                .grid(createGrid(itemStack, syncManager))
                                .sizeRel(1f)
                                .margin(7,7,19,7)
                        )
                ;
    }

    private List<List<IWidget>> createGrid(ItemStack toDisplay, PanelSyncManager syncManager) {
        List<List<IWidget>> cells = new ArrayList<>();

        for (int i = 0; i < 2; i++){
            ArrayList<IWidget> row0 = new ArrayList<>();
            row0.add(new ItemDisplayWidget().item(toDisplay).size(32, 32));
            row0.add(new ProgressWidget().value(new DoubleValue(0.69)).size(64, 32));
            row0.add(new TextFieldWidget()
                            .size(32, 32)
                            .value(new StringSyncValue(() -> {
                                return "69%";
                            }, val -> {
//                    if (!syncManager.isClient()) {
//                        try {
//                            // do nbt stuff
//                        } catch (NBTException ignored) {
//                        }
//                    }
                            }))
                            .background(IDrawable.EMPTY)
                            .setNumbers(0, 100)
            );

            cells.add(row0);
        }

        return cells;
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