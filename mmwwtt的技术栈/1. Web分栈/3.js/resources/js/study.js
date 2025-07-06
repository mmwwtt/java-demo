
//基本知识点
function add1(num1, num2) {       /* 函数关键字声明函数 */
	let sum = num1 + num2;
	console.log(sum);
	return sum;
}
var add2 = function(num1, num2) {  /* 函数表达式声明函数 */
	let sum = num1 + num2;
	console.log(sum);
	return sum;
}

function alertDemo() {     /* 函数关键字声明函数 */
	alert('这是警示框');
}
function logDemo() {       /* 函数关键字声明函数 */
	console.log('这是控制台输出');
}
function promptDemo() {    /* 函数关键字声明函数 */
	prompt('这是输入框');
}

function dataType(){
	let age, temp; /* 声明变量 */
	age = 10;      /* 赋值 */
	let username = 'baby', address = '中国';
	console.log('age:' + age);        /* 字符串拼接 */
	console.log(Number.MAX_VALUE);    /* 数值型最大值 */
	console.log(Number.MIN_VALUE);    /* 数值型最小值 */
	console.log(Number.MAX_VALUE*2);  /* Infinity 无穷大 */
	console.log(-Number.MAX_VALUE*2); /* -Infinity 无穷小 */
	console.log('pink' - 100);        /* NaN 非数字的 */
	console.log(isNaN(123))           /* isNaN判断是否为非数字 */
	console.log(username.length);
	console.log(undefined + 1);    /* Nan */
	console.log(undefined + '1');  /* undefined1 */
	console.log(null + 1);         /* 1 */
	console.log(null + '1');       /* null1 */
	console.log(typeof age);       /* 获取变量数据类型 */
}

function toType(){
	let age = 19;
	/* 转字符串类型 */
	console.log(typeof age.toString());
	console.log(typeof String(age));
	console.log("age:" + age);
	/* 转数字类型 */
	console.log(typeof parseInt('3.14px'));  /* 取整数部分，且会去掉后面的px */
	console.log(typeof parseFloat('3.14'));  /* 得到的数字是浮点型 */
	console.log(typeof Number('12'));  
	console.log('12' - 0);  /* 利用算数运算（ - * /）转换 先将 '12' 转为数字型再运算*/
	/* 转布尔类型 */
	console.log(Boolean('')); /* flase:'', 0, NaN, null, undefined    其余为true*/
}
function baseMath(){
	let age = 19;
	console.log(0.07 * 100); /* 尽量避开使用浮点数运算,会存在精度问题，不能判断两个浮点数是否相等   */
	console.log(++age);
	console.log(--age);
	age+=2;  /* 运算简写 */
	console.log(18 == '18')   //true 只判断值一不一样
	console.log(18 === '18')  //false 判断值和类型 (全等)=== (不全等)!==
	
	switch ('B') {
		case 'A':
			console.log('a');
			break;
		case 'B':
			console.log('b');
			break;
		default:
			console.log('c');
	}
	for(let i = 0; i < 5; i++){
		console.log(i)
	}
}

function mathDemo(){
	console.log(Math.PI);  /* 圆周率 */
	console.log(Math.max(1, 99, 98));      /* 最大值 */
	console.log(Math.max(1, 99, 'isdj'));  /* 有非数字是NaN */
	console.log(Math.floor(3.14))  /* 向下取整 */
	console.log(Math.ceil(3.14))   /* 向上取整 */
	console.log(Math.round(3.4));  /* 四舍五入 注意-3.5四舍五入后为-3*/
	console.log(Math.abs(-3));     /* 绝对值 */
	console.log(Math.random());    /* [0, 1)的随机值 */
}

function dateDemo(){
	/* 日期对象 必须使用new来创建*/
	let date1 = new Date();  //返回系统的当前时间  
	console.log(date1);  
	let date2 = new Date('2019-10-1 8:8:8');
	console.log(date2);  
	let date3 = new Date(2019, 10, 1);//月份是从(0,11) 所以10表示11月
	console.log(date3);  
	console.log(date2.getFullYear()); //返回年
	console.log(date2.getMonth()+1);  //返回月（0，11）
	console.log(date2.getDate());     //返回日 
	console.log(date2.getDay());      //返回星期几  周日返回的是0
	
	let year = date1.getFullYear();
	let month = date1.getMonth() + 1;
	let dates = date1.getDate();
	let day = date1.getDay()
	let hours = date1.getHours();
	let minutes = date1.getMinutes();
	let seconds = date1.getSeconds();
	let arr = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
	console.log('今天是：' + year + '年' + month + '月' + dates + arr[day] + 
	' ' + hours + '时' + minutes + '分' + seconds + '秒');
	
	console.log(date1.getTime()); //获得时间戳，1970.1.1开始的毫秒数  getTime()/valueOf()
	let date4 = +new Date();      //获得时间戳简单写法 
	console.log(date4);
	console.log(Date.now());      //H5新增方法
}

function arrayDemo(){
	let arr0  = new Array();          //new创建数组
	let arr1 = new Array(5);          //new创建数组,长度为5 
	let arr2 = new Array(2,true);     //new创建数组,数据为 2,true
	let arr3 = ['香蕉','qwe',250, true,'asd'];   //2.字面量创建数组（常用）
	let arr4 = [99,87,82,40,0,1,2,3];
	arr2.length = 6;  //修改数组长度
	arr2[3] = 666;    //追加数组元素
	console.log(arr0, arr1, arr2, arr3, arr4);

	console.log(arr0 instanceof Array); //检测是否为数组
	console.log(Array.isArray(arr0));   //检测是否为数组 H5新增方法
	console.log(arr3.push(45, 'pink')); //末尾添加数据，返回值最终长度
	console.log(arr3.unshift(96));      //开头添加数据，返回最终长度
	console.log(arr3.pop());            //删除末尾数据，返回被删数据
	console.log(arr3.shift());          //删除首个数据，返回被删数据
	console.log(arr3.reverse());        //反转数组
	console.log(arr3.sort());           //数组排序 默认(按照字典序)从小到大
	arr4.sort(function(a,b) {           //自定义排序
		return a - b;   //升序
		//return b - a; //降序
	})
	console.log(arr4);
	
	console.log(arr3.indexOf('qwe'));     //查找  返回第一个符合的索引，-1为无   
	console.log(arr3.indexOf('qwe', 3));  //从索引3（包括索引3）开始查找  返回第一个符合的索引，-1为无
	console.log(arr3.lastIndexOf('qwe')); //从后往前查找  返回第一个符合的索引，-1为无   
	console.log(arr3.toString());         //转为字符串,逗号分割
	console.log(arr3.join(' '));          //转为字符串,默认逗号，可以自定义分隔符
	console.log(arr3.concat(arr4));       //连接数组  返回新数组
	console.log(arr3.slice(0,2));         //数组截取，左开右闭 返回被截取的数组
	console.log(arr3.splice(0,2));        //删除元素，左开右闭  返回被删除的数组
}

function objectDemo(){
	/* 创建object方法1 变量声明方式*/
	let obj0 = {   
		uname: '小明',
		sayHi: function(){
			console.log('hi1');
		}
	}
	console.log(obj0.uname,obj0['uname']);   // 调用对象的属性和方法 
	
	/* 创建object方法2 new方式*/
	let obj1 = new Object(); 
	obj1.uname = '小红';       //追加属性和方法 
	obj1.sayHi = function(){
		console.log('hi2');
	}
	
	/* 创建object方法3 构造函数方式 */
	function Student(uname, sex) {  
		this.uname = uname;
		this.sayHi = function() {
			console.log('hi3');
		}
	}
	let obj2 = new Student('小白');
	console.log(obj0,obj1,obj2);
}

function stringDemo(){
	let str0 = 'qqtwyweEEerrtt';
	let str1 = 'mmwwtt';
	let srt2 = 'qwe, fff, 333';
	console.log(str0.length)         //返回字符串长度
	console.log(str0.charAt(3));     //返回当前索引的字符
	console.log(str0.charCodeAt(3)); //返回当前索引字符的ASCLL码 A:65 a:97
	console.log(str0[3]);            //下标返回字符
	console.log(str0.concat(str1));  //字符串拼接
	console.log(str0 + str1);        //简单方便
	
	console.log(str0.substr(1, 5));    //截取字符串（起点，截取长度）
	console.log(str0.slice(1, 3));     //截取字符串 [起点，终点)
	console.log(str0.substring(1, 3)); //截取字符串 [起点，终点)
	console.log(str0.replace('q', 'p')); //替换第一个符合的字符串
	
	console.log(srt2.split(', '));   //将字符串转为数组 并返回
	console.log(str0.toUpperCase()); //转为大写
	console.log(str0.toLowerCase()); //转为小写	
}


//获得dom元素
function domDemo(){
	console.log(document.body);            //获得body标签元素
	console.dir(document.body); //dir输出 DOM元素的相关属性
	console.log(document.documentElement); //获取html标签元素
	
	let fruit = document.getElementById('fruit');           //根据Id获得DOM元素
	console.log(fruit);
	console.log(document.getElementsByTagName('li'));       //根据标签返回DOM元素数组
	console.log(fruit.getElementsByTagName('li'));
	console.log(fruit.getElementsByClassName('like'));      //根据类名返回DOM元素数组
	console.log(document.querySelector('#fruit .like'));    //根据选择器返回第一个符合的DOM元素
	console.log(document.querySelectorAll('#fruit .like')); //根据选择器返回全部符合的DOM元素
}


//给DOM元素注册事件
let div3_Onclick = document.querySelector('#div3 .onclick');
let div3_Onmouseover = document.querySelector('#div3 .onmouseover');
let div3_Onmouseout = document.querySelector('#div3 .onmouseout');
let div3_Onfocus = document.querySelector('#div3 .onfocus');
let div3_Onblur = document.querySelector('#div3 .onblur');
let div3_Onmousemove = document.querySelector('#div3 .onmousemove');
let div3_Onmouseup = document.querySelector('#div3 .onmouseup');
let div3_Onmousedown = document.querySelector('#div3 .onmousedown');
div3_Onclick.onclick = function() {
	console.log('鼠标左键点击触发');
}
div3_Onmouseover.onmouseover = function() {
	console.log('鼠标经过触发');
}
div3_Onmouseout.onmouseout = function() {
	console.log('鼠标离开触发');
}
div3_Onfocus.onfocus = function() {
	console.log('获得焦点触发');
}
div3_Onblur.onblur = function() {
	console.log('失去焦点触发');
}
div3_Onmousemove.onmousemove = function() {
	console.log('鼠标移动触发');
}
div3_Onmouseup.onmouseup = function() {
	console.log('鼠标弹起触发');
}
div3_Onmousedown.onmousedown = function() {
	console.log('鼠标按下触发');
}


//修改DOM元素内容/属性
let div4_pName1 = document.querySelector('#div4 .name1');      //获得元素
let div4_pName2 = document.querySelector('#div4 .name2');
let div4_inputName3 = document.querySelector('#div4 .name3');
let div4_imgPokemon = document.querySelector('#div4 .pokemon');
let div4_btnChangeImg = document.querySelector('#div4 .changeImg');
div4_btnChangeImg.onclick = function() {               //注册事件
	div4_pName1.innerHTML = '<b>妙蛙种子</b>';           //修改元素的内容 ，作为HTML语句来解析
	div4_pName2.innerText = '妙蛙种子'                   //修改元素的内容 ，作为文本内容来解析
	div4_imgPokemon.src = 'resources/img/小蒜头王八.jpg';//修改元素属性
	div4_imgPokemon.title = '小蒜头王八';
	div4_inputName3.value = '妙蛙种子';                  //修改value 
	this.disabled = true;                               //this指向自身元素, true禁用按钮
}


//修改DOM属性样式
let div5_spanStyle1 = document.querySelector('#div5 .style1');
let div5_inputStyle2 = document.querySelector('#div5 .style2');
let div5_spanStyle3 = document.querySelector('#div5 .style3');
div5_spanStyle1.onclick = function() {
	this.style.color = 'pink';  //修改样式属性 属性为驼峰命名
	this.className = 'qwe qaz'; //修改class属性
}
div5_inputStyle2.onfocus = function() {
	this.style.backgroundColor = 'pink';
}
div5_inputStyle2.onblur = function() {
	if(this.value.length < 3){
		div5_spanStyle3.innerText = '输入错误,不能小于3位';
		div5_spanStyle3.style.color = 'red';
	}else {
		div5_spanStyle3.innerText = '输入正确';
		div5_spanStyle3.style.color = 'green';
	}
}

//DOM元素批量绑定事件
let div6_btns = document.querySelectorAll('#div6 .buttons');
for(let i = 0; i<div6_btns.length; i++){
	div6_btns[i].onmouseover = function() {
		for(let j = 0; j<div6_btns.length; j++){
			div6_btns[j].style.fontWeight = '400';
		}
		this.style.fontWeight = '700';
	}
}

//自定义属性
let div7_customPropBtn = document.querySelector('#div7 .customPropBtn');
div7_customPropBtn.onclick = function(){
	let div7_customProp = document.querySelector('#div7 .customProp');
	console.log(div7_customProp.className);              //只能获得内置属性值
	console.log(div7_customProp.getAttribute('data-index'));  //获取自定属性值
	div7_customProp.className = 'q';                     //设置属性值
	div7_customProp.setAttribute('data-index', 'q');     //设置属性值,主要针对与自定义属性 ,class 就是className
	div7_customProp.removeAttribute('data-index');       //删除自定义属性
	div7_customProp.setAttribute('data-num-t', 300);     //H5规定自定义属性以 data- 开头
	/* H5新增获得自定义属性的方法 ie11以上才支持*/
	console.log(div7_customProp.dataset.numT);           //dataset存放 data-开头的属性
	console.log(div7_customProp.dataset['numT']);        //自定义属性有 '-' 需要更改为驼峰命名
}

//父子节点
let div8_FatherAndSonBtn = document.querySelector('#div8 .divFatherAndSon .FatherAndSonBtn');
div8_FatherAndSonBtn.onclick = function(){
	let div8_son1 = document.querySelector('#div8 .divFatherAndSon .son1');
	console.log(div8_son1);
	let div8_FatherAndSon = div8_son1.parentNode;    //获得父节点
	console.log(div8_FatherAndSon);
	console.log(div8_FatherAndSon.childNodes);  //获得子节点数组 (包含DOM元素和文本节点)
	console.log(div8_FatherAndSon.children);    //获得子元素数组(只有DOM元素)
	console.log(div8_FatherAndSon.firstChild);  //返回第一个子节点
	console.log(div8_FatherAndSon.lastChild);   //返回最后一个子节点
	/* iE9以上才支持 */
	console.log(div8_FatherAndSon.firstElementChild);//返回第一个子元素节点
	console.log(div8_FatherAndSon.lastElementChild); //返回最后一个子元素节点
	/* 一般使用索引获取 不会有兼容问题*/
	console.log(div8_FatherAndSon.children[0]);  //根据索引获取节点
}

//兄弟节点
let div8_brothesBtn = document.querySelector('#div8 .brothers .brothesBtn');
div8_brothesBtn.onclick = function(){
	let div8_bro2 = document.querySelector('#div8 .brothers .bro2');
	console.log(div8_bro2.nextSibling);           //得到下一个兄弟节点(包含文本和元素节点)
	console.log(div8_bro2.previousSibling);       //上一个兄弟节点
	/* IE9以上支持 */
	console.log(div8_bro2.nextElementSibling);    //下一个兄弟元素节点
	console.log(div8_bro2.previousElementSibling) //上一个兄弟元素节点
}

//创建  新增/删除节点
let div8_newBtn = document.querySelector('#div8 .newNode .newBtn');
div8_newBtn.onclick = function(){
	let div8_addOrCutUl = document.querySelector('#div8 .addOrCutUl');
	let div8_newLi1 = document.createElement('li');  //创建节点
	let div8_newLi2 = document.createElement('li');
	div8_newLi1.innerHTML = '新增-桃子';
	div8_newLi2.innerHTML = '新增-葡萄';
	div8_addOrCutUl.appendChild(div8_newLi1);      //末尾添加节点
	div8_addOrCutUl.insertBefore(div8_newLi2, div8_addOrCutUl.children[0]); //指定元素前添加节点
	let div8_lis = div8_addOrCutUl.children;       //会lis会跟着节点变化而变化
	div8_addOrCutUl.removeChild(div8_lis[2]);      //删除节点
	let div8_copyLi = div8_lis[1].cloneNode(true); //复制节点 ,括号为空/false,默认只复制节点(浅拷贝  )，不复制内容
	div8_addOrCutUl.appendChild(div8_copyLi);
}

// 事件侦听/注册事件，可以添加相同的事件,处理事件冒泡
let div9_EventListenerBtn = document.querySelector('#div9 .EventListenerBtn');
div9_EventListenerBtn.addEventListener('click', function(){ //添加事件监听
	alert('第一个');
})
div9_EventListenerBtn.addEventListener('click', function(){
	alert('第二个');
})

/* 删除事件 */
let div9_divs_1 = document.querySelectorAll('#div9 .EventListenerDiv div');
div9_divs_1[0].onclick = function() {
	alert(11);
	div9_divs_1[0].onclick = null; //传统方式删除事件
}
div9_divs_1[1].addEventListener('click', fn_1);  //里面的fn_1函数调用不用加（）
function fn_1() {
	alert(22);
	div9_divs_1[1].removeEventListener('click', fn_1); //删除事件
}

//事件流的阶段
let div9_EventListenerFather = document.querySelector('#div9 .EventListenerFather');
let div9_EventListenerDivSon = document.querySelector('#div9 .EventListenerDivSon');
div9_EventListenerFather.addEventListener('click', function() {
	alert('father');
}, false);
div9_EventListenerDivSon.addEventListener('click', function(event) {
	alert('son');
	//e.stopPropagation(); //阻止事件冒泡
	event.cancelBubble = true; //低版本写法  取消冒泡 
}, false);  //true：捕获阶段，由外到内   false（默认）：冒泡阶段，由内到外


//事件对象
let div10_div = document.querySelector('#div10 div');
let div10_ul = document.querySelector('#div10 ul');
div10_ul.onclick = function(e) {  //事件对象e在函数()里面
	console.log(e);
	console.log(e.target); //返回触发事件的对象 li 
	console.log(e.srcElement); //返回触发事件的对象 IE6,7,8使用
	console.log(this);  //返回绑定事件的对象 ul
	console.log(e.type);  //返回事件类型
}
//阻止默认行为（事件） 让链接不跳转，或者按钮不提交
let div10_a = document.querySelector('#div10 a');
div10_a.addEventListener('click', function(e) {
	//e.preventDefault(); //阻止默认行为 dom标准写法 方法
})
div10_a.onclick = function(e) {
	//e.preventDefault(); //阻止默认行为 dom标准写法 方法
	//e.returnValue;  //低版本使用:阻止默认行为  属性
	return false; //阻止默认行为 无兼容问题 但是return 之后的不执行，仅限传统方式
}


/* 事件委托 点击li,往上传到ul触发事件*/
let div11_ul = document.querySelector('#div11 ul'); 
let div11_lis = div11_ul.querySelectorAll('li');
div11_ul.addEventListener('click', function(e){
	alert('事件委托')
	for(var i = 0; i < div11_lis.length; i++) {
		div11_lis[i].style.backgroundColor = '#fff';
	}
	e.target.style.backgroundColor = 'pink';
})
//禁止鼠标右键
div11_ul.addEventListener('contextmenu', function(e){
	e.preventDefault();  //阻止默认事件  contextmenu:右键菜单
})
//禁止选中文字
div11_ul.addEventListener('selectstart', function(e){
	e.preventDefault();  //阻止默认事件 selectstart:开始选择
})

//鼠标事件对象
/* document.addEventListener('click', function(e){
	console.log(e);
	console.log(e.clientX); //返回 可视窗口 中 鼠标的X坐标
	console.log(e.clientY); //返回 可视窗口 中 鼠标的Y坐标
	console.log(e.pageX); //返回 文档页面 中 鼠标的X坐标 IE9+
	console.log(e.pageY); //返回 文档页面 中 鼠标的Y坐标 IE9+
	console.log(e.screenX); //返回 电脑屏幕 中 鼠标的X坐标
	console.log(e.screenY); //返回 电脑屏幕 中 鼠标的Y坐标
}) */
//图像跟随鼠标
let div12_img = document.querySelector('#div12 img');
document.addEventListener('mousemove', function(e) {
	let x = e.pageX;
	let y = e.pageY;
	div12_img.style.left = x-7 + 'px' ;
	div12_img.style.top = y-32 + 'px';
})

//事件监听和定时器
let div13_Btn1 = document.querySelector('#div13 .btn1')
let div13_Btn2 = document.querySelector('#div13 .btn2')
let div13_Btn3 = document.querySelector('#div13 .btn3')
div13_Btn1.addEventListener('click', function(){  //事件监听
	console.log('窗口现宽：' + window.innerWidth)
	console.log('窗口现高：' + window.innerHeight)
})
div13_Btn2.addEventListener('click', function(){
	time1 = setTimeout(function(){       // setTimeout 倒计时定时器
		console.log('setTimeout 定时器')
	},1000);
	time2 = setInterval(function(){      // setInterval 循环定时器
		console.log('setInterval 定时器')
	},1000)
})
div13_Btn3.addEventListener('click', function(){
	clearTimeout(time1);  //清除定时器
	clearTimeout(time2);
})


//pc端js
//offset属性
let div14_div_1 = document.querySelector('#div14 .div_1');
let div14_div_2 = document.querySelector('#div14 .div_2');
let div14_Btn1 = document.querySelector('#div14 button')
div14_Btn1.addEventListener('click', function(){
	console.log(div14_div_2.offsetTop);     //对带有定位的父元素的 上方偏移 没有定位的父元素则相对于body
	console.log(div14_div_2.offsetLeft);    //对带有定位的父元素的 左边框偏移
	console.log(div14_div_2.offsetWidth);   //返回宽度  包含padding 边框 内容宽度
	console.log(div14_div_2.offsetHeight);  //返回高度 包含padding 边框 内容高度
	console.log(div14_div_2.offsetParent);  //返回带有定位的父级元素，如果父级都无定位，返回body
})


//滚动条
// client
let div15 = document.querySelector('#div15');
// console.log(div15.clientWidth);  //返回自身padding和内容区的宽度
// console.log(div15.clientHeight); //返回自身的padding 和内容区的高度
// console.log(div15.clientTop); //返回上边框的大小
// console.log(div15.clientLeft);  //返回左边框的大小
// //scroll 滚动
// console.log(div15.scrollWidth);  //返回实际大小()滚动区域,padding，不含边框
// console.log(div15.scrollHeight);
div15.addEventListener('scroll', function() {
	console.log(div15.scrollTop);  //返回被卷去上侧距离
	console.log(div15.scrollLeft);
});

//立即执行函数
let div16_button=document.querySelector('#div16 button');
div16_button.addEventListener('click',function(){
	//立即执行函数方式1
	(function(a, b) {
		console.log('立即执行函数方式1');
		console.log(a+b);
	})(5,6);
	//立即执行函数方式2
	(function sum(a, b) {
		console.log('立即执行函数方式2');
		console.log(a+b);
	}(7,12));
})


//仿淘宝固定侧边栏
let div17 = document.querySelector('#div17');
let div17_span = document.querySelector('#div17 span');
document.addEventListener('scroll', function() {
	//console.log(window.pageYOffset); //页面被卷进去的头部
	if(window.pageYOffset >= 300 ) {  //window.pageYOffset IE9才支持
	 div17.style.position = 'fixed';
	 div17.style.top = '100px';
	} else {
	 div17.style.position = 'absolute';
	 div17.style.top = '400px';
	}
	if(window.pageYOffset >= 600) {
	 div17_span.style.display = 'block';
	} else {
	 div17_span.style.display = 'none';
	}
}) 		

