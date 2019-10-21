package com.yingke.videoplayer.tiktok;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yingke.videoplayer.R;
import com.yingke.videoplayer.home.BaseRecycleViewAdapter;
import com.yingke.videoplayer.tiktok.bean.ListTiktokBean;
import com.yingke.videoplayer.util.FrescoUtil;
import com.yingke.videoplayer.util.StringUtil;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-21
 * @email 13661091407@163.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class TikTokAdapter extends BaseRecycleViewAdapter<ListTiktokBean> {

    public TikTokAdapter(Context context, List<ListTiktokBean> mDataList) {
        super(context, mDataList);
    }

    @Override
    public int getConvertViewResId(int itemViewType) {
        return R.layout.frag_tiktok_list_item;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(int viewType, View rootView) {
        return new TiktokViewHolder(rootView);
    }

    public class TiktokViewHolder extends BaseViewHolder<ListTiktokBean>{

        // 视频容器
        private FrameLayout mVideoContainer;
        // 封面
        private SimpleDraweeView mDraweeView;
        // 头像
        private SimpleDraweeView mUserAvatar;

        // 点赞
        private LinearLayout mVoteView;
        private ImageView mVoteImage;
        private TextView mVoteCount;
        // 评论
        private LinearLayout mMsgView;
        private TextView mMsgCount;
        // 分享
        private LinearLayout mShareView;
        private TextView mShareCount;
        // 用户名
        private TextView mUserName;
        private TextView mDescription;
        private TextView mMusic;

        private TextView mCreateTime;
        private ImageView mPlayBtn;

        private int position;
        private ListTiktokBean mTiktokBean;

        public TiktokViewHolder(View itemView) {
            super(itemView);
            mVideoContainer = itemView.findViewById(R.id.tiktok_video_container);
            mDraweeView = itemView.findViewById(R.id.tiktok_image);
            mUserAvatar = itemView.findViewById(R.id.tiktok_user_avatar);
            mVoteView = itemView.findViewById(R.id.tiktok_vote_view);
            mVoteImage = itemView.findViewById(R.id.tiktok_vote_image);;
            mVoteCount = itemView.findViewById(R.id.tiktok_vote_count);
            mMsgView = itemView.findViewById(R.id.tiktok_msg_view);
            mMsgCount = itemView.findViewById(R.id.tiktok_msg_count);
            mShareView = itemView.findViewById(R.id.tiktok_share_view);
            mShareCount = itemView.findViewById(R.id.tiktok_share_count);
            mUserName = itemView.findViewById(R.id.tiktok_username);
            mDescription = itemView.findViewById(R.id.tiktok_description);
            mMusic = itemView.findViewById(R.id.tiktok_music);
            mCreateTime = itemView.findViewById(R.id.tiktok_time);
            mPlayBtn = itemView.findViewById(R.id.tiktok_play_icon);
        }

        @Override
        public void onRefreshData(int position, ListTiktokBean data) {
            if (data == null) {
                return;
            }
            this.position = position;
            mTiktokBean = data;

            FrescoUtil.displayImage(mDraweeView, data.getCoverImage());
            FrescoUtil.displayImage(mUserAvatar, data.getUserAvatar());
            if (data.isWatched()) {
                mVoteImage.setImageResource(R.mipmap.icon_heart_red);
            } else {
                mVoteImage.setImageResource(R.mipmap.icon_heart);
            }
            mVoteCount.setText(StringUtil.countFormat(data.getVoteCount()));
            mMsgCount.setText(StringUtil.countFormat(data.getCommentCount()));
            mShareCount.setText(StringUtil.countFormat(data.getShareCount()));
            mUserName.setText(data.getUserName());
            mDescription.setText(data.getDescription());
            mMusic.setText(data.getMusic());
            String time = StringUtil.getTimeStrForUsrMsg(data.getCreateTime());
            if (!TextUtils.isEmpty(time)) {
                mCreateTime.setVisibility(View.VISIBLE);
                mCreateTime.setText(time);
            } else {
                mCreateTime.setVisibility(View.GONE);
            }
        }
    }

    private OnTiktokListener mListener;

    public void setListener(OnTiktokListener listener) {
        mListener = listener;
    }

    /**
     * 监听器
     */
    public interface OnTiktokListener{
        /**
         * 暂停播放,恢复播放
         */
        void doPauseResume();


        /**
         * 点赞点击
         */
        void onVoteClick();

        /**
         * 评论点击
         */
        void onMsgClick();

        /**
         * 分享点击
         */
        void onShareClick();

        /**
         * 个人主页
         */
        void onProfiled(ListTiktokBean tiktokBean);
    }
}
