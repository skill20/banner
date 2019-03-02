package com.base.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.base.R;
import com.base.banner.loader.ImageLoaderInterface;
import com.base.banner.view.BannerViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Banner extends FrameLayout implements ViewPager.OnPageChangeListener {

    private static final String TAG = "Banner";

    private Context mContext;
    private List<String> mTitleList;
    private List mImageList;
    private List<View> mImageViewList;
    private List<ImageView> mIndicatorImageList;

    private int mIndicatorSize;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int bannerBackgroundImage;
    private int mIndicatorMargin = BannerConfig.PADDING_SIZE;
    private int bannerStyle = BannerConfig.CIRCLE_INDICATOR;
    private int delayTime = BannerConfig.TIME;
    private int scrollTime = BannerConfig.DURATION;
    private boolean isAutoPlay = BannerConfig.IS_AUTO_PLAY;
    private boolean isScroll = BannerConfig.IS_SCROLL;
    private boolean supportWrapContent = BannerConfig.SUPPORT_WRAP_CONTENT;
    private int mIndicatorSelectedResId = R.drawable.banner_shape_circle_gray;
    private int mIndicatorUnselectedResId = R.drawable.banner_shape_circle_white;
    private int mLayoutResId = R.layout.layout_banner;
    private int titleHeight;
    private int titleBackground;
    private int titleTextColor;
    private int titleTextSize;


    private int scaleType = 1;
    private int count = 0;

    private BannerViewPager mViewPager;
    private TextView mBannerTitleTv, mNumIndicatorTv;
    private LinearLayout mIndicatorLayout;
    private View mTitleBackgroundView;
    private ImageView mBannerDefaultImage;
    private ImageLoaderInterface imageLoader;
    private BannerPagerAdapter adapter;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnBannerClickListener bannerListener;
    private int currentItem;
    private int gravity;

    private WeakHandler handler = new WeakHandler();
    private int lastPosition;

    private static final String COUNT_TEXT = "%1$d/%2$d";

    public Banner(@NonNull Context context) {
        this(context, null);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mTitleList = new ArrayList<>();
        mImageList = new ArrayList<>();
        mImageViewList = new ArrayList<>();
        mIndicatorImageList = new ArrayList<>();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mIndicatorSize = dm.widthPixels / 80;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        handleTypedArray(context, attrs);
        View view = LayoutInflater.from(context).inflate(mLayoutResId, this, true);
        mBannerDefaultImage = view.findViewById(R.id.banner_default_image);
        mViewPager = view.findViewById(R.id.banner_pager);
        mIndicatorLayout = view.findViewById(R.id.circle_indicator);
        mBannerTitleTv = view.findViewById(R.id.banner_title);
        mNumIndicatorTv = view.findViewById(R.id.num_indicator);
        mBannerDefaultImage.setImageResource(bannerBackgroundImage);
        mTitleBackgroundView = view.findViewById(R.id.title_bg_view);
        initViewPagerScroll();
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_width, mIndicatorSize);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_height, mIndicatorSize);
        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_margin, BannerConfig.PADDING_SIZE);
        mIndicatorSelectedResId = typedArray.getResourceId(R.styleable.Banner_indicator_drawable_selected, R.drawable.banner_shape_circle_gray);
        mIndicatorUnselectedResId = typedArray.getResourceId(R.styleable.Banner_indicator_drawable_unselected, R.drawable.banner_shape_circle_white);
        scaleType = typedArray.getInt(R.styleable.Banner_image_scale_type, scaleType);
        delayTime = typedArray.getInt(R.styleable.Banner_delay_time, BannerConfig.TIME);
        scrollTime = typedArray.getInt(R.styleable.Banner_scroll_time, BannerConfig.DURATION);
        isAutoPlay = typedArray.getBoolean(R.styleable.Banner_is_auto_play, BannerConfig.IS_AUTO_PLAY);
        supportWrapContent = typedArray.getBoolean(R.styleable.Banner_support_wrap_content, BannerConfig.SUPPORT_WRAP_CONTENT);

        titleBackground = typedArray.getColor(R.styleable.Banner_title_background, BannerConfig.TITLE_BACKGROUND);
        titleHeight = typedArray.getDimensionPixelSize(R.styleable.Banner_title_height, BannerConfig.TITLE_HEIGHT);
        titleTextColor = typedArray.getColor(R.styleable.Banner_title_textcolor, BannerConfig.TITLE_TEXT_COLOR);
        titleTextSize = typedArray.getDimensionPixelSize(R.styleable.Banner_title_textsize, BannerConfig.TITLE_TEXT_SIZE);
        mLayoutResId = typedArray.getResourceId(R.styleable.Banner_banner_layout, mLayoutResId);
        bannerBackgroundImage = typedArray.getResourceId(R.styleable.Banner_banner_default_image, R.drawable.no_banner);
        typedArray.recycle();
    }

    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            BannerScroller mScroller = new BannerScroller(mViewPager.getContext());
            mScroller.setDuration(scrollTime);
            mField.set(mViewPager, mScroller);
        } catch (Exception e) {
            BLog.e(TAG, e.getMessage());
        }
    }

    /***************************************public start**************************************/

    public Banner setAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
        return this;
    }

    public Banner setImageLoader(ImageLoaderInterface imageLoader) {
        this.imageLoader = imageLoader;
        return this;
    }

    public Banner setDelayTime(int delayTime) {
        this.delayTime = delayTime;
        return this;
    }


    public Banner setBannerAnimation(Class<? extends ViewPager.PageTransformer> transformer) {
        try {
            setPageTransformer(true, transformer.newInstance());
        } catch (Exception e) {
            BLog.e(TAG, "Please set the PageTransformer class");
        }
        return this;
    }

    /**
     * Set the number of pages that should be retained to either side of the
     * current page in the view hierarchy in an idle state. Pages beyond this
     * limit will be recreated from the adapter when needed.
     *
     * @param limit How many pages will be kept offscreen in an idle state.
     * @return Banner
     */
    public Banner setOffscreenPageLimit(int limit) {
        if (mViewPager != null) {
            mViewPager.setOffscreenPageLimit(limit);
        }
        return this;
    }

    /**
     * Set a {@link ViewPager.PageTransformer} that will be called for each attached page whenever
     * the scroll position is changed. This allows the application to apply custom property
     * transformations to each page, overriding the default sliding look and feel.
     *
     * @param reverseDrawingOrder true if the supplied PageTransformer requires page views
     *                            to be drawn from last to first instead of first to last.
     * @param transformer         PageTransformer that will modify each page's animation properties
     * @return Banner
     */
    public Banner setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        mViewPager.setPageTransformer(reverseDrawingOrder, transformer);
        return this;
    }


    public Banner setIndicatorGravity(int type) {
        switch (type) {
            case BannerConfig.LEFT:
                this.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
                break;
            case BannerConfig.CENTER:
                this.gravity = Gravity.CENTER;
                break;
            case BannerConfig.RIGHT:
                this.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
                break;
        }
        return this;
    }

    public Banner setBannerTitles(List<String> titles) {
        this.mTitleList = titles;
        return this;
    }

    public Banner setBannerStyle(int bannerStyle) {
        this.bannerStyle = bannerStyle;
        return this;
    }

    public Banner setViewPagerScrollAble(boolean isScroll) {
        this.isScroll = isScroll;
        return this;
    }

    public Banner setImageList(List<?> imageUrls) {
        this.mImageList = imageUrls;
        this.count = imageUrls.size();
        return this;
    }

    public void updateData(List<?> imageUrls, List<String> titles) {
        this.mTitleList.clear();
        this.mTitleList.addAll(titles);
        updateData(imageUrls);
    }

    public void updateData(List<?> imageUrls) {
        this.mImageList.clear();
        this.mImageViewList.clear();
        this.mIndicatorImageList.clear();
        this.mImageList.addAll(imageUrls);
        this.count = this.mImageList.size();
        start();
    }

    public void updateBannerStyle(int bannerStyle) {
        mIndicatorLayout.setVisibility(GONE);
        mNumIndicatorTv.setVisibility(GONE);
        mBannerTitleTv.setVisibility(View.GONE);
        mTitleBackgroundView.setVisibility(View.GONE);
        this.bannerStyle = bannerStyle;
        start();
    }

    public Banner start() {
        setBannerStyleUI();
        setRealImageList(mImageList);
        setData();
        return this;
    }


    public void startAutoPlay() {
        handler.removeCallbacks(mPlayRunnable);
        handler.postDelayed(mPlayRunnable, delayTime);
    }

    public void stopAutoPlay() {
        handler.removeCallbacks(mPlayRunnable);
    }

    public interface OnBannerClickListener {
        void onBannerClick(int position);
    }

    public Banner setOnBannerClickListener(OnBannerClickListener listener) {
        this.bannerListener = listener;
        return this;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public void releaseBanner() {
        handler.removeCallbacksAndMessages(null);
    }

    /***************************************public end**************************************/

    private void setBannerStyleUI() {
        int visibility = count > 1 ? View.VISIBLE : View.GONE;
        switch (bannerStyle) {
            case BannerConfig.CIRCLE_INDICATOR:
                mIndicatorLayout.setVisibility(visibility);
                break;
            case BannerConfig.NUM_INDICATOR:
                mNumIndicatorTv.setVisibility(visibility);
                break;
            case BannerConfig.NUM_INDICATOR_TITLE:
                mNumIndicatorTv.setVisibility(visibility);
                setTitleStyleUI();
                break;
            case BannerConfig.CIRCLE_INDICATOR_TITLE:
                mIndicatorLayout.setVisibility(visibility);
                setTitleStyleUI();
                break;
            case BannerConfig.CIRCLE_NUM_INDICATOR_TITLE:
                mIndicatorLayout.setVisibility(visibility);
                mNumIndicatorTv.setVisibility(visibility);
                setTitleStyleUI();
                break;
            case BannerConfig.CIRCLE_NUM_INDICATOR:
                mIndicatorLayout.setVisibility(visibility);
                mNumIndicatorTv.setVisibility(visibility);
                break;
        }
    }


    private void setTitleStyleUI() {
        int size = mTitleList.size();
        if (size != mImageList.size()) {
            throw new RuntimeException("[Banner] --> The number of titles and images is different");
        }
        if (titleBackground != -1) {
            mTitleBackgroundView.setBackgroundColor(titleBackground);
        }
        if (titleHeight != -1) {
            ViewGroup.LayoutParams layoutParams = mTitleBackgroundView.getLayoutParams();
            layoutParams.height = titleHeight;
            mTitleBackgroundView.setLayoutParams(layoutParams);
        }
        if (titleTextColor != -1) {
            mBannerTitleTv.setTextColor(titleTextColor);
        }
        if (titleTextSize != -1) {
            mBannerTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        }
        if (mTitleList != null && size > 0) {
            mBannerTitleTv.setText(mTitleList.get(0));
            mBannerTitleTv.setVisibility(View.VISIBLE);
            mTitleBackgroundView.setVisibility(View.VISIBLE);
        }
    }

    private void setRealImageList(List<?> imagesUrl) {
        if (imagesUrl == null || imagesUrl.size() <= 0) {
            mBannerDefaultImage.setVisibility(VISIBLE);
            BLog.e(TAG, "The image data set is empty.");
            return;
        }
        mImageViewList.clear();
        mBannerDefaultImage.setVisibility(GONE);
        mNumIndicatorTv.setText(String.format(Locale.US, COUNT_TEXT, 1, count));

        createIndicator();

        View imageView = null;
        for (int i = 0; i <= count + 1; i++) {
            if (imageLoader != null) {
                imageView = imageLoader.createImageView(mContext);
            }
            if (imageView == null) {
                imageView = new ImageView(mContext);
            }
            setScaleType(imageView);
            Object obj;
            if (i == 0) {
                obj = imagesUrl.get(count - 1);
            } else if (i == count + 1) {
                obj = imagesUrl.get(0);
            } else {
                obj = imagesUrl.get(i - 1);
            }
            mImageViewList.add(imageView);
            if (imageLoader != null) {
                imageLoader.displayImage(mContext, obj, imageView);
            } else {
                BLog.e(TAG, "Please set images loader.");
            }
        }
    }

    private void setScaleType(View imageView) {
        if (imageView instanceof ImageView) {
            ImageView view = ((ImageView) imageView);
            switch (scaleType) {
                case 0:
                    view.setScaleType(ImageView.ScaleType.CENTER);
                    break;
                case 1:
                    view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case 2:
                    view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    break;
                case 3:
                    view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
                case 4:
                    view.setScaleType(ImageView.ScaleType.FIT_END);
                    break;
                case 5:
                    view.setScaleType(ImageView.ScaleType.FIT_START);
                    break;
                case 6:
                    view.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 7:
                    view.setScaleType(ImageView.ScaleType.MATRIX);
                    break;
            }
        }
    }


    private void createIndicator() {
        mIndicatorImageList.clear();
        mIndicatorLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
            params.leftMargin = mIndicatorMargin;
            params.rightMargin = mIndicatorMargin;
            if (i == 0) {
                imageView.setImageResource(mIndicatorSelectedResId);
            } else {
                imageView.setImageResource(mIndicatorUnselectedResId);
            }
            mIndicatorImageList.add(imageView);
            mIndicatorLayout.addView(imageView, params);
        }
    }

    private void setData() {
        currentItem = 1;
        if (adapter == null) {
            adapter = new BannerPagerAdapter();
            mViewPager.addOnPageChangeListener(this);
        }
        mViewPager.setAdapter(adapter);
        mViewPager.setFocusable(true);
        mViewPager.setCurrentItem(1);
        mViewPager.setSupportWrapContent(supportWrapContent);
        if (gravity != -1)
            mIndicatorLayout.setGravity(gravity);
        if (isScroll && count > 1) {
            mViewPager.setScrollable(true);
        } else {
            mViewPager.setScrollable(false);
        }
        if (isAutoPlay)
            startAutoPlay();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(toRealPosition(position), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        currentItem = position;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(toRealPosition(position));
        }
        if (bannerStyle == BannerConfig.CIRCLE_INDICATOR
                || bannerStyle == BannerConfig.CIRCLE_INDICATOR_TITLE
                || bannerStyle == BannerConfig.CIRCLE_NUM_INDICATOR_TITLE
                || bannerStyle == BannerConfig.CIRCLE_NUM_INDICATOR) {
            mIndicatorImageList.get((lastPosition - 1 + count) % count)
                    .setImageResource(mIndicatorUnselectedResId);
            mIndicatorImageList.get((position - 1 + count) % count)
                    .setImageResource(mIndicatorSelectedResId);
            lastPosition = position;
        }
        if (position == 0) position = count;
        if (position > count) position = 1;
        mBannerTitleTv.setText(mTitleList.get(position - 1));
        mNumIndicatorTv.setText(String.format(Locale.US, COUNT_TEXT, position, count));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
        switch (state) {
            case 0://No operation
                if (currentItem == 0) {
                    mViewPager.setCurrentItem(count, false);
                } else if (currentItem == count + 1) {
                    mViewPager.setCurrentItem(1, false);
                }
                break;
            case 1://start Sliding
                if (currentItem == count + 1) {
                    mViewPager.setCurrentItem(1, false);
                } else if (currentItem == 0) {
                    mViewPager.setCurrentItem(count, false);
                }
                break;
            case 2://end Sliding
                break;
        }
    }


    private final Runnable mPlayRunnable = new Runnable() {
        @Override
        public void run() {
            if (count > 1 && isAutoPlay) {
                currentItem = currentItem % (count + 1) + 1;
                if (currentItem == 1) {
                    mViewPager.setCurrentItem(currentItem, false);
                    handler.post(mPlayRunnable);
                } else {
                    mViewPager.setCurrentItem(currentItem);
                    handler.postDelayed(mPlayRunnable, delayTime);
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isAutoPlay) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                startAutoPlay();
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopAutoPlay();
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    final class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            container.addView(mImageViewList.get(position));
            View view = mImageViewList.get(position);
            if (bannerListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bannerListener.onBannerClick(toRealPosition(position));
                    }
                });
            }
            return view;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

    }

    /**
     * 返回真实的位置
     *
     * @param position
     * @return 下标从0开始
     */
    private int toRealPosition(int position) {
        int realPosition = (position - 1) % count;
        if (realPosition < 0)
            realPosition += count;
        return realPosition;
    }


}
