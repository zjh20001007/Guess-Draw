// pages/ai-classifier/index.js

const app = getApp();

const classifier = require('../../components/aiModels/classifier/classifier.js');
const fetchWechat = require('fetch-wechat');
const tf = require('@tensorflow/tfjs-core');
const cpu = require('@tensorflow/tfjs-backend-cpu');
const webgl = require('@tensorflow/tfjs-backend-webgl');
const plugin = requirePlugin('tfjsPlugin');
const util = require('../../utils/util.js')

const DRAW_SIZE = [255, 255];
const DRAW_PREC = 0.03;

var drawInfos = [];
var startX = 0;
var startY = 0;
var bgColor = "white";
var begin = false;
var curDrawArr = [];
var lastCoord = null; // 记录画笔上一个坐标
var predictStroke = []; // 用于预测的笔画偏移量数组
var drawing = false; // 正在画
var classifing = false; // 正在猜
const promiseObj = new util.promiseContainer(); // 只响应最后一次promise

Page({
  /**
   * 页面的初始数据
   */
  data: {
    userAvatarUrl:'',
    canIUseAvatar: wx.canIUse("open-data.type.userAvatarUrl"),
    timer:'',
    flag:false,
    second1: 25,
    title1:'',
    levelId: 0,
    status: "正在努力加载模型ᕙ༼ ͝°益° ༽ᕗ",
    hidden: true, // 是否隐藏生成海报的canvas
    bgColor: bgColor,
    currentColor: 'black',
    avatarUrl: "",
    curWidthIndex: 0,
    lineWidthArr: [3, 5, 10, 20],
    avaliableColors: ["black", "red", "blue", "gray", "#ff4081",
      "#009681", "#259b24", "green", "#0000CD", "#1E90FF", "#ADD8E6", "#FAEBD7", "#FFF0F5", // orange
      '#FFEBA3', '#FFDE7A', '#FFCE52', '#FFBB29', '#FFA500', '#D98600', '#B36800', '#8C4D00', '#663500',
      // red
      '#FFAFA3', '#FF887A', '#FF5D52', '#FF3029', '#FF0000', '#D90007', '#B3000C', '#8C000E', '#66000E',
      // green
      '#7BB372', '#58A650', '#389931', '#1A8C16', '#008000', '#005903', '#003303',
      // yellow
      '#FFF27A', '#FFF352', '#FFF829', '#FFFF00', '#D2D900', '#A7B300', '#7E8C00', '#586600',
      // cyan
      '#A3FFF3', '#7AFFF2', '#52FFF3', '#29FFF8', '#00FFFF', '#00D2D9', '#00A7B3', '#007E8C', '#005866',
      // blue
      '#A3AFFF', '#7A88FF', '#525DFF', '#2930FF', '#0000FF', '#0700D9', '#0C00B3', '#0E008C', '#0E0066',
      // Violet
      '#FAB1F7', '#EE82EE', '#C463C7', '#9B48A1', '#73317A', '#4D2154',
      "black"
    ],
    classesName: ['class1', 'class2', 'class3', 'class4', 'class5'],
    classesProg: [0, 0, 0, 0, 0]
  },

  getC: function(){
    let that = this
    wx.canvasToTempFilePath({
      x: 0,
      y: 0,
      width: 710,
      heght: 710,
      destWidth: 710,
      destHeight: 710,
      canvasId: "firstCanvas",
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
              openId:openid,
              title: that.data.title1
            },
            name: 'file',
            url: 'http://192.168.123.205:8080/api/file/AIupload',
            header:{
              "Content-Type":false,
            },
            success: (res) => {
              console.log(res)
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
  onUnload() {
    clearInterval(this.data.timer)
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
  countdown: function () {
    var that = this
    var second = this.data.second1
    let openid = wx.getStorageSync('openid')
    if (second == 0) {
      //this.getC()
      that.setData({
        flag: true
      })
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
      wx.redirectTo({
        url: '../Fail/Fail?title1='+JSON.stringify(that.data.title1),
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
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let userAvatarUrl = wx.getStorageSync('user').avatarUrl
    this.setData({
      userAvatarUrl:userAvatarUrl
    })

    wx.showShareMenu({
      withShareTicket: true
    });

    let systemInfo = wx.getSystemInfoSync();
    console.log(systemInfo.platform);
    if (systemInfo.platform == 'android') {
      plugin.configPlugin({
        // polyfill fetch function
        fetchFunc: fetchWechat.fetchFunc(),
        // inject tfjs runtime
        tf,
        cpu,
        canvas: wx.createOffscreenCanvas()
      });
      // setWasmPath('https://cdn.jsdelivr.net/npm/@tensorflow/tfjs-backend-wasm@2.0.0/wasm-out/tfjs-backend-wasm.wasm', true);
      // tf.setBackend('wasm').then(() => console.log('set wasm backend'));
    } else {
      plugin.configPlugin({
        // polyfill fetch function
        fetchFunc: fetchWechat.fetchFunc(),
        // inject tfjs runtime
        tf,
        // inject backend
        webgl,
        // provide webgl canvas
        canvas: wx.createOffscreenCanvas()
      });
    }
    this.countdown();
      let title2 = JSON.parse(options.title1)
      this.setData({
        title1:title2,
      })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    this.context = wx.createCanvasContext("firstCanvas");
    this.init();
    this.fillBackground(this.context);
    this.draw();

    predictStroke = [];
    wx.showLoading({
      title: '加载模型...',
      mask: true,
    });
    classifier.loadModels().then((res) => {
      wx.hideLoading();
      if (res) {
        this.setData({
          status: '你来作画，我来猜！(⊙ᗜ⊙)'
        });
        //this.setCurrentColor(this.data.avaliableColors[Math.floor((Math.random() * this.data.avaliableColors.length))]);
        wx.showToast({
          title: '加载完成',
        })
      } else {
        this.setData({
          status: '模型加载超时（´□｀川）'
        });
        wx.showModal({
          title: '加载失败',
          content: '刷新一下？',
          success(res) {
            if (res.confirm) {
              wx.reLaunch({
                url: '/pages/ai-classifier/index',
              })
            }
          }
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {},

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function (res) {

  },

  /*--------------------- UI事件 --------------------------------------------------- */
  // 绘制开始 手指开始按到屏幕上
  touchStart: function (e) {
    if (classifing) {
      return;
    }
    drawing = true;

    this.lineBegin(e.touches[0].x, e.touches[0].y);
    this.recordPredictStroke(e.touches[0].x, e.touches[0].y);
    this.draw(true);
    curDrawArr.push({
      x: e.touches[0].x,
      y: e.touches[0].y
    });
  },

  // 绘制中 手指在屏幕上移动
  touchMove: function (e) {
    if (classifing) {
      return;
    }
    drawing = true;

    if (begin) {
      this.lineAddPoint(e.touches[0].x, e.touches[0].y);
      this.recordPredictStroke(e.touches[0].x, e.touches[0].y);
      this.draw(true);
      curDrawArr.push({
        x: e.touches[0].x,
        y: e.touches[0].y
      });
    }
  },

  // 绘制结束 手指抬起
  touchEnd: function () {
    drawing = false;
    if (classifing) {
      return;
    }

    drawInfos.push({
      drawArr: curDrawArr,
      color: this.data.currentColor,
      lineWidth: this.data.lineWidthArr[this.data.curWidthIndex],
    });
    // console.log(curDrawArr);
    curDrawArr = [];
    this.lineEnd();
    predictStroke[predictStroke.length - 1][2] = 1.0;
    //console.log(predictStroke);

    this.setData({
      status: Math.random() > 0.5 ? '正在思索中［(－－)］zzz' : '绞尽脑汁中［(－－)］zzz',
    });

    promiseObj.addPromise(new Promise((resolve) => setTimeout(resolve, 800)), () => {
      if (drawing) {
        console.log('Drawing, do not predict.');
        return true;
      }
      classifing = true;
      wx.showLoading({
        title: Math.random() > 0.5 ? '冥思中' : '思考中',
        mask: true
      })
      //console.log(predictStroke);
      classifier.classify(predictStroke, (res) => {
        // console.log(res);
        wx.hideLoading();
        let probs = res.probs;
        for (let i = 0; i < probs.length; i++) {
          probs[i] = Number(probs[i] * 100).toFixed(1);
        }

        classifing = false;
        this.setData({
          status: '我猜出来了！(⊙ᗜ⊙)'
        });
        this.setData({
          classesName: res.names,
          classesProg: probs
        });
        // predictStroke = [];
        if(res.names[0]==this.data.title1){
          this.getC();
          this.setData({
            flag: true
          })
          wx.redirectTo({
            url: '../ShowWord/ShowWord',
          })
        }
      }, res => {
        wx.hideLoading();
        classifing = false;
        this.setData({
          status: '我猜不出来(╥╯^╰╥)',
        });
      });
    });

    //this.setCurrentColor(this.data.avaliableColors[Math.floor((Math.random() * this.data.avaliableColors.length))]);
  },


  // 点击设置线条宽度
  clickChangeWidth: function (e) {
    let index = e.currentTarget.dataset.index;
    this.setLineWidthByIndex(index);
  },

  // 点击设置线条颜色
  clickChangeColor: function (e) {
    let color = e.currentTarget.dataset.color;
    this.setCurrentColor(color);
  },

  // 点击切换到擦除
  clickErase: function () {
    this.setCurrentColor(bgColor);
  },

  // 点击清空canvas
  clickClearAll: function () {
    this.fillBackground(this.context);
    this.draw();
    drawInfos = [];
    lastCoord = null;
    predictStroke = [];
    this.setData({
      classesName: ['class1', 'class2', 'class3', 'class4', 'class5'],
      classesProg: [0, 0, 0, 0, 0]
    });
    classifier.resetClassifier();
    this.init();
  },

  // 点击撤销上一步
  clickFallback: function () {
    if (drawInfos.length >= 1) {
      drawInfos.pop();
    }
    this.reDraw();
  },

  // 点击重绘canvas
  clickReDraw: function () {
    this.reDraw();
  },

  // 保存canvas图像到本地缓存
  clickStore: function () {
    let that = this;
    this.store("firstCanvas", res => {
      wx.saveImageToPhotosAlbum({
        filePath: res,
      })
    });
  },

  // 点击分享
  // 现在的功能是拼接一个分享出去的图像，然后保存到本地相册
  clickShare: function () {
    let that = this;
    // 在用户看不到的地方显示隐藏的canvas
    this.setData({
      hidden: false
    });
    // 截图的时候屏蔽用户操作
    wx.showLoading({
      title: '请稍等',
      mask: true
    })
    // 300毫秒后恢复正常
    setTimeout(() => {
      wx.hideLoading();
      that.setData({
        hidden: true
      });
    }, 300);

    // 截图用户绘制的canvaas
    this.store("firstCanvas", filePath => {
      // 生成海报并分享

      that.sharePost(filePath);

    });
  },

  /*---------------------end of 界面绑定的函数------------------------------------------ */

  // 初始化
  init: function () {
    this.context.setLineCap('round') // 让线条圆润
    this.context.strokeStyle = this.data.currentColor;
    this.context.setLineWidth(this.data.lineWidthArr[this.data.curWidthIndex]);
    this.setData({
      currentColor: this.data.currentColor,
      curWidthIndex: this.data.curWidthIndex
    });
  },

  // 设置线条颜色
  setCurrentColor: function (color) {
    this.data.currentColor = color;
    this.context.strokeStyle = color;
    this.setData({
      currentColor: color
    });
  },

  // 设置线条宽度
  setLineWidthByIndex: function (index) {
    let width = this.data.lineWidthArr[index];
    this.context.setLineWidth(width);
    this.setData({
      curWidthIndex: index
    });
  },

  // 开始绘制线条
  lineBegin: function (x, y) {
    begin = true;
    this.context.beginPath()
    startX = x;
    startY = y;
    this.context.moveTo(startX, startY)
    this.lineAddPoint(x, y);
  },

  // 绘制线条中间添加点
  lineAddPoint: function (x, y) {
    this.context.moveTo(startX, startY)
    this.context.lineTo(x, y)
    this.context.stroke();
    startX = x;
    startY = y;
  },

  // 绘制线条结束
  lineEnd: function () {
    this.context.closePath();
    begin = false;
  },

  // 根据保存的绘制信息重新绘制
  reDraw: function () {
    this.init();
    this.fillBackground(this.context);
    // this.draw(false);
    for (var i = 0; i < drawInfos.length; i++) {
      this.context.strokeStyle = drawInfos[i].color;
      this.context.setLineWidth(drawInfos[i].lineWidth);
      let drawArr = drawInfos[i].drawArr;
      this.lineBegin(drawArr[0].x, drawArr[0].y)
      for (var j = 1; j < drawArr.length; j++) {
        this.lineAddPoint(drawArr[j].x, drawArr[j].y);
        // this.draw(true);
      }

      this.lineEnd();
    }

    this.draw();
  },

  // 将canvas导出为临时图像文件
  // canvasId： 要导出的canvas的id
  // cb: 回调函数
  store: function (canvasId, cb) {
    wx.canvasToTempFilePath({
      destWidth: 400,
      destHeight: 300,
      canvasId: canvasId,
      success: function (res) {
        typeof (cb) == 'function' && cb(res.tempFilePath);
      },
      fail: function (res) {
        console.log("store fail");
        console.log(res);
      }
    })
  },

  // 绘制canvas
  // isReverse: 是否保留之前的像素
  draw: function (isReverse = false, cb) {
    this.context.draw(isReverse, () => {
      if (cb && typeof (cb) == "function") {
        cb();
      }
    });
  },


  // canvas上下文设置背景为白色
  fillBackground: function (context) {
    context.setFillStyle(bgColor);
    context.fillRect(0, 0, 500, 500); //TODO context的宽和高待定
    context.fill();
  },


  // 预览
  pageView: function () {
    wx.canvasToTempFilePath({
      canvasId: 'firstCanvas',
      success: function (res) {
        wx.previewImage({
          current: res.tempFilePath,
          urls: [res.tempFilePath],
          success: function (res) {

          },
          fail: function (res) {

          },
        });
      },
      fail: function (res) {
        wx.showToast({
          title: '保存失败',
          icon: 'none'
        })
      }
    }, this)
  },

  // 分享海报
  sharePost: function (filePath) {
    let url = `/pages/share/share?imgUrl=${filePath}&levelId=${this.data.levelId}`;
    wx.navigateTo({
      url: url
    })
  },

  // 记录笔画偏移量数组，用于预测分类
  recordPredictStroke: function (x, y) {
    var posX = x;
    var posY = y;
    if (lastCoord !== null) {
      // Calc the delta.
      let xDelta = posX - lastCoord[0];
      let yDelta = lastCoord[1] - posY; // Reverse the y coordinate.
      // Normalization.
      xDelta = Number((xDelta / DRAW_SIZE[0]).toFixed(10));
      yDelta = Number((yDelta / DRAW_SIZE[1]).toFixed(10));
      // xDelta = xDelta / DRAW_SIZE[0];
      // yDelta = yDelta / DRAW_SIZE[1];
      if (predictStroke.length > 0) {
        if (xDelta === 0.0 && predictStroke[predictStroke.length - 1][0] === 0.0) {
          // Merge if only move in y axis.
          predictStroke[predictStroke.length - 1][1] += yDelta;
          lastCoord = [posX, posY];
          return;
        }
        if (yDelta === 0.0 && predictStroke[predictStroke.length - 1][1] === 0.0) {
          // Merge if only move in x axis.
          predictStroke[predictStroke.length - 1][0] += xDelta;
          lastCoord = [posX, posY];
          return;
        }
      }
      // Ignore < DRAW_PREC.
      if (Math.abs(xDelta) >= DRAW_PREC || Math.abs(yDelta) >= DRAW_PREC) {
        predictStroke.push([xDelta, yDelta, 0.0]);
        lastCoord = [posX, posY];
      }
      // console.log(predictStroke)
    } else {
      lastCoord = [posX, posY];
    }
  }
})