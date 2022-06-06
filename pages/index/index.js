// index.js
Page({
  data:{
    show: false,
    duration: 300,
    position: 'right',
    round: false,
    overlay: true,
    customStyle: '',
    overlayStyle: 'background-color: rgba(0, 0, 0, 0.7)',
    hiddenmodalput: true,
    placeHolder: '输入房间号',
    inputValue: '',
    roomId: ''
  },
  Rank() {
    wx.navigateTo({
      url: '../Rank/Rank',
    })
  },
  MyPic() {
    wx.navigateTo({
      url: '../MyPic/MyPic',
    })
  },
  ShowWord() {
    wx.navigateTo({
      url: '../ShowWord/ShowWord',
    })
  },
  goToInvite() {
    wx.request({
      url: 'http://192.168.123.205:8080/user/createRoom',
      method: "GET",
      data: {code:''},
      success: (res) => {
        console.log('success')
        console.log(res)
        let code11 = res.data.data
        wx.navigateTo({
          url: '../Invite/Invite?code11='+code11,
        })
      },
      fail: (res) => {
        console.log('fail!')
      }
    })
  },
  doRoomID(){
    this.setData({
      hiddenmodalput: false
    })
  },
  cancel() {
      this.setData({
        hiddenmodalput:true
      })
  },
  bindKeyInput:function(e){
    this.setData({inputValue : e.detail.value});
    console.log(this.data.inputValue)
  },
  confirm() {
    let value1 = this.data.inputValue
    console.log(value1)
    wx.request({
      url: 'http://192.168.123.205:8080/user/joinRoom',
          method: "GET",
          data: {
            roomId: value1
          },
          success: (res) => {
            console.log('success')
            console.log(res)
            if(res.data.data==true){
              wx.navigateTo({
                url: '../Invite/Invite?code11='+value1,
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
  },
  popup(e) {
    const position = e.currentTarget.dataset.position
    let customStyle = 'height: 50%;margin-top:30%;width:80%;margin-left: 10%;margin-right:10%'
    let duration = this.data.duration
    switch(position) {
      case 'top':
      case 'bottom': 
        customStyle = 'height: 30%;'
        break
      case 'right':
        break
    }
    this.setData({
      position,
      show: true,
      customStyle,
      duration
    })
  },
})

