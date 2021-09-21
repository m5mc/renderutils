package net.quantium.renderutils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.quantium.renderutils.blocklock.InBlockLockRenderer;
import net.quantium.renderutils.helper.ReloadShadersCommand;
import net.quantium.renderutils.stack.BloomEffect;
import net.quantium.renderutils.stack.GuiBlurEffect;
import net.quantium.renderutils.stack.PostProcessing;
import org.apache.logging.log4j.Logger;

@Mod(modid = ModProvider.MODID, name = ModProvider.NAME, version = ModProvider.VERSION, acceptableRemoteVersions = "*")
public class ModProvider
{
    public static final String MODID = "qrenderutils";
    public static final String NAME = "Render Utils";
    public static final String VERSION = "0.0.1";

    private static Logger logger;

    private final PostProcessing processing
            = new PostProcessing();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        ClientCommandHandler.instance.registerCommand(new ReloadShadersCommand());
        //ClientCommandHandler.instance.registerCommand(new CheckRenderableCommand());

        Configuration config = new Configuration(event.getSuggestedConfigurationFile());

        boolean bloomEnabled = config.getBoolean("enabled", "bloom", true, "set to false to disable bloom");
        float bloomIntensity = config.getFloat("intensity", "bloom", 1.15f, 0.01f, 10, "Bloom intensity");
        float bloomDetectionThreshold = config.getFloat("threshold", "bloom", 0.25f, 0.01f, 10f, "Bloom detection threshold (in normalized luminance units)");
        int bloomBlurSize = config.getInt("blurSize", "bloom", 24, 1, 100, "Bloom blur size");

        boolean blurEnabled = config.getBoolean("enabled", "blur", true, "set to false to disable blur when in gui");
        int blurBlurSize = config.getInt("blurSize", "blur", 36, 1, 128, "Gui blur size");

        config.save();

        if(bloomEnabled) {
            processing.add(new BloomEffect(bloomIntensity, bloomDetectionThreshold, bloomBlurSize));
        }

        if(blurEnabled) {
            processing.add(new GuiBlurEffect(blurBlurSize));
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRender(RenderWorldLastEvent e)
    {
        if(Minecraft.getMinecraft().world != null) {
            InBlockLockRenderer.render(e.getPartialTicks());

            processing.render(Minecraft.getMinecraft().getFramebuffer());
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false); //bind it back as some pp effects may reset le state
        }
    }
}
