<a title="MirrorTree Wiki" href="https://wiki.mirror.bearcabbage.top/"><img align="right" alt="MirrorTree logo" width="120" height="120" src="/asset/images/logo.svg"></a>

# Lantern-in-Storm

[![GitHub license](https://img.shields.io/badge/license-CC_1.0-blue)](https://creativecommons.org/licenses/by-nc-sa/1.0/)

> Once upon a time, there was a Mirror Tree.
 
## 待办事项（现在只有该条目是有用信息）

TODO List:

- 重构`Spirit`管理用的表
- 关于水晶的搬运（骑乘）
- 对于新的`Manager`的实现
- 对于`lantern`的`level`种类数`（1，2，4，8）/（1，2，3，4/5）`
- `level`对应的`issafe`和关于`unsafe`区域的区域的迷雾效果（`client`）
- 在`unsafe`区域加入`unstable`相关的特效：（贴图丢失/闪烁/音效替换/生物与怪物 ai 随机替换）（可选）
- `commands`关于测试用的命令补充
- `team`转为`gui`界面并且添加团队`tpa`权限和`home`权限（可选）

DONE List：

- 一个没用的`team`系统和一个测试用的`commands`
- 一个`player`的`rtpspawn`实现
- 两种`Lantern`的注册，渲染，事件调用

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
