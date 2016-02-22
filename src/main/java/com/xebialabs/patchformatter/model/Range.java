package com.xebialabs.patchformatter.model;

/**
 * Created by sameer on 19/2/16.
 */
public class Range {

    private final int start;

    private final int end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "com.xebialabs.patchformatter.model.Range{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
