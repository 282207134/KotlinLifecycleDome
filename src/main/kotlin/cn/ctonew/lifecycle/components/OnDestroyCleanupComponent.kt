package cn.ctonew.lifecycle.components

import cn.ctonew.lifecycle.app.AppRuntime
import cn.ctonew.lifecycle.core.LifecycleEvent
import cn.ctonew.lifecycle.core.LifecycleObserver
import cn.ctonew.lifecycle.core.LifecycleOwner

/**
 * `ON_DESTROY` 阶段组件：最终清理。
 *
 * 适合放在 ON_DESTROY 的事情：
 * - 关闭/释放一切资源（文件句柄、数据库连接、线程、定时器、订阅等）
 * - 把运行时状态置空，避免被误用
 *
 * 注意：
 * - 在真实框架里，ON_DESTROY 通常是最后一次能可靠收到的事件。
 * - Web 平台（Wasm）可能永远不会到达 DESTROYED（例如直接关闭标签页）。
 */
class OnDestroyCleanupComponent(
    private val runtime: AppRuntime,
) : LifecycleComponent {

    private val observer = LifecycleObserver { _, event ->
        if (event != LifecycleEvent.ON_DESTROY) return@LifecycleObserver

        runtime.ticker?.cancel()
        runtime.ticker = null

        runtime.connection?.close()
        runtime.connection = null

        runtime.config = null

        println("[ON_DESTROY] 最终清理完成（ticker/connection/config 已释放）")
    }

    override fun install(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(observer)
    }

    override fun uninstall(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(observer)
    }
}
