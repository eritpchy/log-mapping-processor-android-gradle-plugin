package net.xdow.logmapping.gradle.android

import com.google.common.collect.Sets

class LogMappingExtension {

    /** List of log keyword. */
    Set<String> keywords

    /** True to active the debug mode. */
    boolean debug

    /** Enable processor for build type. default: all */
    Set<String> enableBuildType

    /** Allow N jobs at once */
    int jobs = Runtime.getRuntime().availableProcessors()


    public LogMappingExtension enableBuildType(String... enableBuildTypes) {
        if (enableBuildType == null) {
            enableBuildType = Sets.newHashSet();
        }
        Collections.addAll(enableBuildType, enableBuildTypes);
        return this;
    }

    public LogMappingExtension keywords(String... keys) {
        if (keywords == null) {
            keywords = Sets.newHashSet();
        }
        Collections.addAll(keywords, keys);
        return this;
    }
}
