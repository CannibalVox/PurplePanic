package net.technicpack.purplepanic;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.command.ICommandManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Mod(modid = PurplePanicMod.MODID, version = PurplePanicMod.VERSION, name = PurplePanicMod.NAME)
public class PurplePanicMod {
    public static final String MODID = "purplepanic";
    public static final String VERSION = "1.0.3";
    public static final String NAME = "Purple Panic";

    static int genX = -972;
    static int genY = 157;
    static int genZ = -1022;
    static String schematic = "CheatHub";

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        MinecraftServer minecraftserver = MinecraftServer.getServer();

        if (minecraftserver == null) {
            throw new RuntimeException("Couldn't get the instance of MinecraftServer");
        }

        World world = minecraftserver.getEntityWorld();

        if (world == null || world.provider == null || world.provider.dimensionId != 0)
            return;

        if (world.isRemote)
            return;

        FMLLog.info("Purple Panic Load World");

        File testFile = new File(world.getSaveHandler().getWorldDirectory(), "purplepanic.dat");
        if (testFile.exists())
            return;

        (new PanicPatch(-972, 157, -1022, "CheatHub")).execute(minecraftserver);
        (new PanicPatch(555, 241, 2077, "Thyrork")).execute(minecraftserver);
        (new PanicPatch(1013, 119, 2009, "KeyHolder")).execute(minecraftserver);
        (new PanicPatch(-984, 153, -1017, "FixScanner")).execute(minecraftserver);

        try {
            FileUtils.writeStringToFile(testFile, "done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        FMLLog.info("PurplePanic done loading");
    }
}
