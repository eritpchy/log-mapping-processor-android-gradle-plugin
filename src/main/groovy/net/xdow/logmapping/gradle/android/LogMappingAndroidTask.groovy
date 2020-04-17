package net.xdow.logmapping.gradle.android

import net.xdow.logmapping.Launcher
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class LogMappingAndroidTask extends DefaultTask {
    List<File> srcFolders
    File outFolder
    File mappingOutputFile

    void init() {
        def log = project.logger
        printEnvironment(log.&debug)
        if (project.logmapping.debug) {
            printEnvironment(System.out.&println)
        }
    }

    @TaskAction
    void run() {
        init()
        // No source code to log mapping.
        if (srcFolders.size() == 0) {
            project.logger.debug("No source file defined.")
            return
        }
        outFolder.deleteDir()
        mappingOutputFile.getParentFile().mkdirs()

        def args = [
                "--output-dir", outFolder.absolutePath,
                "--mapping-file", mappingOutputFile.absolutePath,
                "--jobs", project.logmapping.jobs,
        ]

        if (project.logmapping.debug) {
            args << "-v"
        }
        srcFolders.each { inputDir ->
            if (!inputDir.exists()) {
                return
            }
            args << "--input-dir" << inputDir.absolutePath
        }
        project.logmapping.keywords.each {
            args << "--log-keyword" << it
        }
        Launcher.main(args as String[])
    }

    def printEnvironment(printer) {
        printer "----------------------------------------"
        if (srcFolders.size() > 0) {
            srcFolders.each {
                printer "source folder: $it"
            }
        } else {
            printer "source folder: []"
        }
        printer "output folder: $outFolder"
        printer "----------------------------------------"
    }
}