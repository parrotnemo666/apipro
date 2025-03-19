<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>外匯交易管理系統</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gray-100">
    <div class="max-w-4xl mx-auto p-6">
        <h1 class="text-2xl font-bold mb-6">外匯交易管理系統</h1>

        <!-- 顯示操作訊息 -->
        <c:if test="${not empty message}">
            <div class="mb-4 p-4 ${message.type == 'error' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'} rounded">
                ${message.content}
            </div>
        </c:if>

        <div class="bg-white rounded-lg shadow p-6 mb-6">
            <div class="space-y-6">
                <!-- 1. 新增CSV到歷史表單 -->
                <div class="border rounded-lg p-4 bg-gray-50">
                    <h3 class="font-medium mb-3">1. 新增CSV到歷史表單</h3>
                    <form action="forexServlet" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="import">
                        <div class="flex items-center gap-4">
                            <div class="relative">
                                <input type="file" name="csvFile" accept=".csv" required
                                       class="absolute inset-0 opacity-0 w-full h-full cursor-pointer">
                                <button type="submit" 
                                        class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
                                    選擇CSV檔案並新增
                                </button>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- 2. 從歷史表單刪除CSV內容 -->
                <div class="border rounded-lg p-4 bg-gray-50">
                    <h3 class="font-medium mb-3">2. 從歷史表單刪除CSV內容</h3>
                    <form action="forexServlet" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="delete">
                        <div class="flex items-center gap-4">
                            <div class="relative">
                                <input type="file" name="csvFile" accept=".csv" required
                                       class="absolute inset-0 opacity-0 w-full h-full cursor-pointer">
                                <button type="submit" 
                                        class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">
                                    選擇CSV檔案並刪除
                                </button>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- 3. 檢查CSV是否與歷史表單重複 -->
                <div class="border rounded-lg p-4 bg-gray-50">
                    <h3 class="font-medium mb-3">3. 檢查CSV是否與歷史表單重複</h3>
                    <form action="forexServlet" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="check">
                        <div class="flex items-center gap-4">
                            <div class="relative">
                                <input type="file" name="csvFile" accept=".csv" required
                                       class="absolute inset-0 opacity-0 w-full h-full cursor-pointer">
                                <button type="submit" 
                                        class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600">
                                    選擇CSV檔案並檢查
                                </button>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- 顯示已選擇的檔案名稱 -->
                <c:if test="${not empty selectedFileName}">
                    <div class="mt-4 p-2 bg-gray-100 rounded">
                        <span class="text-gray-600">
                            目前選擇的檔案：${selectedFileName}
                        </span>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- 功能說明區塊 -->
        <div class="bg-blue-50 rounded-lg p-4">
            <h3 class="font-semibold mb-2">功能說明</h3>
            <ul class="space-y-2 text-sm text-gray-600">
                <li>• 新增功能：將CSV檔案內容新增至歷史表單中</li>
                <li>• 刪除功能：根據CSV檔案內容從歷史表單中刪除對應記錄</li>
                <li>• 檢查功能：檢查CSV檔案中的交易是否與歷史表單中的記錄重複</li>
                <li>• 重複判定：根據交易序號、身分證字號、金額三個條件進行比對</li>
            </ul>
        </div>
    </div>

    <script>
        // 當選擇檔案時更新顯示的檔案名稱
        document.querySelectorAll('input[type="file"]').forEach(input => {
            input.addEventListener('change', function() {
                const fileName = this.files[0]?.name;
                if (fileName) {
                    const form = this.closest('form');
                    const button = form.querySelector('button');
                    button.textContent = '已選擇: ' + fileName;
                }
            });
        });
    </script>
</body>
</html>