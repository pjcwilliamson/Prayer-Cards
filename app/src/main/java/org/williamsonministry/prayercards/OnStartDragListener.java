package org.williamsonministry.prayercards;

import androidx.recyclerview.widget.RecyclerView;

public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a dra.
     *
     * @param viewHolder The holder of the view to drag.
     */

    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}
