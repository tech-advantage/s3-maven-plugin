# S3 Maven plugin

This plugin helps maven user plugin to donwload files from S3 source (AWS or Minio).

## System Requirements

The following specifies the minimum requirements to run this Maven plugin:

| Tools | Version |
|-------|---------|
| Maven | 3.9.4   |
| JDK   | 11      |

## Usage

You should specify the version in your project's plugin configuration:

```XML 

<project>
    ...
    <build>
        <!-- To use the plugin goals in your POM or parent POM -->
        <plugins>
            <plugin>
                <groupId>fr.techad.s3</groupId>
                <artifactId>s3-maven-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <id>download-dependency</id>
                        <goals>
                            <goal>download</goal>
                        </goals>
                        <phase>process-resources</phase>
                        <configuration>
                            <endpoint>https://storage.example.com/</endpoint>
                            <bucket>my-bucket</bucket>
                            <outputDirectory>${project.build.directory}/staging</outputDirectory>
                            <region>my-region</region>
                            <downloads>
                                <download>
                                    <path>path/of/your/file</path>
                                    <fileName>my-file-to-download.zip</fileName>
                                    <unpack>true</unpack>
                                </download>
                            </downloads>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    ...
</project>
```

## Credentials

You can define credentials with  :

* settings.xml
* Environment variables
* Properties
* declaration

In case of multiple declarations, the order to get credentials are in same order of the list defined above.

### Settings.xml

In your settings.xml, add the server declaration:

```XML

<settings>
    <servers>
        <server>
            <id>s3-repo</id>
            <username>ACCESS-KEY</username>
            <password>SECRET-KEY</password>
        </server>
    </servers>
</settings>
```

and add it in your pom.xml in the configuration:

```XML

<configuration>
    <serverId>s3-repo</serverId>
</configuration>
```

### Environment variables

You just define S3_USER and S3_PASSWORD.

```shell
export S3_USER=ACCESSKEY
export S3_PASSWORD=SECRETKEY
```

# Properties

the properties to define are:

* s3.user
* s3.password

Example:

```shell
mvn package -Ds3.user=ACCESSKEY -Ds3.password=SECRETKEY
```

### pom.xml

You can define the credential directly in the pom.xml (not recommended)

```XML

<configuration>
    <accessKey>ACCESSKEY</accessKey>
    <secretKey>SECRETKEY</secretKey>
</configuration>
```

## Parameters

### Main

| Name            | Type   | Required | Since | Description                                                                                          |
|-----------------|--------|----------|-------|------------------------------------------------------------------------------------------------------|
| endpoint        | String | yes      | 1.0.0 | the url of the storage objects server                                                                |
| bucket          | String | yes      | 1.0.0 | the bucket name                                                                                      |
| region          | String | no       | 1.0.0 | the region of the storage                                                                            |
| outputDirectory | File   | no       | 1.0.0 | the directory where downloaded file will be saved. (**Default value**: `${project.build.directory}`) |
| downloads       | List   | no       | 1.0.0 | the list of files to download                                                                        |

### Download

| Name     | Type    | Required | Since | Description                                          |
|----------|---------|----------|-------|------------------------------------------------------|
| path     | String  | yes      | 1.0.0 | the path where file is present                       |
| fileName | String  | yes      | 1.0.0 | the filename to download                             |
| bucket   | String  | no       | 1.0.0 | override the default bucket name                     |
| unpack   | boolean | no       | 1.0.0 | true to unzip the file. (**Default value**: `false`) |
