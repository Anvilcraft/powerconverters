package covers1624.powerconverters.updatechecker;

import covers1624.powerconverters.handler.ConfigurationHandler;
import covers1624.powerconverters.util.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;

public class UpdateManager {
    private int lastPoll = 400;
    private boolean notificationDisplayed = false;
    private UpdateCheckThread updateThread = new UpdateCheckThread();
    private String location
        = "http://minecraft.curseforge.com/mc-mods/225064-power-converters3";
    private String message
        = "Hey PowerConverters has an update, It may include some cool stuff";

    public UpdateManager() {
        if (!ConfigurationHandler.doUpdateCheck) {
            LogHelper.info("Update Checker is turned off.");
        } else {
            this.updateThread.start();
        }
    }

    @SubscribeEvent
    public void tickStart(TickEvent.PlayerTickEvent event) {
        if (this.notificationDisplayed && event.phase == Phase.START) {
            if (this.lastPoll > 0) {
                --this.lastPoll;
            } else {
                this.lastPoll = 400;
                if (this.updateThread.checkFinished()) {
                    this.notificationDisplayed = true;
                    if (this.updateThread.newVersion()
                        && FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                        EntityPlayer player = event.player;
                        IChatComponent chatComponent = Serializer.func_150699_a("[{\"text\":\"" + this.message + "\",\"color\":\"aqua\"}," + "{\"text\":\" " + EnumChatFormatting.WHITE + "[" + EnumChatFormatting.GREEN + "Download" + EnumChatFormatting.WHITE + "]\"," + "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":" + "{\"text\":\"Click this to download the latest version\",\"color\":\"yellow\"}}," + "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + this.location + "\"}}]");
                        player.addChatMessage(chatComponent);
                    }
                }
            }
        }
    }
}
