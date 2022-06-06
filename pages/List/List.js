// pages/List/List.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    ifHost:true,
    imageUrl:'https://thirdwx.qlogo.cn/mmopen/vi_32/SgiaC9tDJfIcbdgDDY1GRIBsg1KbMqjDcKQNBJPrVibFs1ydmBJrCs3MEIH5iczxC0XUuBiaeqlBsuJ8CNLjNEm3WQ/132',
    info:[]
  },
  onLoad(options) {
    let ifhost = JSON.parse(options.ifHost)
    let that = this
    this.setData({
      ifHost:ifhost
    })
    if(this.data.ifHost==true){
      let daa = {
        state:"结束"
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
    }
  
    let infos = this.data.info
    wx.onSocketMessage(function(res){
      console.log(res)
      let datas = JSON.parse(res.data)
      console.log(datas)
      if(datas.status=="得分")
      for(var key in datas){
        if(key!="status"){
          let a = {imageUrl:datas[key].avatarUrl,name:datas[key].nickName,score:datas[key].score}
          infos.push(a)
          that.setData({
            info:infos
          })
        }
      }
    })
  },
  return() {
    wx.closeSocket()
    wx.navigateTo({
      url: '../index/index',
    })
  },
 
})