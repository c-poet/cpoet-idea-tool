# CPoet Tools 🛠️

<div align="center">

**[中文版本](README.md)** | **[English Version](README-en.md)**

</div>

**Download:** [https://plugins.jetbrains.com/plugin/29450-cpoet-tool](https://plugins.jetbrains.com/plugin/29450-cpoet-tool) ⬇️

**CPoet Tools** 是一款 IntelliJ IDEA 插件，集成了常用的开发工具，例如生成增量补丁、Base64 编码/解码、Hash 值计算等。

## 主要功能 🌟

1. **增量补丁生成 📦**
   - 选择文件快速生成 ZIP 格式的增量补丁包 ⚡
   - 自动生成路径说明文件 📝
   - 支持 MapStruct ♻️

2. **文本与编码工具 🔠**
   - 驼峰式和下划线式转换 🔄
   - Base64 编码/解码 🔏
   - 计算 MD5/SHA1 哈希值 🔢

3. **数据库查询辅助 🗄️**
   - 将多个数据库值拼接为 IN 条件 🔗
   - 根据列类型自动判断是否添加单引号 🤖


## Next V0.3.1 🚀

- [x] ✨支持自动替换SQL中的占位符
  ```sql
  -- 🥹大多数日志打印出来是这样的：
  SELECT *
   FROM DEMO
   WHERE CREATE_USER = ?
   AND IS_DELETED <> ?['cpoet', 1];
  -- 😂使用插件可以快速替换为：
   SELECT *
   FROM DEMO
   WHERE CREATE_USER = 'cpoet'
   AND IS_DELETED <> 1;
  ```
- [x] ✨新增Base64配置编码和解码的编码类型（默认UTF-8）
- [ ] 🐛修复补丁生成文件索引未刷新完成的问题
- [ ] 🐛修复补丁生成树多层级模块的情况下显示重复问题
- [x] ✨兼容Idea2022.3至2026.1版本

## Next V0.3.2 📅

- [ ] ✨新增需要快速的配置项到Tools菜单中（语言、Base64编码等）
- [ ] ✨新增快速生成get/set方法和对象Convert方法
- [ ] 🎉补丁生成作为重要功能将单独发布插件，同时CPoet Tool也同步保留生成功能

## 变更日志
查看：[changes.html](changes.html)
