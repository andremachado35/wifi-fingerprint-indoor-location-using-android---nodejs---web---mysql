package com.example.andre.aplicaocalibracao;

import android.annotation.SuppressLint;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

@SuppressLint("ParcelCreator")
public class ListSpaces_RV_Pai_class extends ExpandableGroup<ListSpaces_RV_Filho_class> {
    public ListSpaces_RV_Pai_class(String title, List<ListSpaces_RV_Filho_class> items) {
        super(title, items);
    }
}
