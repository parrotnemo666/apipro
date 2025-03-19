<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Email 發送系統</title>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<style>
/* 基本頁面樣式 */
body {
    font-family: Arial, sans-serif;
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    background-color: #1a1a1a;
    color: #ffffff;
}

/* 主容器樣式 */
.container {
    background-color: #222222;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 標題樣式 */
h1 {
    color: #0066cc;
    text-align: center;
    margin-bottom: 30px;
}

/* 表單組件樣式 */
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

/* 輸入框和文本框樣式 */
input[type="text"], input[type="email"], textarea {
    width: 100%;
    padding: 8px;
    border: 1px solid #444;
    border-radius: 4px;
    box-sizing: border-box;
    background-color: #333;
    color: #ffffff;
    margin-bottom: 5px;
}

/* 文本框特定樣式 */
textarea {
    height: 100px;
    resize: vertical;
}

/* 附件輸入框樣式 */
input[type="file"] {
    background-color: #333;
    color: #ffffff;
    padding: 5px;
    border-radius: 4px;
    width: 100%;
    box-sizing: border-box;
}

/* 按鈕樣式 */
.button {
    background-color: #0066cc;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    width: 100%;
    font-size: 16px;
}

.button:hover {
    background-color: #0052a3;
}

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
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
}

.toast.error {
    background-color: #d32f2f;
    border-left: 5px solid #b71c1c;
}

.toast.success {
    background-color: #2e7d32;
    border-left: 5px solid #1b5e20;
}

/* 載入中提示樣式 */
.loading {
    display: none;
    text-align: center;
    margin-top: 10px;
    color: #888;
}

/* 可選欄位提示 */
.optional {
    color: #666;
    font-size: 0.9em;
    margin-left: 5px;
}

/* 預覽區域樣式 */
.preview-area {
    margin-top: 10px;
    padding: 10px;
    background-color: #333;
    border-radius: 4px;
    display: none;
}

.preview-title {
    color: #888;
    margin-bottom: 10px;
    font-weight: bold;
}

.preview-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px;
    background-color: #444;
    margin-bottom: 5px;
    border-radius: 3px;
}

.file-info {
    display: flex;
    align-items: center;
    gap: 10px;
}

.image-preview {
    max-width: 50px;
    max-height: 50px;
    border-radius: 3px;
    object-fit: cover;
}

.file-details {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.file-name {
    color: #fff;
    font-size: 0.9em;
}

.file-size {
    color: #888;
    font-size: 0.8em;
}

.remove-file {
    background-color: #d32f2f;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 3px;
    cursor: pointer;
    font-size: 0.9em;
}

.remove-file:hover {
    background-color: #b71c1c;
}
</style>
</head>
<body>
    <div class="container">
        <h1>Email 發送系統</h1>
        <form id="emailForm">
            <!-- 收件人區域 -->
            <div class="form-group">
                <label for="recipients">收件人：</label>
                <input type="text" id="recipients" name="recipients" 
                    placeholder="example1@domain.com, example2@domain.com">
            </div>

            <!-- CC收件人區域 -->
            <div class="form-group">
                <label for="ccRecipients">副本收件人：<span class="optional">(選填)</span></label>
                <input type="text" id="ccRecipients" name="ccRecipients"
                    placeholder="cc1@domain.com, cc2@domain.com">
            </div>

            <!-- BCC收件人區域 -->
            <div class="form-group">
                <label for="bccRecipients">密件副本：<span class="optional">(選填)</span></label>
                <input type="text" id="bccRecipients" name="bccRecipients"
                    placeholder="bcc1@domain.com, bcc2@domain.com">
            </div>

            <!-- 郵件主旨 -->
            <div class="form-group">
                <label for="subject">主旨：</label>
                <input type="text" id="subject" name="subject">
            </div>

            <!-- 純文本內容 -->
            <div class="form-group">
                <label for="textbody">純文本內容：<span class="optional">(選填)</span></label>
                <textarea id="textbody" name="textbody"></textarea>
            </div>

            <!-- HTML內容 -->
            <div class="form-group">
                <label for="htmlbody">HTML內容：<span class="optional">(選填)</span></label>
                <textarea id="htmlbody" name="htmlbody"></textarea>
            </div>

            <!-- 附件上傳 -->
            <div class="form-group">
                <label for="attachments">附件：<span class="optional">(僅支援圖片，單檔最大5MB)</span></label>
                <input type="file" id="attachments" name="attachments" multiple accept="image/*">
                <div class="preview-area">
                    <div class="preview-title">已選擇的檔案：</div>
                    <div id="fileList" class="preview-list"></div>
                </div>
            </div>

            <!-- 提交按鈕 -->
            <button type="submit" class="button" id="submitBtn">發送郵件</button>
        </form>
        <!-- 載入中提示 -->
        <div class="loading">處理中...</div>
    </div>

    <!-- Toast 提示元素 -->
    <div id="toast" class="toast"></div>

    <script>
    $(document).ready(function() {
        const form = $('#emailForm');
        const submitBtn = $('#submitBtn');
        const loading = $('.loading');
        const toast = $('#toast');
        const fileInput = $('#attachments');
        const previewArea = $('.preview-area');
        const fileList = $('#fileList');
        
        // 存儲所有選擇的文件
        let selectedFiles = [];

        // 格式化文件大小
        function formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        }

        // 添加文件預覽
        function addFilePreview(file, index) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const previewItem = $(`
                    <div class="preview-item" data-index="${index}">
                        <div class="file-info">
                            <img src="${e.target.result}" class="image-preview" alt="圖片">

                        </div>
                        <button type="button" class="remove-file">移除</button>
                    </div>
                `);
                
                fileList.append(previewItem);
            };
            reader.readAsDataURL(file);
        }

        // 當選擇文件時
        fileInput.on('change', function(e) {
            const files = Array.from(e.target.files);
            
            // 驗證文件
            const validFiles = files.filter(file => {
                // 檢查文件類型
                if (!file.type.startsWith('image/')) {
                    showToast(`${file.name} 不是有效的圖片文件`, false);
                    return false;
                }
                // 檢查文件大小 (5MB)
                if (file.size > 5 * 1024 * 1024) {
                    showToast(`${file.name} 超過5MB限制`, false);
                    return false;
                }
                return true;
            });

            // 添加新文件到已選擇的文件列表
            selectedFiles = [...selectedFiles, ...validFiles];
            
            // 清空預覽區域並重新顯示所有文件
            fileList.empty();
            if (selectedFiles.length > 0) {
                previewArea.show();
                selectedFiles.forEach((file, index) => {
                    addFilePreview(file, index);
                });
            } else {
                previewArea.hide();
            }

            // 清空文件輸入框，允許重複選擇相同文件
            fileInput.val('');
        });

        // 移除文件
        fileList.on('click', '.remove-file', function() {
            const item = $(this).closest('.preview-item');
            const index = parseInt(item.data('index'));
            
            // 從數組中移除文件
            selectedFiles = selectedFiles.filter((_, i) => i !== index);
            
            // 重新渲染預覽
            fileList.empty();
            if (selectedFiles.length > 0) {
                selectedFiles.forEach((file, index) => {
                    addFilePreview(file, index);
                });
            } else {
                previewArea.hide();
            }
        });

        // 顯示提示訊息
        function showToast(message, isSuccess = true) {
            toast.text(message)
                .removeClass('error success')
                .addClass(isSuccess ? 'success' : 'error')
                .fadeIn();

            setTimeout(() => toast.fadeOut(), 3000);
        }

        // 讀取文件為 Base64
        function readFileAsBase64(file) {
            return new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onload = () => {
                    const base64 = reader.result.split(',')[1];
                    resolve(base64);
                };
                reader.onerror = reject;
                reader.readAsDataURL(file);
            });
        }

        // 表單提交
        form.on('submit', async function(e) {
            e.preventDefault();
            
            try {
                submitBtn.prop('disabled', true);
                loading.show();

                // 處理所有選擇的文件
                const attachments = await Promise.all(selectedFiles.map(async file => {
                    const base64Data = await readFileAsBase64(file);
                    return {
                        fileName: file.name,
                        contentType: file.type,
                        imageData: base64Data
                    };
                }));

                // 準備發送的數據
                const emailData = {
                    recipients: $('#recipients').val().trim().split(',').map(email => email.trim()),
                    ccRecipients: $('#ccRecipients').val().trim() ? 
                        $('#ccRecipients').val().trim().split(',').map(email => email.trim()) : [],
                    bccRecipients: $('#bccRecipients').val().trim() ? 
                        $('#bccRecipients').val().trim().split(',').map(email => email.trim()) : [],
                    subject: $('#subject').val().trim(),
                    textbody: $('#textbody').val().trim(),
                    htmlbody: $('#htmlbody').val().trim(),
                    attachments: attachments
                };

                // 發送請求
                const response = await $.ajax({
                    url: '/apipro/api2/email/v2/send',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(emailData)
                });

                console.log('Success:', response);
                showToast('郵件發送成功！');
                
                // 重置表單和文件列表
                form[0].reset();
                selectedFiles = [];
                fileList.empty();
                previewArea.hide();

            } catch (error) {
                console.error('Error:', error);
                showToast(error.responseJSON?.errorMessage || '發送失敗，請稍後重試', false);
            } finally {
                submitBtn.prop('disabled', false);
                loading.hide();
            }
        });
    });
    </script>
</body>
</html>