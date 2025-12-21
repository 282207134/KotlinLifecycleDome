package cn.ctonew.lifecycle.core

/**
 * 一个“教学版”的生命周期实现：
 *
 * - 维护当前 [state]
 * - 接收 [LifecycleEvent] 并推进状态
 * - 通知所有 [LifecycleObserver]
 *
 * 你可以把它当成 Jetpack Lifecycle 的极简模型，用于理解“事件驱动状态机 + 观察者通知”的思路。
 */
class Lifecycle(
    private val owner: LifecycleOwner,
) {
    private val observers: MutableSet<LifecycleObserver> = linkedSetOf()

    var state: LifecycleState = LifecycleState.INITIALIZED
        private set

    /**
     * 添加观察者。
     *
     * 约定：如果当前已处于 [LifecycleState.DESTROYED]，则不再接受新的观察者。
     */
    fun addObserver(observer: LifecycleObserver) {
        if (state == LifecycleState.DESTROYED) return
        observers += observer
    }

    /**
     * 移除观察者。
     */
    fun removeObserver(observer: LifecycleObserver) {
        observers -= observer
    }

    /**
     * 推进生命周期。
     *
     * 设计理念：
     * - 生命周期只能由“拥有者/调度者”推进（例如主程序、框架）。
     * - 组件只能订阅事件并被动响应。
     */
    fun handleEvent(event: LifecycleEvent) {
        if (state == LifecycleState.DESTROYED) return

        val newState = reduce(state, event)
        state = newState

        // 用快照遍历，避免观察者在回调中增删导致并发修改异常。
        val snapshot = observers.toList()
        snapshot.forEach { it.onStateChanged(owner, event) }

        // 进入 DESTROYED 后清理观察者，防止误用与泄漏。
        if (state == LifecycleState.DESTROYED) {
            observers.clear()
        }
    }

    /**
     * 状态机：根据旧状态 + 事件，计算新状态。
     *
     * 为了教学清晰：
     * - 我们对“非法跳转”做严格检查（直接抛异常）。
     * - 真实框架可能会更宽容，或进行补偿性补齐。
     */
    private fun reduce(old: LifecycleState, event: LifecycleEvent): LifecycleState {
        return when (event) {
            LifecycleEvent.ON_CREATE -> {
                require(old == LifecycleState.INITIALIZED) {
                    "非法状态跳转：当前状态=$old，无法处理事件=$event（期望 INITIALIZED）"
                }
                LifecycleState.CREATED
            }

            LifecycleEvent.ON_START -> {
                require(old == LifecycleState.CREATED) {
                    "非法状态跳转：当前状态=$old，无法处理事件=$event（期望 CREATED）"
                }
                LifecycleState.STARTED
            }

            LifecycleEvent.ON_RESUME -> {
                require(old == LifecycleState.STARTED) {
                    "非法状态跳转：当前状态=$old，无法处理事件=$event（期望 STARTED）"
                }
                LifecycleState.RESUMED
            }

            LifecycleEvent.ON_PAUSE -> {
                require(old == LifecycleState.RESUMED) {
                    "非法状态跳转：当前状态=$old，无法处理事件=$event（期望 RESUMED）"
                }
                LifecycleState.STARTED
            }

            LifecycleEvent.ON_STOP -> {
                require(old == LifecycleState.STARTED) {
                    "非法状态跳转：当前状态=$old，无法处理事件=$event（期望 STARTED）"
                }
                LifecycleState.CREATED
            }

            LifecycleEvent.ON_DESTROY -> {
                require(old == LifecycleState.CREATED) {
                    "非法状态跳转：当前状态=$old，无法处理事件=$event（期望 CREATED）"
                }
                LifecycleState.DESTROYED
            }
        }
    }
}
