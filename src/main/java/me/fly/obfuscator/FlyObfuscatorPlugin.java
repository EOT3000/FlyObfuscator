package me.fly.obfuscator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class FlyObfuscatorPlugin extends JavaPlugin implements Listener {
    private final Random random = new Random();
    private int sectionSize = 16;
    private int minX = -128;
    private int maxX = 128;
    private int minZ = -128;
    private int maxZ = 128;

    private final Map<UUID, IntIntImmutablePair> shift = new HashMap<>();

    private ProtocolManager protocolManager;

    //Temporary modification values
    int xMod = 10;
    int zMod = 10;

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        addPacketListenerChunk(PacketType.Play.Server.MAP_CHUNK);
        addPacketListenerChunk(PacketType.Play.Server.CHUNKS_BIOMES);
        addPacketListenerChunk(PacketType.Play.Server.UNLOAD_CHUNK);
        addPacketListenerChunk(PacketType.Play.Server.LIGHT_UPDATE);
    }

    private void addPacketListenerChunk(PacketType type) {
        //All packets with chunk coords are clientbound.
        int direction = 1;
        protocolManager.addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.HIGHEST,
                type
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                StructureModifier<Integer> ints = event.getPacket().getIntegers();

                int x = ints.read(0);
                int z = ints.read(1);

                ints.write(0, x+xMod*direction);
                ints.write(1, z+zMod*direction);
            }
        });
    }

    private void addPacketListenerPosition(PacketType type) {
        int direction = type.isClient() ? -16 : 16;
        protocolManager.addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.HIGHEST,
                type
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                StructureModifier<Integer> ints = event.getPacket().getIntegers();

                int x = ints.read(0);
                int z = ints.read(1);

                ints.write(0, x+xMod*direction);
                ints.write(1, z+zMod*direction);
            }
        });
    }
}
