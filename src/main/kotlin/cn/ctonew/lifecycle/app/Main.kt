package cn.ctonew.lifecycle.app

import cn.ctonew.lifecycle.components.ConsoleLoggerComponent
import cn.ctonew.lifecycle.components.LifecycleComponent
import cn.ctonew.lifecycle.components.OnCreateConfigComponent
import cn.ctonew.lifecycle.components.OnDestroyCleanupComponent
import cn.ctonew.lifecycle.components.OnPauseStopTickerComponent
import cn.ctonew.lifecycle.components.OnResumeStartTickerComponent
import cn.ctonew.lifecycle.components.OnStartOpenConnectionComponent
import cn.ctonew.lifecycle.components.OnStopCloseConnectionComponent
import cn.ctonew.lifecycle.core.LifecycleEvent
import cn.ctonew.lifecycle.core.LifecycleOwner
import cn.ctonew.lifecycle.core.SimpleLifecycleOwner

/**
 * 主程序入口：以生命周期为主线进行准备。
 *
 * 你应该重点观察：
 * - Main 几乎不写业务逻辑，只推进生命周期。
 * - 每个生命周期阶段的内容，都在独立的组件里完成。
 */
fun main() {
    val owner = SimpleLifecycleOwner()
    val runtime = AppRuntime()

    // 把“每个生命周期内要做的事情”拆成一个个组件。
    // 主程序只负责安装它们。
    val components: List<LifecycleComponent> = listOf(
        ConsoleLoggerComponent(),
        OnCreateConfigComponent(runtime),
        OnStartOpenConnectionComponent(runtime),
        OnResumeStartTickerComponent(runtime),
        OnPauseStopTickerComponent(runtime),
        OnStopCloseConnectionComponent(runtime),
        OnDestroyCleanupComponent(runtime),
    )

    installComponents(owner = owner, components = components)

    // 下面模拟一个“典型生命周期流”。真实项目中，这些事件通常由系统/框架触发。
    owner.lifecycle.handleEvent(LifecycleEvent.ON_CREATE)
    owner.lifecycle.handleEvent(LifecycleEvent.ON_START)
    owner.lifecycle.handleEvent(LifecycleEvent.ON_RESUME)

    Thread.sleep(1200L)

    owner.lifecycle.handleEvent(LifecycleEvent.ON_PAUSE)
    owner.lifecycle.handleEvent(LifecycleEvent.ON_STOP)
    owner.lifecycle.handleEvent(LifecycleEvent.ON_DESTROY)

    // 一般而言，进入 ON_DESTROY 后就不需要 uninstall 了（Lifecycle 自己会清空 observer）。
    // 这里保留 uninstall 作为演示：如果你有自定义 owner 的生命周期管理，可能仍然需要显式卸载。
    uninstallComponents(owner = owner, components = components)
}

private fun installComponents(
    owner: LifecycleOwner,
    components: List<LifecycleComponent>,
) {
    components.forEach { it.install(owner) }
}

private fun uninstallComponents(
    owner: LifecycleOwner,
    components: List<LifecycleComponent>,
) {
    components.forEach { it.uninstall(owner) }
}
