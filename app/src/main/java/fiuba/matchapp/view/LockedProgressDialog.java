package fiuba.matchapp.view;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by ger on 06/06/16.
 */
public class LockedProgressDialog extends ProgressDialog {
    public LockedProgressDialog(Context context, int theme) {
        super(context, theme);
        setIndeterminate(true);
        setCanceledOnTouchOutside(false);
    }
}
