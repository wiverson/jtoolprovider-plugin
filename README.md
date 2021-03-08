# jtoolprovider-plugin

This Maven Plugin does two things. First, it automatically transforms your [Maven](https://maven.apache.org) dependency
graph into Java modules. Second, it bridges Maven and built-in Java tools like jdeps, jlink, and jpackage.

Why would you care about any of that? The main reason: this plugin makes it much, much easier to generate native Java
desktop applications with nice, small installers. Here's a
[complete working example of generating a JavaFX desktop app](https://github.com/wiverson/maven-jpackage-template),
including GitHub Actions to automatically generate the macOS, Windows, and Linux versions.

The two goals for this plugin are [`collect-modules`](collect-modules-doc.md) (which processes the Maven dependency tree
to transform Maven dependencies into modules) and [`java-tool`](java-tool-doc.md) (to run any supported JDK tool
directly from in Maven).

By combining this plugin with
[Maven build profiles](https://maven.apache.org/guides/introduction/introduction-to-profiles.html), it's possible to
build a single Maven pom.xml that can generate platform-specific installers. Very useful for things like working
with [JavaFX](https://openjfx.io).

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wiverson/jtoolprovider-plugin/badge.svg)](https://search.maven.org/search?q=a:jtoolprovider-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.wiverson/jtoolprovider-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.wiverson%22%20AND%20a:%22jtoolprovider-plugin%22)
[![mvn verify status](https://github.com/wiverson/jtoolprovider-plugin/workflows/mvn%20verify/badge.svg)](https://github.com/wiverson/jtoolprovider-plugin/actions?query=workflow%3A%22mvn+verify%22)

# Next Steps

- Read the [collect-modules](collect-modules-doc.md) goal documentation
- Read the [java-tool](java-tool-doc.md) goal documentation
- Check out the
  [complete working example this plugin in action](https://github.com/wiverson/maven-jpackage-template) building a
  JavaFX desktop application, with GitHub Actions to generate the macOS, Windows, and Linux versions

# Background Information

- The java-tool goal integrates with Maven via the
  [Java ToolProvider API](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/spi/ToolProvider.html)
  