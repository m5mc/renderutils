package net.quantium.renderutils.helper;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.quantium.renderutils.shaders.CompilationException;
import net.quantium.renderutils.shaders.ShaderProgram;

import java.io.IOException;
import java.util.List;

public class ReloadShadersCommand implements ICommand {

    @Override
    public int compareTo(ICommand arg0) {
        return 0;
    }

    @Override
    public String getName() {
        return "reloadshaders";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/reloadshaders";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = Lists.newArrayList();
        aliases.add("/reloadshaders");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        for(ShaderProgram p : ShaderProgram.getShaders()) {
            try {
                p.reload();

                TextComponentString t = new TextComponentString(p.toString() + ": success");
                t.getStyle().setColor(TextFormatting.GREEN);
                sender.sendMessage(t);
            } catch (CompilationException | IOException e) {
                TextComponentString t = new TextComponentString(p.toString() + ": " + e.getMessage());
                t.getStyle().setColor(TextFormatting.RED);
                sender.sendMessage(t);
            }
        }
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