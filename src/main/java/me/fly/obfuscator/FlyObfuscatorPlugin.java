package me.fly.obfuscator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import me.fly.obfuscator.wrapper.WrapperPlayServerLogin;
import me.fly.obfuscator.wrapper.WrapperPlayServerPosition;
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

        addPacketListenerPosition(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        addPacketListenerPosition(PacketType.Play.Server.TILE_ENTITY_DATA);
        addPacketListenerPosition(PacketType.Play.Server.BLOCK_ACTION);
        addPacketListenerPosition(PacketType.Play.Server.BLOCK_CHANGE);
        addPacketListenerPosition(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
        addPacketListenerPosition(PacketType.Play.Server.DAMAGE_EVENT);
        addPacketListenerPosition(PacketType.Play.Server.WORLD_EVENT);
        addPacketListenerPosition(PacketType.Play.Server.OPEN_SIGN_EDITOR);

        addPacketListenerRawPositionDouble(PacketType.Play.Server.WORLD_PARTICLES);
        addPacketListenerRawPositionDouble(PacketType.Play.Server.VEHICLE_MOVE);

        addPacketListenerPosition(PacketType.Play.Client.BLOCK_DIG);

        addPacketListenerSyncPosition();
        addPacketListenerOptionalPosition(PacketType.Play.Server.DAMAGE_EVENT, 4);
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

    private void addPacketListenerRawPositionInt(PacketType type) {
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


    private void addPacketListenerRawPositionDouble(PacketType type) {
        int direction = type.isClient() ? -16 : 16;
        protocolManager.addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.HIGHEST,
                type
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                StructureModifier<Double> doubles = event.getPacket().getDoubles();

                double x = doubles.read(0);
                double z = doubles.read(1);

                doubles.write(0, x+xMod*direction);
                doubles.write(1, z+zMod*direction);
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
                StructureModifier<BlockPosition> poss = event.getPacket().getBlockPositionModifier();

                BlockPosition pos = poss.read(0);

                int x = pos.getX();
                int z = pos.getZ();

                poss.write(0, pos.add(new BlockPosition(x+xMod*direction, 0, z+zMod*direction)));
            }
        });
    }

    private void addPacketListenerSyncPosition() {
        protocolManager.addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.HIGHEST,
                PacketType.Play.Server.POSITION
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerPosition packet = new WrapperPlayServerPosition(event.getPacket());

                if(packet.getFlags().contains(WrapperPlayServerPosition.PlayerTeleportFlag.X)) {
                    packet.setX(packet.getX()+xMod*16);
                }
                if(packet.getFlags().contains(WrapperPlayServerPosition.PlayerTeleportFlag.Z)) {
                    packet.setZ(packet.getZ()+zMod*16);
                }
            }
        });
    }

    private void addPacketListenerOptionalPosition(PacketType type, int index) {
        int direction = type.isClient() ? -16 : 16;
        protocolManager.addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.HIGHEST,
                type
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                NMS.ifPresentProcessVec(event.getPacket(), index, xMod*direction, xMod*direction);
            }
        });
    }
}
