// pages/info-four/info-four.js
Page({
 
  /**
   * 页面的初始数据
   */
  data: {
    timer:'',
    second: 15,
    message: [],
    inputValue: '',
    user: '',
    toView: '',
    placeHolder: '现在开始聊天吧！',
    response: '',
    at: '',
    name: '',
    msgg: '',
    Rid:'',
    ifHost:false,
    defaultAt: '../../resource/picture/head.png',
    userInfos:'',
    avatarUrls:{
      at1:'../../resource/picture/head.png',
      at2:'../../resource/picture/head.png',
      at3:'../../resource/picture/head.png',
      at4:'../../resource/picture/head.png',
      at5:'../../resource/picture/head.png',
      at6:'../../resource/picture/head.png'
    },
    words: {
      word0: '东西南北',
      word1: '东西南北',
      word2: '东西南北',
      word3: '东西南北'
    },
    title1:'',
    spareWords: {
      word0: '东西南北',
      word1: '东西南北',
      word2: '东西南北',
      word3: '东西南北'
    },
    defaultColor:'#3CB371',
    tempColor1: '#3CB371',
    tempColor2: '#3CB371',
    tempColor3: '#3CB371',
    tempColor4: '#3CB371',
    chooseColor:'#FFD700'
  },
  onUnload(){
    clearInterval(this.data.timer);
  },
  onLoad(option){
    let kk = JSON.parse(option.RoomID)
    console.log(kk)
    // let ifhost = JSON.parse(option.ifHost)
    // this.setData({
    //   ifHost:ifhost
    // })
    let roomid = kk
    console.log(roomid)
    this.setData({
      Rid:roomid
    })
    let kk2 = JSON.parse(option.users)
    console.log(kk2)
    this.setData({
      avatarUrls:kk2
    })
    let ifhost = JSON.parse(option.ifHost)
    this.setData({
      ifHost:ifhost
    })
    let openid = wx.getStorageSync('openid')
    let that = this
    let m = this.data.message
    let user = wx.getStorageSync('user')
    let u = user.avatarUrl
    let xx = 1
    console.log('ws://192.168.123.205:8080/websocket/multiplayer/'+that.data.Rid+'/'+openid)
    // wx.connectSocket({
    //   url: 'ws://192.168.12.50:8080/websocket/multiplayer/'+that.data.Rid+'/'+openid,
    // })
    wx.onSocketOpen(function (res){
      console.log("连接成功")
    })
    let data2 = {
      state: "提供词库",
    }
    if(that.data.ifHost==true){
      wx.sendSocketMessage({
        data: JSON.stringify(data2),
        success(){
          console.log("数据发送成功")
        },
        fail(){
          console.log("数据发送失败")
        }
      })
    }
    // 监听 WebSocket 接受到服务器的消息事件
    wx.onSocketMessage(function(re){
      console.log(re)
      let kk1 = JSON.parse(re.data)
      if(kk1.status=="词名"){
        console.log(kk1)
        let count1 = 0
        let words1 = that.data.words
        let spareWords1 = that.data.spareWords
        let count2 = 0
        console.log(words1)
        for(var key in kk1){
          if(key=="status"){
            console.log(key)
          }
          else if(count1<4){
            let n = "word"+count1.toString()
            console.log(key)
            words1[n] = key
            console.log(words1[n])
            count1 = count1 + 1 
          }else if(count2<4){
            let n = "word"+count2.toString()
            console.log(key)
            spareWords1[n] = key
            console.log(spareWords1[n])
            count2 = count2 +1
          }
        }
        count1 = 0
        count2 = 0
        that.setData({
          words:words1,
          spareWords:spareWords1
        })
        console.log(that.data.words)
        console.log(that.data.spareWords)
      }else if(kk1.status=="聊天"){
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
      }
    })
  },
  onShow() {
    console.log('ssss')
    this.countdown()
  },
 
  // 小程序实现简单的倒计时效果
  // 基本实现功能:1，从60到0的倒计时效果2，倒计时完毕后会有提示
  countdown: function () {
    var that = this
    var second = this.data.second
    if (second == 0) {
      console.log(that.data.avatarUrls)
      if(that.data.title1==''){
        if(that.data.words["word0"]!="status"){
          that.setData({
            title1:that.data.words["word0"] ,
          })
        }else{
          that.setData({
            title1:that.data.words["word1"] ,
          })
        }
      }
      wx.request({
        url: 'http://192.168.123.205:8080/user/chooseWord',
          method: "GET",
          data: {
            roomId:this.data.Rid,
            openId:wx.getStorageSync('openid'),
            word:this.data.title1
          },
          success: (res) => {
            console.log('选词成功！')
          },
          fail: (res) => {
            console.log('fail!')
          }
      })
      // wx.sendSocketMessage({
      //   data: JSON.stringify({state:"选词",word:this.data.title1}),
      //   success: (res)=>{
      //     console.log("success")
      //   },
      //   fail: (res) => {
      //     console.log("fail!")
      //   }
      // })
      wx.navigateTo({
        url: '../draw/draw?users='+JSON.stringify(that.data.avatarUrls)+'&title1='+JSON.stringify(this.data.title1)+'&roomid='+JSON.stringify(this.data.Rid)+'&ifHost='+JSON.stringify(this.data.ifHost),
      })
      that.setData({
        second: 3
      })
      return
    }
    var time = setTimeout(function () {
      that.setData({
        second: second - 1
      })
      that.countdown(that)
    }, 1000)
    that.setData({
      timer:time
    })
  },
 

  bindKeyInput:function(e){
    this.setData({inputValue : e.detail.value});
    console.log(this.data.inputValue)
  },
  choosen1(){
    this.setData({
      title1:this.data.words["word0"] ,
      tempColor1:this.data.chooseColor,
      tempColor2:this.data.defaultColor,
      tempColor3:this.data.defaultColor,
      tempColor4:this.data.defaultColor
    })
  },
  choosen2(){
    this.setData({
      title1:this.data.words["word1"] ,
      tempColor2:this.data.chooseColor,
      tempColor1:this.data.defaultColor,
      tempColor3:this.data.defaultColor,
      tempColor4:this.data.defaultColor
    })
  },
  choosen3(){
    this.setData({
      title1:this.data.words["word2"],
      tempColor3:this.data.chooseColor,
      tempColor2:this.data.defaultColor,
      tempColor1:this.data.defaultColor,
      tempColor4:this.data.defaultColor
    })
  },
  choosen4(){
    this.setData({
      title1:this.data.words["word3"],
      tempColor4:this.data.chooseColor,
      tempColor2:this.data.defaultColor,
      tempColor3:this.data.defaultColor,
      tempColor1:this.data.defaultColor
    })
  },
  change() {
    this.setData({
      words: this.data.spareWords
    })
  },
  sent() {
    let that = this
    let msg = this.data.inputValue
    let user = wx.getStorageSync('user')
    let m = this.data.message
    console.log(user.nickName)
      let data1 = {
        state: "聊天",
        title: 'xx',
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