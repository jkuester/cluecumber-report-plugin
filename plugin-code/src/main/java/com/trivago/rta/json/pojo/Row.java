package com.trivago.rta.json.pojo;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private List<String> cells = new ArrayList<>();

    public List<String> getCells() {
        return cells;
    }

    public void setCells(final List<String> cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        return "Row{" +
                "cells=" + cells +
                '}';
    }
}
