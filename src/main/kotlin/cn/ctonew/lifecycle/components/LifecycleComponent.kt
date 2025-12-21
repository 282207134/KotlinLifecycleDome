package cn.ctonew.lifecycle.components

import cn.ctonew.lifecycle.core.LifecycleOwner

/**
 * 生命周期组件。
 *
 * 组件化的关键点：
 * - 主程序只负责：创建 owner、推进生命周期、安装/卸载组件。
 * - 每个组件只负责：订阅它关心的生命周期事件，并在事件发生时执行自己的逻辑。
 *
 * 典型写法：
 * - 组件内部持有一个 observer
 * - install 时 addObserver
 * - uninstall 时 removeObserver
 */
interface LifecycleComponent {
    /** 安装组件：把观察者挂到 owner.lifecycle 上。 */
    fun install(owner: LifecycleOwner)

    /** 卸载组件：把观察者从 owner.lifecycle 上移除，释放资源。 */
    fun uninstall(owner: LifecycleOwner)
}
