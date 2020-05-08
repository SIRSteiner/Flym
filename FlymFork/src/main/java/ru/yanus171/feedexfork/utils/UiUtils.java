/**
 * Flym
 * <p/>
 * Copyright (c) 2012-2015 Frederic Julian
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.yanus171.feedexfork.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import androidx.collection.LongSparseArray;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import ru.yanus171.feedexfork.MainApplication;
import ru.yanus171.feedexfork.R;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static ru.yanus171.feedexfork.MainApplication.getContext;
import static ru.yanus171.feedexfork.utils.NetworkUtils.GetImageFileUri;

public class UiUtils {

    //static private final HashMap<String, Bitmap> FAVICON_CACHE = new HashMap<>();

    static public void setPreferenceTheme(Activity a) {
        a.setTheme( Theme.GetResID( Theme.STYLE_THEME ) );
    }

    static public int dpToPixel(int dp) {
        return (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }
    static public int mmToPixel(int mm) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, mm, getContext().getResources().getDisplayMetrics());
    }
//    static public void clearFaviconCache() {
//        FAVICON_CACHE.clear();
//    }
    static public void addEmptyFooterView(ListView listView, int dp) {
        View view = new View(listView.getContext());
        view.setMinimumHeight(dpToPixel(dp));
        view.setClickable(true);
        listView.addFooterView(view);
    }

    static public void showMessage(@NonNull Activity activity, @StringRes int messageId) {
        showMessage(activity, activity.getString(messageId));
    }

    static public void toast(@NonNull Context context, @StringRes int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_LONG).show();
    }

    static public void toast(@NonNull Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    static public void SetFontSize(TextView textView, float coeff) {
        textView.setTextSize(COMPLEX_UNIT_DIP, ( 18 + PrefUtils.getFontSizeEntryList() ) * coeff );
    }

    private static void SetSmallFontSize(TextView textView) {
        textView.setTextSize(COMPLEX_UNIT_DIP, 14 + PrefUtils.getFontSizeEntryList() );
    }

    static public void showMessage(@NonNull Activity activity, @NonNull String message) {
        View coordinatorLayout = activity.findViewById(R.id.coordinator_layout);
        Snackbar snackbar = Snackbar.make((coordinatorLayout != null ? coordinatorLayout : activity.findViewById(android.R.id.content)), message, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundResource(R.color.material_grey_900);
        snackbar.show();
    }

    static public Bitmap getFaviconBitmap( /*Long feedID,*/ String iconUrl ) {
        //Bitmap bitmap = UiUtils.FAVICON_CACHE.get(iconUrl);
        //if (bitmap == null && iconUrl != null ) {
        Bitmap bitmap = null;
        if ( iconUrl != null )
            try {
                InputStream imageStream = getContext().getContentResolver().openInputStream( GetImageFileUri( iconUrl, iconUrl ));
                bitmap = BitmapFactory.decodeStream(imageStream);
                //UiUtils.FAVICON_CACHE.put(iconUrl, bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        //}
        return bitmap;
    }

    static public Bitmap getScaledBitmap(Bitmap bitmap, int sizeInDp) {
        if (bitmap != null ) {
            //Bitmap bitmap = Bitmap.createBitmap( sourceBitmap );
            if (bitmap.getWidth() != 0 && bitmap.getHeight() != 0) {
                int bitmapSizeInDip = UiUtils.dpToPixel(sizeInDp);
                if (bitmap.getHeight() != bitmapSizeInDip) {
                    Bitmap tmp = bitmap;
                    bitmap = Bitmap.createScaledBitmap(tmp, bitmapSizeInDip, bitmapSizeInDip, false);
                    tmp.recycle();
                }

                return bitmap;
            }
        }

        return null;
    }

    private static Handler mHandler = null;
    public static void RunOnGuiThread( final Runnable r ) {
        if ( mHandler == null )
            mHandler  = new Handler(Looper.getMainLooper());
        //synchronized ( mHandler ) {
            mHandler.post( r );
        //}
    }
    public static void RunOnGuiThread( final Runnable r, int delay ) {
        if ( mHandler == null )
            mHandler  = new Handler(Looper.getMainLooper());
        //synchronized ( mHandler ) {
            mHandler.postDelayed( r, delay );
        //}

    }
    public static void RemoveFromGuiThread( final Runnable r ) {
        if ( mHandler != null )
            mHandler.removeCallbacks( r );
    }

    public static void HideButtonText(View rootView, int ID, boolean transparent) {
        TextView btn = rootView.findViewById(ID);
        if ( btn != null ) {
            if (transparent)
                btn.setBackgroundColor(Color.TRANSPARENT);
            else
                btn.setBackgroundResource(R.drawable.round_background);
            btn.setText("");
        }
    }

    public static void SetSize( View parent, int ID, int width, int height ) {
        View view = parent.findViewById( ID );
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = width;
        lp.height = height;
    }

    // -------------------------------------------------------------------
    static TextView AddSmallText(LinearLayout layout, int textID) {
        return AddSmallText(layout, null, Gravity.LEFT, null, getContext().getString(textID));
    }
    public static TextView AddSmallText(LinearLayout layout, LinearLayout.LayoutParams lp, int gravity, ColorTB color, String text) {
        TextView result = CreateSmallText( layout.getContext(), gravity, color, text );
        if (lp != null) {
            layout.addView(result, lp);
        } else {
            layout.addView(result);
        }
        return result;
    }
    public static TextView CreateSmallText(Context context, int gravity, ColorTB color, String text) {
        TextView result = new TextView(context);
        result.setAutoLinkMask(Linkify.ALL);
        result.setLinkTextColor(Color.LTGRAY);
        result.setText(text);
        result.setTextIsSelectable(true);
        result.setFocusable(false);
        result.setFocusableInTouchMode(true);

        result.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        result.setGravity(gravity);
        result.setPadding(10, 0, 10, 0);

        result.setTextColor(color != null ? color.Text : Theme.GetMenuFontColor());
        SetSmallFontSize( result );
        return result;
    }

    // -------------------------------------------------------------------
    public static TextView AddText(LinearLayout layout, LinearLayout.LayoutParams lp, String text) {
        TextView result = new TextView(layout.getContext());
        if (lp != null) {
            layout.addView(result, lp);
        } else {
            layout.addView(result);
        }
        result.setText(text);
        result.setTextIsSelectable(true);
        result.setFocusable(false);
        result.setFocusableInTouchMode(false);

        result.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18 + PrefUtils.getFontSizeEntryList());
        result.setTextColor(Theme.GetMenuFontColor());
        result.setGravity(Gravity.CENTER);
        result.setPadding(10, 10, 10, 0);
        return result;
    }


}
