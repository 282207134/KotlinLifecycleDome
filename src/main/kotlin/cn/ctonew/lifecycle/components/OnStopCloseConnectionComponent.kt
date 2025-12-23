package cn.ctonew.lifecycle.components

import cn.ctonew.lifecycle.app.AppRuntime
import cn.ctonew.lifecycle.core.LifecycleEvent
import cn.ctonew.lifecycle.core.LifecycleObserver
import cn.ctonew.lifecycle.core.LifecycleOwner

/**
 * `ON_STOP` 阶段组件：与“不可见”相关的停止工作。
 *
 * ON_STOP 通常代表：
 * - 页面不再对用户可见
 * - 继续保持连接/刷新往往没有意义（除非你明确要后台运行）
 *
 * 适合放在 ON_STOP 的事情：
 * - 停止刷新
 * - 取消订阅
 * - 关闭连接（或降级为低频后台任务）
 */
class OnStopCloseConnectionComponent(
    private val runtime: AppRuntime,
) : LifecycleComponent {

    private val observer = LifecycleObserver { _, event ->
        if (event != LifecycleEvent.ON_STOP) return@LifecycleObserver

        runtime.connection?.close()
        runtime.connection = null

        println("[ON_STOP] 已停止与可见性相关的资源（连接已关闭）")
    }

    override fun install(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(observer)
    }

    override fun uninstall(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(observer)
    }
}
