/*
 * Copyright 2018, Oath Inc
 * Licensed under the terms of the Apache License 2.0. Please refer to accompanying LICENSE file for terms.
 */

package com.oath.halodb;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arjun Mannaly
 */
class FileUtils {

    static void createDirectoryIfNotExists(File directory) throws IOException {
        if (directory.exists() && directory.isDirectory())
            return;

        if (!directory.mkdirs()) {
            throw new IOException("Cannot create directory " + directory.getName());
        }
    }

    static List<Integer> listIndexFiles(File directory) {
        File[] files = directory.listFiles(file -> Constants.INDEX_FILE_PATTERN.matcher(file.getName()).matches());
        if (files == null)
            return Collections.emptyList();

        // sort in ascending order. we want the earliest index files to be processed first.
        return
            Arrays.stream(files)
                // extract file id. 
                .map(file -> Constants.INDEX_FILE_PATTERN.matcher(file.getName()))
                .map(matcher -> {
                    matcher.find();
                    return matcher.group(1);
                })
                .map(Integer::valueOf)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Returns all *.tombstone files in the given directory.
     */
    static File[] listTombstoneFiles(File directory) {
        File[] files = directory.listFiles(file -> Constants.TOMBSTONE_FILE_PATTERN.matcher(file.getName()).matches());
        if (files == null) {
            return new File[0];
        }
        Arrays.sort(files, Comparator.comparing(File::getName));
        return files;
    }

    /**
     * Returns all *.data and *.datac files in the given directory.
     */
    static File[] listDataFiles(File directory) {
        return directory.listFiles(file -> Constants.DATA_FILE_PATTERN.matcher(file.getName()).matches());
    }
}
