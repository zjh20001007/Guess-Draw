var t = function() {
  function t(t, i) {
      var e = [], a = !0, n = !1, o = void 0;
      try {
          for (var s, r = t[Symbol.iterator](); !(a = (s = r.next()).done) && (e.push(s.value), 
          !i || e.length !== i); a = !0) ;
      } catch (t) {
          n = !0, o = t;
      } finally {
          try {
              !a && r.return && r.return();
          } finally {
              if (n) throw o;
          }
      }
      return e;
  }
  return function(i, e) {
      if (Array.isArray(i)) return i;
      if (Symbol.iterator in Object(i)) return t(i, e);
      throw new TypeError("Invalid attempt to destructure non-iterable instance");
  };
}(), i = getApp();

Page({
  data: {
      timer:'',
      Rid:'',
      avatarUrls:{
        at1:'../../resource/picture/head.png',
        at2:'../../resource/picture/head.png',
        at3:'../../resource/picture/head.png',
        at4:'../../resource/picture/head.png',
        at5:'../../resource/picture/head.png',
        at6:'../../resource/picture/head.png'
      },
      canIUseAvatar: wx.canIUse("open-data.type.userAvatarUrl"),
      userInfo: {},
      modalStatus: !1,
      topicList: [],
      cnt: 0,
      title1:'',
      topicIndex: 0,
      nowTopic: {},
      second1:4,
      localPath:'',
      weightArr: [ {
          id: 1,
          width: "8rpx",
          height: "8rpx",
          margin: 36,
          lineWidth: 3
      }, {
          id: 2,
          width: "16rpx",
          height: "16rpx",
          margin: 32,
          lineWidth: 6
      }, {
          id: 3,
          width: "24rpx",
          height: "24rpx",
          margin: 28,
          lineWidth: 9
      }, {
          id: 4,
          width: "32rpx",
          height: "32rpx",
          margin: 24,
          lineWidth: 12
      }, {
          id: 5,
          width: "40rpx",
          height: "40rpx",
          margin: 20,
          lineWidth: 15
      } ],
      colorArr: [ "#010101", "#DA1C34", "#FD049E", "#FE0000", "#FF7F00", "#FF4F40", "#FFA506", "#FE5E78", "#68B505", "#00B280", "#FFC4B0", "#138BFD", "#8A2BE2", "#666666" ],
      pen: {
          lineWidth: 3,
          color: "#010101"
      },
      ctx: null,
      ctxBak: null,
      eraser: {
          color: "#ffffff",
          icon_name: "eraser.png"
      },
      canvasW: 0,
      flag:false
  },
  actions: [],
  changeTopic: function() {
      var t = this.data.topicIndex;
      t < 4 ? this.setData({
          topicIndex: t + 1
      }) : this.loadTopicRand();
  },
  closeModal: function() {
      this.setData({
          modalStatus: !1
      });
  },
  onLoad: function(option) {
    // wx.enableAlertBeforeUnload({
    //   message:"您确定要退出游戏吗",
    //   success:function(res){
    //     console.log("成功");
    //   },
    //   fail:function(errMsg){
    //     console.log("失败");
    //   }
    // })
      this.countdown()
      let openid = wx.getStorageSync('openid')
      wx.request({
        url: 'http://192.168.123.205:8080/user/start',
            method: "GET",
            data: {
              openId: openid
            },
            success: (res) => {
              console.log('success')
              console.log(res)
              if(true){
                this.setData({
                  cnt: res.data.data.count,
                  title1: res.data.data.title
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
  onUnload() {
    clearInterval(this.data.timer);
    if(this.data.flag==false){
      let openid = wx.getStorageSync('openid')
      wx.request({
        url: 'http://192.168.123.205:8080/user/over',
            method: "GET",
            data: {
              openId: openid
            },
            success: (res) => {
              console.log('success')
            },
            fail: (res) => {
              console.log('fail!')
            }
      })
    }
  },
  onReady: function() {
      var t = wx.getSystemInfoSync();
      this.setData({
          canvasW: 2 * t.screenWidth
      });
  },
  initPanel: function() {
      this.actions = [];
      var t = wx.createCanvasContext("my-canvas"), i = wx.createCanvasContext("my-canvas");
      t.setLineCap("round"), t.setLineJoin("round"), i.setLineCap("round"), i.setLineJoin("round"), 
      t.setFillStyle("white"), t.fillRect(0, 0, 710, 710), t.draw(!0), this.setData({
          ctx: t,
          ctxBak: i
      });
  },
  onShow: function() {
      this.initPanel(), this.initEraser();
  },
  touchStart: function(i) {
      var e = t(i.touches, 1)[0], a = this, n = a.data, o = n.ctx, s = n.ctxBak, r = n.pen;
      o.setStrokeStyle(a.data.pen.color), o.setLineWidth(a.data.pen.lineWidth), o.moveTo(e.x, e.y), 
      s.setStrokeStyle(r.color), s.setLineWidth(r.lineWidth), s.moveTo(e.x, e.y);
  },
  touchMove: function(i) {
      var e = t(i.touches, 1)[0], a = this.data, n = a.ctx, o = a.ctxBak, s = e.x, r = e.y;
      n.lineTo(s, r), n.stroke(), n.draw(!0), n.moveTo(s, r), o.lineTo(s, r), o.stroke();
      var c = o.getActions();
      this.actions.push(c), o.moveTo(s, r);
  },
  touchEnd: function() {},
  initEraser: function() {
      this.setData({
          eraser: {
              color: "#ffffff",
              icon_name: "eraser.png"
          }
      });
  },
  chooseEraser: function() {
      this.setData({
          eraser: {
              color: "#FF1C6C",
              icon_name: "eraser_sel.png"
          },
          "pen.color": "#ffffff"
      });
  },
  changeColor: function(t) {
      this.initEraser(), this.setData({
          "pen.color": t.currentTarget.dataset.color
      });
  },
  changeWeight: function(t) {
      this.setData({
          "pen.lineWidth": t.currentTarget.dataset.width
      });
  },
  clearRect: function() {
      this.initEraser();
      var t = this.data.ctx;
      t.clearRect(0, 0, 710, 710), t.setFillStyle("white"), t.fillRect(0, 0, 710, 710), 
      t.draw(!0), this.actions = [];
  },
  canvasToFile: function() {
      var t = arguments.length > 0 && void 0 !== arguments[0] && arguments[0];
      wx.showLoading({
          title: "生成中...",
          mask: !0
      }), wx.canvasToTempFilePath({
          x: 0,
          y: 0,
          width: 710,
          heght: 710,
          destWidth: 710,
          destHeight: 710,
          canvasId: "my-canvas",
          fileType: "jpg",
          quality: 1,
          success: function(i) {
              console.log("sssssssssssssss")
          },
          fail: function(t) {
              console.log("failllllllll");
          }
      });
  },
  inSaving: !1,
  formId: "",
  getC: function(){
    let that = this
    wx.canvasToTempFilePath({
      x: 0,
      y: 0,
      width: 710,
      heght: 710,
      destWidth: 710,
      destHeight: 710,
      canvasId: "my-canvas",
      fileType: "jpg",
      quality: 1,
      success: function(i) {
          console.log("sssssssssssssss")
          console.log(i)
          that.setData({
            localPath: i.tempFilePath
          })
          console.log(that.data.localPath)
          let openid = wx.getStorageSync('openid')
          wx.uploadFile({
            filePath: that.data.localPath,
            method:'POST',
            formData:{
              openId:JSON.stringify({openId:openid})
            },
            name: 'file',
            url: 'http://192.168.123.205:8080/api/file/upload',
            header:{
              "Content-Type":false,
            },
            success: (res) => {
              console.log(res)
              wx.navigateTo({
                url: '../Guess/Guess?roomid='+JSON.stringify(that.data.Rid),
              })
            },
            fail: (res) => {
              console.log("upload fail")
            }
          })
      },
      fail: function(t) {
          console.log("failllllllll");
      }
  });
  },
  doSaveImage: function(t) {
      var i = this;
      this.inSaving || (this.inSaving = !0, this.formId = t.detail.formId, 0 != this.actions.length ? (this.data.ctx.draw(!0), 
      setTimeout(function() {
          i.canvasToFile(i.submit);
      }, 500)) : wx.showToast({
          title: "画布为空",
          duration: 500,
          image: "../../resource/picture/sad.png"
      }));
  },
  countdown: function () {
    var that = this
    var second = this.data.second1
    if (second == 0) {
      that.setData({
        flag: true
      })
      wx.redirectTo({
        url: '../ai-classifier/index?title1='+JSON.stringify(this.data.title1),
      })
      that.setData({
        second: 4
      })
      return
    }
    var time = setTimeout(function () {
      that.setData({
        second1: second - 1
      })
      that.countdown(that)
    }, 1000)
    that.setData({
      timer: time
    })
  }
}
  // submit: function(t) {
  //     var e = this, a = this.data, n = a.topicIndex, o = a.topicList, s = a.canvasW, r = {
  //         formId: e.formId,
  //         keyword: o[n].keyword,
  //         tip: o[n].tip,
  //         canvasW: s,
  //         actions: JSON.stringify(e.actions)
  //     }, c = wx.getStorageSync("userInfo").sessionid, d = i.util.url("entry/wxapp/save", {
  //         state: "we7sid-" + c
  //     });
  //     d = d + "&sign=" + i.util.getSign(d), d += "&m=bei_nhwc", wx.uploadFile({
  //         url: d,
  //         filePath: t,
  //         name: "image",
  //         formData: r,
  //         success: function(t) {
  //             if (e.inSaving = !1, 0 === (t = JSON.parse(t.data)).code) {
  //                 wx.hideLoading(), wx.showToast({
  //                     title: "保存成功",
  //                     duration: 500,
  //                     image: "../../resource/picture/happy.png"
  //                 });
  //                 var i = t.data;
  //                 // wx.redirectTo({
  //                 //     url: "../success/success?image_id=" + i.image_id + "&image_url=" + i.image_url
  //                 // });
  //             } else wx.showToast({
  //                 title: t.message,
  //                 duration: 1e3,
  //                 image: "../../resource/picture/sad.png"
  //             });
  //         }
  //     });
  // },
  
//     onReachBottom: function() {},
//     onShareAppMessage: function() {}
);