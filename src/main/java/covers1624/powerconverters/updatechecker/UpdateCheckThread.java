package covers1624.powerconverters.updatechecker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import covers1624.powerconverters.util.FMLLogHelper;
import covers1624.powerconverters.util.LogHelper;
import org.apache.logging.log4j.Level;

public class UpdateCheckThread extends Thread {
    private String updateURL
        = "https://raw.github.com/covers1624/PowerConverters/master/UpdateInfo.update";
    private boolean checkComplete = false;
    private boolean newVersionAvalable = false;
    private float newVersion = 0.0F;
    private float currentVersion = 0.0F;

    public UpdateCheckThread() {
        super("PowerConverters Update Thread");
    }

    public void run() {
        try {
            URL versionFile = new URL(this.updateURL);
            BufferedReader reader
                = new BufferedReader(new InputStreamReader(versionFile.openStream()));
            String remoteString = reader.readLine();
            String[] localSplit = "1.7.10-2.11".split("-");
            String[] remoteSplit = remoteString.split("-");
            if (this.compareVersions(localSplit[1], remoteSplit[1])) {
                this.newVersionAvalable = true;
                LogHelper.info("We Have An update");
            } else {
                LogHelper.info("We Have No Update");
            }

            this.checkComplete = true;
        } catch (Exception var6) {
            FMLLogHelper.logException(Level.INFO, "It Broke!", var6);
        }
    }

    private boolean compareVersions(String local, String remote) {
        if (Float.parseFloat(local) > Float.parseFloat(remote)) {
            this.newVersion = Float.parseFloat(remote);
            this.currentVersion = Float.parseFloat(local);
            return true;
        } else {
            return false;
        }
    }

    public float getNewVersion() {
        return this.newVersion;
    }

    public boolean checkFinished() {
        return this.checkComplete;
    }

    public boolean newVersion() {
        return this.newVersionAvalable;
    }
}
