package cn.ctonew.lifecycle.components

import cn.ctonew.lifecycle.app.AppRuntime
import cn.ctonew.lifecycle.app.FakeConnection
import cn.ctonew.lifecycle.core.LifecycleEvent
import cn.ctonew.lifecycle.core.LifecycleObserver
import cn.ctonew.lifecycle.core.LifecycleOwner

/**
 * `ON_START` 阶段组件：与“可见性”相关的启动工作。
 *
 * 你可以把 ON_START 理解为：
 * - 应用/页面“要进入前台可见状态”
 * - 但不一定已经可交互（可交互通常是 ON_RESUME）
 *
 * 适合放在 ON_START 的事情：
 * - 打开连接/开始监听（网络、WebSocket、传感器）
 * - 启动刷新任务（但频率不宜过高，真正高频通常放在 ON_RESUME）
 */
class OnStartOpenConnectionComponent(
    private val runtime: AppRuntime,
) : LifecycleComponent {

    private val observer = LifecycleObserver { _, event ->
        if (event != LifecycleEvent.ON_START) return@LifecycleObserver

        val config = requireNotNull(runtime.config) {
            "ON_START 发生时 config 不能为空：请确认 ON_CREATE 是否已执行"
        }

        runtime.connection = FakeConnection("主连接(${config.envName})").also { it.open() }
    }

    override fun install(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(observer)
    }

    override fun uninstall(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(observer)
    }
}
