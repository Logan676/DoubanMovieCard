package com.github.moviecard;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;


import com.github.moviecard2.R;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;

/**
 * 仿豆瓣层叠卡片
 * Created by logan676 on 2019-08-01.
 */
@SuppressLint("NewApi")
public class MovieCardLayout extends ViewGroup {
    public static final String CARD_TAG = "CollapseCardLayout";

    // --------------- 常量值 --------------- //
    private static final float DENSITY = 2.75f;

    private static final int CHILDREN_CONTENT_MARGIN_LEFT = (int) (10 * DENSITY);

    private static final int ITEM_DIVIDER_W = (int) (10 * DENSITY);
    private static final int ITEM_MARGIN_LEFT = (int) (10 * DENSITY);
    private static final int ITEM_MARGIN_RIGHT = (int) (10 * DENSITY);
    private static final int ITEM_MARGIN_TOP = (int) (10 * DENSITY);
    private static final int ITEM_MARGIN_BOTTOM = (int) (10 * DENSITY);

    private static final float SCALE_RATIO_L0 = 0.4f;
    private static final float SCALE_RATIO_L1 = 0.6f;
    private static final float SCALE_RATIO_L2 = 0.8f;
    private static final float SCALE_RATIO_L3 = 1.0f;
    private static final float SCALE_RATIO_L4 = 1.2f;
    private static final float SCALE_RATIO_L5 = 1.0f;


    private static final float ALPHA_RATIO_L0 = 0.2f;
    private static final float ALPHA_RATIO_L1 = 0.5f;
    private static final float ALPHA_RATIO_L2 = 0.7f;
    private static final float ALPHA_RATIO_L3 = 0.95f;
    private static final float ALPHA_RATIO_L4 = 1.0f;

    private static final int CARD_W = (int) (185 * 1);
    private static final int CARD_H = (int) (254 * 1);

    private static final int W = (int) (CARD_W * SCALE_RATIO_L4);
    private static final int H = (int) (CARD_H * SCALE_RATIO_L4);

    private static final int TRANSLATE_X = (int) ((W - CARD_W) * 0.75f);

    // --------------- UI参数 --------------- //
    private int mChildrenContentMarginLeft = CHILDREN_CONTENT_MARGIN_LEFT;

    private int mItemDividerWidth = ITEM_DIVIDER_W;
    private int mItemMarginLeft = ITEM_MARGIN_LEFT;
    private int mItemMarginRight = ITEM_MARGIN_RIGHT;
    private int mItemMarginTop = ITEM_MARGIN_TOP;
    private int mItemMarginBottom = ITEM_MARGIN_BOTTOM;

    private float mScaleRatioL0 = SCALE_RATIO_L0;
    private float mScaleRatioL1 = SCALE_RATIO_L1;
    private float mScaleRatioL2 = SCALE_RATIO_L2;
    private float mScaleRatioL3 = SCALE_RATIO_L3;
    private float mScaleRatioL4 = SCALE_RATIO_L4;
    private float mScaleRatioL5 = SCALE_RATIO_L5;

    private float mAlphaRatioL0 = ALPHA_RATIO_L0;
    private float mAlphaRatioL1 = ALPHA_RATIO_L1;
    private float mAlphaRatioL2 = ALPHA_RATIO_L2;
    private float mAlphaRatioL3 = ALPHA_RATIO_L3;
    private float mAlphaRatioL4 = ALPHA_RATIO_L4;

    private int mCardChildViewWidth = CARD_W;
    private int mCardChildViewHeight = CARD_H;

    private int mCardViewWidth = W;
    private int mCardViewHeight = H;

    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    private int mTranslateX = TRANSLATE_X;

    // --------------- RecycleBin --------------- //

    public static final int SCROLL_DIRECTION_LEFT = 1;
    public static final int SCROLL_DIRECTION_RIGHT = 2;
    public static final int SCROLL_DIRECTION_NONE = 0;
    private int DIRECTION = SCROLL_DIRECTION_NONE;

    public static final int INVALID_POSITION = -1;
    private BaseMovieCardAdapter mAdapter;
    final RecycleBin mRecycler = new RecycleBin();
    final boolean[] mIsScrap = new boolean[1];
    Rect mListPadding = new Rect();
    private boolean mDataChanged;
    private boolean mAdapterHasStableIds;
    private boolean mStackFromBottom;
    private int mSelectedPosition = INVALID_POSITION;

    private int mFirstPosition;

    protected int mPaddingLeft = 0;
    /**
     * The right padding in pixels, that is the distance in pixels between the
     * right edge of this view and the right edge of its content.
     */
    protected int mPaddingRight = 0;
    /**
     * The top padding in pixels, that is the distance in pixels between the
     * top edge of this view and the top edge of its content.
     */
    protected int mPaddingTop;
    /**
     * The bottom padding in pixels, that is the distance in pixels between the
     * bottom edge of this view and the bottom edge of its content.
     */
    protected int mPaddingBottom;

    private int mBottom, mTop;
    private int mItemCount;
    int mWidthMeasureSpec = 0;

    private Context mContext;


    // --------------- 交互 --------------- //

    private static final int TOUCH_SLOP = 8;
    private static final int MIN_FLING_VELOCITY = 400; // dips
    private static final int MIN_DISTANCE_FOR_FLING = 25; // dips

    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;

    private PageTransformer mPageTransformer;

    private final int mTouchSlop;

    private boolean mIsBeingDragged;

    private float mLastMotionX = 0;
    private float mLastMotionY = 0;
    private float mInitialMotionX;
    private float mInitialMotionY;

    private int mScrollX;
    private int mScrollY;

    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;


    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;
    private final int mMinimumVelocity;
    private final int mMaximumVelocity;
    private final int mFlingDistance;

    public MovieCardLayout(Context context) {
        this(context, null);
    }

    public MovieCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            int defStyleRes = 0;
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieCardLayout,
                    defStyleAttr, defStyleRes);

            int contentML = a.getDimensionPixelOffset(R.styleable.MovieCardLayout_childrenContentMarginLeft, CHILDREN_CONTENT_MARGIN_LEFT);
            setChildrenContentMarginLeft(contentML);

            int ML = a.getDimensionPixelOffset(R.styleable.MovieCardLayout_itemMarginLeft, ITEM_MARGIN_LEFT);
            int MR = a.getDimensionPixelOffset(R.styleable.MovieCardLayout_itemMarginRight, ITEM_MARGIN_RIGHT);
            int MT = a.getDimensionPixelOffset(R.styleable.MovieCardLayout_itemMarginTop, ITEM_MARGIN_TOP);
            int MB = a.getDimensionPixelOffset(R.styleable.MovieCardLayout_itemMarginBottom, ITEM_MARGIN_BOTTOM);
            setItemMarginLeft(ML);
            setItemMarginRight(MR);
            setItemMarginTop(MT);
            setItemMarginBottom(MB);

            int w = a.getDimensionPixelOffset(R.styleable.MovieCardLayout_cardWidth, W);
            int h = a.getDimensionPixelOffset(R.styleable.MovieCardLayout_cardHeight, H);

            setCardViewWidth(w);
            setCardViewHeight(h);

            float scale = a.getFloat(R.styleable.MovieCardLayout_cardScale, SCALE_RATIO_L4);
            setSaleRatio(scale);

            float alpha = a.getFloat(R.styleable.MovieCardLayout_cardAlpha, ALPHA_RATIO_L3);
            setAlpha(alpha);

            int dividerW = a.getDimensionPixelOffset(R.styleable.MovieCardLayout_itemDividerWidth, ITEM_DIVIDER_W);
            setItemDividerWidth(dividerW);

            a.recycle();
        }


        setWillNotDraw(false);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setFocusable(true);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = TOUCH_SLOP;
        final float density = context.getResources().getDisplayMetrics().density;

        mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;

        mTop = 0;
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();
        mListPadding.top = mPaddingTop;
        mListPadding.right = mPaddingRight;
        mListPadding.left = mPaddingLeft;
        mListPadding.bottom = mPaddingBottom;
        mBottom = mScreenWidth;

    }

    public void setPageTransformer(PageTransformer transformer) {
        mPageTransformer = transformer;
    }

    void resetList() {
        removeAllViewsInLayout();
        mFirstPosition = 0;
        mDataChanged = false;
        invalidate();
    }

    public void setAdapter(BaseMovieCardAdapter adapter) {
        resetList();
        mRecycler.clear();


        mAdapter = adapter;

        if (mAdapter != null) {
            mAdapterHasStableIds = mAdapter.hasStableIds();
            mItemCount = mAdapter.getCount();

            mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());
        }

        requestLayout();

        mDataChanged = true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean needsInvalidate = false;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged) {
                    final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                    final float x = ev.getX(pointerIndex);
                    final float xDiff = Math.abs(x - mLastMotionX);
                    final float y = ev.getY(pointerIndex);
                    final float yDiff = Math.abs(y - mLastMotionY);
                    if (xDiff > mTouchSlop && xDiff > yDiff) {
                        mIsBeingDragged = true;

                        mLastMotionX = mLastMotionX - mInitialMotionX > 0 ? mInitialMotionX + mTouchSlop :
                                mInitialMotionX - mTouchSlop;
                        // Disallow Parent Intercept, just in case
                        ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }

                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    final float x = ev.getX();
                    needsInvalidate = performDrag(x);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                final int initialVelocity = (int) velocityTracker.getXVelocity(mActivePointerId);
                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(activePointerIndex);
                final int totalDelta = (int) (x - mInitialMotionX);
                boolean scrollNext = fastScrollToNextPage(initialVelocity, totalDelta);
                endDrag();
                autoRollBack(scrollNext, initialVelocity);
                needsInvalidate = true;
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                final float evX = ev.getX(index);
                mLastMotionX = evX;
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                mLastMotionX = ev.getX(ev.findPointerIndex(mActivePointerId));
                break;
        }

        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private boolean fastScrollToNextPage(int velocity, int deltaX) {
        return Math.abs(deltaX) > mFlingDistance &&
                Math.abs(velocity) > mMinimumVelocity;

    }

    private void invalidateAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            onPageScrolled();
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;
//        mIsUnableToDrag = false;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private int getCurrentIndex() {
        int absScrollX = Math.abs(mScrollX);
        int SUM_W = mItemMarginLeft + mCardViewWidth + mItemDividerWidth + mItemMarginRight;
        int curIndex = absScrollX == 0 ? 0 : (int) ((absScrollX) * 1.0f / SUM_W) + 1;
        final int childCount = mItemCount;

        if (absScrollX == 0) {
            return 0;
        }
        curIndex -= mFirstPosition;

        if (curIndex == 0) {
            curIndex = 1;
        }

        if (curIndex >= childCount) {
            curIndex = childCount - 1;
        }
        return curIndex;
    }

    private void autoRollBack(boolean scrollNext, int velocity) {
        int scrollX = mScrollX;
        int curIndex = getCurrentIndex();
        final int childCount = getChildCount();

        if (curIndex < childCount) {
            View child = getChildAt(curIndex);
            if (child == null) {
                return;
            }
            int l = child.getLeft();
            float triggerX = (getPaddingLeft() + mChildrenContentMarginLeft + mCardViewWidth * 1.0f / 2);
            boolean scrollToLeft = scrollNext && velocity < 0;
            boolean scrollToRight = scrollNext && velocity > 0;
            int targetIndex = 0;
            if (scrollToLeft || (l <= triggerX && !scrollToRight)) {
                //叠加
                int dstX = scrollX - (l - getPaddingLeft() - mChildrenContentMarginLeft - mItemMarginLeft);
                smoothScrollTo(dstX, 0);
                targetIndex = curIndex;
            } else {
                //展开
                int l2 = (int) (getPaddingLeft() + mChildrenContentMarginLeft + mItemMarginLeft + mCardViewWidth + mItemMarginRight + mItemDividerWidth + mItemMarginLeft + 0.5f);
                final int dx = l2 - l;
                final int dstX = scrollX + dx;
                smoothScrollTo(dstX, 0);
                if (curIndex >= 1) {
                    targetIndex = curIndex - 1;
                }
            }

            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(targetIndex);
            }
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    private OnPageChangeListener mOnPageChangeListener;

    private ValueAnimator mValueAnimator;

    private void smoothScrollTo(int dstX, int dstY) {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            return;
        }

        mValueAnimator = null;
        int scrX = mScrollX;
        mValueAnimator = ValueAnimator.ofFloat(scrX, dstX);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                scrollToInner((int) x, 0);
            }
        });

        mValueAnimator.setDuration(300);
        mValueAnimator.start();
    }

    private void scrollByInner(int x, int y) {
        scrollToInner(mScrollX + x, mScrollY + y);
    }

    private void scrollToInner(int x, int y) {
        if (mScrollX != x || mScrollY != y) {
            if (x > 0) {
                x = 0;
            }

            float minScrollX = (mItemCount - 1) * (mItemMarginLeft + mCardViewWidth + mItemMarginRight + mItemDividerWidth);

            if (x < -1.0f * minScrollX) {
                x = (int) (-1.0f * minScrollX);
            }

            mScrollX = x;
            mScrollY = y;

            invalidateAnimation();
        }
    }

    private int getScrollXInner() {
        return mScrollX;
    }

    private int getScrollYInner() {
        return mScrollY;
    }

    private float incrementalDeltaY;

    private boolean performDrag(float newX) {

        if (newX > mLastMotionX) {
            DIRECTION = SCROLL_DIRECTION_RIGHT;
        } else if (newX < mLastMotionX) {
            DIRECTION = SCROLL_DIRECTION_LEFT;
        } else {
            DIRECTION = SCROLL_DIRECTION_NONE;
        }

        final float deltaX = newX - mLastMotionX;
        incrementalDeltaY = deltaX;
        mLastMotionX = newX;

        // Translate scroll to relative coordinates.
        final float nextScrollX = getScrollXInner() + deltaX;

        scrollToInner((int) nextScrollX, getScrollYInner());
        return true;
    }

    /**
     * 滑动卡片时，下层的卡片有一个缩放动画和位移和aplha动画，位移的落点是左侧
     */
    protected void onPageScrolled() {
        // Offset any decor views if needed - keep them on-screen at all times.
        final int scrollX = mScrollX;
        final int width = getWidth();
        int childCount = getChildCount();

        int start = 0;
        int count = 0;
        final int firstPosition = mFirstPosition;
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            offsetChildLeftAndRight(scrollX, child, i + firstPosition);

            float scale = scaleChild(child, scrollX, i + firstPosition);
            float alpha = alphaChild(child, scrollX, i + firstPosition);
            float tx = translateChild(child, scrollX, i + firstPosition);

            if (mPageTransformer != null) {
                final float transformPos = (float) (child.getLeft() - scrollX) / ((mItemMarginLeft + mCardViewWidth + mItemDividerWidth));
                mPageTransformer.transformPage(child, transformPos);
            }
        }
        switch (DIRECTION) {
            case SCROLL_DIRECTION_LEFT:
                for (int i = 0; i < childCount; i++) {
                    final View child = getChildAt(i);
                    if (child.getAlpha() >= ALPHA_RATIO_L0) {
                        break;
                    } else {
                        count++;
                        int position = firstPosition + i;
                        // The view will be rebound to new data, clear any
                        // system-managed transient state.
                        mRecycler.addScrapView(child, position);
                        Log.d(CARD_TAG, "mRecycler addScrapView left -> position=" + position);
                    }
                }

                break;
            case SCROLL_DIRECTION_RIGHT:
                int childMaxRight = getWidth() - getPaddingRight();
                for (int i = childCount - 1; i >= 0; i--) {
                    final View child = getChildAt(i);
                    if (child.getLeft() <= childMaxRight) {
                        break;
                    } else {
                        start = i;
                        count++;
                        int position = firstPosition + i;
                        mRecycler.addScrapView(child, position);
                        Log.d(CARD_TAG, "mRecycler addScrapView right -> position=" + position);
                    }
                }

                break;
            case SCROLL_DIRECTION_NONE:
                break;
        }

        if (count > 0) {
            Log.d(CARD_TAG, "mRecycler -> detachViewsFromParent start=" + start + ", count=" + count);
            detachViewsFromParent(start, count);
            mRecycler.removeSkippedScrap();
        }


        if (DIRECTION == SCROLL_DIRECTION_LEFT) {
            mFirstPosition += count;
        }

        final boolean down = DIRECTION == SCROLL_DIRECTION_LEFT;
        final boolean up = DIRECTION == SCROLL_DIRECTION_RIGHT;
        final boolean loadLeft = up && getChildAt(0).getAlpha() >= ALPHA_RATIO_L0;
        final int absIncrementalDeltaY = (int) Math.abs(incrementalDeltaY);
        int lastBottom = getChildAt(getChildCount() - 1).getRight();


        final int end = getWidth() - getPaddingRight();
        final int spaceBelow = lastBottom - end;

        if (loadLeft || spaceBelow < absIncrementalDeltaY) {
            fillGap(down);
        }

        mRecycler.fullyDetachScrapViews();
    }

    private float scaleChild(View child, int scrollX, int childIndex) {
        int r = scrollX +
                childIndex * (mItemMarginLeft + mCardViewWidth + mItemDividerWidth + mItemMarginRight);

        int R = mItemMarginLeft + mCardViewWidth + mItemMarginRight + mItemDividerWidth;

        float scale = mScaleRatioL3;

        if (r > R) {
            scale = mScaleRatioL3;
        } else if (r > 0) {
            float diff = mScaleRatioL4 - mScaleRatioL3;
            scale = mScaleRatioL3 + ((R - r) * 1.0f / (R * 1.0f)) * diff;

        } else if (r > -R) {
            if (r >= -R / 2) {
                float diff = mScaleRatioL4 - mScaleRatioL3;
                scale = mScaleRatioL4 - (r * 1.0f / (-R * 1.0f / 2)) * diff;
            } else {
                scale = mScaleRatioL3;
            }

        } else if (r > -2 * R) {
            float v = (r + R) * 1.0f / (-R * 1.0f);
            float diff = mScaleRatioL3 - mScaleRatioL2;
            scale = mScaleRatioL3 - v * diff;
        } else if (r > -3 * R && r <= -2 * R) {
            float diff = mScaleRatioL2 - mScaleRatioL1;
            int rr = r + 2 * R;
            scale = mScaleRatioL2 - (rr * 1.0f / (-R * 1.f)) * diff;
        } else if (r > -4 * R && r <= -3 * R) {
            float diff = mScaleRatioL1 - mScaleRatioL0;
            int rr = r + 3 * R;
            scale = mScaleRatioL1 - (rr * 1.0f / (-R * 1.f)) * diff;
        } else if (r > -5 * R && r <= -4 * R) {
            float diff = mScaleRatioL0 - 0.2f;
            int rr = r + 4 * R;
            scale = mScaleRatioL0 - (rr * 1.0f / (-R * 1.f)) * diff;
        } else if (r > -6 * R && r <= -5 * R) {
            scale = 0.2f;
        }

        if (0 < scale) {
            if (child instanceof ViewGroup) {
                for (int ci = 0; ci < ((ViewGroup) child).getChildCount(); ci++) {
                    ((ViewGroup) child).getChildAt(ci).setScaleX(scale);
                    ((ViewGroup) child).getChildAt(ci).setScaleY(scale);
                }
            }
        }

        return scale;
    }

    private float alphaChild(View child, int scrollX, int childIndex) {

        int r = scrollX +
                childIndex * (mItemMarginLeft + mCardViewWidth + mItemDividerWidth + mItemMarginRight);

        int R = mItemMarginLeft + mCardViewWidth + mItemMarginRight + mItemDividerWidth;

        float alpha = 1.0f;

        if (r > R) {
        } else if (r > 0) {

        } else if (r > -R) {
            float da = mAlphaRatioL4 - mAlphaRatioL3;
            alpha = mAlphaRatioL4 - (r * 1.0f / (-R * 1.f)) * da;
        } else if (r > -2 * R) {
            float v = (r + R) * 1.0f / (-R * 1.0f);

            float da = mAlphaRatioL3 - mAlphaRatioL2;
            alpha = mAlphaRatioL3 - v * da;

        } else if (r > -3 * R && r <= -2 * R) {
            float da = mAlphaRatioL2 - mAlphaRatioL1;
            alpha = mAlphaRatioL2 - ((r + 2 * R) * 1.0f / (-R * 1.f)) * da;

        } else if (r > -4 * R && r <= -3 * R) {
            float da = mAlphaRatioL1 - 0;
            alpha = mAlphaRatioL1 - ((r + 3 * R) * 1.0f / (-R * 1.f)) * da;
        } else if (r > -5 * R && r <= -4 * R) {
            alpha = 0.f;
        } else if (r > -6 * R && r <= -5 * R) {
            alpha = 0.f;
        } else {
            alpha = 0.f;
        }

        if (alpha > 1.0f) {
            alpha = 1.0f;
        }

        if (alpha < 0.f) {
            alpha = 0.f;
        }

        child.setAlpha(alpha);

        return alpha;
    }

    private float translateChild(View child, int scrollX, int childIndex) {
        int r = scrollX +
                childIndex * (mItemMarginLeft + mCardViewWidth + mItemDividerWidth + mItemMarginRight);

        int R = mItemMarginLeft + mCardViewWidth + mItemMarginRight + mItemDividerWidth;

        float tx = 0.f;

        if (r > R) {

        } else if (r > 0) {
            tx = (r - R) * 1.0f / R * mTranslateX;
        } else {
            tx = r * 1.0f / R * mTranslateX - mTranslateX;
        }

        child.setTranslationX(tx);
        return tx;
    }

    private int offsetChildLeftAndRight(int scrollX, View child, int position) {
        int paddingLeft = getPaddingLeft() + mChildrenContentMarginLeft + mItemMarginLeft +
                position * (mCardViewWidth + mItemMarginRight + mItemMarginLeft + mItemDividerWidth);
        int childLeft = paddingLeft + scrollX;

        final int childOffset = childLeft - child.getLeft();
        if (childOffset != 0) {
            child.offsetLeftAndRight(childOffset);
        }

        int MIN_LEFT = getPaddingLeft() + mChildrenContentMarginLeft + mItemMarginLeft;

        if (child.getLeft() < MIN_LEFT) {
            child.setLeft(MIN_LEFT);
            int r = MIN_LEFT + child.getMeasuredWidth();
            child.setRight(r);
        }

        return paddingLeft;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // For simple implementation, our internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view.  We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));
        mWidthMeasureSpec = widthMeasureSpec;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAdapter == null) {
            resetList();
            return;
        }

        final int childrenTop = mListPadding.left;
        final int childrenBottom = mBottom - mTop - mListPadding.bottom;
        final int childCount = getChildCount();

        Log.d(CARD_TAG, "onLayout count=" + childCount);

        // Pull all children into the RecycleBin.
        // These views will be reused if possible
        final int firstPosition = mFirstPosition;
        final RecycleBin recycleBin = mRecycler;
        if (mDataChanged) {
            for (int i = 0; i < childCount; i++) {
                recycleBin.addScrapView(getChildAt(i), firstPosition + i);
            }
        } else {
            recycleBin.fillActiveViews(childCount, firstPosition);
        }

        // Clear out old views
        detachAllViewsFromParent();
        recycleBin.removeSkippedScrap();

        View oldFirst = null;

        if (childCount == 0) {
            if (!mStackFromBottom) {
                fillFromTop(childrenTop);
            } else {
                fillUp(mItemCount - 1, childrenBottom);
            }
        } else {
            if (mFirstPosition < mItemCount) {
                fillSpecific(mFirstPosition,
                        oldFirst == null ? childrenTop : oldFirst.getTop());
            } else {
                fillSpecific(0, childrenTop);
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.layout(child.getLeft(), child.getTop(),
                        child.getLeft() + child.getMeasuredWidth(),
                        child.getTop() + child.getMeasuredHeight());
            }
        }

        // Flush any cached views that did not get reused above
        recycleBin.scrapActiveViews();

        mDataChanged = false;

        invalidateAnimation();
    }

    void fillGap(boolean down) {
        Log.d(CARD_TAG, "mRecycler -> fillGap down=" + down);
        final int count = getChildCount();
        if (down) {
            int paddingTop = 0;
            final int startOffset = count > 0 ? getChildAt(count - 1).getRight() + mItemDividerWidth :
                    paddingTop;
            fillDown(mFirstPosition + count, startOffset);
        } else {
            int paddingBottom = 0;
            final int startOffset = count > 0 ? getChildAt(0).getLeft() - mItemDividerWidth :
                    getWidth() - paddingBottom;
            fillUp(mFirstPosition - 1, startOffset);
        }
    }

    /**
     * Put a specific item at a specific location on the screen and then build
     * up and down from there.
     *
     * @param position The reference view to use as the starting point
     * @param top      Pixel offset from the top of this view to the top of the
     *                 reference view.
     * @return The selected view, or null if the selected view is outside the
     * visible area.
     */
    private void fillSpecific(int position, int top) {
        View temp = makeAndAddView(position, top, true, mListPadding.left, false);
        // Possibly changed again in fillUp if we add rows above this one.
        mFirstPosition = position;


        final int dividerHeight = mItemDividerWidth;
        if (!mStackFromBottom) {
            fillUp(position - 1, temp.getTop() - dividerHeight);
            fillDown(position + 1, temp.getBottom() + dividerHeight);
        } else {
            fillDown(position + 1, temp.getBottom() + dividerHeight);
            fillUp(position - 1, temp.getTop() - dividerHeight);
        }
    }


    /**
     * Fills the list from top to bottom, starting with mFirstPosition
     *
     * @param nextTop The location where the top of the first item should be
     *                drawn
     * @return The view that is currently selected
     */
    private void fillFromTop(int nextTop) {
        mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
        mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
        if (mFirstPosition < 0) {
            mFirstPosition = 0;
        }
        fillDown(mFirstPosition, nextTop);
    }


    /**
     * Fills the list from pos down to the end of the list view.
     *
     * @param pos      The first position to put in the list
     * @param nextLeft The location where the top of the item associated with pos
     *                 should be drawn
     * @return The view that is currently selected, if it happens to be in the
     * range that we draw.
     */
    private void fillDown(int pos, int nextLeft) {

        int end = (mBottom - mTop);

        while (nextLeft < end && pos < mItemCount) {
            makeAndAddView(pos, nextLeft, true, nextLeft, false);

            nextLeft = mScrollX + getPaddingLeft() + mChildrenContentMarginLeft + mItemMarginLeft + (pos + 1) * (mItemMarginLeft + mCardViewWidth + mItemDividerWidth + mItemMarginRight);
            pos++;
        }
    }

    /**
     * Fills the list from pos up to the top of the list view.
     *
     * @param pos       The first position to put in the list
     * @param nextRight The location where the bottom of the item associated
     *                  with pos should be drawn
     * @return The view that is currently selected
     */
    private void fillUp(int pos, int nextRight) {
        int MIN_LEFT = getPaddingLeft() + mChildrenContentMarginLeft + mItemMarginLeft;
        int end = MIN_LEFT;

        while (nextRight > end && pos >= 0) {
            // is this the selected item?
            boolean selected = pos == mSelectedPosition;
            makeAndAddView(pos, nextRight, false, mListPadding.left, selected);
            nextRight = mScrollX + pos * (getPaddingLeft() + mChildrenContentMarginLeft + mItemMarginLeft) + (pos - 1) * (mItemMarginLeft + mCardViewWidth + mItemDividerWidth + mItemMarginRight);
            pos--;
        }

        mFirstPosition = pos + 1;
    }


    /**
     * Obtains the view and adds it to our list of children. The view can be
     * made fresh, converted from an unused view, or used as is if it was in
     * the recycle bin.
     *
     * @param position     logical position in the list
     * @param left         top or bottom edge of the view to add
     * @param flow         {@code true} to align top edge to y, {@code false} to align
     *                     bottom edge to y
     * @param childrenLeft left edge where children should be positioned
     * @param selected     {@code true} if the position is selected, {@code false}
     *                     otherwise
     * @return the view that was added
     */
    private View makeAndAddView(int position, int left, boolean flow, int childrenLeft,
                                boolean selected) {
        if (!mDataChanged) {
            // Try to use an existing view for this position.
            final View activeView = mRecycler.getActiveView(position);
            if (activeView != null) {
                // Found it. We're reusing an existing child, so it just needs
                // to be positioned like a scrap view.
                setupChild(activeView, position, left, flow, childrenLeft, selected, true);
                return activeView;
            }
        }

        // Make a new view for this position, or convert an unused view if
        // possible.
        final View child = obtainView(position, mIsScrap);

        // This needs to be positioned and measured.
        setupChild(child, position, left, flow, childrenLeft, selected, mIsScrap[0]);

        return child;
    }

    /**
     * Gets a view and have it show the data associated with the specified
     * position. This is called when we have already discovered that the view
     * is not available for reuse in the recycle bin. The only choices left are
     * converting an old view or making a new one.
     *
     * @param position    the position to display
     * @param outMetadata an array of at least 1 boolean where the first entry
     *                    will be set {@code true} if the view is currently
     *                    attached to the window, {@code false} otherwise (e.g.
     *                    newly-inflated or remained scrap for multiple layout
     *                    passes)
     * @return A view displaying the data associated with the specified position
     */
    View obtainView(int position, boolean[] outMetadata) {

        outMetadata[0] = false;

        // Check whether we have a transient state view. Attempt to re-bind the
        // data and discard the view if we fail.
        final View transientView = mRecycler.getTransientStateView(position);
        if (transientView != null) {
            final LayoutParams params = (LayoutParams) transientView.getLayoutParams();
            Log.d(CARD_TAG, "re-bind transientView " + position);

            // If the view type hasn't changed, attempt to re-bind the data.
            if (params.viewType == mAdapter.getItemViewType(position)) {
                final View updatedView = mAdapter.getView(position, transientView, this);

                // If we failed to re-bind the data, scrap the obtained view.
                if (updatedView != transientView) {
                    setItemViewLayoutParams(updatedView, position);
                    mRecycler.addScrapView(updatedView, position);
                }
            }

            outMetadata[0] = true;

            // Finish the temporary detach started in addScrapView().
            transientView.dispatchFinishTemporaryDetach();
            return transientView;
        }

        final View scrapView = mRecycler.getScrapView(position);
        final View child = mAdapter.getView(position, scrapView, this);
        if (scrapView != null) {
            Log.d(CARD_TAG, "re-bind scrapView " + position);
            if (child != scrapView) {
                // Failed to re-bind the data, return scrap to the heap.
                mRecycler.addScrapView(scrapView, position);
            } else if (child.isTemporarilyDetached()) {
                outMetadata[0] = true;

                // Finish the temporary detach started in addScrapView().
                child.dispatchFinishTemporaryDetach();
            }
        }

        setItemViewLayoutParams(child, position);

        return child;
    }

    private void setItemViewLayoutParams(View child, int position) {
        final ViewGroup.LayoutParams vlp = child.getLayoutParams();
        LayoutParams lp;
        if (vlp == null) {
            lp = (LayoutParams) generateDefaultLayoutParams();
        } else if (!checkLayoutParams(vlp)) {
            lp = (LayoutParams) generateLayoutParams(vlp);
        } else {
            lp = (LayoutParams) vlp;
        }

        if (mAdapterHasStableIds) {
            lp.itemId = mAdapter.getItemId(position);
        }
        lp.viewType = mAdapter.getItemViewType(position);
        lp.isEnabled = mAdapter.isEnabled(position);
        if (lp != vlp) {
            child.setLayoutParams(lp);
        }
    }

    /**
     * Adds a view as a child and make sure it is measured (if necessary) and
     * positioned properly.
     *
     * @param child              the view to add
     * @param position           the position of this child
     * @param left               the y position relative to which this view will be positioned
     * @param flowDown           {@code true} to align top edge to y, {@code false} to
     *                           align bottom edge to y
     * @param childrenLeft       left edge where children should be positioned
     * @param selected           {@code true} if the position is selected, {@code false}
     *                           otherwise
     * @param isAttachedToWindow {@code true} if the view is already attached
     *                           to the window, e.g. whether it was reused, or
     *                           {@code false} otherwise
     */
    private void setupChild(View child, int position, int left, boolean flowDown, int childrenLeft,
                            boolean selected, boolean isAttachedToWindow) {

        final boolean needToMeasure = !isAttachedToWindow
                || child.isLayoutRequested();

        // Respect layout params that are already in the view. Otherwise make
        // some up...
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (p == null) {
            Log.d(CARD_TAG, "setupChild LayoutParams is null, generateDefaultLayoutParams");
            p = (LayoutParams) generateDefaultLayoutParams();
        }
        p.viewType = mAdapter.getItemViewType(position);
        p.isEnabled = mAdapter.isEnabled(position);

        if ((isAttachedToWindow && !p.forceAdd)) {
            Log.d(CARD_TAG, "setupChild attachViewToParent");
            attachViewToParent(child, flowDown ? -1 : 0, p);

        } else {
            p.forceAdd = false;
            Log.d(CARD_TAG, "setupChild addViewInLayout");
            addViewInLayout(child, flowDown ? -1 : 0, p, true);
        }

        if (needToMeasure) {
            final int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
                    mListPadding.left + mListPadding.right, p.width);
            final int lpHeight = p.height;
            final int childHeightSpec;
            if (lpHeight > 0) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(),
                        MeasureSpec.UNSPECIFIED);
            }
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }

        final int w = child.getMeasuredWidth();
        final int h = child.getMeasuredHeight();
        final int childTop = getPaddingTop() + mItemMarginTop;
        final int childLeft = flowDown ? left : left - w;

        if (needToMeasure) {
            final int childRight = childLeft + w;
            final int childBottom = childTop + h;
            child.layout(childLeft, childTop, childRight, childBottom);
        } else {
            child.offsetLeftAndRight(childLeft - child.getLeft());
            child.offsetTopAndBottom(childTop - child.getTop());
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(mCardViewWidth, mCardViewHeight, 0);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }


    public void setChildrenContentMarginLeft(int left) {
        mChildrenContentMarginLeft = left;
    }

    public void setItemDividerWidth(int itemDividerWidth) {
        mItemDividerWidth = itemDividerWidth;

        float scale = (mCardChildViewWidth + itemDividerWidth) * 1.0f / mCardChildViewWidth;
        setSaleRatio(scale);
    }

    public void setItemMarginLeft(int itemMarginLeft) {
        mItemMarginLeft = itemMarginLeft;
    }

    public void setItemMarginTop(int itemMarginTop) {
        mItemMarginTop = itemMarginTop;
    }

    public void setItemMarginBottom(int itemMarginBottom) {
        mItemMarginBottom = itemMarginBottom;
    }

    public void setItemMarginRight(int itemMarginRight) {
        mItemMarginRight = itemMarginRight;
    }

    private void setChildCardViewWidth(int cardChildViewWidth) {
        mCardChildViewWidth = cardChildViewWidth;
    }

    private void setChildCardViewHeight(int cardChildViewHeight) {
        mCardChildViewHeight = cardChildViewHeight;
    }

    public void setCardViewWidth(int cardViewWidth) {
        mCardViewWidth = cardViewWidth;

        int childW = (int) (cardViewWidth * 1.0f / mScaleRatioL4);
        setChildCardViewWidth(childW);

        mTranslateX = (int) ((mCardViewWidth - mCardChildViewWidth) * 0.75f);
    }

    public void setCardViewHeight(int cardViewHeight) {
        mCardViewHeight = cardViewHeight;

        int childH = (int) (cardViewHeight * 1.0f / mScaleRatioL4);
        setChildCardViewHeight(childH);
    }

    /**
     * 设置缩放倍数
     *
     * @param scale 取值范围大于1.0f，比如1.2f
     */
    public void setSaleRatio(float scale) {
        if (scale < 1.0f) {
            throw new RuntimeException("scale=" + scale + "不合法，这里的值用于设置要放大的那张卡片的缩放倍数。需要指定大于1.0f的值，比如1.2f");
        }

        if (scale == 1.0f) {
            mCardChildViewWidth = mCardViewWidth;
            mCardChildViewHeight = mCardViewHeight;

            mScaleRatioL4 = scale;
            mScaleRatioL3 = scale;
            mScaleRatioL2 = scale;
            mScaleRatioL1 = mScaleRatioL2 - 0.2f;
            mScaleRatioL0 = mScaleRatioL1 - 0.2f;
        } else {
            mScaleRatioL4 = scale;
            mScaleRatioL3 = mScaleRatioL4 - 0.2f;
            mScaleRatioL2 = mScaleRatioL3 - 0.2f;
            mScaleRatioL1 = mScaleRatioL2 - 0.2f;
            mScaleRatioL0 = mScaleRatioL1 - 0.2f;
        }
    }

    /**
     * 设置alpha值
     *
     * @param alpha 取值范围从0.f到1.0f
     */
    public void setAlpha(float alpha) {
        if (alpha < 0.8f) {
            throw new RuntimeException("alpha=" + alpha + "不合法，设置的alpha值太小，建议使用0.9f");
        }

        if (alpha > 1.0f) {
            throw new RuntimeException("alpha=" + alpha + "不合法，设置的alpha取值范围是0f-1f");
        }

        mAlphaRatioL3 = alpha;
        mAlphaRatioL2 = mAlphaRatioL3 - 0.25f;
        mAlphaRatioL1 = mAlphaRatioL2 - 0.2f;
        mAlphaRatioL0 = mAlphaRatioL1 - 0.3f;
    }


    /**
     * Callback interface for responding to changing state of the selected page.
     */
    public interface OnPageChangeListener {

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        public void onPageSelected(int position);
    }

    /**
     * The RecycleBin facilitates reuse of views across layouts. The RecycleBin has two levels of
     * storage: ActiveViews and ScrapViews. ActiveViews are those views which were onscreen at the
     * start of a layout. By construction, they are displaying current information. At the end of
     * layout, all views in ActiveViews are demoted to ScrapViews. ScrapViews are old views that
     * could potentially be used by the adapter to avoid allocating views unnecessarily.
     *
     * @see AbsListView#setRecyclerListener(AbsListView.RecyclerListener)
     * @see AbsListView.RecyclerListener
     */
    class RecycleBin {
        private AbsListView.RecyclerListener mRecyclerListener;

        /**
         * The position of the first view stored in mActiveViews.
         */
        private int mFirstActivePosition;

        /**
         * Views that were on screen at the start of layout. This array is populated at the start of
         * layout, and at the end of layout all view in mActiveViews are moved to mScrapViews.
         * Views in mActiveViews represent a contiguous range of Views, with position of the first
         * view store in mFirstActivePosition.
         */
        private View[] mActiveViews = new View[0];

        /**
         * Unsorted views that can be used by the adapter as a convert view.
         */
        private ArrayList<View>[] mScrapViews;

        private int mViewTypeCount;

        private ArrayList<View> mCurrentScrap;

        private ArrayList<View> mSkippedScrap;

        private SparseArray<View> mTransientStateViews;
        private LongSparseArray<View> mTransientStateViewsById;

        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            //noinspection unchecked
            ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
            for (int i = 0; i < viewTypeCount; i++) {
                scrapViews[i] = new ArrayList<View>();
            }
            mViewTypeCount = viewTypeCount;
            mCurrentScrap = scrapViews[0];
            mScrapViews = scrapViews;
        }

        public void markChildrenDirty() {
            if (mViewTypeCount == 1) {
                final ArrayList<View> scrap = mCurrentScrap;
                final int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    scrap.get(i).forceLayout();
                }
            } else {
                final int typeCount = mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    final ArrayList<View> scrap = mScrapViews[i];
                    final int scrapCount = scrap.size();
                    for (int j = 0; j < scrapCount; j++) {
                        scrap.get(j).forceLayout();
                    }
                }
            }
            if (mTransientStateViews != null) {
                final int count = mTransientStateViews.size();
                for (int i = 0; i < count; i++) {
                    mTransientStateViews.valueAt(i).forceLayout();
                }
            }
            if (mTransientStateViewsById != null) {
                final int count = mTransientStateViewsById.size();
                for (int i = 0; i < count; i++) {
                    mTransientStateViewsById.valueAt(i).forceLayout();
                }
            }
        }

        public boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0;
        }

        /**
         * Clears the scrap heap.
         */
        void clear() {
            if (mViewTypeCount == 1) {
                final ArrayList<View> scrap = mCurrentScrap;
                clearScrap(scrap);
            } else {
                final int typeCount = mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    final ArrayList<View> scrap = mScrapViews[i];
                    clearScrap(scrap);
                }
            }

            clearTransientStateViews();
        }

        /**
         * Fill ActiveViews with all of the children of the AbsListView.
         *
         * @param childCount          The minimum number of views mActiveViews should hold
         * @param firstActivePosition The position of the first view that will be stored in
         *                            mActiveViews
         */
        void fillActiveViews(int childCount, int firstActivePosition) {
            if (mActiveViews.length < childCount) {
                mActiveViews = new View[childCount];
            }
            mFirstActivePosition = firstActivePosition;

            //noinspection MismatchedReadAndWriteOfArray
            final View[] activeViews = mActiveViews;
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                // Don't put header or footer views into the scrap heap
                if (lp != null && lp.viewType != ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                    // Note:  We do place AdapterView.ITEM_VIEW_TYPE_IGNORE in active views.
                    //        However, we will NOT place them into scrap views.
                    activeViews[i] = child;
                    // Remember the position so that setupChild() doesn't reset state.
                    lp.scrappedFromPosition = firstActivePosition + i;
                }
            }
        }

        /**
         * Get the view corresponding to the specified position. The view will be removed from
         * mActiveViews if it is found.
         *
         * @param position The position to look up in mActiveViews
         * @return The view if it is found, null otherwise
         */
        View getActiveView(int position) {
            int index = position - mFirstActivePosition;
            final View[] activeViews = mActiveViews;
            if (index >= 0 && index < activeViews.length) {
                final View match = activeViews[index];
                activeViews[index] = null;
                return match;
            }
            return null;
        }

        View getTransientStateView(int position) {
            if (mAdapter != null && mAdapterHasStableIds && mTransientStateViewsById != null) {
                long id = mAdapter.getItemId(position);
                View result = mTransientStateViewsById.get(id);
                mTransientStateViewsById.remove(id);
                return result;
            }
            if (mTransientStateViews != null) {
                final int index = mTransientStateViews.indexOfKey(position);
                if (index >= 0) {
                    View result = mTransientStateViews.valueAt(index);
                    mTransientStateViews.removeAt(index);
                    return result;
                }
            }
            return null;
        }

        /**
         * Dumps and fully detaches any currently saved views with transient
         * state.
         */
        void clearTransientStateViews() {
            final SparseArray<View> viewsByPos = mTransientStateViews;
            if (viewsByPos != null) {
                final int N = viewsByPos.size();
                for (int i = 0; i < N; i++) {
                    removeDetachedView(viewsByPos.valueAt(i), false);
                }
                viewsByPos.clear();
            }

            final LongSparseArray<View> viewsById = mTransientStateViewsById;
            if (viewsById != null) {
                final int N = viewsById.size();
                for (int i = 0; i < N; i++) {
                    removeDetachedView(viewsById.valueAt(i), false);
                }
                viewsById.clear();
            }
        }

        /**
         * @return A view from the ScrapViews collection. These are unordered.
         */
        View getScrapView(int position) {
            final int whichScrap = mAdapter.getItemViewType(position);
            if (whichScrap < 0) {
                return null;
            }
            if (mViewTypeCount == 1) {
                return retrieveFromScrap(mCurrentScrap, position);
            } else if (whichScrap < mScrapViews.length) {
                return retrieveFromScrap(mScrapViews[whichScrap], position);
            }
            return null;
        }

        /**
         * Puts a view into the list of scrap views.
         * <p>
         * If the list data hasn't changed or the adapter has stable IDs, views
         * with transient state will be preserved for later retrieval.
         *
         * @param scrap    The view to add
         * @param position The view's position within its parent
         */
        void addScrapView(View scrap, int position) {
            final LayoutParams lp = (LayoutParams) scrap.getLayoutParams();
            if (lp == null) {
                // Can't recycle, but we don't know anything about the view.
                // Ignore it completely.
                return;
            }

            lp.scrappedFromPosition = position;

            // Remove but don't scrap header or footer views, or views that
            // should otherwise not be recycled.
            final int viewType = lp.viewType;
            if (!shouldRecycleViewType(viewType)) {
                // Can't recycle. If it's not a header or footer, which have
                // special handling and should be ignored, then skip the scrap
                // heap and we'll fully detach the view later.
                if (viewType != ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                    getSkippedScrap().add(scrap);
                }
                return;
            }

            scrap.dispatchStartTemporaryDetach();

            // Don't scrap views that have transient state.
            final boolean scrapHasTransientState = scrap.hasTransientState();
            if (scrapHasTransientState) {
                if (mAdapter != null && mAdapterHasStableIds) {
                    // If the adapter has stable IDs, we can reuse the view for
                    // the same data.
                    if (mTransientStateViewsById == null) {
                        mTransientStateViewsById = new LongSparseArray<>();
                    }
                    mTransientStateViewsById.put(lp.itemId, scrap);
                } else if (!mDataChanged) {
                    // If the data hasn't changed, we can reuse the views at
                    // their old positions.
                    if (mTransientStateViews == null) {
                        mTransientStateViews = new SparseArray<>();
                    }
                    mTransientStateViews.put(position, scrap);
                } else {
                    // Otherwise, we'll have to remove the view and start over.
                    clearScrapForRebind(scrap);
                    getSkippedScrap().add(scrap);
                }
            } else {
                clearScrapForRebind(scrap);
                if (mViewTypeCount == 1) {
                    mCurrentScrap.add(scrap);
                } else {
                    mScrapViews[viewType].add(scrap);
                }

                if (mRecyclerListener != null) {
                    mRecyclerListener.onMovedToScrapHeap(scrap);
                }
            }
        }

        private ArrayList<View> getSkippedScrap() {
            if (mSkippedScrap == null) {
                mSkippedScrap = new ArrayList<>();
            }
            return mSkippedScrap;
        }

        /**
         * Finish the removal of any views that skipped the scrap heap.
         */
        void removeSkippedScrap() {
            if (mSkippedScrap == null) {
                return;
            }
            final int count = mSkippedScrap.size();
            for (int i = 0; i < count; i++) {
                removeDetachedView(mSkippedScrap.get(i), false);
            }
            mSkippedScrap.clear();
        }

        /**
         * Move all views remaining in mActiveViews to mScrapViews.
         */
        void scrapActiveViews() {
            final View[] activeViews = mActiveViews;
            final boolean hasListener = mRecyclerListener != null;
            final boolean multipleScraps = mViewTypeCount > 1;

            ArrayList<View> scrapViews = mCurrentScrap;
            final int count = activeViews.length;
            for (int i = count - 1; i >= 0; i--) {
                final View victim = activeViews[i];
                if (victim != null) {
                    final LayoutParams lp
                            = (LayoutParams) victim.getLayoutParams();
                    final int whichScrap = lp.viewType;

                    activeViews[i] = null;

                    if (victim.hasTransientState()) {
                        // Store views with transient state for later use.
                        victim.dispatchStartTemporaryDetach();

                        if (mAdapter != null && mAdapterHasStableIds) {
                            if (mTransientStateViewsById == null) {
                                mTransientStateViewsById = new LongSparseArray<View>();
                            }
                            long id = mAdapter.getItemId(mFirstActivePosition + i);
                            mTransientStateViewsById.put(id, victim);
                        } else if (!mDataChanged) {
                            if (mTransientStateViews == null) {
                                mTransientStateViews = new SparseArray<View>();
                            }
                            mTransientStateViews.put(mFirstActivePosition + i, victim);
                        } else if (whichScrap != ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                            // The data has changed, we can't keep this view.
                            removeDetachedView(victim, false);
                        }
                    } else if (!shouldRecycleViewType(whichScrap)) {
                        // Discard non-recyclable views except headers/footers.
                        if (whichScrap != ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
                            removeDetachedView(victim, false);
                        }
                    } else {
                        // Store everything else on the appropriate scrap heap.
                        if (multipleScraps) {
                            scrapViews = mScrapViews[whichScrap];
                        }

                        lp.scrappedFromPosition = mFirstActivePosition + i;
                        removeDetachedView(victim, false);
                        scrapViews.add(victim);

                        if (hasListener) {
                            mRecyclerListener.onMovedToScrapHeap(victim);
                        }
                    }
                }
            }
            pruneScrapViews();
        }

        /**
         * At the end of a layout pass, all temp detached views should either be re-attached or
         * completely detached. This method ensures that any remaining view in the scrap list is
         * fully detached.
         */
        void fullyDetachScrapViews() {
            final int viewTypeCount = mViewTypeCount;
            final ArrayList<View>[] scrapViews = mScrapViews;
            for (int i = 0; i < viewTypeCount; ++i) {
                final ArrayList<View> scrapPile = scrapViews[i];
                for (int j = scrapPile.size() - 1; j >= 0; j--) {
                    final View view = scrapPile.get(j);
                    if (view.isTemporarilyDetached()) {
                        removeDetachedView(view, false);
                    }
                }
            }
        }

        /**
         * Makes sure that the size of mScrapViews does not exceed the size of
         * mActiveViews, which can happen if an adapter does not recycle its
         * views. Removes cached transient state views that no longer have
         * transient state.
         */
        private void pruneScrapViews() {
            final int maxViews = mActiveViews.length;
            final int viewTypeCount = mViewTypeCount;
            final ArrayList<View>[] scrapViews = mScrapViews;
            for (int i = 0; i < viewTypeCount; ++i) {
                final ArrayList<View> scrapPile = scrapViews[i];
                int size = scrapPile.size();
                while (size > maxViews) {
                    scrapPile.remove(--size);
                }
            }

            final SparseArray<View> transViewsByPos = mTransientStateViews;
            if (transViewsByPos != null) {
                for (int i = 0; i < transViewsByPos.size(); i++) {
                    final View v = transViewsByPos.valueAt(i);
                    if (!v.hasTransientState()) {
                        removeDetachedView(v, false);
                        transViewsByPos.removeAt(i);
                        i--;
                    }
                }
            }

            final LongSparseArray<View> transViewsById = mTransientStateViewsById;
            if (transViewsById != null) {
                for (int i = 0; i < transViewsById.size(); i++) {
                    final View v = transViewsById.valueAt(i);
                    if (!v.hasTransientState()) {
                        removeDetachedView(v, false);
                        transViewsById.removeAt(i);
                        i--;
                    }
                }
            }
        }

        /**
         * Puts all views in the scrap heap into the supplied list.
         */
        void reclaimScrapViews(List<View> views) {
            if (mViewTypeCount == 1) {
                views.addAll(mCurrentScrap);
            } else {
                final int viewTypeCount = mViewTypeCount;
                final ArrayList<View>[] scrapViews = mScrapViews;
                for (int i = 0; i < viewTypeCount; ++i) {
                    final ArrayList<View> scrapPile = scrapViews[i];
                    views.addAll(scrapPile);
                }
            }
        }

        /**
         * Updates the cache color hint of all known views.
         *
         * @param color The new cache color hint.
         */
        void setCacheColorHint(int color) {
            if (mViewTypeCount == 1) {
                final ArrayList<View> scrap = mCurrentScrap;
                final int scrapCount = scrap.size();
                for (int i = 0; i < scrapCount; i++) {
                    scrap.get(i).setDrawingCacheBackgroundColor(color);
                }
            } else {
                final int typeCount = mViewTypeCount;
                for (int i = 0; i < typeCount; i++) {
                    final ArrayList<View> scrap = mScrapViews[i];
                    final int scrapCount = scrap.size();
                    for (int j = 0; j < scrapCount; j++) {
                        scrap.get(j).setDrawingCacheBackgroundColor(color);
                    }
                }
            }
            // Just in case this is called during a layout pass
            final View[] activeViews = mActiveViews;
            final int count = activeViews.length;
            for (int i = 0; i < count; ++i) {
                final View victim = activeViews[i];
                if (victim != null) {
                    victim.setDrawingCacheBackgroundColor(color);
                }
            }
        }

        private View retrieveFromScrap(ArrayList<View> scrapViews, int position) {
            final int size = scrapViews.size();
            if (size > 0) {
                // See if we still have a view for this position or ID.
                // Traverse backwards to find the most recently used scrap view
                for (int i = size - 1; i >= 0; i--) {
                    final View view = scrapViews.get(i);
                    final LayoutParams params =
                            (LayoutParams) view.getLayoutParams();

                    if (mAdapterHasStableIds) {
                        final long id = mAdapter.getItemId(position);
                        if (id == params.itemId) {
                            return scrapViews.remove(i);
                        }
                    } else if (params.scrappedFromPosition == position) {
                        final View scrap = scrapViews.remove(i);
                        clearScrapForRebind(scrap);
                        return scrap;
                    }
                }
                final View scrap = scrapViews.remove(size - 1);
                clearScrapForRebind(scrap);
                return scrap;
            } else {
                return null;
            }
        }

        private void clearScrap(final ArrayList<View> scrap) {
            final int scrapCount = scrap.size();
            for (int j = 0; j < scrapCount; j++) {
                removeDetachedView(scrap.remove(scrapCount - 1 - j), false);
            }
        }

        private void clearScrapForRebind(View view) {
            view.setAccessibilityDelegate(null);
        }
    }

    /**
     * AbsListView extends LayoutParams to provide a place to hold the view type.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        /**
         * View type for this view, as returned by
         * {@link android.widget.Adapter#getItemViewType(int) }
         */
        int viewType;

        /**
         * When this boolean is set, the view has been added to the AbsListView
         * at least once. It is used to know whether headers/footers have already
         * been added to the list view and whether they should be treated as
         * recycled views or not.
         */
        boolean recycledHeaderFooter;

        /**
         * When an AbsListView is measured with an AT_MOST measure spec, it needs
         * to obtain children views to measure itself. When doing so, the children
         * are not attached to the window, but put in the recycler which assumes
         * they've been attached before. Setting this flag will force the reused
         * view to be attached to the window rather than just attached to the
         * parent.
         */
        boolean forceAdd;

        /**
         * The position the view was removed from when pulled out of the
         * scrap heap.
         *
         * @hide
         */
        int scrappedFromPosition;

        /**
         * The ID the view represents
         */
        long itemId = -1;

        /**
         * Whether the adapter considers the item enabled.
         */
        boolean isEnabled;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.viewType = viewType;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

}
