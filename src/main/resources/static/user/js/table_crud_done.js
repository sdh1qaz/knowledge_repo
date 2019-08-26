// 刷新页面
function refresh() {
	$table.bootstrapTable('refresh');
}

// 刷新按钮点击事件
$("#btn_refresh").on("click", function() {
	refresh();
});

// 切换视图按钮点击事件
$("#btn_toggleview").on("click", function() {
	$table.bootstrapTable('toggleView');
});

// 显隐分页按钮点击事件
$("#btn_togglepage").on("click", function() {
	$table.bootstrapTable('togglePagination');
});

// 跳转按钮点击事件
$("#btn_selectpage").on("click", function() {
	var pageNum = 3;
	$table.bootstrapTable('selectPage', pageNum);
});
