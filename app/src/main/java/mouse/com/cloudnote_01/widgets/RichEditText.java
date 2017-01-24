package mouse.com.cloudnote_01.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;


public class RichEditText extends EditText {
    public RichEditText(Context context) {
        this(context, null);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
