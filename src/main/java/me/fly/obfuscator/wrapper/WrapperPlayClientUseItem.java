/** PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.fly.obfuscator.wrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.AutoWrapper;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.Direction;
import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class WrapperPlayClientUseItem extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Client.USE_ITEM;

    public WrapperPlayClientUseItem() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientUseItem(PacketContainer packet) {
        super(packet, TYPE);
    }

    public Hand getHand() {
        return handle.getHands().read(0);
    }

    public void setHand(Hand value) {
        handle.getHands().write(0, value);
    }

    private static final Class<?> POSITION_CLASS = MovingObjectPositionBlock.class;

    public MovingObjectPositionBlock getPosition() {
        return handle.getModifier().<MovingObjectPositionBlock>withType(POSITION_CLASS).read(0);
    }

    public void setPosition(MovingObjectPositionBlock position) {
        handle.getModifier().withType(POSITION_CLASS).write(0, position);
    }

    public void add(int x, int z) {
        MovingObjectPositionBlock position = getPosition();

        setPosition(new MovingObjectPositionBlock(position.e().a(x,0,z), position.b(), position.a(), position.d()));
    }
}