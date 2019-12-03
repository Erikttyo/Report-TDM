package com.example.reporttdm.helper;

import com.example.reporttdm.model.Barang;

import java.util.Comparator;

public class SortbyStokASC implements Comparator<Barang> {
    // Used for sorting in ascending order of
    // roll number
    public int compare(Barang a, Barang b)
    {
        return Integer.parseInt(a.getStok()) - Integer.parseInt(b.getStok());
    }
}
