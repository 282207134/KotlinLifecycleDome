package cn.ctonew.lifecycle.core

/**
 * 生命周期所有者。
 *
 * 任何“有生命周期”的对象都可以实现它：例如应用、页面、窗口，甚至某个功能子树。
 */
interface LifecycleOwner {
    val lifecycle: Lifecycle
}

/**
 * 一个最简单的 [LifecycleOwner] 实现。
 *
 * 用于教学：主程序创建它，并通过它来驱动生命周期事件。
 */
class SimpleLifecycleOwner : LifecycleOwner {
    override val lifecycle: Lifecycle = Lifecycle(this)
}
