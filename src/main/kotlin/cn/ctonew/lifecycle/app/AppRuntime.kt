package cn.ctonew.lifecycle.app

import java.util.Timer

/**
 * 应用运行时上下文：用于在“多个生命周期组件之间”共享少量状态。
 *
 * 设计原则：
 * - 组件之间尽量不要互相直接调用。
 * - 如果确实需要共享资源（例如连接、定时器、配置），统一放在这个上下文里。
 * - 主程序负责创建并把它注入到各组件。
 */
class AppRuntime {
    var config: AppConfig? = null
    var connection: FakeConnection? = null
    var ticker: Timer? = null
}

/** 一次性配置（示例）。 */
data class AppConfig(
    val envName: String,
    val versionName: String,
)

/**
 * 一个非常简化的“连接”示例，用来演示在 ON_START 打开、在 ON_STOP 关闭的资源管理。
 */
class FakeConnection(
    private val name: String,
) {
    private var opened: Boolean = false

    fun open() {
        if (opened) return
        opened = true
        println("[FakeConnection] 连接已打开：$name")
    }

    fun close() {
        if (!opened) return
        opened = false
        println("[FakeConnection] 连接已关闭：$name")
    }
}
