<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AES 加密/解密工具</title>
    <!-- 引入 Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- 引入 jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        .form-container {
            max-width: 800px;
            margin: 30px auto;
            padding: 20px;
            background-color: #f8f9fa;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .result-box {
            background-color: #ffffff;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            padding: 10px;
            margin-top: 10px;
        }
        .info-text {
            color: #6c757d;
            font-size: 0.9rem;
        }
        .loading {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(255, 255, 255, 0.8);
            display: none;
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }
        .loading-content {
            text-align: center;
            padding: 20px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>
    <!-- Loading 遮罩 -->
    <div class="loading">
        <div class="loading-content">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <div class="mt-2">處理中...</div>
        </div>
    </div>

    <div class="container">
        <div class="form-container">
            <h2 class="text-center mb-4">AES 加密/解密工具</h2>
            <p class="info-text text-center mb-4">
                AES數據塊長度為128位，所以IV長度需要為16個字符，<br>
                密鑰根據指定密鑰位數分別為16、24、32個字符
            </p>

            <form id="aesForm" method="post">
                <!-- 輸入區域 -->
                <div class="mb-3">
                    <label for="inputText" class="form-label">請輸入要加密或解密的內容：</label>
                    <textarea class="form-control" id="inputText" name="inputText" rows="4"></textarea>
                </div>

                <!-- 密鑰和IV顯示區域 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="key" class="form-label">密鑰：</label>
                        <input type="text" class="form-control" id="key" name="key" readonly>
                    </div>
                    <div class="col-md-6">
                        <label for="iv" class="form-label">IV：</label>
                        <input type="text" class="form-control" id="iv" name="iv" readonly>
                    </div>
                </div>

                <!-- 結果顯示區域 -->
                <div class="mb-3">
                    <label for="result" class="form-label">結果：</label>
                    <textarea class="form-control" id="result" name="result" rows="4" ></textarea>
                </div>

                <!-- 按鈕組 -->
                <div class="d-flex gap-2 justify-content-center">
                    <button type="button" class="btn btn-primary" onclick="handleEncrypt()">
                        <i class="bi bi-lock"></i> AES加密
                    </button>
                    <button type="button" class="btn btn-secondary" onclick="handleDecrypt()">
                        <i class="bi bi-unlock"></i> AES解密
                    </button>
                    <button type="button" class="btn btn-danger" onclick="handleClear()">
                        <i class="bi bi-trash"></i> 清空所有
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // 顯示載入中遮罩
        function showLoading() {
            $('.loading').css('display', 'flex');
        }

        // 隱藏載入中遮罩
        function hideLoading() {
            $('.loading').css('display', 'none');
        }

        // 加密處理函數
        function handleEncrypt() {
            const inputText = $('#inputText').val();
            if (!inputText) {
                alert('請輸入要加密的內容！');
                return;
            }

            showLoading();
            
            $.ajax({
                url: '/apipro/api2/aes/v2/encrypt',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    data: inputText
                }),
                success: function(response) {
                    if (response.errorCode) {
                        alert(`錯誤：${response.errorMessage}`);
                    } else {
                        $('#key').val(response.key);
                        $('#iv').val(response.iv);
                        $('#result').val(response.data);
                    }
                },
                error: function(xhr, status, error) {
                    alert('加密過程發生錯誤：' + error);
                },
                complete: function() {
                    hideLoading();
                }
            });
        }

        // 解密處理函數
        function handleDecrypt() {
            const inputText = $('#inputText').val();
            const key = $('#key').val();
            const iv = $('#iv').val();

            if (!inputText || !key || !iv) {
                alert('請確保已輸入加密文本、密鑰和IV！');
                return;
            }

            showLoading();

            $.ajax({
                url: '/apipro/api2/aes/v2/decrypt',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    data: inputText,
                    key: key,
                    iv: iv
                }),
                success: function(response) {
                    if (response.errorCode) {
                        alert(`錯誤：${response.errorMessage}`);
                    } else {
                        $('#result').val(response.data);
                    }
                },
                error: function(xhr, status, error) {
                    alert('解密過程發生錯誤：' + error);
                },
                complete: function() {
                    hideLoading();
                }
            });
        }

        // 清空所有欄位
        function handleClear() {
            $('#inputText').val('');
            $('#key').val('');
            $('#iv').val('');
            $('#result').val('');
        }

        // 頁面載入完成後的初始化
        $(document).ready(function() {
            // 可以在這裡添加任何需要的初始化代碼
            console.log('頁面已完成載入');
        });
    </script>
</body>
</html>