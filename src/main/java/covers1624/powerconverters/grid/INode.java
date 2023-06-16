package covers1624.powerconverters.grid;

public interface INode {
    boolean isNotValid();

    void firstTick(IGridController var1);

    void updateInternalTypes(IGridController var1);
}
