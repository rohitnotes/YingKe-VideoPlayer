package com.yingke.videoplayer.home;

import com.yingke.videoplayer.home.bean.ListVideoData;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.xxx.com 2019 All right reserved </p>
 *
 * @author tuke 时间 2019-10-26
 * @email
 * <p>
 * 最后修改人：无
 * <p>
 */
public class ListVideoStickEvent {

    public ListVideoData mListVideoData;
    public boolean isStickAd;

    public ListVideoStickEvent(ListVideoData listVideoData, boolean isStickAd) {
        mListVideoData = listVideoData;
        this.isStickAd = isStickAd;
    }
}
