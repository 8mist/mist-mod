package fr.mist.utils.mc;

import fr.mist.MistMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class McUtils {
    public static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    public static Window window() {
        return mc().getWindow();
    }

    public static ClientPlayerEntity player() {
        return mc().player;
    }

    public static void sendPacket(Packet<?> packet) {
        if (mc().player == null || mc().getNetworkHandler() == null) {
            MistMod.error(
                    "Tried to send packet: \"" + packet.getClass().getSimpleName() + "\", but connection was null.");
            return;
        }

        mc().getNetworkHandler().sendPacket(packet);
    }

    public static void sendMessageToClient(Text text) {
        player().sendMessage(text, false);
    }

    public static void sendMessageStyledToClient(String content) {
        Text text = Text.literal(content).setStyle(Style.EMPTY.withColor(Formatting.WHITE));
        player().sendMessage(text, false);
    }
}
