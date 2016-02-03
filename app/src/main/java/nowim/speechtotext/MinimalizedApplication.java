/**
 * Created by Wojciech on 2015-11-20.
 */

package nowim.speechtotext;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


public class MinimalizedApplication extends Service {

    private WindowManager windowManager;
    private ImageView appHead;
    WindowManager.LayoutParams parameters;

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        appHead = new ImageView(this);
        appHead.setImageResource(R.mipmap.ic_minimalized_head);

        parameters= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        parameters.gravity = Gravity.TOP | Gravity.LEFT;
        parameters.x = 0;
        parameters.y = 100;                                             //initial coords

        appHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = parameters.x;
                        initialY = parameters.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if( (Math.abs(initialTouchX - event.getRawX())<5) && (Math.abs(initialTouchY - event.getRawY())<5) )
                        {

                            Toast t = Toast.makeText(getApplicationContext(),
                                    "Clicked",
                                    Toast.LENGTH_SHORT);
                            t.show();
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        parameters.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        parameters.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(appHead, parameters);
                        return true;
                }
                return false;
            }
        });
        windowManager.addView(appHead, parameters);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (appHead != null)
            windowManager.removeView(appHead);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //
        return null;
    }
}
