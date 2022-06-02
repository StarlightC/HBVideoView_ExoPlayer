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
                player.bufferingProgressLD.value = player.instance.bufferedPercentage
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

    override fun onSeekProcessed(eventTime: AnalyticsListener.EventTime) {
        player.seekCompleteLD.value = true
    }

}