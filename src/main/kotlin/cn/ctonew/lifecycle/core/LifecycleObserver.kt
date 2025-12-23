package cn.ctonew.lifecycle.core

/**
 * 生命周期观察者。
 *
 * 当 [Lifecycle] 收到事件并推进状态时，会把事件广播给所有观察者。
 *
 * 设计要点：
 * - 观察者不负责推进生命周期（也就是不应该主动调用 handleEvent）。
 * - 观察者只负责“在某个事件发生时做自己的事”。
 */
fun interface LifecycleObserver {
    fun onStateChanged(owner: LifecycleOwner, event: LifecycleEvent)
}
