package com.yingke.videoplayer.home.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.yingke.player.java.IVideoBean;
import com.yingke.player.java.PlayerUtils;
import com.yingke.player.java.controller.MediaController;
import com.yingke.videoplayer.home.adapter.ListVideoAdapter;
import com.yingke.videoplayer.home.item.ListVideoVH;
import com.yingke.videoplayer.home.pip.SuspensionView;
import com.yingke.videoplayer.home.player.ListIjkMediaController;
import com.yingke.videoplayer.util.DeviceUtil;
import com.yingke.videoplayer.util.PlayerUtil;
import com.yingke.videoplayer.widget.BaseListVideoView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：单视频管理器
 * </p>
 * <p>Copyright corp.netease.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-23
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class SinglePlayerManager {


    public static SinglePlayerManager getInstance() {
        return INSTANCE.instance;
    }

    private static class INSTANCE{
        public static SinglePlayerManager instance = new SinglePlayerManager();
    }

    private RecyclerView mRecyclerView;
    private IVideoBean mCurrentVideoBean;
    private BaseListVideoView mCurrentListVideoView;

    public void attachRecycleView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        if (recyclerView == null) {
            throw new IllegalArgumentException("recyclerView in manager must nt be null!");
        }
    }

    public void attachVideoPlayer(IVideoBean bean, BaseListVideoView listVideoView) {

        if (isEnableAndShowing()) {
            stopFloatWindow(true);
        }
        releaseVideoPlayer();
        mCurrentVideoBean = bean;
        mCurrentListVideoView = listVideoView;
    }

    /**
     * 释放播放器
     */
    public void releaseVideoPlayer() {
        if (mCurrentListVideoView != null) {

            if (isEnableAndShowing() ) {
                return;
            }

            removePlayer();
            mCurrentListVideoView = null;
            mCurrentVideoBean = null;
        }
    }

    /**
     * 移除播放器
     */
    private void removePlayer() {
        if (mCurrentListVideoView == null) {
            return;
        }
        ViewParent parent = mCurrentListVideoView.getParent();
        if (parent instanceof FrameLayout) {
            ViewParent itemView = parent.getParent();
            if (itemView != null) {
                showIdleView((View) itemView, true);
            }
        }
    }

    /**
     * 移除但不释放 播放器
     */
    private void removePlayerNotRelease() {
        if (mCurrentListVideoView == null) {
            return;
        }
        ViewParent videoParent = mCurrentListVideoView.getParent();
        if (videoParent instanceof ViewGroup) {
            ViewParent itemView = videoParent.getParent();
            if (itemView != null) {
                showIdleView((View) itemView, false);
            }
        }
    }

    /**
     * 显示空闲页
     * @param itemView
     * @param releasePlayer
     */
    private void showIdleView(View itemView, boolean releasePlayer) {
        if (itemView == null) {
            return;
        }
        RecyclerView.ViewHolder holder = mRecyclerView.findContainingViewHolder(itemView);
        if (holder instanceof ListVideoAdapter.ListVideoHolder) {
            ListVideoVH listVideoVH = ((ListVideoAdapter.ListVideoHolder) holder).getListVideoVH();
            listVideoVH.showIdleView(releasePlayer);
        }
    }

    /**
     * @return 当前播放数据
     */
    public IVideoBean getCurrentVideoBean() {
        return mCurrentVideoBean;
    }

    /**
     * @return 当前播放器
     */
    public BaseListVideoView getCurrentListVideoView() {
        return mCurrentListVideoView;
    }

    /**
     * 播放
     */
    public void onResume() {
        if (mCurrentListVideoView != null
                && mCurrentListVideoView.isInPlaybackState()
                && !mCurrentListVideoView.isPlaying()) {
            mCurrentListVideoView.start();
        }
    }

    /**
     * 暂停
     */
    public void onPause(){
        if (mCurrentListVideoView != null && mCurrentListVideoView.isPlaying()) {
            mCurrentListVideoView.pause();
        }
    }

    //#### 画中画（悬浮窗/小屏）

    public static final int PIP_TYPE_FLOAT_WINDOW = 0;
    public static final int PIP_TYPE_TINY_SCREEN = 1;

    // 类型
    private int mPipType = -1;
    // 使能
    private boolean mIsPipEnable = false;
    // 显示
    private boolean mIsShowing = false;
    // 悬浮窗视图
    private SuspensionView mSuspensionView;


    /**
     * 启动画中画
     * @param context
     * @param isPipEnable
     */
    public void enablePip(Context context, boolean isPipEnable){
        enablePip(context, isPipEnable, PIP_TYPE_TINY_SCREEN);
    }

    /**
     * 启动画中画
     * @param context
     * @param isPipEnable
     */
    public void enablePip(Context context, boolean isPipEnable, int pipType) {
        mIsPipEnable = isPipEnable;
        mPipType = pipType;
        if (isSuspensionType()) {
            mSuspensionView = new SuspensionView(context);
        }
    }

    public boolean isPipEnable() {
        return mIsPipEnable;
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * @return 画中画显示
     */
    public boolean isEnableAndShowing(){
        return isPipEnable() && isShowing();
    }

    /**
     * 开始画中画
     */
    public void startFloatWindow() {
        if (!mIsPipEnable || mIsShowing || mCurrentListVideoView == null) {
            return;
        }
        mIsShowing = true;

        removePlayerNotRelease();
        MediaController controller = mCurrentListVideoView.getControllerView();
        if (controller instanceof ListIjkMediaController) {
            ((ListIjkMediaController) controller).showPipController();
        }

        if (isSuspensionType()) {
            // 悬浮窗添加
            mSuspensionView.addView(mCurrentListVideoView);
            mSuspensionView.attachToWindow();
        } else {
            // contentView添加
            Activity activity = PlayerUtils.scanForActivity(mRecyclerView.getContext());
            if (activity == null){
                return;
            }
            // 实际是添加到contentView上
            ViewGroup contentView = activity.findViewById(android.R.id.content);
            int width = PlayerUtils.getScreenWidth(mRecyclerView.getContext()) / 2;
            int height = width * 9 / 16;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            params.gravity = Gravity.TOP | Gravity.END;
            params.topMargin = DeviceUtil.getStatusBarHeight(mRecyclerView.getContext());
            // 添加到ContentView
            contentView.addView(mCurrentListVideoView, params);
        }

    }

    /**
     * 关闭画中画
     */
    public void stopFloatWindow() {
        stopFloatWindow(true);
    }

    /**
     * 关闭画中画
     */
    public void stopFloatWindow(boolean releasePlayer) {
        if (!mIsPipEnable || !mIsShowing || mCurrentListVideoView == null) {
            return;
        }
        mIsShowing = false;
        if (releasePlayer) {
            // 完全销毁
            mCurrentListVideoView.release();
            if (isSuspensionType()) {
                // 悬浮窗移除
                mSuspensionView.removeAllViews();
                mSuspensionView.detachFromWindow();
            } else {
                // contentView 移除
                Activity activity = PlayerUtils.scanForActivity(mRecyclerView.getContext());
                if (activity == null){
                    return;
                }
                ViewGroup contentView = activity.findViewById(android.R.id.content);
                // 移除
                contentView.removeView(mCurrentListVideoView);
            }

            mCurrentListVideoView = null;
            mCurrentVideoBean = null;
        } else {
            // 不销毁，回到列表中

            MediaController controller = mCurrentListVideoView.getControllerView();
            if (controller instanceof ListIjkMediaController) {
                ((ListIjkMediaController) controller).showNormalController();
            }

            if (isSuspensionType()) {
                // 悬浮窗移除
                mSuspensionView.removeAllViews();
                mSuspensionView.detachFromWindow();
            } else {
                // contentView 移除
                Activity activity = PlayerUtils.scanForActivity(mRecyclerView.getContext());
                if (activity == null){
                    return;
                }
                ViewGroup contentView = activity.findViewById(android.R.id.content);
                // 移除
                contentView.removeView(mCurrentListVideoView);
            }
        }
    }

    /**
     * @return ture 悬浮窗类型
     */
    public boolean isSuspensionType() {
        return mPipType == PIP_TYPE_FLOAT_WINDOW;
    }

    public void reset() {
        mCurrentListVideoView = null;
        mCurrentVideoBean = null;
        mRecyclerView = null;
        mIsPipEnable = false;
        mIsShowing = false;
        mSuspensionView = null;
        mPipType = -1;
    }

}
