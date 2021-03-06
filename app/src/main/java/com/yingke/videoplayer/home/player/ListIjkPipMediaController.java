package com.yingke.videoplayer.home.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yingke.player.java.controller.MediaController;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.util.SinglePlayerManager;

/**
 * 功能：带画中画 的控制器
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-25
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ListIjkPipMediaController extends MediaController {

    private RelativeLayout mPipView;
    private ImageView mFullView;
    private ImageView mCloseView;

    public ListIjkPipMediaController(Context context) {
        super(context);
    }

    public ListIjkPipMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListIjkPipMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.list_media_controller_pip;
    }

    @Override
    protected void initView() {
        super.initView();
        mPipView =  mRootView.findViewById(R.id.controller_content_pip);
        mFullView =  mRootView.findViewById(R.id.controller_port_pip_full);
        mCloseView =  mRootView.findViewById(R.id.controller_port_pip_close);
        mCloseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SinglePlayerManager.getInstance().stopFloatWindow();
            }
        });
        mFullView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mPipView.setVisibility(GONE);
    }

    /**
     * 普通控制器
     */
    @Override
    public void showNormalController() {
        super.showNormalController();
        hidePipController();
    }

    /**
     * 画中画控制器
     */
    public void showPipController() {
        hideNormalController();
        mPipView.setVisibility(VISIBLE);
    }

    /**
     * 隐藏画中画控制器
     */
    public void hidePipController(){
        mPipView.setVisibility(GONE);
    }
}
