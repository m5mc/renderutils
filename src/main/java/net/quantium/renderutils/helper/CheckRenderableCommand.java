package net.quantium.renderutils.helper;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.quantium.renderutils.shaders.CompilationException;
import net.quantium.renderutils.shaders.ShaderProgram;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CheckRenderableCommand implements ICommand {

    @Override
    public int compareTo(ICommand arg0) {
        return 0;
    }

    @Override
    public String getName() {
        return "chk";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/chk";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = Lists.newArrayList();
        aliases.add("/chk");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if( sender.getCommandSenderEntity() == null) return;

        RayTraceResult res = sender.getCommandSenderEntity().rayTrace(8, 0);
        if( res.typeOfHit != RayTraceResult.Type.BLOCK) return;

        for(EnumFacing f : EnumFacing.VALUES) {
            sender.sendMessage(new TextComponentString(f + ": " + sender.getEntityWorld().getBlockState(res.getBlockPos()).shouldSideBeRendered( sender.getEntityWorld(), res.getBlockPos(), f)));
        }

        sender.sendMessage(new TextComponentString("opaque: " + sender.getEntityWorld().getBlockState(res.getBlockPos()).isOpaqueCube()));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}