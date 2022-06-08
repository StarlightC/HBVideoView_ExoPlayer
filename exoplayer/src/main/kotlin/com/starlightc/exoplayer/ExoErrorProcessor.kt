package com.starlightc.exoplayer


import com.starlightc.video.core.Constant
import com.starlightc.video.core.SimpleLogger
import com.starlightc.video.core.interfaces.ErrorProcessor

/**
 * @author StarlightC
 * @since 2022/5/26
 *
 */
open class ExoErrorProcessor: ErrorProcessor {
    override fun getName(): String {
        return Constant.EXOPLAYER
    }

    override fun process(what: Int, extra: Int): Int {
        return when (what) {
            Constant.EXOPLAYER_ERROR_CODE_LOADING -> {
                SimpleLogger.instance.debugE("视频错误: LOADING_ERROR")
                1
            }
            Constant.EXOPLAYER_ERROR_CODE_IO -> {
                SimpleLogger.instance.debugE("视频错误: IO_ERROR")
                1
            }
            else -> {
                SimpleLogger.instance.debugE("视频错误: $what")
                1
            }
        }
    }
}