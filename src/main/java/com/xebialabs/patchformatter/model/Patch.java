package com.xebialabs.patchformatter.model;

import java.util.List;

/**
 * Created by sameer on 19/2/16.
 */
public class Patch {

    private final String fileName;

    private final List<Range> lineRanges;

    private String branch;

    public Patch(String fileName, List<Range> lineRanges, String branch) {
        this.fileName = fileName;
        this.lineRanges = lineRanges;
        this.branch = branch;
    }

    public String getFileName() {
        return fileName;
    }

    public List<Range> getLineRanges() {
        return lineRanges;
    }

    @Override
    public String toString() {
        return "com.xebialabs.patchformatter.model.Patch{" +
                "fileName='" + fileName + '\'' +
                ", lineRanges=" + lineRanges +
                '}';
    }

    public String getBranch() {
        return branch;
    }
}
