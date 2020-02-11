package com.example.expandable_recyclerview;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

public class MealExpandableRecyclerView extends ExpandableGroup <Product> {

    public MealExpandableRecyclerView(String title, List<Product> items) {
        super(title, items);
    }
}
