package fr.techad;

import fr.techad.artifact.Download;
import io.minio.MinioClient;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

import java.util.List;

public abstract class AbstractMinioMojo extends AbstractMojo {

    @Parameter(property = "accessKey")
    protected String accessKey;

    @Parameter(property = "secretKey")
    protected String secretKey;

    @Parameter(property = "endpoint")
    protected String endpoint;

    @Parameter(property = "bucket")
    private String bucket;

    @Parameter(property = "serverId")
    private String serverId;

    @Parameter(property = "region", defaultValue = "")
    private String region;

    @Parameter(property = "downloads")
    protected List<Download> downloads;

    //@Parameter( property = "uploads" )
    //protected List<Upload> uploads;

    private MinioClient minioClient;

    @Parameter(defaultValue = "${settings}", readonly = true, required = true)
    private Settings settings;

    /**
     *
     * @return the bucket name
     */
    public String getBucket() {
        return bucket;
    }

    /**
     * Create a minio client. The credential is extracted by priority order : server in settings, environment variables and properties
     * @return the minio client
     */
    protected MinioClient openConnectionInternal() {
        if (minioClient == null) {

            String user;
            String password;
            if (serverId != null) {
                String[] credential = getServerCredentials(serverId);
                user = credential[0];
                password = credential[1];
                getLog().info("ServerId: " + serverId);
            } else {
                user = getEnvProp("S3_USER", "s3.user", this.accessKey);
                password = getEnvProp("S3_PASSWORD", "s3.password", this.secretKey);
            }
            minioClient = MinioClient.builder()
                    .endpoint(this.endpoint)
                    .credentials(user, password)
                    .region(this.region)
                    .build();
        }
        return minioClient;
    }

    private String getEnvProp(String envVarName, String propertyKey, String pomValue) {
        String value = System.getenv(envVarName);
        if (value != null) {
            return value;
        }
        value = System.getProperty(propertyKey);
        if (value != null) {
            return value;
        }

        return pomValue;
    }

    private String[] getServerCredentials(String serverId) {
        String[] credentials = new String[2];
        Server server = settings.getServer(serverId);
        if (server != null) {
            credentials[0] = server.getUsername();
            credentials[1] = server.getPassword();
            getLog().debug("Username: " + credentials[0]);
        } else {
            getLog().error("Server with id '" + serverId + "' not found in settings.xml");
        }
        return credentials;
    }
}
