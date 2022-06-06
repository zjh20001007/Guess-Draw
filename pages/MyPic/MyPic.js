// pages/MyPic/MyPic.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    list: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    let openid = wx.getStorageSync('openid')
    wx.request({
      url: 'http://192.168.123.205:8080/user/myPic',
          method: "GET",
          data: {
            openId: openid
          },
          success: (res) => {
            console.log('success')
            console.log(res)
            if(true){
              this.setData({
                list: res.data.data
              })
            }else{
              wx.showToast({
                title: '密码错误',
                icon: 'error',
                duration: 2000
            })
            }
          },
          fail: (res) => {
            console.log('fail!')
          }
    })
    // this.setData({
    //   list: [
    //     {
    //       name: '水电费个森岛帆高',
    //       coverPath: '../../resource/picture/te.jpg'
    //     },
    //     {
    //       name: '问题就会',
    //       coverPath: '../../resource/picture/te.jpg'
    //     },
    //     {
    //       name: '或管窥蠡测',
    //       coverPath: '../../resource/picture/te.jpg'
    //     }
    //   ]
    // })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})