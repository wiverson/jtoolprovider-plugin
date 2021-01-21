# jtoolprovider-plugin
 
A simple [Maven](https://maven.apache.org) Plugin to bridge Maven and the 
[Java ToolProvider API](https://docs.oracle.com/javase/10/docs/api/java/util/spi/ToolProvider.html).

This plugin is particularly helpful when working with tools such as jdeps, jlink, and jpackage.
By combining this plugin with
[Maven build profiles](https://maven.apache.org/guides/introduction/introduction-to-profiles.html), 
it's possible to build a single Maven pom.xml that can generate platform-specific installers.
Very useful for things like working with [JavaFX](https://openjfx.io).

For a complete example of the use of this Plugin to generate JavaFX macOS and Windows native desktop
applications - complete with native installer 
packages - [see this template](https://github.com/wiverson/maven-jpackage-template).

This plugin quickly and easily bridges the rich capabilities of Maven with core JDK tools 
that lack full Maven plugin integration.  While tools like jdeps, jlink, and jpackage can be
wrangled with shell scripts, Maven is much, much easier for working with things like 
CLASSPATH, module paths, and directories.

The following tools work out-of-the-box with the Java 15 ToolProvider API (and therefore this plugin):

- jmod, jar, javac, javadoc, javap, jdeps, jlink

As of Java 15, jpackage requires additional configuration as described below.

For ordinary use, you should stick with the standard Maven plugins for javac, jar, 
and javadoc.

The following tools do NOT appear to work with the Java 15 ToolProvider API: 
- jaotc, jarsigner, java, jcmd, jconsole, jdb, jdeprscan, jfr, jhsdb, jimage,
jinfo, jps, jrunscript, jshell, jstack, jstat, jstatd, rmid, rmiregistry, serialver, jmap

## Shortcuts

The plugin now supports a set of shortcuts to make it easier to write less verbose commands
for tools - in particular, jdeps, jlink, and jpackage. For example, instead of writing:

```
<arg>--module-path</arg>
<arg>${javafx.libs}</arg>
```

You can now just write

```
<modulePath>${javafx.libs}</modulePath>
```

The list of shortcuts can be found at the end of this document.

## jpackage

jpackage will ONLY work with the Java 15 ToolProvider (and therefore this plugin) if you use the 
following argument when you launch the JVM:

`--add-modules jdk.incubator.jpackage`

You can add that configuration in a variety of places depending on IDE and build tool. 

In IntelliJ, for example, you might need to add it to your JUnit template.

There are a lot of options for [configuring Maven](https://maven.apache.org/configure.html).
The best option will likely vary depending on your situation (e.g. local dev environment, 
CI system, etc.)

jpackage is expected to move out of incubator status with the release of Java 16.

# Build Info

**Current version** 

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wiverson/jtoolprovider-plugin/badge.svg)](https://search.maven.org/search?q=a:jtoolprovider-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.wiverson/jtoolprovider-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.wiverson%22%20AND%20a:%22jtoolprovider-plugin%22)
[![mvn verify status](https://github.com/wiverson/jtoolprovider-plugin/workflows/mvn%20verify/badge.svg)](https://github.com/wiverson/jtoolprovider-plugin/actions?query=workflow%3A%22mvn+verify%22)

# Simple Example Plugin Configuration

```xml
<plugin>
    <groupId>io.github.wiverson</groupId>
    <artifactId>jtoolprovider-plugin</artifactId>
    <version>*use current version*</version>
    <executions>
        <execution>
            <id>test</id>
            <phase>test-compile</phase>
            <goals>
                <goal>java-tool</goal>
            </goals>
            <configuration>
                <toolName>jar</toolName>
                <args>
                    <arg>--version</arg>
                </args>
            </configuration>
        </execution>
    </executions>
</plugin>
```

# Kitchen Sink

Here's an example with all of the currently available configuration options.

Most of the interesting configuration options are the arguments passed to
the underlying tool.

```xml
 <plugin>
    <groupId>io.github.wiverson</groupId>
    <artifactId>jtoolprovider-plugin</artifactId>
    <version>*use current version*</version>
    <executions>
        <execution>
            <id>test</id>
            <phase>test-compile</phase>
            <goals>
                <goal>java-tool</goal>
            </goals>
            <configuration>
                <toolName>jar</toolName>
                <writeOutputToLog>true</writeOutputToLog>
                <writeErrorsToLog>true</writeErrorsToLog>
                <failOnError>true</failOnError>
                <args>
                    <arg>--version</arg>
                </args>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Shortcuts

The following shortcuts can be used to make expressing arguments cleaner. See the top of this 
document for examples. You can just use the args/arg versions and/or these shortcuts. The 
shortcuts are all passed to the tool before the args/arg values.

- classPath
- addModules
- appImage
- appVersion
- check
- compress
- copyright
- description
- dest
- dir
- disablePlugin
- exclude
- fileAssociations
- generateModuleInfo
- generateOpenModule
- hashModules
- headerFiles
- icon
- input
- installDir
- javaOptions
- jlinkOptions
- launcher
- licenseFile
- limitModules
- macPackageIdentifier
- macPackageName
- macPackageSigningPrefix
- macSigningKeyUserName
- macSigningKeychain
- mainJar
- modulePath
- module
- multiRelease
- name
- output
- postProcessPath
- regex
- require
- resourceDir
- resourcesLastSorter
- runtimeImage
- saveOpts
- suggestProviders
- system
- temp
- type
- upgradeModulePath
- vendor
- winMenuGroup