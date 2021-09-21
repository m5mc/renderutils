package net.quantium.renderutils.stack;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.quantium.renderutils.shaders.ShaderProgram;
import org.lwjgl.opengl.GL11;

public class BloomEffect implements IPostProcessingEffect {
    private static final ShaderProgram shaderLuma = new ShaderProgram("/assets/qrenderutils/shaders/vertex.glsl", "/assets/qrenderutils/shaders/luma.glsl") {

        @Override
        protected void init(Handle h){
            h.setInteger("source", 0);
        }
    };

    private static final ShaderProgram shaderBlur = new ShaderProgram("/assets/qrenderutils/shaders/vertex.glsl", "/assets/qrenderutils/shaders/blur.glsl") {

        @Override
        protected void init(Handle h){
            h.setInteger("source", 0);
        }
    };

    public final float threshold;// = 0.25f;
    public final float intensity;// = 1.15f;
    public final float blur;// = 24;

    private final Framebuffer lumaBuffer = new Framebuffer(1, 1, false);
    private final Framebuffer blurHBuffer = new Framebuffer(1, 1, false);

    public BloomEffect(float intensity, float threshold, int blur) {
        this.threshold = threshold;
        this.intensity = intensity;
        this.blur = blur;
    }

    public void process(Framebuffer src) {
        int w = src.framebufferTextureWidth;
        int h = src.framebufferTextureHeight;

        PostProcessing.resizeBuffer(lumaBuffer, w, h);
        PostProcessing.resizeBuffer(blurHBuffer, w, h);

        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();

        //luma
        ShaderProgram.Handle hnd = shaderLuma.use();
        hnd.setFloat("threshold", threshold);
        hnd.setFloat("intensity", intensity);

        lumaBuffer.bindFramebuffer(false);
        src.bindFramebufferTexture();

        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
        PostProcessing.drawFullQuad();

        hnd.revert();

        ShaderProgram.Handle hnd1 = shaderBlur.use();
        hnd1.setVec2("texOffset", 1f / w, 1f / h);
        hnd1.setInteger("blurSize", (int)blur);
        hnd1.setFloat("sigma", blur * 0.3f);

        //blur h
        blurHBuffer.bindFramebuffer(false);
        lumaBuffer.bindFramebufferTexture();

        hnd1.setVec2("blurVec", 1, 0);

        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);
        PostProcessing.drawFullQuad();

        src.bindFramebuffer(false);
        blurHBuffer.bindFramebufferTexture();

        hnd1.setVec2("blurVec", 0, 1);

        //GlStateManager.disableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        PostProcessing.drawFullQuad();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        hnd1.revert();

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }
}
