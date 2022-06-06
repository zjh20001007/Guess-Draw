// pages/Cover/Cover.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    UserInfo: '',
    code: '',
    openid: ''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
    let user = wx.getStorageSync('user')
    this.setData({
      UserInfo: user,
      response: ''
    })
  },

  login() {
    let that = this
    console.log('ssss')
    wx.login({
      success: res => {
        console.log(res.code)
        that.setData({
          code: res.code
        })
        console.log(that.data.code)
      }
    })
    wx.getUserProfile({
      desc: '用于完善会员资料',
      success: res => {
        let user = res.userInfo
        //缓存
        wx.setStorageSync('user', user)
        console.log('success', res)
        this.setData({
          UserInfo: user
        })
        wx.request({
          url: 'http://192.168.123.205:8080/user/login',
          method: "GET",
          data: {
            code:this.data.code,
            nickName:this.data.UserInfo.nickName,
            avatarUrl:this.data.UserInfo.avatarUrl
          },
          success: (res) => {
            console.log('success')
            console.log(res)
            that.setData({
              openid: res.data.openid
            })
            wx.setStorageSync('openid', this.data.openid)
            console.log(this.data.openid)
          },
          fail: (res) => {
            console.log('fail!')
          }
        })


        wx.navigateTo({
          url: '../index/index',
        })
        

      },
      fail: res => {
        console.log('fail', res)
        }
    })
    console.log('aaaa')
  }
})