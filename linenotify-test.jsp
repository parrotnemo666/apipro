<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
    <meta charset="UTF-8">
    <!-- 確保移動設備的響應式設計 -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Line Notify 測試頁面</title>
    <!-- 引入 jQuery 函式庫，用於 AJAX 請求和 DOM 操作 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <style>
        /* 基本頁面樣式 */
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #1a1a1a; /* 深色背景 */
            color: #ffffff;
        }
        /* 主容器樣式 */
        .container {
            background-color: #222222; /* 稍淺的深色背景，形成層次感 */
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }
        /* 標題樣式：使用 LINE 的主題色 */
        h1 {
            color: #00b900; /* LINE 的綠色 */
            text-align: center;
            margin-bottom: 30px;
        }
        /* 表單組件的通用樣式 */
        .form-group {
            margin-bottom: 20px;
        }
        /* 標籤樣式 */
        label {
            display: block;
            margin-bottom: 5px;
            color: #cccccc;
            font-weight: bold;
        }
        /* 輸入框和文本框的共用樣式 */
        input[type="text"], 
        textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #444;
            border-radius: 4px;
            box-sizing: border-box;
            background-color: #333;
            color: #ffffff;
        }
        /* 文本框特定樣式 */
        textarea {
            height: 100px;
            resize: vertical; /* 只允許垂直調整大小 */
        }
        /* 字數統計器樣式 */
        .char-count {
            color: #888;
            font-size: 0.9em;
            text-align: right;
            margin-top: 5px;
        }
        /* 按鈕樣式 */
        .button {
            background-color: #00b900;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
            font-size: 16px;
        }
        /* 按鈕懸停效果 */
        .button:hover {
            background-color: #009900;
        }
        /* 禁用按鈕樣式 */
        .button:disabled {
            background-color: #444;
            cursor: not-allowed;
        }
        /* Toast 提示框樣式 */
        .toast {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 25px;
            border-radius: 4px;
            color: white;
            font-size: 14px;
            display: none;
            z-index: 1000;
            max-width: 300px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.2);
        }
        /* 錯誤提示樣式 */
        .toast.error {
            background-color: #d32f2f;
            border-left: 5px solid #b71c1c;
        }
        /* 成功提示樣式 */
        .toast.success {
            background-color: #2e7d32;
            border-left: 5px solid #1b5e20;
        }
        /* 載入中提示的樣式 */
        .loading {
            display: none;
            text-align: center;
            margin-top: 10px;
            color: #888;
        }
    </style>
</head>
<body>
    <!-- 主容器 -->
    <div class="container">
        <h1>Line Notify 測試工具</h1>
        <!-- 表單區域 -->
        <form id="lineNotifyForm">
            <!-- Token 輸入區 -->
            <div class="form-group">
                <label for="token">Access Token:</label>
                <input type="text" id="token" name="token" required>
            </div>
            <!-- 訊息輸入區 -->
            <div class="form-group">
                <label for="message">訊息內容:</label>
                <textarea id="message" name="message" required></textarea>
                <div class="char-count">剩餘字數: <span id="charCount">200</span></div>
            </div>
            <!-- 提交按鈕 -->
            <button type="submit" class="button" id="submitBtn">發送通知</button>
        </form>
        <!-- 載入中提示 -->
        <div class="loading">處理中...</div>
    </div>

    <!-- Toast 提示元素 -->
    <div id="toast" class="toast"></div>

    <script>
        $(document).ready(function() {
            // 常量定義
            const MAX_LENGTH = 200;  // 訊息最大長度限制
            
            // DOM 元素快取
            let submitBtn = $('#submitBtn');       // 提交按鈕
            let messageField = $('#message');      // 訊息輸入框
            let charCount = $('#charCount');       // 字數計數器
            let loading = $('.loading');           // 載入提示
            let toast = $('#toast');               // Toast 提示框

            // 錯誤代碼與對應訊息的映射表
            const ERROR_MESSAGES = {
                'LN001': '請求內容不能為空，請檢查後重試',
                'LN002': 'Token 不能為空，請檢查後重試',
                'LN003': '訊息內容不能為空，請檢查後重試',
                'LN101': 'SSL 連接錯誤，請聯絡相關人員',
                'LN102': '代理伺服器連接失敗，請聯絡相關人員',
                'LN103': '網路連接錯誤，請聯絡相關人員',
                'LN201': '無效的存取權杖，請檢查Token',
                'LN301': 'LINE Notify 服務異常，請稍後再試',
                'LN501': '資料庫操作失敗，請聯絡相關人員',
                'LN502': '資料庫未知錯誤，請聯絡相關人員',
                'LN999': '系統發生未知錯誤，請聯絡相關人員'
            };

            /**
             * 顯示 Toast 提示訊息
             * @param {string} message - 要顯示的訊息
             * @param {boolean} isSuccess - 是否為成功提示
             */
            function showToast(message, isSuccess = true) {
                toast.text(message)
                    .removeClass('error success')
                    .addClass(isSuccess ? 'success' : 'error')
                    .fadeIn();

                // 3秒後自動隱藏
                setTimeout(() => {
                    toast.fadeOut();
                }, 3000);
            }

            // 監聽訊息輸入，實時更新字數統計
            messageField.on('input', function() {
                let remaining = MAX_LENGTH - $(this).val().length;
                charCount.text(remaining);
                
                // 根據剩餘字數設置按鈕狀態和顏色
                if (remaining < 0) {
                    charCount.css('color', 'red');
                    submitBtn.prop('disabled', true);
                } else {
                    charCount.css('color', '#888');
                    submitBtn.prop('disabled', false);
                }
            });

            // 表單提交處理
            $('#lineNotifyForm').on('submit', function(e) {
                e.preventDefault(); // 阻止表單預設提交行為
                
                // 獲取表單數據
                let token = $('#token').val().trim();
                let message = messageField.val().trim();
                
                // 前端驗證
                if (!token) {
                    showToast(ERROR_MESSAGES['LN002'], false);
                    return;
                }
                if (!message) {
                    showToast(ERROR_MESSAGES['LN003'], false);
                    return;
                }
                if (message.length > MAX_LENGTH) {
                    showToast(`訊息內容不能超過 ${MAX_LENGTH} 字`, false);
                    return;
                }

                // 發送前的 UI 更新
                submitBtn.prop('disabled', true);
                loading.show();

                // 發送 AJAX 請求
                $.ajax({
                    url: '/apipro/api2/linenotifyV2',    // API 端點
                    type: 'POST',                       // HTTP 方法
                    contentType: 'application/json',     // 請求內容類型
                    data: JSON.stringify({              // 請求數據
                        token: token,
                        message: message
                    }),
                    success: function(response) {
                        // 成功處理
                        console.log('成功響應:', response);
                        showToast('發送成功！');
                        // 清空表單
                        messageField.val('');
                        charCount.text(MAX_LENGTH);
                    },
                    error: function(xhr) {
                        // 錯誤處理
                        console.log('錯誤狀態:', xhr.status);
                        console.log('錯誤響應:', xhr.responseText);
                        
                        let errorMessage;
                        try {
                            // 解析錯誤響應
                            let response = JSON.parse(xhr.responseText);
                            // 使用預定義的錯誤訊息，如果沒有對應的訊息則使用預設訊息
                            errorMessage = ERROR_MESSAGES[response.errorCode] || 
                                         `錯誤代碼 ${response.errorCode}：請聯絡相關人員`;
                            
                            // 記錄詳細錯誤信息
                            console.log('錯誤代碼:', response.errorCode);
                            console.log('錯誤類型:', response.errorType);
                            console.log('錯誤消息:', response.errorMessage);
                        } catch(e) {
                            // JSON 解析失敗處理
                            console.error('解析錯誤響應失敗:', e);
                            errorMessage = '系統發生未知錯誤，請聯絡相關人員';
                        }
                        // 顯示錯誤提示
                        showToast(errorMessage, false);
                    },
                    complete: function() {
                        // 無論成功或失敗都執行的操作
                        submitBtn.prop('disabled', false);
                        loading.hide();
                    }
                });
            });
        });
    </script>
</body>
</html>