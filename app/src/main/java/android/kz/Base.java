package android.kz;
import android.app.Activity;
import android.view.View;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.content.Intent;
import android.os.Build;
import android.view.WindowManager;
import android.view.Window;

public class Base extends Activity
{
	private int mview;
	
	@Override
	protected void onStart()
	{
		super.onStart();
		closeAndroidPDialog();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (!this.isTaskRoot()) {
			Intent intent = getIntent();
			if (intent != null) {
				String action = intent.getAction();
				if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
					finish();
					return;
				}
			}
		}

		setContentView(this.mview);
		getWindow().addFlags(0x08000000);
		getWindow().addFlags(1024);
		if(getActionBar()!=null)
		getActionBar().hide();
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else if (Build.VERSION.SDK_INT >= 19) {
			//for new api versions.
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}
		if((getRequestedOrientation()==2?true:null)!=null){
			SinkFullScreen s= SinkFullScreen.INSTANCE;
			s.blockStatusCutout(getWindow());
		}else{
			SinkFullScreen s= SinkFullScreen.INSTANCE;
			s.extendStatusCutout(getWindow(),this);
		}
	
	}
	public void back(View v){
		this.finish();
	}
	public void inlayout(int inviewlayout)
	{
		this.mview = inviewlayout;
	}
	public View Bar(int bar){
		int result = 0;
		int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) 
			result = this.getResources().getDimensionPixelSize(resourceId);
		View topbar=findViewById(bar);
		LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) topbar.getLayoutParams();
		linearParams.height = result;
		topbar.setLayoutParams(linearParams); 
		return topbar;
		}
	private  void closeAndroidPDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

