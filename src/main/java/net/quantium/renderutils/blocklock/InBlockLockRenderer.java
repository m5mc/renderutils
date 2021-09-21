package net.quantium.renderutils.blocklock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

public class InBlockLockRenderer {
    private static void renderState(IBlockAccess access, BlockPos pos, double d0, double d1, double d2, boolean cullFront) {
        if(cullFront) GlStateManager.cullFace(GlStateManager.CullFace.FRONT);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.pushMatrix();

        int packedLight = access.getCombinedLight(pos, 0);
        int j = packedLight % 65536;
        int k = packedLight / 65536;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.translate(-d0 + pos.getX(), -d1 + pos.getY(), -d2 + pos.getZ() + 1);
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(Blocks.STONE.getDefaultState(), 0.8f);

        GlStateManager.popMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();

        if(cullFront) GlStateManager.cullFace(GlStateManager.CullFace.BACK);
    }

    public static void render(float partialTicks) {
        Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
        if(renderViewEntity == null) return;
        if(Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.isSpectator()) return;
        
        IBlockAccess access = Minecraft.getMinecraft().world;

        final double d0 = renderViewEntity.lastTickPosX + ((renderViewEntity.posX - renderViewEntity.lastTickPosX) * partialTicks);
        final double d1 = renderViewEntity.lastTickPosY + ((renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks);
        final double d2 = renderViewEntity.lastTickPosZ + ((renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * partialTicks);

        Vec3d campos = ActiveRenderInfo.getCameraPosition();

        double x0 = d0 + campos.x;
        double y0 = d1 + campos.y;
        double z0 = d2 + campos.z;

        BlockPos pos = new BlockPos(x0, y0, z0);
        IBlockState state = access.getBlockState(pos);

        if(state.isOpaqueCube()) {
            for(EnumFacing f : EnumFacing.VALUES) {
                BlockPos pos1 = pos.offset(f);
                IBlockState adjacent = access.getBlockState(pos1);

                if(!adjacent.shouldSideBeRendered(access, pos1, f.getOpposite()) && adjacent.isOpaqueCube())
                    renderState(access, pos1, d0, d1, d2, false);
            }

            //renderState(access, state, pos, d0, d1, d2, true);
        }
    }
}
