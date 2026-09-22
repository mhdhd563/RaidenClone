# Raiden Clone - 雷电风格飞行射击游戏

经典雷电致敬作品，使用 LibGDX 开发，支持 Android / Desktop。

## ✨ 游戏特性
- 🎮 **雷电风格** 垂直卷轴射击
- 👾 **4 种敌机 + BOSS** 独特 AI 模式
- 🌊 **5 种波次阵型** + 每 5 波 BOSS 战
- 💣 **无限清屏炸弹** - 双击/空格触发全屏冲击波
- ⚡ **火力升级系统** - 4 级扩散弹
- 🎁 **道具掉落** - 火力/炸弹/生命/分数
- ✨ **粒子特效** - 爆炸/拖尾/冲击波/屏幕震动
- 📱 **触摸/键盘双模** - 拖动移动、自动射击、双击放炸弹
- 🎨 **100% 程序化生成** - 无外部素材依赖

## 🚀 快速开始

### 本地构建 APK
```bash
# 1. 克隆项目
git clone https://github.com/<你的用户名>/RaidenClone.git
cd RaidenClone

# 2. 配置 Android SDK 路径 (根据你的系统修改)
echo "sdk.dir=$HOME/Android/Sdk" > local.properties  # Linux
# echo "sdk.dir=$HOME/Library/Android/sdk" > local.properties  # macOS
# echo "sdk.dir=C:\\Users\\$USER\\AppData\\Local\\Android\\Sdk" > local.properties  # Windows

# 3. 构建
chmod +x gradlew
./gradlew android:assembleDebug
```

**输出**: `android/build/outputs/apk/debug/android-debug.apk`

### 桌面版运行
```bash
./gradlew desktop:run
```

## 🤖 GitHub Actions 自动构建 (推荐)

推送代码即可自动生成 APK，**无需本地配置 Android SDK**：

1. **Fork/创建仓库** 到你的 GitHub
2. **推送代码**:
   ```bash
   git remote add origin https://github.com/<你的用户名>/RaidenClone.git
   git push -u origin main
   ```
3. **查看 Actions** → 点击最新工作流 → 下载 `RaidenClone-Debug-APK`
4. **或创建 Release** 自动打包:
   ```bash
   git tag v1.0.0 && git push origin v1.0.0
   ```

## 🎮 操作说明

| 平台 | 移动 | 射击 | 炸弹(无限) |
|------|------|------|------------|
| 触摸屏 | 拖动 | 自动 | 双击屏幕 |
| 键盘 | WASD/方向键 | 自动 | 空格键 |

## 📁 项目结构
```
├── core/           # 核心游戏逻辑 (跨平台)
├── android/        # Android 启动器
├── desktop/        # 桌面启动器
├── .github/workflows/  # CI/CD 配置
└── BUILD.md        # 详细构建文档
```

## 🛠 技术栈
- **LibGDX** 1.12.1 - 跨平台游戏框架
- **Kotlin/Gradle** - 构建系统
- **Java 17** - 目标版本
- **Android SDK 34** - 编译目标

## 📄 许可证
MIT License - 可自由使用修改

---

**享受游戏！** 🎮 有问题欢迎提 Issue