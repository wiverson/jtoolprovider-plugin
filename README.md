# java-tools-plugin
 
A simple Java plugin to bridge Maven and the Java ToolProvider API (java.util.spi.ToolProvider).

Below is a (not particularly helpful) example of invoking the jar tool.
The jar tools is mostly useful because it's the most generally available.
More useful are tools such as jdeps, jlink, and jpackage.

The following tools DO work out-of-the-box with Java 15:

- jmod, jar, javac, javadoc, javap, jdeps, jlink

The following tools do NOT appear to work with Java 15: 
- jaotc, jarsigner, java, jcmd, jconsole, jdb, jdeprscan, jfr, jhsdb, jimage,
jinfo, jps, jrunscript, jshell, jstack, jstat, jstatd, rmid, rmiregistry, serialver, jmap

jpackage will ONLY work in Java 15 if you use the following JVM argument:

`--add-modules jdk.incubator.jpackage`

You can add that configuration in a variety of places depending on IDE and build tool. 

In IntelliJ, for example, you might need to add it to your JUnit template.

For command-line Maven on a Unix system, the easiest thing might be to 
create a ~.mvn/jvm.config file, with the only contents the
 `--add-modules jdk.incubator.jpackage` entry.

That said, there are a lot of options for [configuring Maven](https://maven.apache.org/configure.html).

# Example Plugin Configuration

```xml
<plugin>
    <groupId>io.github.wiverson</groupId>
    <artifactId>java-tools-plugin</artifactId>
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
