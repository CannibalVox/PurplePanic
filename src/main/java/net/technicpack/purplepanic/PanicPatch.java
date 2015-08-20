package net.technicpack.purplepanic;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.command.ICommandManager;
import net.minecraft.server.MinecraftServer;

public class PanicPatch {
    private int x;
    private int y;
    private int z;
    private String schematic;

    public PanicPatch(int x, int y, int z, String schematic) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.schematic = schematic;
    }

    public void execute(MinecraftServer minecraftserver) {
        ICommandManager icommandmanager = minecraftserver.getCommandManager();
        int value = icommandmanager.executeCommand(minecraftserver, "/schematicaGenerate " + schematic + " " + Integer.toString(x) + " " + Integer.toString(y) + " "+ Integer.toString(z));
        if (value < 1) {
            FMLLog.getLogger().error("Schematica failed to generate the cheat area- code "+value);
            return;
        }
    }
}
