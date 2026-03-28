# CPoet Tools 🛠️

<div align="center">

**[中文版本](README.md)** | **[English Version](README-en.md)**

</div>

**Download:** [https://plugins.jetbrains.com/plugin/29450-cpoet-tool](https://plugins.jetbrains.com/plugin/29450-cpoet-tool) ⬇️

**CPoet Tools** is an IntelliJ IDEA plugin that integrates commonly used development utilities, such as generating incremental patches, Base64 encoding/decoding, hash value calculation, etc. 🚀

## Features ✨

1. **Incremental Patch Generation 📦**
    - Select files to quickly generate ZIP format incremental patch packages ⚡
    - Auto-generates path documentation 📝
    - Supports MapStruct ♻️

2. **Text & Encoding Utilities 🔠**
    - Convert between camelCase and underscore_case 🔄
    - Base64 encoding/decoding 🔏
    - Calculate MD5/SHA1 hash values 🔢

3. **Database Query Assistance 🗄️**
    - Concatenate multiple database values for IN conditions 🔗
    - Automatically determines whether to add single quotes based on column type 🤖


## Next V0.3.1 🚀

- [x] ✨ Support for automatic replacement of SQL placeholders
  -- 🥹 Most logs are printed like this:
  SELECT *
   FROM DEMO
   WHERE CREATE_USER = ?
   AND IS_DELETED <> ?['cpoet', 1];
  -- 😂 Use the plugin to quickly replace with:
   SELECT *
   FROM DEMO
   WHERE CREATE_USER = 'cpoet'
   AND IS_DELETED <> 1;
- [x] ✨ Add encoding type configuration for Base64 encoding and decoding (default UTF-8)
- [ ] 🐛 Fix the issue where patch generation file index was not refreshed completely
- [ ] 🐛 Fix the duplicate display issue in patch generation tree with multi-level modules
- [x] ✨ Compatible with IDEA versions 2022.3 to 2026.1

## Next V0.3.2 📅

- [ ] ✨ Add quick access to configuration options in Tools menu (language, Base64 encoding, etc.)
- [ ] ✨ Add quick generation of getter/setter methods and object Convert methods
- [ ] 🎉 Patch generation will be released as a standalone plugin as an important feature, while CPoet Tool will also retain the generation functionality
