package me.frostedsnowman.asyncpvp.commons.texts;

import org.bukkit.ChatColor;
import org.fusesource.jansi.Ansi;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class Texts {

    private static final Map<ChatColor, String> ANSI_COLORS = new EnumMap<>(ChatColor.class);
    private static final ChatColor[] CHAT_COLORS = ChatColor.values();

    private static boolean ansiSupported = false;

    static {

        try {
            Class.forName("org.fusesource.jansi.Ansi");

            ANSI_COLORS.put(ChatColor.BLACK, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).boldOff().toString());
            ANSI_COLORS.put(ChatColor.DARK_BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).boldOff().toString());
            ANSI_COLORS.put(ChatColor.DARK_GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).boldOff().toString());
            ANSI_COLORS.put(ChatColor.DARK_AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).boldOff().toString());
            ANSI_COLORS.put(ChatColor.DARK_RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).boldOff().toString());
            ANSI_COLORS.put(ChatColor.DARK_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).boldOff().toString());
            ANSI_COLORS.put(ChatColor.GOLD, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).boldOff().toString());
            ANSI_COLORS.put(ChatColor.GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString());
            ANSI_COLORS.put(ChatColor.DARK_GRAY, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString());
            ANSI_COLORS.put(ChatColor.BLUE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString());
            ANSI_COLORS.put(ChatColor.GREEN, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString());
            ANSI_COLORS.put(ChatColor.AQUA, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString());
            ANSI_COLORS.put(ChatColor.RED, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString());
            ANSI_COLORS.put(ChatColor.LIGHT_PURPLE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.MAGENTA).bold().toString());
            ANSI_COLORS.put(ChatColor.YELLOW, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.YELLOW).bold().toString());
            ANSI_COLORS.put(ChatColor.WHITE, Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString());
            ANSI_COLORS.put(ChatColor.MAGIC, Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString());
            ANSI_COLORS.put(ChatColor.BOLD, Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString());
            ANSI_COLORS.put(ChatColor.STRIKETHROUGH, Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString());
            ANSI_COLORS.put(ChatColor.UNDERLINE, Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString());
            ANSI_COLORS.put(ChatColor.ITALIC, Ansi.ansi().a(Ansi.Attribute.ITALIC).toString());
            ANSI_COLORS.put(ChatColor.RESET, Ansi.ansi().a(Ansi.Attribute.RESET).toString());

            ansiSupported = true;

        } catch (ClassNotFoundException ignored) {}
    }


    private Texts() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @Nonnull
    public static String colorBukkit(String in) {
        Objects.requireNonNull(in, "in");
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    @Nonnull
    public static String colorAnsi(String in) {
        Objects.requireNonNull(in, "in");

        if (!ansiSupported) {
            return in;
        }
        for (ChatColor chatColor : CHAT_COLORS) {
            in = colorBukkit(in).replace(chatColor.toString(), ANSI_COLORS.getOrDefault(chatColor, ""));
        }
        return in + ANSI_COLORS.getOrDefault(ChatColor.RESET, "");
    }
}
