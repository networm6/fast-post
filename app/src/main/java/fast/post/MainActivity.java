package fast.post;

import android.os.Bundle;
import android.view.View;
import java.util.List;
import android.widget.Toast;
import android.kz.Base;
import android.kz.Toastkeeper;
import android.kz.KzPermissions;
import android.kz.Permission;
import android.kz.OnPermission;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.animation.ValueAnimator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.*;
import android.widget.EditText;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;


public class MainActivity extends Base 
{
	static BlurDrawable blurDrawable;
	ValueAnimator animator;
	NumberAnimTextView bar;
	static ImageView mBlurImage;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

		inlayout(R.layout.main);

        super.onCreate(savedInstanceState);


		requestPermission();
		mBlurImage = findViewById(R.id.img);
        final Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.bar);
		blurDrawable = new BlurDrawable(this, getResources(), bp,23);

        mBlurImage.setImageDrawable(blurDrawable.getBlurDrawable());

		bar=(NumberAnimTextView)	Bar(R.id.mainView1);
		bar.setDuration(2500);
// 设置数字增加范围

		bar.setNumberString("25");



	}



	public void requestPermission() {
        KzPermissions.with(this)
			//targetSdkVersion要注意，有的权限要大于23，有的要大于26，不满足的话，会log显示的
			//.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
			.permission(Permission.SYSTEM_ALERT_WINDOW,Permission.WRITE_EXTERNAL_STORAGE)

			.request(new OnPermission() {

				@Override
				public void hasPermission(List<String> granted, boolean isAll) {
					if (isAll) {
						toa("获取权限成功",Toastkeeper.GRAVITY_TOP);
					}else {
						toa("获取权限成功，部分权限未正常授予",Toastkeeper.GRAVITY_RIGHT);
					}
				}

				@Override
				public void noPermission(List<String> denied, boolean quick) {
					if(quick) {
						toa("被永久拒绝授权，请手动授予权限",Toastkeeper.GRAVITY_CENTER);
						//如果是被永久拒绝就跳转到应用权限系统设置页面
						KzPermissions.gotoPermissionSettings(MainActivity.this);
					}else {
						toa("获取权限失败",Toastkeeper.GRAVITY_BOTTOM);
					}
				}
			});
    }


	void toa(String in,int t){
		Toastkeeper.getInstance()
			.createBuilder(this)
			.setMessage(in)
			.setGravity(t)
			.show();
	}
    public void setting(View view) {
		toa("正在进入",Toastkeeper.GRAVITY_RIGHT);
        KzPermissions.gotoPermissionSettings(MainActivity.this);
    }
	public void open (View v){
		try{
			//跳转系统自带界面 辅助功能界面
			Intent intent =new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
			startActivity(intent);
			toa("请打开",Toastkeeper.GRAVITY_BOTTOM);
		}catch(Exception e) {
			e.printStackTrace();
		}
    }
	public  void writeStr2Txt(String content, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file, true);
            outputStream.write(content.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();
			toa("ok",Toastkeeper.GRAVITY_RIGHT);
        } catch (IOException e) {
            e.printStackTrace();
			toa(e.getMessage(),Toastkeeper.GRAVITY_LEFT);
        }
    }
	public void gety(View v){
		String pp= android.os.	Environment.getExternalStorageDirectory().getPath() ;
		String nnn=pp+"/ck.txt";

		((EditText)findViewById(R.id.mainEditText1)).setText(getFileContent(new File(nnn)));

	}

	private String getFileContent(File file) {
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
                        }
                        instream.close();//关闭输入流
                    }
				} catch (IOException e) {
					toa(e.getMessage(),Toastkeeper.GRAVITY_BOTTOM);
                }
            }
        }
        return content;
    }




	public void putn(View v){
		String pp= android.os.	Environment.getExternalStorageDirectory().getPath() ;
		String nnn=pp+"/ck.txt";
		writeStr2Txt(""+((EditText)findViewById(R.id.mainEditText2)).getText().toString(),nnn);
	}
	public void appwriter(View v) throws PackageManager.NameNotFoundException{
		if (checkApkExist(this,"com.tencent.mobileqq")){
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
										 "mqqwpa://im/chat?chat_type=wpa&uin="+"330771794"+"&version=1")));//qqNum 替换为客服的QQ账号
		}else {
			toa("未安装QQ",Toastkeeper.GRAVITY_LEFT);
		}
	}

	public  boolean checkApkExist(Context context, String packageName) throws PackageManager.NameNotFoundException{//检查QQ包是否存在
		if (packageName==null||"".equals(packageName)){
			return false;
		}
		try {
			ApplicationInfo info=context.getPackageManager().getApplicationInfo(packageName
																				, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}



}




