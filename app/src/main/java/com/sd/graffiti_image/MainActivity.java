package com.sd.graffiti_image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sd.graffiti_image.databinding.ActivityMainBinding;
import com.sd.lib.graffiti_image.GraffitiImageGestureLayout;
import com.sd.lib.graffiti_image.GraffitiImageView;
import com.sd.lib.utils.FImageUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBitmap = FImageUtil.getBitmapFromRes(this, R.drawable.a);
        mBinding.viewGraffitiGesture.setCallback(new GraffitiImageGestureLayout.Callback()
        {
            @Override
            public GraffitiImageView.Group createGroup()
            {
                final GraffitiImageView.Group group = new GraffitiImageView.Group(mBitmap);
                group.setItemSpacing(50);
                return group;
            }

            @Override
            public boolean canAddItem()
            {
                final int itemCount = mBinding.viewGraffitiGesture.getGraffitiImageView().getItemCount();
                Log.i(TAG, "canAddItem count:" + itemCount);
                if (itemCount >= 100)
                {
                    Toast.makeText(MainActivity.this, "too much item", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return super.canAddItem();
            }
        });

        mBinding.viewGraffitiGesture.getGraffitiImageView().setGroupCountCallback(new GraffitiImageView.GroupCountCallback()
        {
            @Override
            public void onGroupCountChanged(int count)
            {
                Log.i(TAG, "onGroupCountChanged:" + count);
            }
        });
        mBinding.viewGraffitiGesture.getGraffitiImageView().setAnimatorCallback(new GraffitiImageView.AnimatorCallback()
        {
            @Override
            public void onAnimationStart()
            {
                Log.i(TAG, "onAnimationStart");
            }

            @Override
            public void onAnimationEnd()
            {
                Log.i(TAG, "onAnimationEnd");
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        if (v == mBinding.btnCancel)
        {
            mBinding.viewGraffitiGesture.getGraffitiImageView().removeLastGroup();
            mBinding.viewGraffitiGesture.getGraffitiImageView().showGraffiti();
        } else if (v == mBinding.btnAnimatorGroup)
        {
            mBinding.viewGraffitiGesture.getGraffitiImageView().getGraffitiAnimator().setDuration(null);
            mBinding.viewGraffitiGesture.getGraffitiImageView().getGraffitiAnimator().start();
        } else if (v == mBinding.btnAnimatorDuration)
        {
            mBinding.viewGraffitiGesture.getGraffitiImageView().getGraffitiAnimator().setDuration(5 * 1000L);
            mBinding.viewGraffitiGesture.getGraffitiImageView().getGraffitiAnimator().start();
        }
    }
}