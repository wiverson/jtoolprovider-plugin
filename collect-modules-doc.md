# Goal: collect-modules

## You Probably Don't Want To Use This Goal

First of all, a brief note. The Java module system really isn't set up to be usable by ordinary developers. As
seen in this [very, very long GitHub issue](https://github.com/wiverson/maven-jpackage-template/issues/8), 
despite many days of effort I was unable to create a working modules build for a Spring Boot application. And that's
with a decent working knowledge of how the module system works - in the end the produced builds still ran into
mysterious errors (e.g. a runtime message that spring-core can't see spring-annotations, despite both being declared
as open modules).

In the end, my conclusion is that the module system and adding module-info to libraries is fine for very specific
use cases (e.g. JavaFX), but for the vast, vast majority of developers it is probably preferable best to just
regard it as a JDK development implementation detail, with the only useful scenario being the production of a
stripped down JVM.

In other words, while this particular usage of this plugin (generating modules) is an interesting artifact, at
this point I'd be sorely pressed to come up with a scenario where I would actually recommend using it.

So... umm. Yeah.

## Documentation

The goal `collect-modules` makes it much easier to integrate the standard Maven dependency system with the
[Java module system](https://www.baeldung.com/java-9-modularity). Java modules allow
[jlink](https://docs.oracle.com/en/java/javase/15/docs/specs/man/jlink.html) tool to generate a nice, slim custom JVM,
which in turn allows [jpackage](https://docs.oracle.com/en/java/javase/15/docs/specs/man/jpackage.html)
to build nice, tiny native desktop installers.

Unfortunately, there are a lot of challenges working with Java modules. The tools and the error messages are confusing.
The terminology conflicts with both standard Java terminology and standard Maven terminology. Worst of all, many of the
published JARs currently available in Maven central have correct Maven dependencies, but incorrect Java module
declarations.

This utility tries to help make this easier. Here are the basic steps:

- Look at the Maven-assembled dependency tree
- For every jar in the tree, if it has a module-info, put it into a "declared-modules"
  directory
- If the jar doesn't have a module-info, put it into a "declared-not-modules" directory
- Use an option to skip adding a jar (for example, if you are using JavaFX jmods)
- Use an option to strip the module info for a badly declared module.
- If a jar doesn't have a module-info file, either because it never had it or because it was stripped, use jdeps to
  generate one and add it to the jar.

All of this works with both regular and [multi-release jars](https://www.baeldung.com/java-multi-release-jar).

In the example below, you can see HikariCP listed under `stripJars`. This is because even though
[HikariCP includes a module-info.class under a Java 11 multi-release section](https://github.com/brettwooldridge/HikariCP/blob/dev/src/main/java11/module-info.java)
, that module-info.class includes a static reference to several other modules (
e.g. `requires static org.hibernate.orm.core`). Confusingly, a `static` declaration in the HikariCP module-info is
actually supposed to mean that the dependency is *optional*. Sadly, even those are declared as optional, `jdeps` is
unable to process the HikariCP module without the presence of these dependencies. In other words, when jdeps sees an
optional dependency declared in a module-info.class, it refuses to work unless the entire module-info transitive
dependency chain is present.

If that all seemed like a confusing mess... yup, it's a mess.

The workaround for HikariCP is... to just strip the module-info from the HikariCP jar and generate a new module-info
based on the declared Maven dependencies and using jdeps. This plugin makes that process as painless as possible.

```xml

<plugin>
    <groupId>io.github.wiverson</groupId>
    <artifactId>jtoolprovider-plugin</artifactId>
    <version>version-goes-here</version>
    <executions>
        <execution>
            <id>collect-modules</id>
            <phase>process-resources</phase>
            <goals>
                <goal>collect-modules</goal>
            </goals>
            <configuration>
                <providedModuleDirectories>
                    <directory>${javafx.mods}</directory>
                    <directory>${java.home}\jmods\</directory>
                </providedModuleDirectories>
                <ignoreJars>
                    <jar>javafx-controls-15.jar</jar>
                    <jar>javafx-graphics-15.jar</jar>
                    <jar>javafx-base-15.jar</jar>
                </ignoreJars>
                <stripJars>
                    <jar>HikariCP</jar>
                </stripJars>
            </configuration>
        </execution>
    </executions>
</plugin>
```

# Options

```java
// Set by default to the Maven supplied compilation classpath
@Parameter(defaultValue = "${project.compileClasspathElements}", readonly = true, required = true)
private List<String> compilePath;

// Where to place the generate module-info.java files, which are then compiled and added to the relevant jar files
@Parameter(required = true, defaultValue = "${project.build.directory}/module-info-work")
private File moduleInfoWorkDirectory;

// Where this goal will place any jars containing module-info declarations found on the classpath
@Parameter(required = true, defaultValue = "${project.build.directory}/declared-modules")
private File foundModulesDirectory;

// Where this goal will place any jars with no module-info declarations found on the classpath
@Parameter(required = true, defaultValue = "${project.build.directory}/declared-not-modules")
private File notModulesDirectory;

// Provided module directories (e.g. JDK supplied modules, JavaFX modules, etc)
@Parameter
private List<File> providedModuleDirectories;

// Simple/dumb matches against these values will cause the plugin to skip this entry on the classpath
@Parameter(alias = "ignoreJars")
private List<String> ignoreJars;

// Any jar that has a module-info.class declaration that contains this string in the name will have the
// module-info.class declaration stripped and then regenerated by jdeps
@Parameter(alias = "stripJars")
private List<String> stripJars;

// Required by jdeps for multi-module resolution. By default it just uses the current JDK version 
@Parameter
private int javaVersion=Runtime.version().feature();

// If turned on the goal will generate a lot of debugging output
@Parameter
private boolean debug;

// By default the goal will clean up the working directories above before execution. Probably only need to set
// this to false for very rare debugging
@Parameter(name = "autoClean", defaultValue = "true")
private boolean autoClean;

// The key for these properties are a simple String contains comparison. The value is the desired action.
// Supported values are:
//     info - this will cause jdeps to use --generate-module-info to generate the module-info.java
//     open - this will cause jdeps to use --generate-open-module to generate the module-info.java
//     /a/path/of/some/kind/module-info.java - will skip the jdeps execution for this specific jar
//                                             and use the custom module-info.java instead.
@Parameter
private Properties moduleInfoOverrides;
```