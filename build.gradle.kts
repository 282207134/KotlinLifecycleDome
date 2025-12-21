plugins {
    kotlin("jvm") version "2.0.21"
    application
}

repositories {
    mavenCentral()
}

application {
    // 入口函数：src/main/kotlin/cn/ctonew/lifecycle/app/Main.kt
    mainClass.set("cn.ctonew.lifecycle.app.MainKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        // 让示例更接近实际工程：把 Kotlin 默认警告当成错误，便于学习阶段及时修正
        allWarningsAsErrors.set(false)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}
