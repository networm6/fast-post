package android.kz;
import android.view.Gravity;
import android.widget.Toast;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;
import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;
import android.graphics.Color;

public class Toastkeeper  {

    //默认位置5种位置(注解方式限定设置的位置)
    public static final int GRAVITY_CENTER = Gravity.CENTER;
    public static final int GRAVITY_TOP = Gravity.TOP;
    public static final int GRAVITY_BOTTOM = Gravity.BOTTOM;
    public static final int GRAVITY_LEFT = Gravity.LEFT;
    public static final int GRAVITY_RIGHT = Gravity.RIGHT;
    //默认的两种toast显示时长
    public static final int DURATION_SHORT = Toast.LENGTH_SHORT;
    public static final int DURATION_LONG = Toast.LENGTH_LONG;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface IGravity {
    }

    //只有一个toast
    private Toast mToast;
    private boolean isShowing = false;
    //默认的view
    private TextView normalTextView = null;
    private Builder lastBuilder = null;
    //主线程的handle
    private Handler sMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isShowing = false;
        }
    };

    //私有构造
    private Toastkeeper() {
    }

    private static class SingleTonHoler {
        private static Toastkeeper INSTANCE = new Toastkeeper();
    }

    public static Toastkeeper getInstance() {
        return SingleTonHoler.INSTANCE;
    }

    /**
     * 是否是主线程
     *
     * @return
     */
    private boolean isMainLooper() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    /**
     * 显示toast
     *
     * @param context
     * @param builder
     * @return
     */
    private void showToast(Context context, final Builder builder) {
        if (null == context) {
            throw new NullPointerException("you set context is null!");
        }

        if (null == builder) {
            throw new NullPointerException("you set builder is null!");
        }
        final Context appContext = context.getApplicationContext();
        //兼容在子线程中显示
        if (isMainLooper()) {
            showToastSafety(appContext, builder);
        } else {
            sMainHandler.post(new Runnable() {
					@Override
					public void run() {
						showToastSafety(appContext, builder);
					}
				});
        }
    }


    /**
     * 取消toast
     */
    private void dimissToast() {
        if (mToast != null) {
            mToast.cancel();
            isShowing = false;
        }
    }

    /**
     * 创建构造
     *
     * @param context
     * @return
     */
    public Builder createBuilder(Context context) {
        Builder builder = new Builder(context);
        return builder;
    }

    //*****************************构造builder***********************

    public final class Builder {

        private Context mContext;
        //文本
        private CharSequence mMessage;
        //其他属性参数
        private int mDuration = DURATION_LONG;
        private int mGravity = GRAVITY_CENTER;
        private int mOffsetX = 0;
        private int mOffsetY = 0;
        private boolean mUseSystem = false;
        private boolean mCancleSame = true;
        private View mToastView = null;
        
        int mlayoutId = 0;

        Builder(Context context) {
            mContext = context;
        }

        /**
         * 设置信息
         *
         * @param text
         * @return
         */
        public Builder setMessage(String text) {
            mMessage = text;
            return this;
        }

        /**
         * 设置时长
         *
         * @param duration
         * @return
         */
        public Builder setDuration(@Duration int duration) {
            mDuration = duration;
            return this;
        }

        /**
         * 设置位置
         *
         * @param gravity
         * @return
         */
        public Builder setGravity(@IGravity int gravity) {
            mGravity = gravity;
            return this;
        }

        /**
         * 设置X偏移
         *
         * @param offsetX
         * @return
         */
        public Builder setOffsetX(int offsetX) {
            mOffsetX = offsetX;
            return this;
        }

        /**
         * 设置Y偏移
         *
         * @param offsetY
         * @return
         */
        public Builder setOffsetY(int offsetY) {
            mOffsetY = offsetY;
            return this;
        }

        /**
         * 设置使用系统的toast
         *
         * @param useSystem
         * @return
         */
        public Builder setUseSystem(boolean useSystem) {
            mUseSystem = useSystem;
            return this;
        }

        /**
         * 是否取消相同的（相同的不会重新创建，消失之后才能创建新的）
         *
         * @param cancleSame
         * @return
         */
        public Builder setCancleTheSame(boolean cancleSame) {
            mCancleSame = cancleSame;
            return this;
        }

        /**
         * 设置自定义view
         *
         * @param layoutId
         * @return
         */
        public Builder setView( int layoutId) {
            if (layoutId == 0) {
                throw new NullPointerException("your set layout is null");
            }
            if (null == mContext) {
                throw new NullPointerException("context  is null");
            }

            mlayoutId = layoutId;
            setView(LayoutInflater.from(mContext).inflate(layoutId, null));
            return this;
        }

        /**
         * 设置自定义view
         *
         * @param view
         * @return
         */
        public Builder setView(View view) {
            mToastView = view;
            return this;
        }

        /**
         * 此方法用于添加toast
         */
        public Builder show() {
            Toastkeeper.this.showToast(mContext, this);
            return this;
        }

        /**
         * 此方法用于生命周期结束取消toast
         */
        public void dimiss() {
            Toastkeeper.this.dimissToast();
        }

        @Override
        public String toString() {
            String builderString = "";
            if (mlayoutId != 0) {
                builderString = "Builder{" +
					"mContext=" + mContext +
					", mMessage=" + mMessage +
					", mDuration=" + mDuration +
					", mGravity=" + mGravity +
					", mOffsetX=" + mOffsetX +
					", mOffsetY=" + mOffsetY +
					", mlayoutId=" + mlayoutId +
					'}';
            } else {
                builderString = "Builder{" +
					"mContext=" + mContext +
					", mMessage=" + mMessage +
					", mDuration=" + mDuration +
					", mGravity=" + mGravity +
					", mOffsetX=" + mOffsetX +
					", mOffsetY=" + mOffsetY +
					", mToastView=" + mToastView +
					'}';
            }
            return builderString;
        }
    }


    /**
     * 创建toast
     *
     * @param context
     * @param builder
     */
    private void showToastSafety(Context context, Builder builder) {

        if (lastBuilder == null) {
            createDiffToast(context, builder);
        } else {
            boolean sameTag = handleSameToast(builder);
            if (!sameTag) {
                if (mToast != null) {
                    mToast.cancel();
                }
                createDiffToast(context, builder);
            } else {
                //相同的toast时候
                if (!isShowing) {
                    createDiffToast(context, builder);
                }
            }
        }
    }

    /**
     * 创建不同的toast根据类型
     * <p>
     * 消息有三种情况
     * 第一系统
     * 第二种默认的
     * 第三中可以自定义布局
     *
     * @param context
     * @param builder
     * @param类型1是相同的toast/类型2是创建新toast
     */
    private void createDiffToast(Context context, Builder builder) {
        if (builder.mUseSystem) {
            if (builder.mMessage == null) {
                throw new NullPointerException("The message must not be null");
            } else {
                mToast = Toast.makeText(context, builder.mMessage, Toast.LENGTH_SHORT);
            }
        } else {
            mToast = new Toast(context);
            mToast.setGravity(builder.mGravity, builder.mOffsetX, builder.mOffsetY);
            mToast.setDuration(builder.mDuration);
            View toastView = getToastView(context, builder.mToastView, builder.mMessage);
            mToast.setView(toastView);
        }


        mToast.show();
        isShowing = true;
        lastBuilder = builder;

        sMainHandler.removeMessages(1);
        int mDuration = builder.mDuration == DURATION_LONG ? 3500 : 2000;
        sMainHandler.sendEmptyMessageDelayed(1, mDuration);

    }


    /**
     * 处理相同消息的tag
     * 如果使用系统toast的话，只比较Message，
     * 如果使用自定义的，则比较bean
     * 再次就是需要比较是否主动取消相同的消息
     *
     * @param builder
     * @return
     */
    private boolean handleSameToast(Builder builder) {
        boolean sameBuilder = false;
        boolean mCancleSame = builder.mCancleSame;
        boolean mUseSystem = builder.mUseSystem;

        if (mUseSystem) {
            sameBuilder = lastBuilder.mMessage.equals(builder.mMessage);
        } else {
            String lastBuilderS = lastBuilder.toString();
            String currentBuilderS = builder.toString();
            sameBuilder = lastBuilderS.equals(currentBuilderS);
        }

        return mCancleSame && sameBuilder;
    }

    /**
     * 创建toast的view
     *
     * @param context
     * @param toastView
     * @param mMessage
     * @return
     */
    private View getToastView(Context context, View toastView, CharSequence mMessage) {
        View mToastView = null;
        if (null == toastView) {
            if (mMessage == null) {
                throw new NullPointerException("The message must not be null");
            } else {
                if (normalTextView == null) {
                    normalTextView = new TextView(context);
                }
                normalTextView.setGravity(Gravity.CENTER);
                normalTextView.setBackgroundColor(0xEE333333);
                normalTextView.setTextColor(Color.WHITE);
                normalTextView.setPadding(50, 35, 50, 35);
                normalTextView.setText(mMessage);
                mToastView = normalTextView;
            }
        } else {
            mToastView = toastView;
        }

        return mToastView;
    }
}
