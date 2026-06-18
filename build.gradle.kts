plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "2.0.1"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "4.0.2"
}

group = "com.yiaobang"
version = "1.0.0"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(26)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.yiaobang.seatswap")
    mainClass.set("com.yiaobang.seatswap.SeatSwapApplication")
    applicationDefaultJvmArgs = listOf("--enable-native-access=javafx.graphics")
}

javafx {
    version = "26.0.1"
    modules = listOf("javafx.controls")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {

    imageZip.set(layout.buildDirectory.file("distributions/SeatSwap-Portable.zip"))
    options.set(
        listOf(
            "--strip-debug",
            "--no-header-files",
            "--no-man-pages",
            "--compress", "zip-9",             // 1. 升级为最高级别的压缩率（打包会慢一点，但体积更小）
            "--strip-native-commands"          // 2. 核心：裁剪掉 JDK 自带的无用命令行工具
        )
    )
    launcher {
        name = "SeatSwap" // 双击运行的快捷方式名称

    }

    // ================= 新增的 jpackage 打包配置 =================
    jpackage {
        icon = "src/main/resources/icon.ico"
        // 最终安装包和程序的名
        installerName = "SeatSwap"


        // 2. 关键：确保打包后的应用程序在运行时，也会自动带上这个高版本 Java 的安全参数，避免弹窗警告
        jvmArgs.addAll(listOf("--enable-native-access=javafx.graphics"))

        // 可选：让生成的安装程序自动创建桌面快捷方式和开始菜单项
        installerOptions.addAll(listOf("--win-shortcut", "--win-menu"))
    }
}