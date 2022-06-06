// pages/Guess/Guess.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    second:40,
    tip:'一种食物',
    drawer: '明早睡到九点',
    restNum:5,
    message: [],
    inputValue: '',
    showM:false,
    ifHost:false,
    count:1,
    user: '',
    painting:'',
    title:'',
    second11:10,
    toView: '',
    placeHolder: '输入答案进行猜词',
    response: '',
    at: '',
    name: '',
    msgg: '',
    rightAnwser:false,
    RID:'',
    defaultAt: '../../resource/picture/head.png',
    userInfos:'',
    second22:3,
    avatarUrls:{
      at1:'../../resource/picture/head.png',
      at2:'../../resource/picture/head.png',
      at3:'../../resource/picture/head.png',
      at4:'../../resource/picture/head.png',
      at5:'../../resource/picture/head.png',
      at6:'../../resource/picture/head.png'
    }
  },
  onLoad: function(t) {
    //this.countdown()
    this.startGuess()
    let kk2 = JSON.parse(t.users)
    console.log(kk2)
    this.setData({
      avatarUrls:kk2
    })
    let ifhost = JSON.parse(t.ifHost)
    this.setData({
      ifHost:ifhost
    })
    let openid = wx.getStorageSync('openid')
    let that = this
    let m = this.data.message
    let user = wx.getStorageSync('user')
    let u = user.avatarUrl
    let xx = 1
    let roomid = JSON.parse(t.roomid)
    this.setData({
      RID:roomid
    })
    // wx.connectSocket({
    //   url: 'ws://192.168.12.50:8080/websocket/multiplayer/'+this.data.RID+'/'+openid,
    // })
    wx.onSocketOpen(function (res){
      console.log("连接成功")
    })
    // wx.sendSocketMessage({
    //   data: JSON.stringify({state:"猜词",count:1}),
    //   success(){
    //     console.log("数据发送成功")
    //   },
    //   fail(){
    //     console.log("数据发送失败")
    //   }
    // })
    // 监听 WebSocket 接受到服务器的消息事件
    wx.onSocketMessage(function(re){
      console.log(re)
      let kk1 = JSON.parse(re.data)

      console.log(kk1)
      if(kk1.status=="聊天"){
        xx = kk1.message
      console.log(xx)
      that.setData({
        at:kk1.avatarUrl,
        name:kk1.nickName,
        msgg:kk1.message
      })
      console.log('ceshi')
      console.log(that.data.msgg)
      let a = {head:that.data.at,name:that.data.name, msg:that.data.msgg}
      console.log(a)
      m.push(a)
      that.setData({
        message: m,
        user: user,
        urll: a
    })
    setTimeout(function () {
      that.setData({
        toView: "msg-" + (m.length - 1),
      })
    }, 100)
      }else if(kk1.status=="猜图"){
        that.setData({
          drawer:kk1.name,
          painting:"http://192.168.123.205:8080"+kk1.url,
          restNum:kk1.remain,
          tip: kk1.prompt,
          title:kk1.title
        })
      }
    })
  },
  onShow(){
    //this.countdown1()
  },
  startGuess(){
    let that = this
    wx.showToast({
      title: '马上开始猜词',
      icon:'loading',
      duration:3000
    })
    setTimeout(function(){
      if(that.data.ifHost==true){
        wx.sendSocketMessage({
          data: JSON.stringify({state:"猜词",count:1}),
          success(){
            console.log("数据发送成功")
          },
          fail(){
            console.log("数据发送失败")
          }
        })
      }
      that.countdown();
    },3001)
  },
  bindKeyInput:function(e){
    this.setData({inputValue : e.detail.value});
    console.log(this.data.inputValue)
  },
  countdown1(){
    var that = this
    var second = this.data.second11
    console.log(second)
    if(second == 0){
      //申请下一轮的画作
      that.setData({
        showM:false,
        second:40,
        rightAnwser:false
      })
      let count1 = that.data.count
      that.setData({
        count: count1+1
      })
      if(that.data.ifHost==true){
        wx.sendSocketMessage({
          data: JSON.stringify({state:"猜词",count:count1+1}),
          success(){
            console.log("数据发送成功")
          },
          fail(){
            console.log("数据发送失败")
          }
        })
      }
      that.countdown()
      return 
    }
    var time = setTimeout(function () {
      that.setData({
        second11: second - 1
      })
      that.countdown1(that)
    }, 1000)
  },
  countdown: function () {
    var that = this
    var second = this.data.second
    if (second == 0) {
      if(that.data.restNum!=0){
        that.setData({
          showM:true,
          second11: 5,
        })
        that.countdown1()
        return
      }else{
        that.setData({
          showM:true,
          second11: 5,
        })
        that.countdown1()
        setTimeout(function(){
          that.setData({
            second:-1
          })
          wx.redirectTo({
            url: '../List/List?&ifHost='+JSON.stringify(that.data.ifHost),
          })
        },5001)
        return
      }
    }
    var time = setTimeout(function () {
      that.setData({
        second: second - 1
      })
      that.countdown(that)
    }, 1000)
  },
  sent() {
    let that = this
    let msg = this.data.inputValue
    let user = wx.getStorageSync('user')
    let m = this.data.message
    let answer = this.data.title
    console.log(user.nickName)
    console.log(this.data.drawer)
    if(that.data.rightAnwser==true){
      if(msg.includes(answer)){
        wx.showToast({
          title: '不能重复作答',
          icon: 'error',
          duration: 2000
        })
        return
      }
    }
    if(user.nickName==this.data.drawer){
      if(msg.includes(answer)){
        wx.showToast({
          title: '作者不能作答',
          icon: 'error',
          duration: 2000
        })
        return
      }
    }
    if(msg.includes(answer)){
      this.setData({
        rightAnwser:true
      })
    }
    console.log(user.nickName)
      let data1 = {
        state: "聊天",
        title: that.data.title,
        message: msg
      }
      that.setData({
        inputValue: '',
        placeHolder: ''
      })
    // 通过 WebSocket 连接发送数据
    wx.sendSocketMessage({
      data: JSON.stringify(data1),
      success(){
        console.log("数据发送成功")
      },
      fail(){
        console.log("数据发送失败")
      }
      
    })
  }
})