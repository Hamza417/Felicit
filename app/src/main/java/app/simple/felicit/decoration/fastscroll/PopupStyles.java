package app.simple.felicit.decoration.fastscroll;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Consumer;

import app.simple.felicit.R;

public class PopupStyles {

    public static Consumer<TextView> DEFAULT = popupView -> {
        Resources resources = popupView.getResources();
        int minimumSize = resources.getDimensionPixelSize(R.dimen.fs_popup_min_size);
        popupView.setMinimumWidth(minimumSize);
        popupView.setMinimumHeight(minimumSize);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)
                popupView.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMarginEnd(resources.getDimensionPixelOffset(R.dimen.fs_popup_margin_end));
        popupView.setLayoutParams(layoutParams);
        Context context = popupView.getContext();
        popupView.setBackground(new AutoMirrorDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_fast_scroller_popup_background)));
        popupView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        popupView.setGravity(Gravity.CENTER);
        popupView.setIncludeFontPadding(false);
        popupView.setSingleLine(true);
        popupView.setTypeface(ResourcesCompat.getFont(context, R.font.bold));
        popupView.setTextColor(ContextCompat.getColor(context, R.color.fast_scroller_text));
        popupView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimensionPixelSize(R.dimen.fs_popup_text_size));
    };

    private PopupStyles() {
    }
}
