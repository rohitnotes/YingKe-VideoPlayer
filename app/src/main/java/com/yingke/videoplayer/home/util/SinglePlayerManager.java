package com.yingke.videoplayer.home.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.yingke.player.java.IVideoBean;
import com.yingke.player.java.controller.MediaController;
import com.yingke.videoplayer.home.adapter.ListVideoAdapter;
import com.yingke.videoplayer.home.item.ListVideoVH;
import com.yingke.videoplayer.home.landscape.LandListVideoAdapter;
import com.yingke.videoplayer.home.landscape.LandListVideoVH;
import com.yingke.videoplayer.home.pip.SuspensionView;
import com.yingke.videoplayer.home.player.ListIjkAdMediaController;
import com.yingke.videoplayer.widget.BaseListVideoView;
import com.yingke.widget.base.BaseRecycleViewAdapter;
import com.yingke.widget.pulltorefresh.wrapper.HeaderAndFooterWrapper;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：单视频管理器
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-23
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class SinglePlayerManager {

    public static final String TAG = "SinglePlayerManager";


    public static SinglePlayerManager getInstance() {
        return INSTANCE.instance;
    }

    private static class INSTANCE{
        public static SinglePlayerManager instance = new SinglePlayerManager();
    }

    private RecyclerView mRecyclerView;
    private IVideoBean mCurrentVideoBean;
    private BaseListVideoView mCurrentListVideoView;

    /**
     * 绑定列表
     * @param recyclerView
     */
    public void attachRecycleView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        if (recyclerView == null) {
            throw new IllegalArgumentException("recyclerView in manager must nt be null!");
        }
    }

    /**
     * @param bean
     * @param listVideoView
     */
    public void attachVideoPlayer(IVideoBean bean, BaseListVideoView listVideoView) {

        if (isEnableAndShowing()) {
            stopFloatWindow(true);
        }
        // 上一个播放器释放
        releaseVideoPlayer();
        mCurrentVideoBean = bean;
        mCurrentListVideoView = listVideoView;
    }

    /**
     * 移除并释放播放器
     */
    public void releaseVideoPlayer() {
        if (mCurrentListVideoView != null) {

            if (isEnableAndShowing() ) {
                return;
            }

            removePlayerAndRelease();
            mCurrentListVideoView = null;
            mCurrentVideoBean = null;
        }
    }

    /**
     * 移除并释放播放器
     */
    private void removePlayerAndRelease() {
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
     * 移除但不释放 播放器，恢复到封面状态
     */
    public void removePlayerNotRelease() {
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
        if (itemView == null || mRecyclerView == null) {
            return;
        }
        RecyclerView.ViewHolder holder = mRecyclerView.findContainingViewHolder(itemView);
        if (holder instanceof ListVideoAdapter.ListVideoHolder) {
            // 竖屏播放器时 显示空闲页
            ListVideoVH listVideoVH = ((ListVideoAdapter.ListVideoHolder) holder).getListVideoVH();
            listVideoVH.showIdleView(releasePlayer);
        } else if (holder instanceof LandListVideoAdapter.LandVideoViewHolder){
            // 横屏播放器时 显示空闲页
            LandListVideoVH landListVideoVH = ((LandListVideoAdapter.LandVideoViewHolder) holder).getLandListVideoVH();
            landListVideoVH.showIdleView(true);
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
     * @return 当前数据的位置
     */
    public int getCurrentPos() {
        int position = -1;

        if (mCurrentVideoBean != null && mRecyclerView != null) {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter instanceof BaseRecycleViewAdapter) {
                position = ((BaseRecycleViewAdapter) adapter).getPosition(mCurrentVideoBean);
            } else if (adapter instanceof HeaderAndFooterWrapper) {
                RecyclerView.Adapter innerAdapter  = ((HeaderAndFooterWrapper) adapter).getInnerAdapter();
                if (innerAdapter instanceof BaseRecycleViewAdapter) {
                    position = ((BaseRecycleViewAdapter) innerAdapter).getPosition(mCurrentVideoBean);
                }
            }
        }
        return position;
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

    //#### 画中画（悬浮窗/小屏）仅用于竖屏播放器，横屏时许关闭！

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
     * 启动画中画 仅用于竖屏播放器，横屏时许关闭！
     * @param isPipEnable
     */
    public void enablePip( boolean isPipEnable){
        enablePip(isPipEnable, PIP_TYPE_FLOAT_WINDOW);
    }

    /**
     * 启动画中画 仅用于竖屏播放器，横屏时许关闭！
     * @param isPipEnable
     * @param pipType
     */
    public void enablePip( boolean isPipEnable, int pipType) {
        mIsPipEnable = isPipEnable;
        mPipType = pipType;
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
        if (!mIsPipEnable || mIsShowing || mCurrentListVideoView == null || mRecyclerView == null) {
            return;
        }
        mIsShowing = true;

        // 移除但不释放 播放器
        removePlayerNotRelease();

        // 显示画中画控制器
        MediaController controller = mCurrentListVideoView.getControllerView();
        if (controller instanceof ListIjkAdMediaController) {
            ((ListIjkAdMediaController) controller).showPipController();
        }

        if (isSuspensionType()) {
            // 悬浮窗添加

            if (mSuspensionView == null) {
                mSuspensionView = new SuspensionView(mRecyclerView.getContext());
            }

            mSuspensionView.addView(mCurrentListVideoView);
            mSuspensionView.attachToWindow();
        } else {
            // contentView 添加
            mCurrentListVideoView.startTinyScreen();
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
     * 1，手动点叉叉，关闭
     * 2，点击不同的视频资源，关闭
     * @param releasePlayer 需要回到列表-false
     */
    public void stopFloatWindow(boolean releasePlayer) {
        if (!mIsPipEnable || !mIsShowing || mCurrentListVideoView == null) {
            return;
        }
        mIsShowing = false;
        if (releasePlayer) {
            // 完全销毁
            mCurrentListVideoView.release();
            // 修改成最初类型
            mCurrentVideoBean.setCurrentType(mCurrentVideoBean.getFirstType());

            if (isSuspensionType()) {
                // 悬浮窗移除
                if (mSuspensionView != null) {
                    mSuspensionView.removeAllViews();
                    mSuspensionView.detachFromWindow();
                }

            } else {
                // contentView 移除
                mCurrentListVideoView.stopTinyScreen();
            }

            mCurrentListVideoView = null;
            mCurrentVideoBean = null;
        } else {
            // 不销毁，回到列表中

            MediaController controller = mCurrentListVideoView.getControllerView();

            if (controller instanceof ListIjkAdMediaController) {
                switch (mCurrentVideoBean.getCurrentType()) {
                    case IVideoBean.TYPE_AD:
                        // 广告
                        ((ListIjkAdMediaController) controller).showAdController();
                        break;
                    case IVideoBean.TYPE_REAL:
                        // 源视频
                        ((ListIjkAdMediaController) controller).showNormalController();
                        break;
                }
            }

            if (isSuspensionType()) {
                // 悬浮窗移除
                mSuspensionView.removeAllViews();
                mSuspensionView.detachFromWindow();
            } else {
                // contentView 移除
                mCurrentListVideoView.stopTinyScreen();
            }
        }
    }

    /**
     * @return ture 悬浮窗类型
     */
    public boolean isSuspensionType() {
        return mPipType == PIP_TYPE_FLOAT_WINDOW;
    }

    /**
     * 仅重置数据
     */
    public void resetData() {
        mCurrentListVideoView = null;
        mCurrentVideoBean = null;
        mRecyclerView = null;
    }


    /**
     * 重置数据 和 悬浮窗设置
     */
    public void reset() {
        mIsPipEnable = false;
        mIsShowing = false;
        mSuspensionView = null;
        mPipType = -1;
    }

}
