# java-tools-plugin
 
A simple [Maven](https://maven.apache.org) plugin to bridge Maven builds and the 
[Java ToolProvider API](https://docs.oracle.com/javase/10/docs/api/java/util/spi/ToolProvider.html).

Below is a simple example of invoking the jar tool. 
More useful are combining the plugin with other tools such as jdeps, jlink, and jpackage.
By combining this plugin with
[Maven build profiles](https://maven.apache.org/guides/introduction/introduction-to-profiles.html), 
it's possible to build a single Maven pom.xml that can generate platform-specific installers.
Very useful for things like working with [JavaFX](https://openjfx.io).

This plugin quickly and easily bridges the rich options offered
by Maven with the core JDK tools set in the absence of full Maven plugin integration.
While tools like jdeps, jlink, and jpackage can be wrangled with shell scripts, 
Maven is much, much easier for working with things like classpaths and directories.

The following tools work out-of-the-box with Java 15:

- jmod, jar, javac, javadoc, javap, jdeps, jlink

For ordinary use, you should stick with the standard Maven plugins for javac, jar, 
and javadoc.

The following tools do NOT appear to work with Java 15: 
- jaotc, jarsigner, java, jcmd, jconsole, jdb, jdeprscan, jfr, jhsdb, jimage,
jinfo, jps, jrunscript, jshell, jstack, jstat, jstatd, rmid, rmiregistry, serialver, jmap

## jpackage

jpackage will ONLY work in Java 15 if you use the following argument when you launch the JVM:

`--add-modules jdk.incubator.jpackage`

You can add that configuration in a variety of places depending on IDE and build tool. 

In IntelliJ, for example, you might need to add it to your JUnit template.

There are a lot of options for [configuring Maven](https://maven.apache.org/configure.html).
The best option will likely vary depending on your situation (e.g. local dev environment, 
CI system, etc.)

jpackage is expected to move out of incubator status with the release of Java 16.

# Simple Example Plugin Configuration

```xml
<plugin>
    <groupId>io.github.wiverson</groupId>
    <artifactId>jtoolprovider-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
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
    <version>1.0-SNAPSHOT</version>
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