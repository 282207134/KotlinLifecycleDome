# 02｜Lifecycle（生命周期）——尽可能详细的中文说明

本文以 **Jetpack Compose / Compose Multiplatform** 的生命周期概念为基准，结合本仓库的“教学版实现”，解释：

- 生命周期是什么
- 状态（State）与事件（Event）是什么
- 事件如何推动状态变化
- 多平台（iOS / Desktop / Web）上生命周期如何映射
- 在 Compose Multiplatform 中如何使用官方 Lifecycle

---

## 1. 生命周期是什么？

**生命周期（Lifecycle）** 是一种用“有限状态机（Finite State Machine）”描述对象存在过程的方法。

以“应用/页面/窗口”为例，它通常会经历：

- 被创建（Create）
- 进入可见/可交互（Start/Resume）
- 暂时失去焦点/不可交互（Pause）
- 不再可见（Stop）
- 被销毁（Destroy）

如果我们把这些阶段规范成统一的状态与事件：

- 组件就可以 **订阅生命周期** 来做资源管理（初始化/订阅/释放）。
- 主程序就可以 **按生命周期推进** 来组织启动流程，而不是把所有逻辑堆在一个入口函数里。

---

## 2. 状态（State）与事件（Event）

> Jetpack/Compose 的核心：**事件驱动状态变化**。

### 2.1 常见生命周期状态（Lifecycle.State）

Jetpack Lifecycle 的典型状态：

- `INITIALIZED`：已初始化但尚未完成创建（通常是对象刚 new 出来）
- `CREATED`：已创建
- `STARTED`：已开始（对用户可见）
- `RESUMED`：已恢复（对用户可见且可交互，拥有焦点）
- `DESTROYED`：已销毁

> 注意：在 Jetpack 中并没有 `STOPPED` 这个状态；“停止”的语义通常表现为从 `STARTED` 回到 `CREATED`。

### 2.2 常见生命周期事件（Lifecycle.Event）

典型事件：

- `ON_CREATE`
- `ON_START`
- `ON_RESUME`
- `ON_PAUSE`
- `ON_STOP`
- `ON_DESTROY`

### 2.3 事件推动状态变化（最重要）

你可以把它理解为一条单向推进/回退的路径：

```text
INITIALIZED
  | ON_CREATE
  v
CREATED
  | ON_START
  v
STARTED
  | ON_RESUME
  v
RESUMED
  | ON_PAUSE
  v
STARTED
  | ON_STOP
  v
CREATED
  | ON_DESTROY
  v
DESTROYED
```

实践建议（非常关键）：

- **在 `ON_CREATE` 做“只需要一次”的初始化**：配置加载、依赖创建、一次性资源准备。
- **在 `ON_START` 做“与可见性相关”的开始工作**：开始监听、开始刷新。
- **在 `ON_RESUME` 做“与交互/焦点相关”的工作**：恢复动画、恢复输入处理、开启计时器。
- **在 `ON_PAUSE` 做“快速可逆”的暂停工作**：暂停计时器、暂停高频任务。
- **在 `ON_STOP` 做“与不可见相关”的停止工作**：停止刷新、取消订阅。
- **在 `ON_DESTROY` 做最终释放**：关闭资源、停止后台任务、防止泄漏。

---

## 3. Compose Multiplatform 中的 Lifecycle

Compose Multiplatform 采用 Jetpack Compose 的生命周期概念，并提供了跨平台可用的 `LifecycleOwner`。

### 3.1 依赖（commonMain）

在 `commonMain` 源集添加：

```kotlin
org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.9.6
```

### 3.2 为什么需要 Lifecycle-aware（生命周期感知）组件？

在 UI（尤其是声明式 UI）里，界面可能频繁重组（recompose）。

- 如果把资源初始化/订阅写在可组合函数里，很容易重复创建、重复订阅。
- 生命周期感知组件可以把“订阅与释放”与生命周期绑定，减少泄漏和重复工作。

### 3.3 LifecycleOwner 的获取

在 Compose 中，`LifecycleOwner` 一般通过 `CompositionLocal` 提供（例如 `LocalLifecycleOwner`）。

如果你希望某个 composable 子树拥有独立生命周期，可以创建自己的 `LifecycleOwner` 并在该子树提供它。

### 3.4 在 Compose 中订阅生命周期事件（代码示例）

在声明式 UI 中，推荐使用 `DisposableEffect` 来绑定“订阅/取消订阅”的时机：

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun 订阅生命周期事件(
    onEvent: (Lifecycle.Event) -> Unit,
) {
    val owner = LocalLifecycleOwner.current

    // DisposableEffect 的语义：当 key 变化或离开组合时，自动执行 onDispose。
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            onEvent(event)
        }

        owner.lifecycle.addObserver(observer)

        onDispose {
            owner.lifecycle.removeObserver(observer)
        }
    }
}
```

你可以把它看作“在 Compose 世界里”实现生命周期组件最常用的底座。

### 3.5 把生命周期逻辑拆成可复用组件（推荐做法）

为了让主程序（或 `AppRoot`）保持干净，你可以把生命周期处理封装成独立组件：

```kotlin
interface ComposeLifecycleComponent {
    @Composable
    fun Install()
}

class OnResume打印组件 : ComposeLifecycleComponent {
    @Composable
    override fun Install() {
        订阅生命周期事件 { event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                println("进入 ON_RESUME")
            }
        }
    }
}
```

主界面只负责“安装组件”，不写细节：

```kotlin
@Composable
fun AppRoot(components: List<ComposeLifecycleComponent>) {
    components.forEach { it.Install() }

    // ...你的 UI 内容
}
```

---

## 4. 多平台生命周期映射（iOS / Web / Desktop）

> 下面的映射来自 Compose Multiplatform 文档的要点整理。

### 4.1 iOS 映射

| iOS 原生事件/通知 | Lifecycle 事件 | 状态变化 |
|---|---|---|
| viewWillAppear | ON_START | CREATED → STARTED |
| didBecomeActive | ON_RESUME | STARTED → RESUMED |
| willResignActive | ON_PAUSE | RESUMED → STARTED |
| viewDidDisappear / didEnterBackground | ON_STOP | STARTED → CREATED |
| willEnterForeground | ON_START | CREATED → STARTED |
| viewControllerDidLeaveWindowHierarchy | ON_DESTROY | CREATED → DESTROYED |

### 4.2 Web（Wasm）限制

由于 Wasm target 的限制：

- 可能会 **跳过 `CREATED`**（应用总是附加在页面上）
- 通常 **不会到达 `DESTROYED`**（网页常在关闭标签页时直接终止）

常见映射（概念上）：

| Web 原生事件 | Lifecycle 事件 | 状态变化 |
|---|---|---|
| visibilitychange（变为可见） | ON_START | CREATED → STARTED |
| focus | ON_RESUME | STARTED → RESUMED |
| blur | ON_PAUSE | RESUMED → STARTED |
| visibilitychange（不可见） | ON_STOP | STARTED → CREATED |

### 4.3 Desktop（Swing）映射

| Swing 回调 | Lifecycle 事件 | 状态变化 |
|---|---|---|
| windowGainedFocus | ON_RESUME | STARTED → RESUMED |
| windowLostFocus | ON_PAUSE | RESUMED → STARTED |
| windowDeiconified | ON_START | CREATED → STARTED |
| windowIconified | ON_STOP | STARTED → CREATED |
| dispose | ON_DESTROY | CREATED → DESTROYED |

---

## 5. 协程与 Dispatchers.Main.immediate（重要坑点）

在多平台生命周期中使用协程时，`Lifecycle.coroutineScope` 通常与 `Dispatchers.Main.immediate` 绑定。

- 在 Desktop 目标上，`Dispatchers.Main.immediate` 可能默认不可用。
- 需要在项目中添加 `kotlinx-coroutines-swing` 让 `Dispatchers.Main` 在 Swing 环境可用。

---

## 6. 本仓库“教学版 Lifecycle”与你要掌握的点

本项目的 `core` 包实现了一个简化版 Lifecycle，目的不是替代官方库，而是帮助你掌握：

1. **状态机思维**：事件如何推动状态变化。
2. **组件化思维**：把每个事件要做的事拆到不同组件中。
3. **主程序干净**：Main 只推进生命周期，不写业务细节。

你可以把这种结构直接迁移到真实 Compose Multiplatform 工程：

- Main（或 AppRoot）里推进生命周期/安装组件
- 每个组件在需要时通过 `LocalLifecycleOwner.current.lifecycle` 订阅事件

---

## 7. 延伸阅读建议

- 官方文档：Compose Multiplatform Lifecycle（建议对照阅读）
- Jetpack Lifecycle 基础概念：LifecycleOwner / Observer / State / Event
