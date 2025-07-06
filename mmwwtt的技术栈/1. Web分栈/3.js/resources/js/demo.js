// 倒计时
function countDownTime(time){
	let nowTime = +new Date();
	let inputTime = +new Date(time);
	let times = (inputTime - nowTime)/1000; //获得秒数
	let d = parseInt(times / 60/60/24);
	let h = parseInt(times / 60/60 %24);
	let m = parseInt(times / 60 % 60);
	let s = parseInt(times % 60);
	console.log(d + '天' + h + '时' + m + '分' + s + '秒') 
	return d + '天' + h + '时' + m + '分' + s + '秒';
}


// 切换背景图案
let div1_imgs = document.querySelectorAll('#div1 .imgs');
let div1_demoBgImg = document.querySelector('#div1');
for(let i = 0; i < div1_imgs.length; i++) {
	div1_imgs[i].onclick = function() {
		div1_demoBgImg.style.backgroundImage = 'url(' + this.src + ')';
	}
}


// 全选和全不选
let div2_all = document.querySelector('#div2 .formDemo1 .all');
let div2_fruits = document.querySelectorAll('#div2 .formDemo1 .fruit');
div2_all.onclick = function() {
	for(let i = 0; i < div2_fruits.length; i++) {
		div2_fruits[i].checked = this.checked;
	}
}
for(let i = 0; i < div2_fruits.length; i++) {
	div2_fruits[i].onclick = function() {
		div2_all.checked = true;
		for(let i = 0; i < div2_fruits.length; i++) {
			if(!div2_fruits[i].checked) {
				div2_all.checked = false;
				break;
			}
		}
	}
}

//tab栏切换
var div3_lis = document.querySelectorAll('#div3 li');
var div3_contents = document.querySelectorAll('#div3 .content div')
for(let i = 0; i < div3_lis.length; i++) {
	div3_lis[i].onclick = function() {
		for(let i = 0; i < div3_lis.length; i++) {
			div3_lis[i].style.backgroundColor = 'skyblue';
			div3_contents[i].style.display = 'none';
		}
		this.style.backgroundColor = 'pink';
		div3_contents[i].style.display = 'block';
	}
}

//练习 下拉列表
let div4_dropDownUl = document.querySelector('#div4 .dropDownUl');
let div4_dropDownLi = div4_dropDownUl.children;
for(let i = 0; i < div4_dropDownLi.length; i++) {
	div4_dropDownLi[i].onmouseover = function() {
		this.children[1].style.display = 'block';
	}
	div4_dropDownLi[i].onmouseout = function() {
		this.children[1].style.display = 'none';
	}
}

//练习 发布留言案例
let div5_leavingBtn = document.querySelector('#div5 button');
let div5_messageTextarea = document.querySelector('#div5 textarea');
let div5_messageUl = document.querySelector('#div5 ul');

let div5_as_1 =  div5_messageUl.querySelectorAll('#div5 a');
div5_leavingBtn.onclick = function() {
	if (div5_messageTextarea.value == ''){
		alert('请输入内容');
		return;
	}
	let li = document.createElement('li');
	li.innerHTML = div5_messageTextarea.value + "<button class='delete'>删除</button>";
	div5_messageUl.appendChild(li);
	div5_messageUl.insertBefore(li, div5_messageUl.children[0]);
	let div5_deleteBtns = div5_messageUl.querySelectorAll('#div5 .delete');
	for(let i = 0; i < div5_deleteBtns.length; i++){
		div5_deleteBtns[i].onclick = function() {
			console.log(div5_deleteBtns);
			div5_messageUl.removeChild(this.parentNode); //将包含 a 的父节点li删除
		}
	}
}

//动态生成表格
let div6_datas = [{
		name: '小红',
		subject: '高数',
		score: 100
	},{
		name: '小明',
		subject: '高数',
		score: 180
	},{
		name: '小蓝',
		subject: '高数',
		score: 18
	}]
	
let div6_studentTable = document.querySelector('#div6 .studentTable');
for (let i = 0; i < div6_datas.length; i++) {
	let tr = document.createElement('tr'); //根据数量创建行
	for (let k in div6_datas[i]) {
		let td = document.createElement('td');
		td.innerHTML = div6_datas[i][k];  //给每个单元格数据
		tr.appendChild(td);      // tr添加td
	}
	let td = document.createElement('td');
	td.innerHTML = "<a href='javascript:;'>删除</a>";
	tr.appendChild(td);
	div6_studentTable.appendChild(tr)  //table 添加 tr
}
let deletes = document.querySelectorAll('#studentTable a');
for (let i = 0; i < deletes.length; i++) {
	deletes[i].onclick = function() {
		studentTable.removeChild(this.parentNode.parentNode);
	}
}



//键盘事件
/* 执行顺序:down,press,up */
//keyup 按键弹起时触发
let div7 = document.querySelector('#div7');
div7.onkeyup = function(e) {
	console.log('我弹起了up');
	console.log(e);
	console.log(e.keyCode);//获得按键的ASCLL值 keyup/keydown不区分大小写 a/A 都是65
}
//keydown 按键按下时触发
div7.onkeyup.onkeydown = function(e) {  
	console.log('我按下了down');
}
//keypress 按键按下时触发 但不能识别功能键，ctrl,shift,箭头等
div7.onkeyup.onkeypress = function(e) {
	console.log('我按下了press');
	console.log(e.keyCode); //获得按键的ASCLL值 区分字母大小写
}

//按s光标定到输入框 练习
let div7_search = document.querySelector('#div7 input');
document.addEventListener('keyup', function(e) {
	if(e.keyCode === 83) {
		div7_search.focus();  //让搜索框获得焦点
	}
})
	
//模拟京东快递单号的查询效果  练习
let div7_con = document.querySelector('#div7 .con');
let div7_jd = document.querySelector('#div7 .jd');
div7_jd.addEventListener('keyup', function() {
	div7_con.style.display = 'block';
	div7_con.innerText = div7_jd.value;  //innerText更改值
	if(div7_jd.value == ''){
		div7_con.style.display = 'none';
	}
})
div7_jd.addEventListener('blur', function() {  //失去焦点隐藏盒子
	div7_con.style.display = 'none';
})
div7_jd.addEventListener('focus', function() {  //获得焦点显示盒子
	if(div7_jd.value !== ''){
		div7_con.style.display = 'block';
	}
})



/* 5秒后隐藏图片  练习*/
let div8_ad = document.querySelector('#div8 .ad');
let time2 = setTimeout(function(){
	div8_ad.style.display = 'none';
},5000);
/* 清除定时器 */
let button2 = document.querySelector('#div8 .button2');
button2.addEventListener('click', function() {
	clearTimeout(time2);
})

//setIntervar 反复调用 
setInterval(function() {
	//console.log('继续输出');
},1000);


//倒计时效果
function number(num) {
	num = num < 10 ? '0' + num : num;
	return num;
}
let inputTime = +new Date('2022-4-4 21:00:00');
function countDown() {
	let nowTime = +new Date();
	let times = (inputTime - nowTime) / 1000;
	let h = parseInt(times / 60 / 60 % 24);
	h = number(h);
	div8_hour.innerHTML = h;
	let m = parseInt(times / 60 % 60);
	m = number(m);
	div8_mimute.innerHTML = m;
	let s = parseInt(times % 60);
	s = number(s);
	div8_second.innerHTML = s;
}
let div8_hour = document.querySelector('#div8 .hour');
let div8_mimute = document.querySelector('#div8 .minute');
let div8_second = document.querySelector('#div8 .second');
let div8_button3 = document.querySelector('#div8 .button3');
let div8_button4 = document.querySelector('#div8 .button4');
countDown();//setInterval 第一次也需要1s后执行，所以要先调用一次
let div8_time = null;
let div8_time3 = setInterval(countDown, 1000);
div8_button3.addEventListener('click', function() {
	console.log(5566);
	clearInterval(div8_time3);
})
div8_button4.addEventListener('click', function() {
	countDown();
	console.log(55);
	div8_time3 = setInterval(countDown, 1000);
}) 

//发送验证码（60秒内不能点击）
let div8_button5 = document.querySelector('#div8 .button5');
let div8_time_tmp = 10;
div8_button5.addEventListener('click', function() {
	this.disabled = true;
	let timer4 = setInterval(function() {
		if(div8_time_tmp == 0) {
			div8_button5.disabled = false;
			clearInterval(timer4);
			div8_button5.innerHTML = '发送验证码';
			div8_time_tmp = 10;
			return;
		}
		div8_button5.innerHTML = '还剩下' + div8_time_tmp + '秒' ;
		div8_time_tmp--;
	}, 1000);
})

//5秒后跳转页面
let button6 = document.querySelector('#div8 .button6');
button6.addEventListener('click', function() {
	console.log(window.location.href);
	let time_tmp_1 = 5;
	button6.innerHTML = time_tmp_1 + '秒钟后跳转到百度';
	setInterval(function() {
		if(time_tmp_1 == 0)
		{
			location.href = 'http://www.baidu.com'; //修改页面网址
			return;
		}
		time_tmp_1--;
		button6.innerHTML = time_tmp_1 + '秒钟后跳转到百度';
	}, 1000);
})


//location和history对象的方法
let div9_button7 = document.querySelector('#div9 .button7');
let div9_button8 = document.querySelector('#div9 .button8');
let div9_button9 = document.querySelector('#div9 .button9');
div9_button7.addEventListener('click', function() {
	location.assign('http://www.baidu.com');
})
div9_button8.addEventListener('click', function() {
	location.replace('http://www.baidu.com');
})
div9_button9.addEventListener('click', function() {
	location.reload('http://www.baidu.com');
})

//navigator对象:包含了浏览器的相关信息
console.log(navigator);
console.log(navigator.userAgent);
//判断浏览器属于什么类型，分别转到什么页面
if((navigator.userAgent.match(/(phone|pad|pod|iphone|ipod|ios|ipad)/i))) {
	//window.location.href = 'http://www.baidu.com';
}else {
	//window.location.href = 'https://www.jd.com/';
}

//history对象 与浏览器的历史记录进行交互
let div9_button10 = document.querySelector('#div9 .button10');
div9_button10.addEventListener('click', function() {
	history.forward();  //相当于历史记录的前进
})

let div9_button11 = document.querySelector('#div9 .button11');
div9_button11.addEventListener('click', function() {
	history.back(); //相当于历史记录的后退
})

let div9_button12 = document.querySelector('#div9 .button12');
div9_button12.addEventListener('click', function() {
	history.go(1); //正数前进n个页面，复数后退n个页面
})


let div10 = document.querySelector('#div10');
div10.addEventListener('mousemove', function(e) {
	let x = e.pageX - div10.offsetLeft
	let y = e.pageY - div10.offsetTop
	div10.innerHTML = 'x坐标是' + x + ', y坐标是' + y;
})



//点击弹出登入框
let div11_h3 = document.querySelector('#div11 h3');
let div11_button = document.querySelector('#div11 button');
let div11 = document.querySelector('#div11');
let div11_div = document.querySelector('#div11 div');
div11_button.addEventListener('click', function() {
	div11.style.backgroundColor = '#fff';
	div11_div.style.display = 'none';
})
div11_h3.addEventListener('click', function() {
	div11.style.backgroundColor = '#ccc';
	div11_div.style.display = 'block';
})
div11_div.addEventListener('mousedown', function(e) {
	let x = e.pageX - div11_div.offsetLeft;
	let y = e.pageY - div11_div.offsetTop;
	document.addEventListener('mousemove', move)
	function move(e){  //函数拿出来取个名字，以后好移除
		div11_div.style.left = e.pageX - x + 'px';
		div11_div.style.top = e.pageY - y + 'px';
	}
	document.addEventListener('mouseup', function(e) {
		document.removeEventListener('mousemove', move);
	})
})



//京东放大镜效果
let div12_preview_img = document.querySelector('#div12.preview_img');
let div12_mask = document.querySelector('#div12 .mask');
let div12_big = document.querySelector('#div12 .big');
let div12_img_1 = document.querySelector('#div12 .img_1');
let div12_bigImg = document.querySelector('#div12 .bigImg');
div12_preview_img.addEventListener('mouseover', function() {
	div12_mask.style.display = 'block';
	div12_big.style.display = 'block';
})
div12_preview_img.addEventListener('mouseout', function() {
	div12_mask.style.display = 'none';
	div12_big.style.display = 'none';
})
div12_preview_img.addEventListener('mousemove', function(e) {
	let x = e.pageX - this.offsetLeft - div12_mask.offsetWidth/2;
	let y = e.pageY - this.offsetTop -div12_mask.offsetHeight/2;
	if(x < 0) {
		x = 0;
	}else if(x > div12_img_1.offsetWidth - div12_mask.offsetWidth) {
		x = div12_img_1.offsetWidth - div12_mask.offsetWidth;
	}
	if(y < 0) {
		y = 0;
	}else if(y > div12_img_1.offsetHeight - div12_mask.offsetHeight) {
		y = div12_img_1.offsetHeight - div12_mask.offsetHeight;
	}
	div12_mask.style.left = x + 'px';
	div12_mask.style.top = y + 'px';
	let bigMaxX = div12_bigImg.offsetWidth - div12_big.offsetWidth;
	let bigMaxY = div12_bigImg.offsetHeight - div12_big.offsetHeight;
	let bigX = x * bigMaxX / (div12_img_1.offsetWidth - div12_mask.offsetWidth);
	let bigY = y * bigMaxY / (div12_img_1.offsetHeight - div12_mask.offsetHeight);
	div12_bigImg.style.left = -bigX + 'px';
	div12_bigImg.style.top = -bigY + 'px';
})


//精斗云案例
let div13_liss = document.querySelectorAll('#div13 ul li');
let div13_tmp = -1;
for(let i = 0; i < div13_liss.length; i++) {
	div13_liss[i].setAttribute('index', i);
	div13_liss[i].addEventListener('mouseover', function() {
		for(let i = 0; i < div13_liss.length; i++) {
			div13_liss[i].style.backgroundColor = '#fff';
		}
		this.style.backgroundColor = 'purple';
		this.style.color = '#eee';
	})
	div13_liss[i].addEventListener('mouseleave', function() {
		for(let i = 0; i < div13_liss.length; i++) {
			div13_liss[i].style.backgroundColor = '#fff';
			div13_liss[i].style.color = '#000';
		}
		if(div13_tmp == -1)
			return;
		div13_liss[div13_tmp].style.backgroundColor = 'purple';
		div13_liss[div13_tmp].style.color = 'forestgreen';
	})
	div13_liss[i].addEventListener('click', function() {
		if(div13_tmp != -1) {
			div13_liss[div13_tmp].style.color = '#000';
		}
		let index = this.getAttribute('index');
		div13_tmp = index;
		div13_liss[div13_tmp].style.backgroundColor = 'purple';
		div13_liss[div13_tmp].style.color = 'forestgreen';
	})
}



//动画函数封装
let div14_1 = document.querySelector('#div14 .div1');
let div14_2 = document.querySelector('#div14 .div2');
let div14_button1 = document.querySelector('#div14 .button1');
let div14_button2 = document.querySelector('#div14 .button2');
/* var timer_1 = setInterval(function() {
	if (div14_1.offsetLeft >= 400) {
		clearInterval(timer_1);
	}
	div14_1.style.left = div14_1.offsetLeft + 1 + 'px';
},30); */

//动画函数封装
function animate_1(obj, target) {
	clearInterval(obj.timer_1); //清除之前的定时器，就不会叠加动画效果了
	obj.timer_1 = setInterval(function() {  //不同对象添加不同定时器,避免了var重复创建空间
		if (obj.offsetLeft == target) {
			clearInterval(obj.timer_1);
		}
		obj.style.left = obj.offsetLeft + 1 + 'px';
	}, 15);
}

//缓动动画公式（目标值-现在的位置）/ 10  作为每次的步长  规范升级
function animate_2(obj, target, callback) {
	clearInterval(obj.timer_1); 
	obj.timer_1 = setInterval(function() {  
		let step = (target - obj.offsetLeft) / 30; //Math.ceil向上取整，确保是整数，避免小数运算
		step = step > 0 ? Math.ceil(step) : Math.floor(step);  //step小于0向下取整
		if (obj.offsetLeft == target) {
			clearInterval(obj.timer_1);
			if(callback) {
				callback();  //使用回调函数
			}
		}
		obj.style.left = obj.offsetLeft + step + 'px';
	}, 15);
}		
//调用封装函数
div14_button1.addEventListener('click', function() {
	animate_1(div14_1, 500);
	animate_2(div14_2, 500);
})
div14_button2.addEventListener('click', function() {
	animate_1(div14_1, 800);
	animate_2(div14_2, 800, function() { //function 回调函数
		div14_2.style.backgroundColor = 'red';
	});
})





//案例 动画
let div15 = document.querySelector('#div15');
let div15_div = document.querySelector('#div15 div');
div15.addEventListener('mouseenter', function() {
	animate_2(div15_div , 0);
	div15.children[0].innerHTML = '++';
})
div15.addEventListener('mouseleave', function() {
	animate_2(div15_div , -160);
	div15.children[0].innerHTML = '--';
})


//网页轮播图
let div16_a1 = document.querySelector('#div16 .a1');
let div16_a2 = document.querySelector('#div16 .a2');
let div16 = document.querySelector('#div16');
let div16_ul = document.querySelector('#div16 ul');
let div16_ol = document.querySelector('#div16 ol');
div16.addEventListener('mouseenter', function() {  //鼠标经过显示按钮
	div16_a1.style.display = 'block';
	div16_a2.style.display = 'block';
	clearInterval(timer_2)
})
div16.addEventListener('mouseleave', function() {  //鼠标经过显示按钮
	div16_a1.style.display = 'none';
	div16_a2.style.display = 'none';
	timer_2 = setInterval(function() {
		div16_a2.click();
	}, 2000);
})
let l = div16_ul.children.length;
for(let i = 0; i < l; i++) {
	let li = document.createElement('li');
	div16_ol.appendChild(li);
	li.setAttribute('index', i); //通过自定义属性设置索引号，这个i不能作为索引
	li.addEventListener('mouseover', function() {
		for(let i = 0; i < div16_ol.children.length; i++) {
			div16_ol.children[i].className = '';
		}
		this.className = 'current';
		let index = this.getAttribute('index');
		num = index;
		circle = index;
		animate_2(div16_ul, index * -200);
	})
}
div16_ol.children[0].className = 'current';
let first = div16_ul.children[0].cloneNode(true);//后来克隆出来的图片,没有inde属性，不会多个白点
div16_ul.appendChild(first);
let num = 0;
let circle = 0; //控制小圆圈的播放
let flag = true; //节流阀
div16_a2.addEventListener('click', function() {

	if(flag == false){
		return;
	}
	flag = false; //关闭节流阀
	//无缝滚动 
	if(num == div16_ul.children.length-1) {
		num = 0;
		div16_ul.style.left = 0
	}
	num++;
	animate_2(div16_ul, num * -200, function() {
		flag = true;
	});
	
	circle++;
	if (circle == div16_ol.children.length){
		circle = 0;
	}
	for(let i = 0; i < div16_ol.children.length; i++) {
		div16_ol.children[i].className = '';
	}

	div16_ol.children[circle].className = 'current';
})
div16_a1.addEventListener('click', function() {
	if(flag==false){
		return;
	}
	flag = false;
	//无缝滚动 
	if(num == 0) {
		num = div16_ul.children.length-1;
		div16_ul.style.left = num * -200 + 'px';
	}
	num--;
	animate_2(div16_ul, num * -200, function() {
		flag = true;  //当动画关闭再开启节流阀
	});
	
	circle--;
	if (circle < 0){
		circle = div16_ol.children.length - 1;
	}
	for(let i = 0; i < div16_ol.children.length; i++) {
		div16_ol.children[i].className = '';
	}
	div16_ol.children[circle].className = 'current';
})
let timer_2 = setInterval(function() {
	div16_a2.click();  //手动调用点击事件
}, 2000);