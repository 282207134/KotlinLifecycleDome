package cn.ctonew.lifecycle.components

import cn.ctonew.lifecycle.app.AppRuntime
import cn.ctonew.lifecycle.core.LifecycleEvent
import cn.ctonew.lifecycle.core.LifecycleObserver
import cn.ctonew.lifecycle.core.LifecycleOwner
import kotlin.concurrent.fixedRateTimer

/**
 * `ON_RESUME` 阶段组件：与“获得焦点 / 可交互”相关的工作。
 *
 * ON_RESUME 通常代表：
 * - 用户已经能操作界面（键盘、鼠标、触摸等）
 * - 窗口/页面已经获得焦点
 *
 * 适合放在 ON_RESUME 的事情：
 * - 恢复高频刷新（动画、轮询、计时器）
 * - 恢复输入监听
 */
class OnResumeStartTickerComponent(
    private val runtime: AppRuntime,
) : LifecycleComponent {

    private val observer = LifecycleObserver { _, event ->
        if (event != LifecycleEvent.ON_RESUME) return@LifecycleObserver

        // 这里用 Timer 模拟“高频工作”。真实项目中你可能用协程/Flow。
        // 注意：高频任务务必在 ON_PAUSE 停止，否则很容易造成资源浪费与泄漏。
        runtime.ticker = fixedRateTimer(
            name = "ui-ticker",
            initialDelay = 0L,
            period = 500L,
        ) {
            println("[ON_RESUME] tick...（模拟界面高频任务）")
        }
    }

    override fun install(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(observer)
    }

    override fun uninstall(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(observer)
    }
}
