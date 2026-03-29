# Risk Guard 專案文檔中心

歡迎來到 Risk Guard 技術文檔中心。本專案是一個基於 Spring Boot 與六邊形架構開發的風險管理/安全防護系統，核心功能包括 JWT 認證、動態權限過濾與選單管理。

---

## 🏗️ 1. 系統架構
瞭解專案的核心設計原則與技術架構。
- **[SYSTEM_OVERVIEW.md](./architecture/SYSTEM_OVERVIEW.md)**: 六邊形架構詳解、JWT 認證與授權流程。

## 🎯 2. 功能模組 (Features)
深入瞭解各個業務模組的實作細節。
- **[認證模組 (Auth)](./features/auth/README.md)**: 登入、註冊、Token 刷新與安全機制。
- **[選單模組 (Menu)](./features/menu/README.md)**: 動態選單生成、遞迴權限過濾。

## 📡 3. API 規範
- **[統一 API 契約](./api/api-contract.yml)**: 基於 OpenAPI 3.0 的接口定義，涵蓋認證與選單模組。

## 📖 4. 開發者指南
- **[DEVELOPER_GUIDE.md](./guides/DEVELOPER_GUIDE.md)**: 環境架設、啟動步驟、編碼規範與測試說明。

## 🛠️ 5. 工具與腳本
位於 `doc/tools/scripts/`：
- **`api-test.http`**: IntelliJ IDEA 專用測試文件，支援 Token 自動化管理與環境變數，推薦開發時使用。
- **`api-test.sh`**: 整合測試腳本，自動執行登入、選單獲取與個人資訊查詢。
- **`test-menu-auth.sh`**: 針對選單權限修復的專屬測試工具。

---
**提示**: 本目錄經過重新組織，旨在保持文檔與程式碼的高度同步。如需查看代碼實現，請參考專案根目錄下的各個 `guard-*` 模組。
