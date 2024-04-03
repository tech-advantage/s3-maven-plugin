package fr.techad.artifact;

import org.apache.maven.plugins.annotations.Parameter;

public class Download  extends Artifact {

    /**
     * Whether to unpack the file in case it is an archive (.zip)
     */
    @Parameter(property = "unpack", defaultValue = "false")
    private boolean unpack;

    public boolean isUnpack() {
        return unpack;
    }

    public void setUnpack(boolean unpack) {
        this.unpack = unpack;
    }
}
