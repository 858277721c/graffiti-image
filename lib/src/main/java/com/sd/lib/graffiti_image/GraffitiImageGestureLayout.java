package com.sd.lib.graffiti_image;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class GraffitiImageGestureLayout extends FrameLayout
{
    public GraffitiImageGestureLayout(Context context)
    {
        super(context);
    }

    public GraffitiImageGestureLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GraffitiImageGestureLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    private GraffitiImageView mGraffitiImageView;
    private GraffitiImageView.Group mCurrentGroup;

    /** 是否限制触摸边界 */
    private boolean mLimitTouchBounds = true;
    private Callback mCallback;

    /**
     * 设置回调对象
     *
     * @param callback
     */
    public void setCallback(Callback callback)
    {
        mCallback = callback;
    }

    /**
     * 设置是否限制触摸边界
     *
     * @param limit
     */
    public void setLimitTouchBounds(boolean limit)
    {
        mLimitTouchBounds = limit;
    }

    public GraffitiImageView getGraffitiImageView()
    {
        return mGraffitiImageView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
            processActionDown(event);
        } else if (action == MotionEvent.ACTION_MOVE)
        {
            processActionMove(event);
        } else if (action == MotionEvent.ACTION_UP)
        {
            mCurrentGroup = null;
        }

        super.onTouchEvent(event);
        return true;
    }

    /**
     * 处理触摸事件{@link MotionEvent#ACTION_DOWN}
     *
     * @param event
     */
    private void processActionDown(MotionEvent event)
    {
        if (!mCallback.canAddItem())
            return;

        final GraffitiImageView.Group group = mCallback.createGroup();
        if (group == null)
            return;

        final GraffitiImageView.Item item = new GraffitiImageView.Item(event.getX(), event.getY());

        group.addItem(item);
        mCurrentGroup = group;
        mCallback.onItemCreate(item);

        mGraffitiImageView.addGroup(group);
        mGraffitiImageView.showGraffiti();
    }

    /**
     * 处理触摸事件{@link MotionEvent#ACTION_MOVE}
     *
     * @param event
     */
    private void processActionMove(MotionEvent event)
    {
        final GraffitiImageView.Group group = mCurrentGroup;
        if (group == null)
            return;

        final GraffitiImageView.Item lastItem = group.lastItem();
        if (lastItem == null)
        {
            mCurrentGroup = null;
            return;
        }

        final float x = event.getX();
        final float y = event.getY();
        if (mLimitTouchBounds)
        {
            if (x < 0 || x > getWidth())
                return;

            if (y < 0 || y > getHeight())
                return;
        }

        final float lastX = lastItem.getX();
        final float lastY = lastItem.getY();

        final int stepX = lastItem.getHorizontalSpacing();
        final int stepY = lastItem.getVerticalSpacing();

        final List<int[]> listPoint = getPoints(lastX, lastY, x, y, stepX, stepY);
        if (listPoint == null || listPoint.isEmpty())
            return;

        for (int[] point : listPoint)
        {
            if (mCallback.canAddItem())
            {
                final GraffitiImageView.Item item = new GraffitiImageView.Item(point[0], point[1]);
                group.addItem(item);
                mCallback.onItemCreate(item);

                mGraffitiImageView.showGraffiti();
            } else
            {
                mCurrentGroup = null;
                break;
            }
        }
    }

    /**
     * 返回两个坐标之间需要生成的点
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param stepX
     * @param stepY
     * @return
     */
    private List<int[]> getPoints(float x1, float y1, float x2, float y2, int stepX, int stepY)
    {
        if (stepX <= 0 || stepY <= 0)
            return null;

        final float dx = x2 - x1;
        final float dy = y2 - y1;
        final float absDx = Math.abs(dx);
        final float absDy = Math.abs(dy);
        if (absDx <= 0 && absDy <= 0)
            return null;

        final int countX = (int) (absDx / stepX);
        final int countY = (int) (absDy / stepY);
        final int countMax = Math.max(countX, countY);
        if (countMax <= 0)
            return null;

        if (countX > countY)
        {
            stepY = (int) Math.abs(dy / countMax);
        } else if (countY > countX)
        {
            stepX = (int) Math.abs(dx / countMax);
        }

        if (dx == 0)
            stepX = 0;

        if (dy == 0)
            stepY = 0;

        final List<int[]> listPoint = new ArrayList<>();
        for (int i = 0; i < countMax; i++)
        {
            final int delta = (i + 1);
            final int deltaX = delta * stepX;
            final int deltaY = delta * stepY;

            int finalX = (int) x1;
            if (deltaX > 0)
            {
                finalX = dx > 0 ? (finalX + deltaX) : (finalX - deltaX);
            }

            int finalY = (int) y1;
            if (deltaY > 0)
            {
                finalY = dy > 0 ? (finalY + deltaY) : (finalY - deltaY);
            }

            final int[] item = new int[]{finalX, finalY};
            listPoint.add(item);
        }
        return listPoint;
    }

    @Override
    public void onViewAdded(View child)
    {
        super.onViewAdded(child);
        if (getChildCount() > 1)
            throw new RuntimeException("too much child");

        if (child instanceof GraffitiImageView)
            mGraffitiImageView = (GraffitiImageView) child;
    }

    @Override
    public void onViewRemoved(View child)
    {
        super.onViewRemoved(child);
        if (mGraffitiImageView == child)
            throw new RuntimeException("child can not be remove");
    }

    public static abstract class Callback
    {
        /**
         * 是否可以添加Item
         *
         * @return
         */
        public boolean canAddItem()
        {
            return true;
        }

        public abstract GraffitiImageView.Group createGroup();

        public void onItemCreate(GraffitiImageView.Item item)
        {
        }
    }
}
