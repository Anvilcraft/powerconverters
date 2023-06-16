package covers1624.powerconverters.grid;

import java.util.Iterator;
import java.util.LinkedHashSet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class GridTickHandler implements IGridController {
    public static final GridTickHandler energy = new GridTickHandler("Energy");
    private LinkedHashSet<IGrid> tickingGridsToRegenerate = new LinkedHashSet<>();
    private LinkedHashSet<IGrid> tickingGridsToAdd = new LinkedHashSet<>();
    private LinkedHashSet<IGrid> tickingGrids = new LinkedHashSet<>();
    private LinkedHashSet<IGrid> tickingGridsToRemove = new LinkedHashSet<>();
    private LinkedHashSet<INode> conduit = new LinkedHashSet<>();
    private LinkedHashSet<INode> conduitToAdd = new LinkedHashSet<>();
    private LinkedHashSet<INode> conduitToUpd = new LinkedHashSet<>();
    private final String label;

    public GridTickHandler(String name) {
        name.hashCode();
        this.label = "GridTickHandler[" + name + "]";
    }

    public void addGrid(IGrid grid) {
        this.tickingGridsToAdd.add(grid);
        this.tickingGridsToRemove.remove(grid);
    }

    public void removeGrid(IGrid grid) {
        this.tickingGridsToRemove.add(grid);
        this.tickingGridsToAdd.remove(grid);
    }

    public void regenerateGrid(IGrid grid) {
        this.tickingGridsToRegenerate.add(grid);
    }

    public boolean isGridTicking(IGrid grid) {
        return this.tickingGrids.contains(grid);
    }

    public void addConduitForTick(INode node) {
        this.conduitToAdd.add(node);
    }

    public void addConduitForUpdate(INode node) {
        this.conduitToUpd.add(node);
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == Phase.START) {
            this.tickStart();
        } else {
            this.tickEnd();
        }
    }

    public void tickStart() {
        Iterator iter;
        if (!this.tickingGridsToRegenerate.isEmpty()) {
            synchronized (this.tickingGridsToRegenerate) {
                iter = this.tickingGridsToRegenerate.iterator();

                while (iter.hasNext()) {
                    IGrid grid = (IGrid) iter.next();
                    grid.markSweep();
                }

                this.tickingGridsToRegenerate.clear();
            }
        }

        if (!this.conduitToUpd.isEmpty()) {
            synchronized (this.conduitToUpd) {
                this.conduit.addAll(this.conduitToUpd);
                this.conduitToUpd.clear();
            }
        }

        if (!this.conduit.isEmpty()) {
            INode cond = null;

            try {
                iter = this.conduit.iterator();

                while (iter.hasNext()) {
                    cond = (INode) iter.next();
                    if (!cond.isNotValid()) {
                        cond.updateInternalTypes(this);
                    }
                }

                this.conduit.clear();
            } catch (Throwable var7) {
                throw new RuntimeException("Crashing on conduit " + cond, var7);
            }
        }

        if (!this.tickingGrids.isEmpty()) {
            Iterator i$ = this.tickingGrids.iterator();

            while (i$.hasNext()) {
                IGrid grid = (IGrid) i$.next();
                grid.doGridPreUpdate();
            }
        }
    }

    public void tickEnd() {
        if (!this.tickingGridsToRemove.isEmpty()) {
            synchronized (this.tickingGridsToRemove) {
                this.tickingGrids.removeAll(this.tickingGridsToRemove);
                this.tickingGridsToRemove.clear();
            }
        }

        if (!this.tickingGridsToAdd.isEmpty()) {
            synchronized (this.tickingGridsToAdd) {
                this.tickingGrids.addAll(this.tickingGridsToAdd);
                this.tickingGridsToAdd.clear();
            }
        }

        Iterator cond;
        if (!this.tickingGrids.isEmpty()) {
            cond = this.tickingGrids.iterator();

            while (cond.hasNext()) {
                IGrid grid = (IGrid) cond.next();
                grid.doGridUpdate();
            }
        }

        if (!this.conduitToAdd.isEmpty()) {
            synchronized (this.conduitToAdd) {
                this.conduit.addAll(this.conduitToAdd);
                this.conduitToAdd.clear();
            }
        }

        if (!this.conduit.isEmpty()) {
            cond = null;

            try {
                Iterator iter = this.conduit.iterator();

                while (iter.hasNext()) {
                    INode cond1 = (INode) iter.next();
                    if (!cond1.isNotValid()) {
                        cond1.firstTick(this);
                    }
                }

                this.conduit.clear();
            } catch (Throwable var8) {
                throw new RuntimeException("Crashing on conduit " + cond, var8);
            }
        }
    }

    public String toString() {
        return this.label + "@" + this.hashCode();
    }
}
