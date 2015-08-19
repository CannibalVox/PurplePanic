package net.technicpack.purplepanic;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.command.ICommandManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Mod(modid = PurplePanicMod.MODID, version = PurplePanicMod.VERSION, name = PurplePanicMod.NAME)
public class PurplePanicMod {
    public static final String MODID = "purplepanic";
    public static final String VERSION = "1.0";
    public static final String NAME = "Purple Panic";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    static int genX = -972;
    static int genY = 157;
    static int genZ = -1022;
    static String schematic = "CheatHub";

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world == null || event.world.provider == null || event.world.provider.dimensionId != 0)
            return;

        if (event.world.isRemote)
            return;

        File testFile = new File(event.world.getSaveHandler().getWorldDirectory(), "purplepanic.dat");
        if (testFile.exists())
            return;

        MinecraftServer minecraftserver = MinecraftServer.getServer();

        if (minecraftserver == null) {
            throw new RuntimeException("Couldn't get the instance of MinecraftServer");
        }

        ICommandManager icommandmanager = minecraftserver.getCommandManager();
        if (icommandmanager.executeCommand(RConConsoleSource.instance, "/schematicaGenerate " + schematic + " " + Integer.toString(genX) + " " + Integer.toString(genY) + " "+ Integer.toString(genZ)) < 1) {
            System.err.print("Schematica failed to generate the cheat area.");
            return;
        }

        try {
            FileUtils.writeStringToFile(testFile, "done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
