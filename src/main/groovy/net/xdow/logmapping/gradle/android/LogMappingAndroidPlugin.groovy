package net.xdow.logmapping.gradle.android

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile

class LogMappingAndroidPlugin implements Plugin<Project> {
    @Override
    void apply(final Project project) {
        def hasAppPlugin = project.plugins.hasPlugin AppPlugin
        def hasLibraryPlugin = project.plugins.hasPlugin LibraryPlugin

        // Ensure the Android plugin has been added in app or library form, but not both.
        if (!hasAppPlugin && !hasLibraryPlugin) {
            throw new IllegalStateException("The 'android' or 'android-library' plugin is required.")
        } else if (hasAppPlugin && hasLibraryPlugin) {
            throw new IllegalStateException("Having both 'android' and 'android-library' plugin is not supported.")
        }

        project.extensions.create "logmapping", LogMappingExtension

        // Adds task before the evaluation of the project to access of values
        // overloaded by the developer.
        project.afterEvaluate {
            def variants = hasAppPlugin ? project.android.applicationVariants : project.android.libraryVariants
            variants.all { variant ->
                String variantName = variant.name
                if (((Set<String>)project.logmapping.enableBuildType).size() > 0
                        && !(project.logmapping.enableBuildType).contains(variantName)) {
                    System.out.println("Log mapping processor status: Skip    variant: ${variantName}")
                    return
                }
                System.out.println("Log mapping processor status: Enable  variant: ${variantName}")

                def processedFileOutputDir = project.file("${project.buildDir}/generated/source/log_mapping_processor/${variant.dirName}")
                def mappingOutput = project.file("${project.buildDir}/outputs/mapping/${variant.dirName}/log.mapping.txt")
                List<File> srcFolderList = new ArrayList<>();
                List<ConfigurableFileTree> variantSrcFolders = ((List<ConfigurableFileTree>) variant.variantData.javaSources).findAll({ f ->
                    return !f.dir.path.contains(project.buildDir.path)
                })
                for (ConfigurableFileTree fileTree : variantSrcFolders) {
                    srcFolderList.add(fileTree.dir)
                }

                def task = project.task("processLogMapping${variantName.capitalize()}",
                        type: LogMappingAndroidTask, dependsOn: "generate${variantName.capitalize()}Sources") {
                    srcFolders = srcFolderList
                    outFolder = processedFileOutputDir
                    mappingOutputFile = mappingOutput
                }
                def compileJavaTask = getJavaCompiler(variant)
                compileJavaTask.dependsOn task

                //remove original source from java compiler
                def variantSrcFoldersMap = toVariantSrcFoldersMap(variantSrcFolders)
                compileJavaTask.exclude { def defaultFileTreeElement ->
                    return variantSrcFoldersMap.containsKey(defaultFileTreeElement.file)
                }

                //add log mapping processed source to java compiler
                compileJavaTask.source = compileJavaTask.source + project.fileTree(processedFileOutputDir).builtBy(task)
            }
        }
    }

    private static def getJavaCompiler(def variant) {
        if (variant.hasProperty('javaCompileProvider')) {
            //gradle 4.10.1 +
            TaskProvider<JavaCompile> provider = variant.javaCompileProvider
            return provider.get()
        } else {
            return variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile
        }
    }

    private static def toVariantSrcFoldersMap(List<ConfigurableFileTree> variantSrcFolders) {
        def map = [:]
        variantSrcFolders.each {
            it.files.each { file ->
                map.put(file, "")
            }
        }
        return map
    }
}
