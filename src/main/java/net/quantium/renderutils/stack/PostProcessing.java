package net.quantium.renderutils.stack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public final class PostProcessing {
    private List<IPostProcessingEffect> effects = new ArrayList<>();

    public PostProcessing add(IPostProcessingEffect effect) {
        if(effect == null) throw new IllegalArgumentException("null");
        effects.add(effect);
        return this;
    }

    public boolean remove(IPostProcessingEffect effect) {
        if(effect == null) throw new IllegalArgumentException("null");
        return effects.remove(effect);
    }

    public void render(Framebuffer src) {
        for(IPostProcessingEffect effect : effects) {
            effect.process(src);
        }
    }

    public static void drawFullQuad() {
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        b.pos(-1, -1, 0).tex(0, 0).endVertex();
        b.pos(+1, -1, 0).tex(1, 0).endVertex();
        b.pos(+1, +1, 0).tex(1, 1).endVertex();
        b.pos(-1, +1, 0).tex(0, 1).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void resizeBuffer(Framebuffer src, int w, int h) {
        if(src.framebufferWidth == w && src.framebufferHeight == h) return;

        src.deleteFramebuffer();
        src.createFramebuffer(w, h);
        src.checkFramebufferComplete();
    }

    public static void blit(Framebuffer src, Framebuffer dest, int bit) {
        OpenGlHelper.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, dest.framebufferObject);
        OpenGlHelper.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, src.framebufferObject);

        GL30.glBlitFramebuffer(0, 0, src.framebufferWidth, src.framebufferHeight, 0, 0, dest.framebufferWidth, dest.framebufferHeight, bit, GL11.GL_NEAREST);
    }

    public static void blit(Framebuffer src, Framebuffer dest) {
        blit(src, dest, GL11.GL_COLOR_BUFFER_BIT);
    }
}
