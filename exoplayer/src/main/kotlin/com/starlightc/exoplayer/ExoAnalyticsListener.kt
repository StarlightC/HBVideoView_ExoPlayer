package com.starlightc.exoplayer

import android.view.Surface
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.LoadEventInfo
import com.google.android.exoplayer2.source.MediaLoadData
import com.starlightc.video.core.Constant
import com.starlightc.video.core.SimpleLogger
import com.starlightc.video.core.infomation.PlayInfo
import com.starlightc.video.core.infomation.PlayerState
import com.starlightc.video.core.infomation.VideoSize
import java.io.IOException

/**
 * @author StarlightC
 * @since 2022/5/31
 *
 * TODO: description
 */
class ExoAnalyticsListener(val player: ExoPlayer): AnalyticsListener {

    /**
     * Called when a media source started loading data.
     *
     * @param eventTime The event time.
     * @param loadEventInfo The [LoadEventInfo] defining the load event.
     * @param mediaLoadData The [MediaLoadData] defining the data being loaded.
     */
    override fun onLoadStarted(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData
    ) {
        player.videoInfoLD.value = PlayInfo(Constant.EXOPLAYER_INFO_CODE_LOADING_START, Constant.EXOPLAYER_INFO_CODE_LOADING_START)
        SimpleLogger.instance.debugI("ExoPlayer 开始加载")
    }

    /**
     * Called when a media source completed loading data.
     *
     * @param eventTime The event time.
     * @param loadEventInfo The [LoadEventInfo] defining the load event.
     * @param mediaLoadData The [MediaLoadData] defining the data being loaded.
     */
    override fun onLoadCompleted(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData
    ) {
        player.videoInfoLD.value = PlayInfo(Constant.EXOPLAYER_INFO_CODE_LOADING_COMPLETED, Constant.EXOPLAYER_INFO_CODE_LOADING_COMPLETED)
        SimpleLogger.instance.debugI("ExoPlayer 加载完毕")
    }

    /**
     * Called when a media source canceled loading data.
     *
     * @param eventTime The event time.
     * @param loadEventInfo The [LoadEventInfo] defining the load event.
     * @param mediaLoadData The [MediaLoadData] defining the data being loaded.
     */
    override fun onLoadCanceled(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData
    ) {
        player.videoInfoLD.value = PlayInfo(Constant.EXOPLAYER_INFO_CODE_LOADING_CANCELED, Constant.EXOPLAYER_INFO_CODE_LOADING_CANCELED)
        SimpleLogger.instance.debugI("ExoPlayer 加载取消")
    }

    /**
     * Called when a media source loading error occurred.
     *
     * <p>This method being called does not indicate that playback has failed, or that it will fail.
     * The player may be able to recover from the error. Hence applications should <em>not</em>
     * implement this method to display a user visible error or initiate an application level retry.
     * {@link Player.Listener#onPlayerError} is the appropriate place to implement such behavior. This
     * method is called to provide the application with an opportunity to log the error if it wishes
     * to do so.
     *
     * @param eventTime The event time.
     * @param loadEventInfo The {@link LoadEventInfo} defining the load event.
     * @param mediaLoadData The {@link MediaLoadData} defining the data being loaded.
     * @param error The load error.
     * @param wasCanceled Whether the load was canceled as a result of the error.
     */
    override fun onLoadError(
        eventTime: AnalyticsListener.EventTime,
        loadEventInfo: LoadEventInfo,
        mediaLoadData: MediaLoadData,
        error: IOException,
        wasCanceled: Boolean
    ) {
        player.videoErrorLD.value  = PlayInfo(Constant.EXOPLAYER_ERROR_CODE_LOADING, Constant.EXOPLAYER_ERROR_CODE_LOADING)
    }

    /**
     * Called when a frame is rendered for the first time since setting the surface, or since the
     * renderer was reset, or since the stream being rendered was changed.
     *
     * @param eventTime The event time.
     * @param output The output to which a frame has been rendered. Normally a [Surface],
     * however may also be other output types (e.g., a [VideoDecoderOutputBufferRenderer]).
     * @param renderTimeMs [SystemClock.elapsedRealtime] when the first frame was rendered.
     */
    override fun onRenderedFirstFrame(
        eventTime: AnalyticsListener.EventTime,
        output: Any,
        renderTimeMs: Long
    ) {
        player.videoInfoLD.value = PlayInfo(Constant.EXOPLAYER_INFO_CODE_RENDERING_STARTED, Constant.EXOPLAYER_INFO_CODE_RENDERING_STARTED)
    }

    /**
     * Called when the playback state changed.
     *
     * @param eventTime The event time.
     * @param state The new [playback state][Player.State].
     */
    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        super.onPlaybackStateChanged(eventTime, state)
        when(state){
            Player.STATE_READY -> {
                if (player.instance.isPlaying){
                    player.playerStateLD.value = PlayerState.STARTED
                } else {
                    player.playerStateLD.value = PlayerState.PREPARED
                }
            }
            Player.STATE_BUFFERING -> {
                player.playerStateLD.value = PlayerState.CACHING
            }
            Player.STATE_ENDED -> {
                player.playerStateLD.value = PlayerState.COMPLETED
            }
            Player.STATE_IDLE -> {
                if (player.playerStateLD.value != PlayerState.STOPPED) {
                    player.playerStateLD.value = PlayerState.IDLE
                }
            }
        }
    }

    /**
     * Called when the player starts or stops playing.
     *
     * @param eventTime The event time.
     * @param isPlaying Whether the player is playing.
     */
    override fun onIsPlayingChanged(eventTime: AnalyticsListener.EventTime, isPlaying: Boolean) {
        if (isPlaying) {
            player.videoInfoLD.value = PlayInfo(Constant.EXOPLAYER_INFO_CODE_IS_PLAYING, Constant.EXOPLAYER_INFO_CODE_IS_PLAYING)
            SimpleLogger.instance.debugI("正在播放")
        }
    }

    override fun onPositionDiscontinuity(eventTime: AnalyticsListener.EventTime ,
                                         oldPosition: Player.PositionInfo ,
                                         newPosition: Player.PositionInfo ,
    @Player.DiscontinuityReason reason: Int ) {
        player.seekCompleteLD.value = true
    }

    override fun onVideoSizeChanged(eventTime: AnalyticsListener.EventTime, videoSize: com.google.android.exoplayer2.video.VideoSize) {
        player.videoSizeLD.value = VideoSize(videoSize.width, videoSize.height)
    }

    override fun onBandwidthEstimate(
        eventTime: AnalyticsListener.EventTime,
        totalLoadTimeMs: Int,
        totalBytesLoaded: Long,
        bitrateEstimate: Long
    ) {
        SimpleLogger.instance.debugI("当前网速估计：${bitrateEstimate/(1024f * 1024f)}MB/s 总加载时间: $totalLoadTimeMs   已加载的字节总数: $totalBytesLoaded ")
        player.networkSpeedLD.value = bitrateEstimate
    }
}