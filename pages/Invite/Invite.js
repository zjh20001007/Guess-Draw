// pages/Invite/Invite.js
Page({
  data: {
    message: [],
    inputValue: '',
    user: '',
    toView: '',
    placeHolder: '现在开始聊天吧！',
    response: '',
    at: '',
    name: '',
    msgg: '',
    ifHost: false,
    RoomID: '',
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
    davatarUrls:{
      at1:'../../resource/picture/head.png',
      at2:'../../resource/picture/head.png',
      at3:'../../resource/picture/head.png',
      at4:'../../resource/picture/head.png',
      at5:'../../resource/picture/head.png',
      at6:'../../resource/picture/head.png'
    }
  },

  onLoad: function(option) {
    let rID = JSON.parse(option.code11)
    this.setData({
      RoomID: rID
    })
    let openid = wx.getStorageSync('openid')
    let that = this
    let m = this.data.message
    let user = wx.getStorageSync('user')
    let u = user.avatarUrl
    console.log(u)
    let xx = 1
    console.log('ws://192.168.123.205:8080/websocket/multiplayer/'+that.data.RoomID+'/'+openid)
    wx.connectSocket({
      url: 'ws://192.168.123.205:8080/websocket/multiplayer/'+that.data.RoomID+'/'+openid,
    })
    wx.onSocketOpen(function (res){
      console.log("连接成功")
    })
    // 监听 WebSocket 接受到服务器的消息事件
    wx.onSocketMessage(function(re){
      let kk = JSON.parse(re.data)
      console.log(kk)
      if(kk.status=="用户信息"){
        that.setData({
          avatarUrls:{at1:that.data.defaultAt,at2:that.data.defaultAt,at3:that.data.defaultAt,at4:that.data.defaultAt,at5:that.data.defaultAt,at6:that.data.defaultAt}
        })
        console.log(that.data.avatarUrls)
        that.setData({
          userInfos:kk
        })
        let count = 0
        let flag11 = false
        for(var key in kk){
          if(key=="avatarUrl"){
            flag11 = true
            console.log("gggg")
            break
          }
          else if(key!="status"){
            console.log("ggggff")
            // that.setData({
            //   avatarUrls:that.data.davatarUrls
            // })
            // that.setData({
            //   avatarUrls:{at1:that.data.defaultAt,at2:that.data.defaultAt,at3:that.data.defaultAt,at4:that.data.defaultAt,at5:that.data.defaultAt,at6:that.data.defaultAt}
            // })
            // console.log(that.data.avatarUrls)
            let atList22 = that.data.avatarUrls
            console.log(atList22)
            // if(atList22.at1!=that.data.defaultAt){
            //   atList22.at1 = that.data.defaultAt
            // }
            // else if(atList22.at2!=that.data.defaultAt){
            //   atList22.at2 = that.data.defaultAt
            // }
            // else if(atList22.at3!=that.data.defaultAt){
            //   atList22.at3 = that.data.defaultAt
            // }
            // else if(atList22.at4!=that.data.defaultAt){
            //   atList22.at4 = that.data.defaultAt
            // }
            // else if(atList22.at5!=that.data.defaultAt){
            //   atList22.at5 = that.data.defaultAt
            // }
            // else if(atList22.at6!=that.data.defaultAt){
            //   atList22.at6 = that.data.defaultAt
            // }
            that.setData({
              avatarUrls:atList22
            })
            let atList1 = that.data.avatarUrls
            console.log(atList1)
            if(atList1.at1==kk[key].avatarUrl){
              console.log(1)
            }
            else if(atList1.at2==kk[key].avatarUrl){
              console.log(1)
            }
            else if(atList1.at3==kk[key].avatarUrl){
              console.log(1)
            }
            else if(atList1.at4==kk[key].avatarUrl){
              console.log(1)
            }
            else if(atList1.at5==kk[key].avatarUrl){
              console.log(1)
            }
            else if(atList1.at6==kk[key].avatarUrl){
              console.log(1)
            }
            else if(atList1.at1==that.data.defaultAt){
              atList1.at1 = kk[key].avatarUrl
            }
            else if(atList1.at2==that.data.defaultAt){
              atList1.at2 = kk[key].avatarUrl
            }
            else if(atList1.at3==that.data.defaultAt){
              atList1.at3 = kk[key].avatarUrl
            }
            else if(atList1.at4==that.data.defaultAt){
              atList1.at4 = kk[key].avatarUrl
            }
            else if(atList1.at5==that.data.defaultAt){
              atList1.at5 = kk[key].avatarUrl
            }
            else if(atList1.at6==that.data.defaultAt){
              atList1.at6 = kk[key].avatarUrl
            }
            that.setData({
              avatarUrls: atList1
            })
            console.log(atList1) 
          }
        }
        if(flag11==true){
          for(var key in kk){
            if(key=="avatarUrl"){
              that.setData({
                // avatarUrls:that.data.davatarUrls,
                ifHost:true
              })
              let atList = that.data.avatarUrls
            console.log(atList)
            atList.at1 = kk[key]
            // if(atList.at1==that.data.defaultAt){
            //   atList.at1 = kk[key]
            // }
            // else if(atList.at2==that.data.defaultAt){
            //   atList.at2 = kk[key]
            // }
            // else if(atList.at3==that.data.defaultAt){
            //   atList.at3 = kk[key]
            // }
            // else if(atList.at4==that.data.defaultAt){
            //   atList.at4 = kk[key]
            // }
            // else if(atList.at5==that.data.defaultAt){
            //   atList.at5 = kk[key]
            // }
            // else if(atList.at6==that.data.defaultAt){
            //   atList.at6 = kk[key]
            // }
            that.setData({
              avatarUrls: atList
            })
            }
          }
          flag11==false
        }
        console.log(count)
      }else if(kk.status=="聊天"){
        xx = kk.message
        console.log(xx)
        that.setData({
          at:kk.avatarUrl,
          name:kk.nickName,
          msgg:kk.message
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
      }else if(kk.status=="开始游戏"){
        wx.navigateTo({
          url: '../ChooseWord/ChooseWord?users='+JSON.stringify(that.data.userInfos)+'&RoomID='+JSON.stringify(that.data.RoomID)+'&ifHost='+JSON.stringify(that.data.ifHost),
         })
      }
      
    setTimeout(function () {
      that.setData({
        toView: "msg-" + (m.length - 1),
      })
    }, 100)
    })
  },
  returnHome(){
    wx.showModal({
      content: '您确定要退出房间吗?',
      success (res) {
        if (res.confirm) {
          wx.closeSocket()
          wx.navigateTo({
            url: '../index/index',
          })
          console.log('用户点击确定')
        } else if (res.cancel) {
          console.log('用户点击取消')
        }
      }
    })
  },
  startGame() {
    let that = this
    if(this.data.avatarUrls.at2==this.data.defaultAt){
      wx.showToast({
        title: '一个人不能开始',
        icon: 'error',
        duration: 2000
      })
      return
    }
    this.setData({
      userInfos: this.data.avatarUrls
    })
    console.log(that.data.userInfos)
    console.log(that.data.RoomID)
    if(this.data.ifHost==true){
      console.log("进入该页面")
      let daa = {
        state:'开始游戏',
      }
      wx.sendSocketMessage({
        data: JSON.stringify(daa),
        success(){
          console.log("数据发送成功")
        },
        fail(){
          console.log("数据发送失败")
        }
      })
    }else{
      wx.showToast({
        title: '你不是房主',
        icon:'error',
        duration: 1500
      })
    }
    
    // wx.navigateTo({
    //   url: '../ChooseWord/ChooseWord?users='+JSON.stringify(that.data.userInfos)+'&RoomID='+that.data.RoomID,
    // })
  },
  bindKeyInput:function(e){
    this.setData({inputValue : e.detail.value});
    console.log(this.data.inputValue)
  },
  onShareAppMessage(res) {
    let id = this.data.RoomID // 分享产品的Id
    if (res.from === 'button') {
      // 来自页面内转发按钮
      console.log(res.target)
    }
    return {
      title: '快来参加游戏吧！',
      path: `pages/Cover/Cover`, // 分享后打开的页面
      imageUrl: '../../resource/picture/share1.png'
    }
  },
  sent() {
    let that = this
    let msg = this.data.inputValue
    let user = wx.getStorageSync('user')
    let m = this.data.message
    console.log(user.nickName)
      let data1 = {
        state: "聊天",
        title: '龘',
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