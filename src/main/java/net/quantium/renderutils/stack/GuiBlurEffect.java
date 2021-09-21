package net.quantium.renderutils.stack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.quantium.renderutils.shaders.ShaderProgram;
import org.lwjgl.opengl.GL11;

public class GuiBlurEffect implements IPostProcessingEffect {
    private static final ShaderProgram shaderBlur = new ShaderProgram("/assets/qrenderutils/shaders/vertex.glsl", "/assets/qrenderutils/shaders/blur.glsl") {

        @Override
        protected void init(Handle h){
            h.setInteger("source", 0);
        }
    };

    private final Framebuffer blurHBuffer = new Framebuffer(1, 1, false);

    private float intensity = 0;
    private final float size;

    public GuiBlurEffect(float size) {
        this.size = size;
    }

    private static float move(float o, float t, float d) {
        if (o < t) return Math.min(o + d, t);
        if (o > t) return Math.max(o - d, t);
        return t;
    }

    public static float getIntensity(GuiScreen gui) {
        if(gui == null) return 0;
        if(gui instanceof GuiChat) return 0;
        return 1;
    }

    public void process(Framebuffer src) {
        float targetIntensity = getIntensity(Minecraft.getMinecraft().currentScreen);
        intensity = move(intensity, targetIntensity, 0.1f);

        if(intensity > 0) {
            float size = intensity * this.size;
            float sigma = intensity * this.size / 3.6f;

            int w = src.framebufferTextureWidth;
            int h = src.framebufferTextureHeight;

            PostProcessing.resizeBuffer(blurHBuffer, w, h);

            GlStateManager.disableCull();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();

            ShaderProgram.Handle hnd1 = shaderBlur.use();
            hnd1.setVec2("texOffset", 1f / w, 1f / h);
            hnd1.setFloat("sigma", sigma);
            hnd1.setInteger("blurSize", (int)(Math.ceil(size / 2) * 2));

            //blur h
            blurHBuffer.bindFramebuffer(false);
            src.bindFramebufferTexture();

            hnd1.setVec2("blurVec", 1, 0);

            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
            PostProcessing.drawFullQuad();

            src.bindFramebuffer(false);
            blurHBuffer.bindFramebufferTexture();

            hnd1.setVec2("blurVec", 0, 1);

            PostProcessing.drawFullQuad();

            hnd1.revert();

            GlStateManager.enableCull();
            GlStateManager.enableDepth();
        }
    }
}
