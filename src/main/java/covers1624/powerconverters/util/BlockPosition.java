package covers1624.powerconverters.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPosition {
    public int x;
    public int y;
    public int z;
    public ForgeDirection orientation;
    public static final int[][] SIDE_COORD_MOD
        = new int[][] { { 0, -1, 0 }, { 0, 1, 0 },  { 0, 0, -1 },
                        { 0, 0, 1 },  { -1, 0, 0 }, { 1, 0, 0 } };

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.orientation = ForgeDirection.UNKNOWN;
    }

    public BlockPosition(int x, int y, int z, ForgeDirection corientation) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.orientation = corientation;
    }

    public BlockPosition(BlockPosition p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
        this.orientation = p.orientation;
    }

    public BlockPosition(NBTTagCompound nbttagcompound) {
        this.x = nbttagcompound.getInteger("i");
        this.y = nbttagcompound.getInteger("j");
        this.z = nbttagcompound.getInteger("k");
        this.orientation = ForgeDirection.UNKNOWN;
    }

    public BlockPosition(TileEntity tile) {
        this.x = tile.xCoord;
        this.y = tile.yCoord;
        this.z = tile.zCoord;
        this.orientation = ForgeDirection.UNKNOWN;
    }

    public BlockPosition copy() {
        return new BlockPosition(this.x, this.y, this.z, this.orientation);
    }

    public BlockPosition step(int dir, int dist) {
        int[] d = SIDE_COORD_MOD[dir];
        this.x += d[0] * dist;
        this.y += d[1] * dist;
        this.z += d[2] * dist;
        return this;
    }

    public BlockPosition step(ForgeDirection dir) {
        this.x += dir.offsetX;
        this.y += dir.offsetY;
        this.z += dir.offsetZ;
        return this;
    }

    public BlockPosition step(ForgeDirection dir, int dist) {
        this.x += dir.offsetX * dist;
        this.y += dir.offsetY * dist;
        this.z += dir.offsetZ * dist;
        return this;
    }

    public void moveRight(int step) {
        switch (this.orientation) {
            case SOUTH:
                this.x -= step;
                break;
            case NORTH:
                this.x += step;
                break;
            case EAST:
                this.z += step;
                break;
            case WEST:
                this.z -= step;
        }
    }

    public void moveLeft(int step) {
        this.moveRight(-step);
    }

    public void moveForwards(int step) {
        switch (this.orientation) {
            case SOUTH:
                this.z += step;
                break;
            case NORTH:
                this.z -= step;
                break;
            case EAST:
                this.x += step;
                break;
            case WEST:
                this.x -= step;
                break;
            case UP:
                this.y += step;
                break;
            case DOWN:
                this.y -= step;
        }
    }

    public void moveBackwards(int step) {
        this.moveForwards(-step);
    }

    public void moveUp(int step) {
        switch (this.orientation) {
            case SOUTH:
            case NORTH:
            case EAST:
            case WEST:
                this.y += step;
            default:
        }
    }

    public void moveDown(int step) {
        this.moveUp(-step);
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setDouble("i", (double) this.x);
        nbttagcompound.setDouble("j", (double) this.y);
        nbttagcompound.setDouble("k", (double) this.z);
    }

    public String toString() {
        return this.orientation == null
            ? "{" + this.x + ", " + this.y + ", " + this.z + "}"
            : "{" + this.x + ", " + this.y + ", " + this.z + ";"
                + this.orientation.toString() + "}";
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BlockPosition)) {
            return false;
        } else {
            BlockPosition bp = (BlockPosition) obj;
            return bp.x == this.x && bp.y == this.y && bp.z == this.z
                && bp.orientation == this.orientation;
        }
    }

    public int hashCode() {
        return this.x & 4095 | this.y & '\uff00' | this.z & 16773120;
    }

    public BlockPosition min(BlockPosition p) {
        return new BlockPosition(
            p.x > this.x ? this.x : p.x,
            p.y > this.y ? this.y : p.y,
            p.z > this.z ? this.z : p.z
        );
    }

    public BlockPosition max(BlockPosition p) {
        return new BlockPosition(
            p.x < this.x ? this.x : p.x,
            p.y < this.y ? this.y : p.y,
            p.z < this.z ? this.z : p.z
        );
    }

    public List getAdjacent(boolean includeVertical) {
        List a = new ArrayList();
        a.add(new BlockPosition(this.x + 1, this.y, this.z, ForgeDirection.EAST));
        a.add(new BlockPosition(this.x - 1, this.y, this.z, ForgeDirection.WEST));
        a.add(new BlockPosition(this.x, this.y, this.z + 1, ForgeDirection.SOUTH));
        a.add(new BlockPosition(this.x, this.y, this.z - 1, ForgeDirection.NORTH));
        if (includeVertical) {
            a.add(new BlockPosition(this.x, this.y + 1, this.z, ForgeDirection.UP));
            a.add(new BlockPosition(this.x, this.y - 1, this.z, ForgeDirection.DOWN));
        }

        return a;
    }

    public TileEntity getTileEntity(World world) {
        return world.getTileEntity(this.x, this.y, this.z);
    }

    public Block getBlock(World world) {
        return world.getBlock(this.x, this.y, this.z);
    }

    public Object getTileEntity(World world, Class targetClass) {
        TileEntity te = world.getTileEntity(this.x, this.y, this.z);
        return targetClass.isInstance(te) ? te : null;
    }

    public static TileEntity getTileEntityRaw(World world, int x, int y, int z) {
        return !world.blockExists(x, y, z)
            ? null
            : world.getChunkFromBlockCoords(x, z).getTileEntityUnsafe(x & 15, y, z & 15);
    }

    public static Object
    getTileEntityRaw(World world, int x, int y, int z, Class targetClass) {
        TileEntity te = getTileEntityRaw(world, x, y, z);
        return targetClass.isInstance(te) ? te : null;
    }

    public static boolean blockExists(TileEntity start, ForgeDirection dir) {
        int x = start.xCoord + dir.offsetX;
        int y = start.yCoord + dir.offsetY;
        int z = start.zCoord + dir.offsetZ;
        return start.getWorldObj().blockExists(x, y, z);
    }

    public static TileEntity
    getAdjacentTileEntity(TileEntity start, ForgeDirection direction) {
        BlockPosition p = new BlockPosition(start);
        p.orientation = direction;
        p.moveForwards(1);
        return start.getWorldObj().getTileEntity(p.x, p.y, p.z);
    }

    public static Object
    getAdjacentTileEntity(TileEntity start, ForgeDirection direction, Class targetClass) {
        TileEntity te = getAdjacentTileEntity(start, direction);
        return targetClass.isInstance(te) ? te : null;
    }
}
