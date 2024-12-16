<a title="MirrorTree Wiki" href="https://wiki.mirror.bearcabbage.top/"><img align="right" alt="MirrorTree logo" width="120" height="120" src="/asset/images/logo.svg"></a>

# Lantern-in-Storm

[![GitHub license](https://img.shields.io/github/license/BaicaiBear/Lantern-in-Storm
)](https://img.shields.io/github/license/BaicaiBear/Lantern-in-Storm)

> Once upon a time, there was a Mirror Tree.
 
## 待办事项（现在只有该条目是有用信息）

### TODO List:

- 修复关于水晶的搬运（骑乘）的错误
- 修复单击灯笼但多次执行代码的错误
- 玩家获得`Spirits`与成就的绑定
- 玩家在`unstable`区域的负面效果
- 对于`lantern`分配灵魂的交互设计
  - `lanternlevel`种类对应灵魂数`（1，2，4，8）/（1，2，3，4，5）`
  - 利用视角连续变化、结合渲染等
  - 为灯笼增加id序号标识（可选）
  - 添加/locatelantern [id]或者[player] 查找相关lantern的pos和world信息（可选）
  - 添加使用成就数和玩家灯笼内所存的灵魂数进行检查的/ce check [player]对于玩家的灵魂数做检查并且重置为正确的灵魂数（可选）
- 氛围渲染
  - `safe`和`unsafe`边缘和内部区域的迷雾效果（`client`）
     - 通过全局灯笼加载来实现对于未加载区块的迷雾+灯笼驱散效果（可选）
  - 在`unsafe`区域加入`unstable`相关的特效：（贴图丢失/闪烁/音效替换/生物与怪物 ai 随机替换/幽灵粒子效果/脚步声）（可选）
- `team`转为`gui`界面并且添加团队`tpa`权限和`home`权限（可选）

### DONE List：

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
