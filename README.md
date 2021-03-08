# jtoolprovider-plugin

A simple [Maven](https://maven.apache.org) Plugin to bridge Maven and the
[Java ToolProvider API](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/spi/ToolProvider.html),
along with a utility goal to make it easier to work with Java modules.

The two goals for this plugin are `collect-modules` (which processes the Maven dependency tree to transform Maven
dependencies into modules) and `java-tool` (to run any supported JDK tool directly from in Maven).

This plugin is particularly helpful when working with tools such as jdeps, jlink, and jpackage. By combining this plugin
with
[Maven build profiles](https://maven.apache.org/guides/introduction/introduction-to-profiles.html), it's possible to
build a single Maven pom.xml that can generate platform-specific installers. Very useful for things like working
with [JavaFX](https://openjfx.io).

For a complete example of the use of this Plugin to generate JavaFX macOS and Windows native desktop applications -
complete with native installer packages - [see this template](https://github.com/wiverson/maven-jpackage-template).

This plugin quickly and easily bridges the rich capabilities of Maven with core JDK tools that lack full Maven plugin
integration. While tools like jdeps, jlink, and jpackage can be wrangled with shell scripts, Maven is much, much easier
for working with things like CLASSPATH, module paths, and directories.

### Current Version

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wiverson/jtoolprovider-plugin/badge.svg)](https://search.maven.org/search?q=a:jtoolprovider-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.wiverson/jtoolprovider-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.wiverson%22%20AND%20a:%22jtoolprovider-plugin%22)
[![mvn verify status](https://github.com/wiverson/jtoolprovider-plugin/workflows/mvn%20verify/badge.svg)](https://github.com/wiverson/jtoolprovider-plugin/actions?query=workflow%3A%22mvn+verify%22)

# Next Steps

- Read the [collect-modules](collect-modules-doc.md) documentation
- Read the [java-tool](java-tool-doc.md) documentation
- Check out the
  [complete working example of use of this plugin to build a JavaFX desktop application](https://github.com/wiverson/maven-jpackage-template)