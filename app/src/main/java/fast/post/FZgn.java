package fast.post;

import android.accessibilityservice.AccessibilityService;
import android.kz.Toastkeeper;
import android.view.WindowManager;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Button;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.ClipboardManager;
import android.annotation.TargetApi;
import android.os.Build;
import android.content.ClipData;
import java.util.List;
import android.view.accessibility.AccessibilityEvent;
import android.content.Intent;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileInputStream;
import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.net.URL;
import java.net.HttpURLConnection;
public class FZgn extends AccessibilityService {
    /**
     *  必须重写的方法：此方法用了接受系统发来的event。在你注册的event发生是被调用。在整个生命周期会被调用多次。
     */
	public  boolean ty=false;
	List<String> ck;
	int statusBarHeight;
	void toa(String in,int t){
		Toastkeeper.getInstance()
			.createBuilder(this)
			.setMessage(in)
			.setGravity(t)
			.show();
	}
	private void createToucher()
    {
        //赋值WindowManager&LayoutParam.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        final WindowManager windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        //注意，这里的width和height均使用px而非dp.这里我偷了个懒
        //如果你想完全对应布局设置，需要先获取到机器的dpi
        //px与dp的换算为px = dp * (dpi / 160).
        params.width = 300;
        params.height = 300;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        final LinearLayout toucherLayout = (LinearLayout) inflater.inflate(R.layout.xfc, null);
        //添加toucherlayout
        windowManager.addView(toucherLayout, params);


        //主动计算出当前View的宽高信息.
        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);

        //用于检测状态栏高度.

        int resourceId = getResources().getIdentifier("status_bar_height","dimen","android");
        if (resourceId > 0)
        {
			statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        //浮动窗口按钮.
        Button imageButton1 = (Button) toucherLayout.findViewById(R.id.xfcImageButton1);

		imageButton1.setOnClickListener(new View.OnClickListener() {
				long[] hints = new long[2];
				@Override
				public void onClick(View v) {

					if(ty==false){
						ty=true;
						toa("已开启",Toastkeeper.GRAVITY_TOP);

					}else{
						ty=false;
						toa("请手动关闭",Toastkeeper.GRAVITY_RIGHT);
						try{
							//跳转系统自带界面 辅助功能界面
							Intent intent =new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
							startActivity(intent);
							toa("请打开",Toastkeeper.GRAVITY_BOTTOM);
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			});



		imageButton1.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//ImageButton我放在了布局中心，布局一共300dp
					params.x = (int) event.getRawX() - 150;
					//这就是状态栏偏移量用的地方
					params.y = (int) event.getRawY() - 150 - statusBarHeight;
					windowManager.updateViewLayout(toucherLayout,params);
					return false;
				}
			});



	}


    public void pastaText(AccessibilityNodeInfo nodeInfo,Context context, String key) {
        //android>21 = 5.0时可以用ACTION_SET_TEXT
        //android>18 3.0.1可以通过复制的手段,先确定焦点，再粘贴ACTION_PASTE
        //使用剪切板
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //获取焦点
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        //需要替换的key
        clipboard.setPrimaryClip(ClipData.newPlainText("text", key));
        //粘贴进入内容
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
    }
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByID(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
		//findAndPerformActionButton("继续");
		AccessibilityNodeInfo ti = findViewByID("xiaoheng.okhttp_php_mysql:id/activitymainEditText1");
		if (ti != null) {

			//把口令粘贴到输入框中
			Collections.shuffle(ck);
			//pastaText(ti, this, ""+ck.get(0));
            findAndPerformActionButton("增");
			}
		if(ty==true){
			AccessibilityNodeInfo qq = findViewByID("com.tencent.mobileqq:id/input");
			if (qq != null) {

				//把口令粘贴到输入框中
				Collections.shuffle(ck);
				pastaText(qq, this, ""+ck.get(0));
				//findAndPerformActionTextView("发送");
				findAndPerformActionButton("发送");
			}

			AccessibilityNodeInfo tim = findViewByID("com.tencent.tim:id/input");
			if (tim != null) {

				//把口令粘贴到输入框中
				Collections.shuffle(ck);
				pastaText(tim, this, ""+ck.get(0));
				findAndPerformActionTextView("发送");
			}
			AccessibilityNodeInfo wx = findViewByID("com.tencent.mm:id/amb");
			if (wx != null) {

				//把口令粘贴到输入框中
				Collections.shuffle(ck);
				pastaText(wx, this, ""+ck.get(0));
				AccessibilityNodeInfo bu=findViewByID("com.tencent.mm:id/ami");
				performViewClick(bu);
			}


		}
	}
    public void performViewClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        while (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }
	/**
     *  必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt() {
    }

	/**
     *  当系统连接上你的服务时被调用
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
		createToucher();
		ck=new ArrayList<String>();
		ck.add("ee");
		
		String pp= android.os.	Environment.getExternalStorageDirectory().getPath() ;
		String nnn=pp+"/ck.txt";
		getFileContent(new File(nnn));
    }


    /**
     *  在系统要关闭此service时调用。
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

	private void getFileContent(File file) {
        String content = "";
        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
            if (file.getName().endsWith("txt")) {//文件格式为""文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader
							= new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line = "";
                        //分行读取
                        while ((line = buffreader.readLine()) != null) {
                            content += line + "\n";
							ck.add(line);
                        }
                        instream.close();//关闭输入流
                    }
				} catch (IOException e) {
					toa(e.getMessage(),Toastkeeper.GRAVITY_BOTTOM);
                }
            }
        }

    }






	private void findAndPerformActionButton(String text) {
        if (getRootInActiveWindow() == null)//取得当前激活窗体的根节点
            return;
        //通过文字找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            // 执行点击行为
            if (node.getClassName().equals("android.widget.Button") && node.isEnabled()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private void findAndPerformActionTextView(String text) {
        if (getRootInActiveWindow() == null)
            return;
        //通过文字找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < nodes.size(); i++) {
            AccessibilityNodeInfo node = nodes.get(i);
            // 执行按钮点击行为
            if (node.getClassName().equals("android.widget.TextView") && node.isEnabled()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }


	
	
	
	
	
	

	

   
	
	
}

