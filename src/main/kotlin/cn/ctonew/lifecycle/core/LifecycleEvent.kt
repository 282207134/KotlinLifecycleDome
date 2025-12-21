package cn.ctonew.lifecycle.core

/**
 * 生命周期事件（参考 Jetpack Lifecycle 的 Event）。
 *
 * 事件会推动状态变化：例如 `ON_START` 会把状态从 `CREATED` 推进到 `STARTED`。
 */
enum class LifecycleEvent {
    /** 对应：INITIALIZED → CREATED */
    ON_CREATE,

    /** 对应：CREATED → STARTED */
    ON_START,

    /** 对应：STARTED → RESUMED */
    ON_RESUME,

    /** 对应：RESUMED → STARTED */
    ON_PAUSE,

    /** 对应：STARTED → CREATED */
    ON_STOP,

    /** 对应：CREATED → DESTROYED */
    ON_DESTROY,
}
