package thermaldynamics.ducts.attachments.facades;

import cofh.core.network.PacketCoFHBase;
import cofh.repack.codechicken.lib.vec.Cuboid6;
import cofh.repack.codechicken.lib.vec.Rotation;
import cofh.repack.codechicken.lib.vec.Vector3;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thermaldynamics.block.Attachment;
import thermaldynamics.block.AttachmentRegistry;
import thermaldynamics.block.TileMultiBlock;

public class Facade extends Attachment {
    static Cuboid6 bound = new Cuboid6(0, 0, 0, 1, 0.1, 1);

    static Cuboid6[] bounds = {
            bound,
            bound.copy().apply(Rotation.sideRotations[1].at(Vector3.center)),
            bound.copy().apply(Rotation.sideRotations[2].at(Vector3.center)),
            bound.copy().apply(Rotation.sideRotations[3].at(Vector3.center)),
            bound.copy().apply(Rotation.sideRotations[4].at(Vector3.center)),
            bound.copy().apply(Rotation.sideRotations[5].at(Vector3.center))
    };

    Block block;
    int meta;

    public Facade(TileMultiBlock tile, byte side, Block block, int meta) {
        super(tile, side);
        this.block = block;
        this.meta = meta;
    }

    public Facade(TileMultiBlock tile, byte side) {
        super(tile, side);
    }

    @Override
    public int getID() {
        return AttachmentRegistry.FACADE;
    }

    @Override
    public Cuboid6 getCuboid() {
        return bounds[side].copy();
    }

    @Override
    public boolean onWrenched() {
        tile.removeFacade(this);

        for (ItemStack stack : getDrops()) {
            dropItemStack(stack);
        }
        return true;
    }

    @Override
    public TileMultiBlock.NeighborTypes getNeighbourType() {
        return TileMultiBlock.NeighborTypes.NONE;
    }

    @Override
    public boolean isNode() {
        return false;
    }

    @Override
    public boolean render(int pass, RenderBlocks renderBlocks) {
        if (!block.canRenderInPass(pass))
            return false;

        return FacadeRenderer.renderFacade(renderBlocks, tile.xCoord, tile.yCoord, tile.zCoord, side, block, meta, getCuboid());
    }

    @Override
    public boolean makesSideSolid() {
        return true;
    }

    @Override
    public ItemStack getPickBlock() {
        return null;
    }

    @Override
    public List<ItemStack> getDrops() {
        return new LinkedList<ItemStack>();
    }

    @Override
    public void addDescriptionToPacket(PacketCoFHBase packet) {
        packet.addShort(Block.getIdFromBlock(block));
        packet.addByte(meta);
    }

    @Override
    public void getDescriptionFromPacket(PacketCoFHBase packet) {
        block = Block.getBlockById(packet.getShort());
        meta = packet.getByte();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("block", Block.blockRegistry.getNameForObject(block));
        tag.setByte("meta", (byte) meta);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        block = Block.getBlockFromName(tag.getString("block"));
        if (block == null) block = Blocks.air;
        meta = tag.getByte("meta");
    }
}
