package covers1624.powerconverters.api.bridge;

import covers1624.powerconverters.api.registry.PowerSystemRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class BridgeSideData {
    public ForgeDirection side;
    public PowerSystemRegistry.PowerSystem powerSystem;
    public boolean isConsumer;
    public boolean isProducer;
    public boolean isConnected;
    public int voltageNameIndex;
    public double outputRate;

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("VoltageIndex", this.voltageNameIndex);
        tag.setBoolean("IsConsumer", this.isConsumer);
        tag.setBoolean("IsProducer", this.isProducer);
        if (this.powerSystem != null) {
            tag.setInteger("PowerSystem", this.powerSystem.getId());
        }

        tag.setBoolean("Connected", this.isConnected);
        tag.setDouble("OutputRate", this.outputRate);
    }

    public void loadFromNBT(NBTTagCompound tag) {
        this.voltageNameIndex = tag.getInteger("VoltageIndex");
        this.isConsumer = tag.getBoolean("IsConsumer");
        this.isProducer = tag.getBoolean("IsProducer");
        if (tag.hasKey("PowerSystem")) {
            this.powerSystem
                = PowerSystemRegistry.getPowerSystemById(tag.getInteger("PowerSystem"));
        }

        this.isConnected = tag.getBoolean("Connected");
        this.outputRate = tag.getDouble("OutputRate");
    }
}
