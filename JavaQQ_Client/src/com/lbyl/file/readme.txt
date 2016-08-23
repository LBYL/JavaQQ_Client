通信协议：
1.注册事件：
	type：register
	name
	pwd
1.1注册应答
	type：regRespon
	regResult success或existingdata
2.登录事件
	type：login
	name
	pwd
2.1登录应答
	type：loginResPon
	logResult ：success或nodata或wrongpsd

4、公共发言
	type:chat
	n：chatmsg