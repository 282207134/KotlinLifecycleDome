package cn.ctonew.lifecycle.components

import cn.ctonew.lifecycle.core.LifecycleObserver
import cn.ctonew.lifecycle.core.LifecycleOwner
import java.time.LocalTime

/**
 * 控制台日志组件：
 *
 * - 用于观察生命周期事件流是否符合预期。
 * - 这个组件本身不承载业务，只做“可视化”。
 */
class ConsoleLoggerComponent : LifecycleComponent {
    private val observer = LifecycleObserver { owner, event ->
        val now = LocalTime.now().toReadableString()
        println("[$now] 收到事件=$event，当前状态=${owner.lifecycle.state}")
    }

    override fun install(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(observer)
    }

    override fun uninstall(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(observer)
    }

    private fun LocalTime.toReadableString(): String {
        // 只展示到毫秒，方便阅读
        return "%02d:%02d:%02d.%03d".format(hour, minute, second, nano / 1_000_000)
    }
}
