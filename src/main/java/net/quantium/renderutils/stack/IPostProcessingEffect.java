package net.quantium.renderutils.stack;

import net.minecraft.client.shader.Framebuffer;

public interface IPostProcessingEffect {

    void process(Framebuffer src);
}
