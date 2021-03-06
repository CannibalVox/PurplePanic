package net.technicpack.purplepanic;

import com.google.gson.*;
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
import java.util.Iterator;

@Mod(modid = PurplePanicMod.MODID, version = PurplePanicMod.VERSION, name = PurplePanicMod.NAME)
public class PurplePanicMod {
    public static final String MODID = "purplepanic";
    public static final String VERSION = "1.0.6";
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

        int currentVersion = 0;
        if (testFile.exists()) {
            String versionStr = "0";
            try {
                versionStr = FileUtils.readFileToString(testFile);
            } catch (IOException ex) {
                //File has problem, that means it's probably version 0
            }

            if (versionStr.equals("done"))
                currentVersion = 1;
            else {
                try {
                    currentVersion = Integer.parseInt(versionStr);
                } catch (NumberFormatException ex) {
                    //Version # has problem, that means it's probably version 0
                }
            }
        }

        if (currentVersion < 1) {
            (new PanicPatch(-972, 157, -1022, "CheatHub")).execute(minecraftserver);
            (new PanicPatch(555, 241, 2077, "Thyrork")).execute(minecraftserver);
        }

        if (currentVersion < 2) {
            (new PanicPatch(1013, 119, 2009, "KeyHolder")).execute(minecraftserver);
            (new PanicPatch(-984, 153, -1017, "FixScanner")).execute(minecraftserver);
        }

        if (currentVersion < 3) {
            FMLLog.info("Scanning customnpc's folder for evidence of game completion.");
            File worldDir = world.getSaveHandler().getWorldDirectory();

            if (worldDir.exists()) {
                File customNpcsDir = new File(worldDir, "customnpcs");
                if (customNpcsDir.exists()) {
                    File playerDataDir = new File(customNpcsDir, "playerdata");

                    if (playerDataDir.exists()) {
                        int dialogStatus = enumeratePlayerDir(playerDataDir);

                        switch (dialogStatus) {
                            case 43:
                                (new PanicPatch(829, 80, 802, "Victory")).execute(minecraftserver);
                                break;
                            case 44:
                                (new PanicPatch(1295, 80, 1524, "Victory")).execute(minecraftserver);
                                break;
                            case 45:
                                (new PanicPatch(564, 90, 1181, "Victory")).execute(minecraftserver);
                                break;
                        }
                    } else {
                        FMLLog.info("Could not find "+playerDataDir.getAbsolutePath());
                    }
                } else {
                    FMLLog.info("Could not find "+customNpcsDir.getAbsolutePath());
                }
            } else {
                FMLLog.info("Could not find "+worldDir.getAbsolutePath());
            }

            FMLLog.info("Finished scanning.");
        }

        try {
            FileUtils.writeStringToFile(testFile, "3");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        FMLLog.info("PurplePanic done loading");
    }

    private int enumeratePlayerDir(File playerDir) {
        Iterator<File> files = FileUtils.iterateFiles(playerDir, new String[] {"json"}, true);
        Gson gson = new Gson();

        FMLLog.info("Scanning "+playerDir.getAbsolutePath());

        while (files.hasNext()) {
            File file = files.next();

            try {
                String fileContents = FileUtils.readFileToString(file);
                JsonObject object = gson.fromJson(fileContents, JsonObject.class);
                FMLLog.info("Scanning "+object.get("PlayerName").getAsString());

                if (object.has("DialogData")) {
                    JsonArray array = object.getAsJsonArray("DialogData");

                    for (JsonElement element : array) {
                        if (element.isJsonObject()) {
                            JsonObject dialog = element.getAsJsonObject();
                            if (dialog.has("Dialog")) {
                                int dialogNumber = dialog.get("Dialog").getAsInt();
                                if (dialogNumber >= 43 && dialogNumber <= 45) {
                                    FMLLog.info("Found completed game.");
                                    return dialogNumber;
                                }
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return 0;
    }
}
