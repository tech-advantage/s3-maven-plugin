package fr.techad;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.bzip2.BZip2UnArchiver;
import org.codehaus.plexus.archiver.gzip.GZipUnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.archiver.snappy.SnappyUnArchiver;
import org.codehaus.plexus.archiver.xz.XZUnArchiver;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Mojo(name = "download", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MinioDownloadMojo extends AbstractMinioMojo {
    /**
     * Location of the output directory .
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDirectory", required = true)
    private File outputDirectory;

    @Inject
    private ArchiverManager archiverManager;

    public void execute() {
        getLog().info("Number of files to download: " + this.downloads.size());

        getLog().info("Connect to: " + this.endpoint);
        MinioClient minioClient = openConnectionInternal();

        downloads.forEach(download -> {
            String path = download.getPath() + "/" + download.getFileName();
            getLog().debug("Object URI: " + path);
            String targetBucket = download.getBucket() != null ? download.getBucket() : getBucket();
            getLog().debug("Bucket: " + targetBucket);

            try {
                String targetFile = FilenameUtils.concat(outputDirectory.getAbsolutePath(), download.getFileName());
                minioClient.downloadObject(DownloadObjectArgs.builder()
                        .bucket(targetBucket)
                        .object(path)
                        .overwrite(true)
                        .filename(targetFile)
                        .build());

                if (download.isUnpack()) {
                    getLog().info("Unpack: " + targetFile);
                    unpack(targetFile, download.getFileName());
                }
            } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
                getLog().error(e.getMessage(), e);
            }
        });
    }

    private void unpack(String targetFile, String filename) {
        try {
            File outputFile = new File(targetFile);

            UnArchiver unarchiver = this.archiverManager.getUnArchiver(outputFile);
            unarchiver.setSourceFile(outputFile);

            if (isFileUnArchiver(unarchiver)) {
                unarchiver.setDestFile(new File(this.outputDirectory, FilenameUtils.getBaseName(filename)));
            } else {
                File dest = new File(FilenameUtils.removeExtension(targetFile));
                dest.mkdirs();
                unarchiver.setDestDirectory(dest);
            }

            unarchiver.extract();
            Files.deleteIfExists(outputFile.toPath());
        } catch (IOException | NoSuchArchiverException e) {
            getLog().error(e.getMessage());
        }
    }

    private boolean isFileUnArchiver(final UnArchiver unarchiver) {
        return unarchiver instanceof BZip2UnArchiver ||
                unarchiver instanceof GZipUnArchiver ||
                unarchiver instanceof SnappyUnArchiver ||
                unarchiver instanceof XZUnArchiver;
    }
}
