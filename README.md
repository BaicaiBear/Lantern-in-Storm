<a title="MirrorTree Wiki" href="https://wiki.mirror.bearcabbage.top/"><img alt="MirrorTree logo" align="right" height="168" src="/asset/images/logo.svg" width="168"/></a>

# Lantern-in-Storm

[![GitHub license](https://img.shields.io/github/license/BaicaiBear/Lantern-in-Storm
)](https://img.shields.io/github/license/BaicaiBear/Lantern-in-Storm)

> Once upon a time, there was a Mirror Tree, yet merely the shadow of a dream.

为了便于使用，在 12.30 之前，`README.md` 将会以中文为主要工作语言。

## 待办事项（现在只有该条目是有用信息）

我们建议在添加新待办事项时依照`markdownlint`的规则（但是不遵守也无所谓，会有人工自动格式化 XD）。




#### DONE List

- 接入了[AnnoyingEffects](https://github.com/AC-Mnky/AnnoyingEffects)提供的负面效果 (1.2)
- 添加了公共灯笼和灵魂碎片item，成就碎片计划通过FTBQ任务有限发放
- `lantern`分配灵魂的简易左右键交互设计
- 完善了PrivateLantern的特性
- 参考 [TeamGalacticraft/Galacticraft](https://github.com/TeamGalacticraft/Galacticraft/)中氧气泡的实现方式实现灯笼区域边界渲染 (12.21)
- 修复了关于水晶的搬运（骑乘）的错误 (12.16)
- 重构`Spirit`数据结构和业务逻辑 (12.16)
- 两种`Lantern`的注册，渲染，事件调用 (12.14)
- 一个`player`的`rtpspawn`实现 (较早)
- 一个没用的`team`系统和一个测试用的`commands` (较早，弃用)

## Introduction

A Minecraft mod for the new loop of MirrorTree.

## About This Repository

(say sth here)

### Core File Structure

```bash
.
├── gradle
├── src
│   ├── main
│   │   └── java
│   └── resources
├── asset          # documentation assets
├── README.md      # overview intro
└──  LICENSE
```

### Documentation Norm

A new java documentation norm remains unspecified.

### Git Norm

Here we propose a recommended Git message template.

```git
<type>(<scoop>): <subject>
// <BLANK LINE>
<body>
// <BLANK LINE>
<footer>
```

- `<type>`: The type of the commit, such as `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, etc.
- `<scoop>` (optional) : The scope of the commit, such as `algorithm`, `communication`, `archive`, etc.
- `<subject>`: A brief summary of the commit. It should be in the imperative mood without `dot (.)` at the end of a line.
- `<body>` (optional) : A detailed description of the commit.
- `<footer>` (optional) : A footer for the commit, such as `BREAKING CHANGE`, `ISSUES CLOSED`, etc.

> [!NOTE]
> Although pull requests are different from commits, they may follow the same format as commit messages.
