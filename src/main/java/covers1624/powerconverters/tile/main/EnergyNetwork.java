package covers1624.powerconverters.tile.main;

import java.util.Iterator;

import cofh.api.energy.EnergyStorage;
import covers1624.powerconverters.grid.GridTickHandler;
import covers1624.powerconverters.grid.IGrid;
import covers1624.powerconverters.util.BlockPosition;
import covers1624.repack.cofh.lib.util.ArrayHashList;
import covers1624.repack.cofh.lib.util.LinkedHashList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyNetwork implements IGrid {
    public static final int TRANSFER_RATE = 1000;
    public static final int STORAGE = 6000;
    static final GridTickHandler HANDLER;
    private ArrayHashList nodeSet = new ArrayHashList();
    LinkedHashList conduitSet;
    private TileEnergyConduit master;
    private int overflowSelector;
    private boolean regenerating = false;
    EnergyStorage storage = new EnergyStorage(480, 80);
    public int distribution;
    public int distributionSide;

    protected EnergyNetwork() {
        this.storage.setCapacity(6000);
        this.storage.setMaxTransfer(1000);
    }

    public EnergyNetwork(TileEnergyConduit base) {
        this.storage.setCapacity(6000);
        this.storage.setMaxTransfer(1000);
        this.conduitSet = new LinkedHashList();
        this.regenerating = true;
        this.addConduit(base);
        this.regenerating = false;
    }

    public int getNodeShare(TileEnergyConduit conduit) {
        int size = this.nodeSet.size();
        if (size <= 1) {
            return this.storage.getEnergyStored();
        } else {
            int amt = 0;
            if (this.master == conduit) {
                amt = this.storage.getEnergyStored() % size;
            }

            return amt + this.storage.getEnergyStored() / size;
        }
    }

    public void addConduit(TileEnergyConduit conduit) {
        if (this.conduitSet.add(conduit)) {
            if (!this.conduitAdded(conduit)) {
                return;
            }

            if (conduit.isNode) {
                if (this.nodeSet.add(conduit)) {
                    this.nodeAdded(conduit);
                }
            } else if (!this.nodeSet.isEmpty()) {
                int share = this.getNodeShare(conduit);
                if (this.nodeSet.remove(conduit)) {
                    conduit.energyForGrid = this.storage.extractEnergy(share, false);
                    this.nodeRemoved(conduit);
                }
            }
        }
    }

    public void removeConduit(TileEnergyConduit conduit) {
        this.conduitSet.remove(conduit);
        if (!this.nodeSet.isEmpty()) {
            int share = this.getNodeShare(conduit);
            if (this.nodeSet.remove(conduit)) {
                conduit.energyForGrid = this.storage.extractEnergy(share, false);
                this.nodeRemoved(conduit);
            }
        }
    }

    public void regenerate() {
        this.regenerating = true;
        HANDLER.regenerateGrid(this);
    }

    public boolean isRegenerating() {
        return this.regenerating;
    }

    public void doGridPreUpdate() {
        if (!this.regenerating) {
            if (this.nodeSet.isEmpty()) {
                HANDLER.removeGrid(this);
            } else {
                EnergyStorage tempStorage = this.storage;
                if (tempStorage.getEnergyStored() < tempStorage.getMaxEnergyStored()) {
                    ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
                    Iterator i$ = this.nodeSet.iterator();

                    while (i$.hasNext()) {
                        TileEnergyConduit conduit = (TileEnergyConduit) i$.next();
                        int i = 6;

                        while (i-- > 0) {
                            conduit.extract(directions[i], tempStorage);
                        }
                    }
                }
            }
        }
    }

    public void doGridUpdate() {
        if (!this.regenerating) {
            if (this.nodeSet.isEmpty()) {
                HANDLER.removeGrid(this);
            } else {
                EnergyStorage storage = this.storage;
                if (storage.getEnergyStored() > 0) {
                    ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
                    int size = this.nodeSet.size();
                    int toDistribute = storage.getEnergyStored() / size;
                    int sideDistribute = toDistribute / 6;
                    this.distribution = toDistribute;
                    this.distributionSide = sideDistribute;
                    int overflow = this.overflowSelector
                        = (this.overflowSelector + 1) % size;
                    TileEnergyConduit master
                        = (TileEnergyConduit) this.nodeSet.get(overflow);
                    if (sideDistribute > 0) {
                        Iterator i$ = this.nodeSet.iterator();

                    label70:
                        while (true) {
                            TileEnergyConduit cond;
                            do {
                                if (!i$.hasNext()) {
                                    break label70;
                                }

                                cond = (TileEnergyConduit) i$.next();
                            } while (cond == master);

                            int e = 0;

                            for (int i = 6; i-- > 0;
                                 e += cond.transfer(directions[i], sideDistribute)) {}

                            if (e > 0) {
                                storage.modifyEnergyStored(-e);
                            }
                        }
                    }

                    toDistribute += storage.getEnergyStored() % size;
                    sideDistribute = toDistribute / 6;
                    int e;
                    int i;
                    if (sideDistribute > 0) {
                        e = 0;

                        for (i = 6; i-- > 0;
                             e += master.transfer(directions[i], sideDistribute)) {}

                        if (e > 0) {
                            storage.modifyEnergyStored(-e);
                        }
                    } else if (toDistribute > 0) {
                        e = 0;

                        for (i = 6; i-- > 0 && e < toDistribute;
                             e += master.transfer(directions[i], toDistribute - e)) {}

                        if (e > 0) {
                            storage.modifyEnergyStored(-e);
                        }
                    }
                }
            }
        }
    }

    public void markSweep() {
        this.destroyGrid();
        if (!this.conduitSet.isEmpty()) {
            TileEnergyConduit main = (TileEnergyConduit) this.conduitSet.poke();
            LinkedHashList oldSet = this.conduitSet;
            this.nodeSet.clear();
            this.conduitSet = new LinkedHashList(Math.min(oldSet.size() / 6, 5));
            LinkedHashList toCheck = new LinkedHashList();
            LinkedHashList checked = new LinkedHashList();
            BlockPosition bp = new BlockPosition(0, 0, 0);
            ForgeDirection[] dir = ForgeDirection.VALID_DIRECTIONS;
            toCheck.add(main);
            checked.add(main);

            while (!toCheck.isEmpty()) {
                main = (TileEnergyConduit) toCheck.shift();
                this.addConduit(main);
                World world = main.getWorldObj();
                int i = 6;

                while (i-- > 0) {
                    bp.x = main.xCoord;
                    bp.y = main.yCoord;
                    bp.z = main.zCoord;
                    bp.step(dir[i]);
                    if (world.blockExists(bp.x, bp.y, bp.z)) {
                        TileEntity te = bp.getTileEntity(world);
                        if (te instanceof TileEnergyConduit) {
                            TileEnergyConduit teConduit = (TileEnergyConduit) te;
                            if (main.canInterface(teConduit, dir[i ^ 1])
                                && checked.add(teConduit)) {
                                toCheck.add(teConduit);
                            }
                        }
                    }
                }

                oldSet.remove(main);
            }

            if (!oldSet.isEmpty()) {
                EnergyNetwork newGrid = new EnergyNetwork();
                newGrid.conduitSet = oldSet;
                newGrid.regenerating = true;
                newGrid.markSweep();
            }

            if (this.nodeSet.isEmpty()) {
                HANDLER.removeGrid(this);
            } else {
                HANDLER.addGrid(this);
            }

            this.rebalanceGrid();
            this.regenerating = false;
        }
    }

    public void destroyGrid() {
        this.master = null;
        this.regenerating = true;
        Iterator i$ = this.nodeSet.iterator();

        TileEnergyConduit currentConduit;
        while (i$.hasNext()) {
            currentConduit = (TileEnergyConduit) i$.next();
            this.destroyNode(currentConduit);
        }

        i$ = this.conduitSet.iterator();

        while (i$.hasNext()) {
            currentConduit = (TileEnergyConduit) i$.next();
            this.destroyConduit(currentConduit);
        }

        HANDLER.removeGrid(this);
    }

    public void destroyNode(TileEnergyConduit conduit) {
        conduit.energyForGrid = this.getNodeShare(conduit);
        conduit.grid = null;
    }

    public void destroyConduit(TileEnergyConduit conduit) {
        conduit.grid = null;
    }

    public boolean canMergeGrid(EnergyNetwork otherGrid) {
        return otherGrid != null;
    }

    public void mergeGrid(EnergyNetwork grid) {
        if (grid != this) {
            boolean r = this.regenerating || grid.regenerating;
            grid.destroyGrid();
            if (!this.regenerating & r) {
                this.regenerate();
            }

            this.regenerating = true;
            Iterator i$ = grid.conduitSet.iterator();

            while (i$.hasNext()) {
                TileEnergyConduit conduit = (TileEnergyConduit) i$.next();
                this.addConduit(conduit);
            }

            this.regenerating = r;
            grid.conduitSet.clear();
            grid.nodeSet.clear();
        }
    }

    public void nodeAdded(TileEnergyConduit conduit) {
        if (this.master == null) {
            this.master = conduit;
            HANDLER.addGrid(this);
        }

        this.rebalanceGrid();
        this.storage.modifyEnergyStored(conduit.energyForGrid);
    }

    public void nodeRemoved(TileEnergyConduit conduit) {
        this.rebalanceGrid();
        if (conduit == this.master) {
            if (this.nodeSet.isEmpty()) {
                this.master = null;
                HANDLER.removeGrid(this);
            } else {
                this.master = (TileEnergyConduit) this.nodeSet.get(0);
            }
        }
    }

    public boolean conduitAdded(TileEnergyConduit conduit) {
        if (conduit.grid != null) {
            if (conduit.grid == this) {
                return false;
            }

            this.conduitSet.remove(conduit);
            if (!this.canMergeGrid(conduit.grid)) {
                return false;
            }

            this.mergeGrid(conduit.grid);
        } else {
            conduit.setGrid(this);
        }

        return true;
    }

    public void rebalanceGrid() {
        this.storage.setCapacity(this.nodeSet.size() * 6000);
    }

    public int getConduitCount() {
        return this.conduitSet.size();
    }

    public int getNodeCount() {
        return this.nodeSet.size();
    }

    static {
        HANDLER = GridTickHandler.energy;
    }
}
