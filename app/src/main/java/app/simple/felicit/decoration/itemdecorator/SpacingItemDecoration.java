package app.simple.felicit.decoration.itemdecorator;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;
    private final int mNumCol;

    public SpacingItemDecoration(int space, int numCol) {
        this.space = space;
        this.mNumCol=numCol;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NotNull View view,
                               RecyclerView parent, @NotNull RecyclerView.State state) {

        //outRect.right = space;
        outRect.bottom = space;
        //outRect.left = space;

        //Log.d("ttt", "item position" + parent.getChildLayoutPosition(view));
        int position=parent.getChildLayoutPosition(view);

        if(mNumCol<=2) {
            if (position == 0) {
                outRect.left = space;
                outRect.right = space / 2;
            } else {
                if ((position % mNumCol) != 0) {
                    outRect.left = space / 2;
                    outRect.right = space;
                } else {
                    outRect.left = space;
                    outRect.right = space / 2;
                }
            }
        }else{
            if (position == 0) {
                outRect.left = space;
                outRect.right = space / 2;
            } else {
                if ((position % mNumCol) == 0) {
                    outRect.left = space;
                    outRect.right = space/2;
                } else if((position % mNumCol) == (mNumCol-1)){
                    outRect.left = space/2;
                    outRect.right = space;
                }else{
                    outRect.left=space/2;
                    outRect.right=space/2;
                }
            }

        }

        if(position<mNumCol){
            outRect.top=space;
        }else{
            outRect.top=0;
        }
        // Add top margin only for the first item to avoid double space between items
        /*
        if (parent.getChildLayoutPosition(view) == 0 ) {

        } else {
            outRect.top = 0;
        }*/
    }
}
