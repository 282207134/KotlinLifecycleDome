# Kotlin 生命周期驱动 + 组件化示例（中文注释 / 中文文档）

这个仓库用于学习 **Kotlin** 中“**以生命周期为主线组织主程序**”的写法：

- 主程序只负责：**创建生命周期、推进生命周期事件、安装/卸载组件**。
- 具体业务逻辑被拆到不同的“组件（Component）”里：
  - 每个组件只关注一个或一组生命周期事件（例如只处理 `ON_CREATE`，或只处理 `ON_RESUME/ON_PAUSE`）。
  - 组件之间互不耦合，便于维护、替换、测试。
- **所有注释与文档均为中文**，并且生命周期部分会尽可能详细。

> 说明：本示例实现了一个“简化版 Lifecycle（参考 Jetpack / Compose 的概念）”，用于理解生命周期思想。
> 当你在 Compose Multiplatform 中开发时，可以直接使用官方的 Lifecycle 实现（见 `docs/02_Lifecycle_生命周期.md`）。

## 目录结构

```
.
├── docs/
│   ├── 01_项目结构与开发方式.md
│   └── 02_Lifecycle_生命周期.md
├── src/main/kotlin/
│   └── cn/ctonew/lifecycle/
│       ├── app/          # 主程序入口（只推进生命周期 + 安装组件）
│       ├── core/         # 生命周期核心：状态、事件、Lifecycle、Owner
│       └── components/   # 各生命周期阶段的独立组件
├── build.gradle.kts
└── settings.gradle.kts
```

## 如何运行（本地）

你可以用 IntelliJ IDEA 打开本项目，然后运行 `cn.ctonew.lifecycle.app.MainKt`。

如果你本地安装了 Gradle，也可以：

```bash
gradle run
```

## 推荐阅读

- `docs/01_项目结构与开发方式.md`
- `docs/02_Lifecycle_生命周期.md`
