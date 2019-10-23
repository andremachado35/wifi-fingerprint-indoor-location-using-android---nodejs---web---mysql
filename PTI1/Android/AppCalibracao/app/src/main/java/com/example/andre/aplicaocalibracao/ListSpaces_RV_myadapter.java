package com.example.andre.aplicaocalibracao;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;


public class ListSpaces_RV_myadapter extends ExpandableRecyclerViewAdapter<ListSpaces_RV_Pai_ViewHolder, ListSpaces_RV_Filho_ViewHolder>{
    private Activity activity;
    public ListSpaces_RV_myadapter(Activity activity, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.activity = activity;
    }

    @Override
    public ListSpaces_RV_Pai_ViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_pai_viewholder, parent, false);
        return new ListSpaces_RV_Pai_ViewHolder(view);
    }

    @Override
    public ListSpaces_RV_Filho_ViewHolder onCreateChildViewHolder(ViewGroup parent, final int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_rv_filho_viewholder, parent, false);
        return new ListSpaces_RV_Filho_ViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ListSpaces_RV_Filho_ViewHolder holder, final int flatPosition, ExpandableGroup group, final int childIndex) {
        final ListSpaces_RV_Filho_class listSpaces_RV_Filho_class = ((ListSpaces_RV_Pai_class) group).getItems().get(childIndex);
        holder.onBind(listSpaces_RV_Filho_class,group);

    }

    @Override
    public void onBindGroupViewHolder(ListSpaces_RV_Pai_ViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setGroupName(group);
    }
}

