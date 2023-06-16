package covers1624.powerconverters.tile.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import covers1624.powerconverters.api.registry.PowerSystemRegistry;
import covers1624.powerconverters.block.BlockPowerConverter;
import covers1624.powerconverters.util.BlockPosition;
import covers1624.powerconverters.util.IAdvancedLogTile;
import covers1624.powerconverters.util.INeighboorUpdateTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityBridgeComponent
    extends TileEntity implements INeighboorUpdateTile, IAdvancedLogTile {
    private Map _adjacentBridges = new HashMap();
    private Map _adjacentTiles = new HashMap();
    private Class _adjacentClass;
    protected PowerSystemRegistry.PowerSystem powerSystem;
    protected int _voltageIndex;
    protected String type;
    private boolean _initialized;

    protected TileEntityBridgeComponent(
        PowerSystemRegistry.PowerSystem powersystem,
        int voltageNameIndex,
        Class adjacentClass
    ) {
        this.powerSystem = powersystem;
        this._voltageIndex = voltageNameIndex;
        this._adjacentClass = adjacentClass;
    }

    public void updateEntity() {
        super.updateEntity();
        if (!this._initialized && !super.tileEntityInvalid) {
            this.onNeighboorChanged();
            this._initialized = true;
        }
    }

    public void onNeighboorChanged() {
        Map adjacentBridges = new HashMap();
        Map adjacentTiles = new HashMap();
        ForgeDirection[] arr$ = ForgeDirection.VALID_DIRECTIONS;
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            ForgeDirection d = arr$[i$];
            TileEntity te = BlockPosition.getAdjacentTileEntity(this, d);
            if (te != null && te instanceof TileEntityEnergyBridge) {
                adjacentBridges.put(d, (TileEntityEnergyBridge) te);
            } else if (te != null && this._adjacentClass.isAssignableFrom(te.getClass())) {
                adjacentTiles.put(d, te);
            }
        }

        this._adjacentBridges = adjacentBridges;
        this._adjacentTiles = adjacentTiles;
    }

    public PowerSystemRegistry.PowerSystem getPowerSystem() {
        return this.powerSystem;
    }

    public boolean isConnected() {
        return this._adjacentTiles.size() > 0;
    }

    public boolean isSideConnected(int side) {
        return this._adjacentTiles.get(ForgeDirection.getOrientation(side)) != null;
    }

    public boolean isSideConnectedClient(int side) {
        TileEntity te = BlockPosition.getAdjacentTileEntity(
            this, ForgeDirection.getOrientation(side)
        );
        return te != null && this._adjacentClass.isAssignableFrom(te.getClass());
    }

    public int getVoltageIndex() {
        return this._voltageIndex;
    }

    public TileEntityEnergyBridge getFirstBridge() {
        return this._adjacentBridges.size() == 0
            ? null
            : (TileEntityEnergyBridge) this._adjacentBridges.values().toArray()[0];
    }

    protected Map getBridges() {
        return this._adjacentBridges;
    }

    protected Map getTiles() {
        return this._adjacentTiles;
    }

    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        if (this._voltageIndex == 0) {
            this._voltageIndex = tagCompound.getInteger("voltageIndex");
        }
    }

    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger("voltageIndex", this._voltageIndex);
    }

    public boolean isGettingRedstone() {
        Block block = super.worldObj.getBlock(super.xCoord, super.yCoord, super.zCoord);
        if (block instanceof BlockPowerConverter) {
            BlockPowerConverter blockPowerConverter = (BlockPowerConverter) block;
            return blockPowerConverter.isGettingRedstone();
        } else {
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public void addWailaInfo(List info) {
        info.add("Type: " + this.type);
        info.add("PowerSystem: " + this.powerSystem.getName());
        if (this.type.equals("Consumer")) {
            info.add("Consumer Dissabled: " + this.powerSystem.consumerDissabled());
        } else if (this.type.equals("Producer")) {
            info.add("Producer Dissabled: " + this.powerSystem.producerDissabled());
        }
    }

    public void
    getTileInfo(List info, ForgeDirection side, EntityPlayer player, boolean debug) {
        info.add(new ChatComponentText("Is getting Redstone: " + this.isGettingRedstone())
        );
    }
}
