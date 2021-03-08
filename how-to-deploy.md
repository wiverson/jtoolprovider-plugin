
# How To Deploy

Run this command to deploy to central.

`mvn --batch-mode release:prepare release:perform -Prelease-to-central`

# settings.xml

The following is a template for the needed ~/.m2/settings.xml file.

Once that is done, go to [Nexus](https://oss.sonatype.org/#stagingRepositories) to close the open repo.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <servers>
        <server>
            <id>release</id>
            <username>nexus username goes here</username>
            <password>nexus password goes here</password>
        </server>
        <server>
            <id>nexus</id>
            <username>nexus username goes here</username>
            <password>nexus password goes here</password>
        </server>
        <server>
            <id>github</id>
            <username>github user id goes here</username>
            <password>github personal token goes here</password>
        </server>
        <server>
            <id>gpg.passphrase</id>
            <passphrase>passphrase to unlock the gpg key goes here</passphrase>
        </server>
    </servers>
</settings>
```
