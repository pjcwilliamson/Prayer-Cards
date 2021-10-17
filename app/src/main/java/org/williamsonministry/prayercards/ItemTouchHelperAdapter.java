package org.williamsonministry.prayercards;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperAdapter {

    //This all comes from https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf

    boolean onItemMove(int fromPosition, int toPosition, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);

}
