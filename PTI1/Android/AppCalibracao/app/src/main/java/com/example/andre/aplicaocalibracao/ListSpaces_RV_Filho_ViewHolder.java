package com.example.andre.aplicaocalibracao;
import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;


public class ListSpaces_RV_Filho_ViewHolder extends ChildViewHolder {

    private TextView filho;

    public ListSpaces_RV_Filho_ViewHolder(View itemView) {
        super(itemView);
        filho = (TextView) itemView.findViewById(R.id.filho);
    }

    public void onBind(ListSpaces_RV_Filho_class listSpaces_RV_Filho_class, ExpandableGroup group) {
        filho.setText(listSpaces_RV_Filho_class.getName());
    }
}

