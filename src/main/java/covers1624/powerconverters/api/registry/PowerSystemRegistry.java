package covers1624.powerconverters.api.registry;

import java.util.HashMap;

import covers1624.powerconverters.util.LogHelper;
import cpw.mods.fml.common.Loader;

public class PowerSystemRegistry {
    private static HashMap powerSystems = new HashMap();
    private static int nextPowerSystemId = 0;

    public static PowerSystem createNewPowerSystem(
        String name,
        String abbreviation,
        int scaleAmmount,
        String[] voltageNames,
        int[] voltageValues,
        String unit
    ) {
        return new PowerSystem(
            name, abbreviation, scaleAmmount, voltageNames, voltageValues, unit
        );
    }

    public static void registerPowerSystem(PowerSystem powerSystem) {
        registerPowerSystem(powerSystem, nextPowerSystemId, true);
        ++nextPowerSystemId;
    }

    public static void
    registerPowerSystem(PowerSystem powerSystem, int id, boolean force) {
        if (powerSystems.get(id) == null) {
            powerSystem.id = id;
            powerSystems.put(id, powerSystem);
        } else {
            LogHelper.trace(
                "PowerSystem allready Registered %s and %s is trying to be registered. It is recomended that you let the mod decide what to do here.",
                ((PowerSystem) powerSystems.get(id)).getName(),
                powerSystem.getName()
            );
            if (force) {
                LogHelper.warn(
                    "Mod %s is forcing that PowerSystem id %s equals %s THIS MAY BREAK ALL FUNCTIONALITY!",
                    Loader.instance().activeModContainer().getModId(),
                    String.valueOf(id),
                    powerSystem.getName()
                );
                powerSystems.remove(id);
                powerSystem.id = id;
                powerSystems.put(id, powerSystem);
            }
        }
    }

    public static void unregisterPowerSystem(int id) {
        if (powerSystems.containsKey(id)) {
            LogHelper.warn(
                "Someone is trying to remove PowerSystem %s THIS MAY BREAK ALL FUNCTIONALITY!",
                ((PowerSystem) powerSystems.get(id)).getName()
            );
            powerSystems.remove(id);
        }
    }

    public static PowerSystem getPowerSystemById(int id) {
        return (PowerSystem) powerSystems.get(id);
    }

    public static class PowerSystem {
        private String abbreviation;
        private String name;
        private int scaleAmmount;
        private String[] voltageNames;
        private int[] voltageValues;
        private String unit;
        private int id;
        private boolean consumerDissabled;
        private boolean producerDissabled;

        public PowerSystem(
            String name, String abbreviation, int scaleAmmount, String unit
        ) {
            this(name, abbreviation, scaleAmmount, (String[]) null, (int[]) null, unit);
        }

        public PowerSystem(
            String name,
            String abbreviation,
            int scaleAmmount,
            String unit,
            boolean consumerDissabled,
            boolean producerDissabled
        ) {
            this(
                name,
                abbreviation,
                scaleAmmount,
                (String[]) null,
                (int[]) null,
                unit,
                consumerDissabled,
                producerDissabled
            );
        }

        public PowerSystem(
            String name,
            String abbreviation,
            int scaleAmmount,
            String[] voltageNames,
            int[] voltageValues,
            String unit
        ) {
            this.consumerDissabled = false;
            this.producerDissabled = false;
            this.name = name;
            this.abbreviation = abbreviation;
            this.scaleAmmount = scaleAmmount;
            this.voltageNames = voltageNames;
            this.voltageValues = voltageValues;
            this.unit = unit;
        }

        public PowerSystem(
            String name,
            String abbreviation,
            int scaleAmmount,
            String[] voltageNames,
            int[] voltageValues,
            String unit,
            boolean consumerDissabled,
            boolean producerDissabled
        ) {
            this.consumerDissabled = false;
            this.producerDissabled = false;
            this.name = name;
            this.abbreviation = abbreviation;
            this.scaleAmmount = scaleAmmount;
            this.voltageNames = voltageNames;
            this.voltageValues = voltageValues;
            this.unit = unit;
            this.consumerDissabled = consumerDissabled;
            this.producerDissabled = producerDissabled;
        }

        public String getName() {
            return this.name;
        }

        public String getAbbreviation() {
            return this.abbreviation;
        }

        public String[] getVoltageNames() {
            return this.voltageNames;
        }

        public int[] getVoltageValues() {
            return this.voltageValues;
        }

        public int getScaleAmmount() {
            return this.scaleAmmount;
        }

        public String getUnit() {
            return this.unit;
        }

        public int getId() {
            return this.id;
        }

        public boolean consumerDissabled() {
            return this.consumerDissabled;
        }

        public boolean producerDissabled() {
            return this.producerDissabled;
        }

        public PowerSystem setProducerState(boolean state) {
            this.producerDissabled = state;
            return this;
        }

        public PowerSystem setConsumerState(boolean state) {
            this.consumerDissabled = state;
            return this;
        }
    }
}
