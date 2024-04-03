package fr.techad.artifact;

import org.apache.maven.plugins.annotations.Parameter;

public abstract class Artifact {

    @Parameter(property = "bucket", required = false)
    protected String bucket;

    @Parameter(property = "path", required = true)
    protected String path;

    @Parameter(property = "fileName", required = true)
    protected String fileName;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
