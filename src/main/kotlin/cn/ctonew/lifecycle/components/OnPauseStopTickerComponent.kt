package cn.ctonew.lifecycle.components

import cn.ctonew.lifecycle.app.AppRuntime
import cn.ctonew.lifecycle.core.LifecycleEvent
import cn.ctonew.lifecycle.core.LifecycleObserver
import cn.ctonew.lifecycle.core.LifecycleOwner

/**
 * `ON_PAUSE` 阶段组件：把“可逆的高频任务”暂停掉。
 *
 * ON_PAUSE 通常代表：
 * - 用户暂时无法与界面交互（失去焦点）
 * - 但页面可能仍然可见
 *
 * 适合放在 ON_PAUSE 的事情：
 * - 暂停计时器/动画
 * - 暂停音视频播放（视业务）
 * - 做快速的状态保存（例如保存草稿、缓存输入）
 */
class OnPauseStopTickerComponent(
    private val runtime: AppRuntime,
) : LifecycleComponent {

    private val observer = LifecycleObserver { _, event ->
        if (event != LifecycleEvent.ON_PAUSE) return@LifecycleObserver

        runtime.ticker?.cancel()
        runtime.ticker = null

        println("[ON_PAUSE] 已暂停高频任务（ticker 已取消）")
    }

    override fun install(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(observer)
    }

    override fun uninstall(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(observer)
    }
}
