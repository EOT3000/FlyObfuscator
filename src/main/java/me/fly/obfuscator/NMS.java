package me.fly.obfuscator;


import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3D;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NMS {
    public static void ifPresentProcessVec(PacketContainer container, int index, int addX, int addZ) {
        @SuppressWarnings("unchecked")
        Optional<Vec3D> optional = (Optional<Vec3D>) container.getModifier().read(index);

        optional.ifPresent(vec3D -> container.getModifier().write(index, vec3D.a(addX, 0, addZ)));
    }

    public enum MovementFlag {
        X,
        Y,
        Z,
        Y_ROT,
        X_ROT;



        public static Set<MovementFlag> fromNMS(Set<RelativeMovement> nms) {
            return nms.stream().map((x) -> MovementFlag.values()[x.ordinal()]).collect(Collectors.toSet());
        }

        public static Set<RelativeMovement> toNMS(Set<MovementFlag> buk) {
            return buk.stream().map((x) -> RelativeMovement.values()[x.ordinal()]).collect(Collectors.toSet());
        }

        public static Set<MovementFlag> fromInt(int mask) {
            Set<RelativeMovement> set = RelativeMovement.a(mask);

            return fromNMS(set);
        }

        public static int toInt(Set<MovementFlag> flags) {
            Set<RelativeMovement> nms = toNMS(flags);

            return RelativeMovement.a(nms);
        }
    }
}
