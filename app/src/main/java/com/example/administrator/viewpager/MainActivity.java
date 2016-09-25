package com.example.administrator.viewpager;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private final int TIME_INTERVAL = 3000;
    private final int MAX_COUNT = 10000;
    private ViewPager viewPager;
    private TextView currentPageNumberText;
    private Button buttonNext;
    private Button buttonPrevious;
    private ViewGroup linearGroup;
    private int currentNumber;
    private boolean autoPlayFlag;
    private int[] imgs = new int[]{
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4
    };
    private ImageView[] imageViews;
    private ImageView[] dots;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123) {
                if (autoPlayFlag) {
                    showNext();
                }
                Message message = new Message();
                message.what = 0x123;
                handler.sendMessageDelayed(message, TIME_INTERVAL);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) this.findViewById(R.id.viewpage);
        currentPageNumberText = (TextView) this.findViewById(R.id.currentPageNumber);
        buttonNext = (Button) this.findViewById(R.id.buttonNext);
        buttonPrevious = (Button) this.findViewById(R.id.buttonPrevious);
        linearGroup = (ViewGroup) this.findViewById(R.id.LinearGroup);
        autoPlayFlag = true;
        //加载图片到imageViews
        imageViews = new ImageView[imgs.length];
        for (int i = 0; i < imgs.length; i++) {
            imageViews[i] = new ImageView(this);
            imageViews[i].setImageResource(imgs[i]);
            imageViews[i].setScaleType(ImageView.ScaleType.FIT_XY);
        }
        //加载圆点到GroupView
        dots = new ImageView[imageViews.length];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            if (i == 0) {
                dots[i].setImageResource(R.drawable.dot_focused);
            } else {
                dots[i].setImageResource(R.drawable.dot_normal);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);
            layoutParams.setMargins(5, 0, 5, 0);
            dots[i].setLayoutParams(layoutParams);
            linearGroup.addView(dots[i]);
        }

        //设置Adapter
        viewPager.setAdapter(new MyAdapter());
        //设置监听
        viewPager.setOnPageChangeListener(this);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        currentNumber = (imageViews.length) * 100;
        viewPager.setCurrentItem(currentNumber);

        //下一个按钮
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNext();
            }
        });
        //上一个按钮
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrevious();
            }
        });

        Message msg = new Message();
        msg.what = 0x123;
        handler.sendMessageDelayed(msg, TIME_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(0x123);
        super.onDestroy();
    }

    public class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return MAX_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews[position % imageViews.length]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViews[position % imageViews.length], 0);
            return imageViews[position % imageViews.length];
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //设置显示文字
        currentNumber = position;
        int dotCurrent = position % imageViews.length;
        currentPageNumberText.setText(String.valueOf(dotCurrent));
        //设置dots显示
        setCurrentDots(dotCurrent);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                //无动作、初始状态
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                //点击、滑屏
                autoPlayFlag = false;
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                //释放
                autoPlayFlag = true;
                break;
        }
    }

    public void showNext() {
        currentNumber++;
        viewPager.setCurrentItem(currentNumber);
    }

    public void showPrevious() {
        currentNumber--;
        viewPager.setCurrentItem(currentNumber);
    }
    public void setCurrentDots(int dotCurrent){
        for (int i = 0; i < dots.length; i++) {
            ImageView dot = dots[i];
            dot.setImageResource(R.drawable.dot_normal);
        }
        dots[dotCurrent].setImageResource(R.drawable.dot_focused);
    }
}
