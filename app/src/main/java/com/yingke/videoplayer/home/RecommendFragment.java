package com.yingke.videoplayer.home;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yingke.videoplayer.R;
import com.yingke.videoplayer.base.BaseFragment;
import com.yingke.videoplayer.bean.VideoBean;
import com.yingke.videoplayer.widget.ListIjkVideoView;

/**
 * 功能：
 * </p>
 * <p>Copyright xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019/9/23
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class RecommendFragment extends BaseFragment {

    private FrameLayout mContainer;

    public static RecommendFragment newInstance(){
        return new RecommendFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.frag_recommend;
    }

    @Override
    protected void initView(View rootView) {
        mContainer = rootView.findViewById(R.id.container);
    }

    @Override
    protected void initData() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ListIjkVideoView ijkVideoView = new ListIjkVideoView(getContext());
        mContainer.addView(ijkVideoView, params);
        VideoBean videoBean = new VideoBean();
        ijkVideoView.setVideoOnline(videoBean);
    }


}
