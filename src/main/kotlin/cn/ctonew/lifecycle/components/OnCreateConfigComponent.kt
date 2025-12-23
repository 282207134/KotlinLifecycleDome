package cn.ctonew.lifecycle.components

import cn.ctonew.lifecycle.app.AppConfig
import cn.ctonew.lifecycle.app.AppRuntime
import cn.ctonew.lifecycle.core.LifecycleEvent
import cn.ctonew.lifecycle.core.LifecycleObserver
import cn.ctonew.lifecycle.core.LifecycleOwner

/**
 * `ON_CREATE` 阶段组件：一次性初始化（建议尽可能详细理解这一段）。
 *
 * 适合放在 ON_CREATE 的事情（经验规律）：
 * - 创建依赖（DI 容器、Repository、Service）
 * - 读取配置、解析命令行参数
 * - 初始化日志、埋点基础设施
 * - 初始化缓存目录、数据库（仅创建/打开，不做耗时查询）
 *
 * 不太建议放在 ON_CREATE 的事情：
 * - 与“是否可见/是否可交互”强相关的任务（更适合 ON_START / ON_RESUME）
 */
class OnCreateConfigComponent(
    private val runtime: AppRuntime,
) : LifecycleComponent {

    private val observer = LifecycleObserver { _, event ->
        if (event != LifecycleEvent.ON_CREATE) return@LifecycleObserver

        // 一次性配置初始化：真实项目中通常来自本地文件、远端下发、环境变量等。
        runtime.config = AppConfig(
            envName = "dev",
            versionName = "0.1.0",
        )

        println("[ON_CREATE] 配置已加载：${runtime.config}")
    }

    override fun install(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(observer)
    }

    override fun uninstall(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(observer)
    }
}
