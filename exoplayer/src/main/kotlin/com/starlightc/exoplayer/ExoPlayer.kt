package com.starlightc.exoplayer

import android.content.Context
import android.view.Surface
import android.view.SurfaceHolder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.starlightc.video.core.Constant
import com.starlightc.video.core.SimpleLogger
import com.starlightc.video.core.infomation.PlayInfo
import com.starlightc.video.core.infomation.PlayerState
import com.starlightc.video.core.infomation.VideoDataSource
import com.starlightc.video.core.infomation.VideoSize
import com.starlightc.video.core.interfaces.ErrorProcessor
import com.starlightc.video.core.interfaces.IMediaPlayer
import com.starlightc.video.core.interfaces.InfoProcessor
import com.starlightc.video.core.interfaces.Settings

/**
 * @author StarlightC
 * @since 2022/5/26
 *
 * ExoPlayer封装
 */

open class ExoPlayer: IMediaPlayer<ExoPlayer> {

    override fun getInfoProcessor(): InfoProcessor {
        return ExoInfoProcessor()
    }

    override fun getErrorProcessor(): ErrorProcessor {
        return ExoErrorProcessor()
    }


    override lateinit var lifecycleRegistry: LifecycleRegistry

    /**
     * 播放器实例
     */
    override lateinit var instance: ExoPlayer

    /**
     * 准备后是否播放
     */
    override var playOnReady: Boolean = false

    /**
     * 是否正在播放
     */
    override val isPlaying: Boolean
        get() {
            try {
                return instance.isPlaying
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

    /**
     * 最后位置
     */
    override var lastPosition: Long = 0L

    /**
     * 开始播放位置
     */
    override var startPosition: Long = 0L

    /**
     * 当前位置
     */
    override val currentPosition: Long
        get() = try {
            lastPosition = instance.currentPosition
            lastPosition
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    /**
     * 视频长度
     */
    override val duration: Long
        get() = try {
            instance.duration
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    /**
     * 视频高度
     */
    override val videoHeight: Int
        get() = try {
            instance.videoSize.height
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    /**
     * 视频宽度
     */
    override val videoWidth: Int
        get() = try {
            instance.videoSize.width
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    /**
     * 当前播放器状态
     */
    override val playerState: PlayerState
        get() = playerStateLD.value ?: PlayerState.IDLE

    /**
     * 播放器目标状态
     */
    override var targetState: PlayerState = PlayerState.IDLE

    /**
     * 播放器状态监听
     */
    override val playerStateLD: MutableLiveData<PlayerState> = MutableLiveData()

    /**
     * 播放器尺寸
     */
    override val videoSizeLD: MutableLiveData<VideoSize> = MutableLiveData()

    /**
     * 加载进度
     */
    override val bufferingProgressLD: MutableLiveData<Int> = MutableLiveData()

    /**
     * 是否跳转完成
     */
    override val seekCompleteLD: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * 视频播放器输出信息
     */
    override val videoInfoLD: MutableLiveData<PlayInfo> = MutableLiveData()

    /**
     * 视频报错
     */
    override val videoErrorLD: MutableLiveData<PlayInfo> = MutableLiveData()
    override val videoList: ArrayList<VideoDataSource> = ArrayList()
    override var currentVideo: VideoDataSource? = null

    /**
     * 缓存的播放位置
     */
    override var cacheSeekPosition: Long = 0L

    override lateinit var context: Context

    override fun create(context: Context) {
        this.context = context
        instance = ExoPlayer.Builder(context).build()
        lifecycleRegistry = if (context is LifecycleOwner) {
            LifecycleRegistry(context)
        } else {
            LifecycleRegistry(this)
        }
        playerStateLD.value = PlayerState.IDLE
        targetState = PlayerState.IDLE
        instance.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build(), false)
        instance.addAnalyticsListener(ExoAnalyticsListener(this))
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    /**
     * 获取Player名称
     */
    override fun getPlayerName(): String {
        return Constant.EXOPLAYER
    }

    /**
     * 初始化设置
     */
    override fun initSettings(settings: Settings) {
        // do something
    }

    /**
     * 开始
     */
    override fun start() {
        try {
            SimpleLogger.instance.debugI("IjkPlayer start")
            instance.play()
            if (playerState == PlayerState.PREPARED && startPosition in 0L until duration) {
                seekTo(startPosition)
                startPosition = 0L
            }
            playerStateLD.value = PlayerState.STARTED
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /**
     * 准备
     */
    override fun prepare() {
        try {
            playerStateLD.value = PlayerState.PREPARING
            instance.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 异步准备
     */
    override fun prepareAsync() {
        try {
            playerStateLD.value = PlayerState.PREPARING
            instance.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 暂停
     */
    override fun pause() {
        try {
            playerStateLD.value = PlayerState.PAUSED
            instance.pause()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /**
     * 停止
     */
    override fun stop() {
        try {
            playerStateLD.value = PlayerState.STOPPED
            instance.stop()
            lastPosition = currentPosition
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 跳转到指定到位置
     */
    override fun seekTo(time: Long) {
        try {
            instance.seekTo(time)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /**
     * 重置
     */
    override fun reset() {
        try {
            instance.seekTo(0)
            instance.stop()
            startPosition = 0
            lastPosition = 0
            videoList.clear()
            currentVideo = null
            playerStateLD.value = PlayerState.IDLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 释放
     */
    override fun release() {
        SimpleLogger.instance.debugI("ExoPlayer Release")
        try {
            playerStateLD.value = PlayerState.END
            instance.release()
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAcceptError(what: Int, extra: Int) {
        videoErrorLD.value = PlayInfo(what, extra)
        playerStateLD.value = PlayerState.ERROR
    }

    override fun onAcceptInfo(what: Int, extra: Int) {
        videoInfoLD.value = PlayInfo(what, extra)
    }

    /**
     * 设置音量
     */
    override fun setVolume(volume: Float) {
        try {
            instance.volume = volume
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置循环播放
     */
    override fun setLooping(isLoop: Boolean) {
        try {
            instance.repeatMode = if (isLoop) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置播放容器
     */
    override fun setSurface(surface: Surface?) {
        try {
            instance.setVideoSurface(surface)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置播放容器
     */
    override fun setDisplay(surfaceHolder: SurfaceHolder) {
        try {
            instance.setVideoSurfaceHolder(surfaceHolder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 播放器静音
     */
    override fun mutePlayer() {
       setVolume(0f)
    }

    /**
     * 取消播放器静音
     */
    override fun cancelMutePlayer() {
        setVolume(1f)
    }

    /**
     * 添加视频源
     */
    override fun addVideoDataSource(data: VideoDataSource) {
        videoList.add(data)
    }

    /**
     * 选择视频源
     */
    override fun selectVideo(index: Int) {
        currentVideo = videoList[index]
        val uri = currentVideo!!.uri
        if (uri == null) {
            SimpleLogger.instance.debugE("Empty Uri")
            return
        }
        val mediaType = Util.inferContentType(uri)
        val mediaItemBuilder = MediaItem.Builder().setUri(uri ?: return)
        SimpleLogger.instance.debugI("MediaType: $mediaType")
        if (C.TYPE_HLS == mediaType) {
            if (hlsDataSourceFactory == null) {
                val httpDatasourceFactory = DefaultHttpDataSource.Factory()
                val extractorsFactory =
                    DefaultHlsExtractorFactory(FLAG_ALLOW_NON_IDR_KEYFRAMES, true)
                hlsDataSourceFactory = HlsMediaSource
                    .Factory(httpDatasourceFactory)
                    .setExtractorFactory(extractorsFactory)
            }
            val hlsMediaSource = hlsDataSourceFactory?.createMediaSource(MediaItem.fromUri(uri))
            if (hlsMediaSource == null) {
                SimpleLogger.instance.debugE("Create HlsMediaSource Failed")
                return
            } else {
                instance.setMediaSource(hlsMediaSource)
            }
        } else {
            instance.setMediaItem(mediaItemBuilder.build())
        }
        playerStateLD.value = PlayerState.INITIALIZED
    }

    /**
     * 清空视频列表
     */
    override fun clearVideoDataSourceList() {
        videoList.clear()
        currentVideo = null
    }

    /**
     * 获取网速信息
     * @return -1表示该内核不支持获取
     */
    override fun getNetworkSpeedInfo(): Long {
        return -1L
        // TODO: 实时网速信息
    }

    /**
     * 设置倍速
     */
    override fun setSpeed(speed: Float) {
        val playbackParam = PlaybackParameters(speed, 1.0f)
        instance.playbackParameters = playbackParam
    }

    /**
     * 获取倍速信息
     */
    override fun getSpeed(): Float {
        return  instance.playbackParameters.speed
    }

    /**
     * 获取当前码率
     */
    override fun getBitrate(): Long {
        return -1L
        // TODO Bitrate
    }

    /**
     * 选择码率
     */
    override fun selectBitrate(bitrate: Long) {
        //TODO: Select bitrate
    }

    /**
     * Returns the Lifecycle of the provider.
     *
     * @return The lifecycle of the provider.
     */
    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private var hlsDataSourceFactory: HlsMediaSource.Factory? = null
}