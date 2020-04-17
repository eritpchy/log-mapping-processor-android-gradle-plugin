# Log Mapping Processor Android Gradle plugin
[ ![Download](https://api.bintray.com/packages/xscript/maven/log-mapping-android-gradle-plugin/images/download.svg?version=latest) ](https://bintray.com/xscript/maven/log-mapping-android-gradle-plugin)

A gradle plugin to run log code transformation using [Log Mapping Processor](https://github.com/eritpchy/log-mapping-processor) on a project built with Gradle.


<p align="center">
    <img src="https://github.com/eritpchy/log-mapping-processor/raw/master/files/diagram.svg">
</p>

![sample](https://github.com/eritpchy/log-mapping-processor/raw/master/files/sample.png)


## Basic usage

To use log-mapping-android-gradle-plugin, you need to add the plugin classes to the build script's classpath. To do this, you use a `buildscript` block. The following example shows how you might do this when the JAR containing the plugin has been published to a local repository:

```groovy
buildscript {
    repositories {      
      jcenter()
    }
    dependencies {
        classpath 'net.xdow:log-mapping-android-gradle-plugin:1.0.6'
    }
}

apply plugin: 'net.xdow.logmapping'
```

Consequently, when `gradle build` is run on your project, the source code is first rewritten by `Log Mapping Processor` before compilation.

## Configuration

```groovy
logmapping {
    debug false
    keywords "net.xdow.Log.debug",
             "net.xdow.Log.error"
    enableBuildType "debug", "release"
    jobs Runtime.getRuntime().availableProcessors()
}
```
## Compiling
To compile Log Mapping Processor Android Gradle plugin, you need a Java Development Kit 1.8 (JDK8)
```shell script
git clone https://github.com/eritpchy/log-mapping-processor-android-gradle-plugin
cd log-mapping-processor-android-gradle-plugin
./gradlew jar
```

## References
- [Log Mapping Processor](https://github.com/eritpchy/log-mapping-processor)
- [Log Mapping Reverse](https://github.com/eritpchy/log-mapping-reverse)
- [JavaParser](https://github.com/javaparser/javaparser)