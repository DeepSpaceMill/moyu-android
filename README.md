# 末语引擎 - Android Wrapper

本项目是末语引擎的 Android 包装外壳，用于将引擎核心、框架和游戏内容打包为 APK。

你可以将本项目作为 Android Studio 项目导入，或者直接使用 Gradle 命令行工具进行构建和安装。

你也可以将本项目作为模板，创建你自己的 Android 项目，并将引擎库和游戏内容集成到你的项目中。

## 环境要求

- JDK 17
- Android SDK 36
- Android NDK >=30
- 如需本地构建 `libmoyu.so`，请参考引擎主仓库的相关说明。

## 引擎库放置

从引擎主仓库的 Release 页面下载预构建的 Android 平台二进制包，解压后获得 `libmoyu.so` 文件。

将其放置到 Android 项目的 `jniLibs` 目录下：

```text
app/src/main/jniLibs/arm64-v8a/libmoyu.so
```

## 游戏内容放置

Android APK 的 assets 根目录为：

```text
app/src/main/assets/
```

游戏资源以 `index.json` 所在目录为基准，并固定从其下的内层 `assets/` 目录读取。因此这里存在两层不同含义的 `assets`：

```text
app/src/main/assets/          # Android APK assets 根目录
app/src/main/assets/assets/   # 游戏素材根目录
```

最终目录结构应类似于：

```text
app/src/main/assets/
├── index.json
├── main.js
└── assets/
    ├── data/
    │   └── ui.json
    ├── scenario/
    │   └── start.sixu
    ├── fonts/
    │   └── SourceHanSansSC-Regular.otf
    ├── ui/
    ├── audio/
    └── ...
```

## 编译与安装

在项目根目录执行：

```bash
# Linux/MacOS
./gradlew :app:assembleDebug

# Windows
gradlew.bat :app:assembleDebug
```

安装 debug APK：

```bash
# Linux/MacOS
./gradlew :app:installDebug

# Windows
gradlew.bat :app:installDebug
```

启动 Activity：

```bash
adb shell am start -n ink.momoyu.example/.MainActivity
```

构建 release APK：

```bash
# Linux/MacOS
./gradlew :app:assembleRelease

# Windows
gradlew.bat :app:assembleRelease
```

## License

本项目遵循 MIT 许可证，详情请参阅 LICENSE 文件。
