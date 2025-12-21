package cn.ctonew.lifecycle.core

/**
 * 生命周期状态（参考 Jetpack Lifecycle 的 State）。
 *
 * 你可以把它理解为“对象当前处于哪一个阶段”。
 *
 * 注意：这里刻意保持与 Jetpack 的命名一致，便于你把学习成果迁移到 Android / Compose Multiplatform。
 */
enum class LifecycleState {
    /**
     * 已初始化但尚未创建完成。
     *
     * 典型场景：对象刚被构造出来，但还没有进入“可被外部认为已创建”的阶段。
     */
    INITIALIZED,

    /**
     * 已创建。
     *
     * 典型场景：可以在这里完成一次性初始化（创建依赖、读取配置、准备资源）。
     */
    CREATED,

    /**
     * 已开始（通常代表“对用户可见”）。
     */
    STARTED,

    /**
     * 已恢复（通常代表“对用户可见且可交互 / 获得焦点”）。
     */
    RESUMED,

    /**
     * 已销毁。
     *
     * 进入该状态后不应再接收任何生命周期事件。
     */
    DESTROYED,
}
