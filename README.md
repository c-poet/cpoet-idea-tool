# CPoet Tools 🛠️

**Download:** [https://plugins.jetbrains.com/plugin/29450-cpoet-tool](https://plugins.jetbrains.com/plugin/29450-cpoet-tool) ⬇️

**CPoet Tools** is an IntelliJ IDEA plugin that integrates commonly used development utilities, such as generating incremental patches, Base64 encoding/decoding, calculating MD5 and SHA1 hash values, etc. 🚀

## Features ✨

1. **Incremental Patch Generation 📦**
   - Select files to quickly generate ZIP format incremental patch packages 🚀
   - Auto-generates path documentation 📄
   - Supports MapStruct 🔄

2. **Text & Encoding Utilities 🔤**
   - Convert between camelCase and underscore_case 🔄
   - Base64 encoding/decoding 🔐
   - Calculate MD5/SHA1 hash values 🔢

3. **Database Query Assistance 🗃️**
   - Concatenate multiple database values for IN conditions 🔗
   - Automatically determines whether to add single quotes based on column type 🤖

**CPoet Tools** 是一个 IntelliJ IDEA 插件，集成了开发中常用的工具，比如生成增量补丁、Base64 编码和解码、计算字符串的 MD5 和 SHA1 值等。🎯

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


## Next V0.3.1
- [ ] 支持自动替换Database占位符
  ```sql
  SELECT *
   FROM DEMO
   WHERE CREATE_USER = ?
   AND IS_DELETED <> ?['cpoet', 1];
  ```
- [ ] 修复补丁生成文件索引未刷新完成的问题
- [ ] 修复补丁生成树多层级模块的情况下显示重复问题
