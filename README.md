# VideoButton(视频按钮)
在android上实现视频按钮效果，拥有拍照和录像两种模式。


<p>    
  <img src="https://github.com/BeiHaiCoding/VideoButton/blob/5997e21dce6b84161045f416e564fc0c32b17054/image/Screenshot_20210426-013152_300x533.png" alt="Latest Stable  Version" />
  <img src="https://github.com/BeiHaiCoding/VideoButton/blob/5997e21dce6b84161045f416e564fc0c32b17054/image/Screenshot_20210426-013201_300x533.png" alt="Latest Stable  Version" />
</p>

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)


使用说明
-----

在项目build.gradle文件下添加
```
allprojects {
     repositories {
         maven { url 'https://jitpack.io' }
     }
 }
 ```

在模块build.gradle文件下添加
```
implementation 'com.github.BeiHaiCoding:VideoButton:1.1'
```

XML
-----

```
    <com.beihai.videobutton.VideoButton
           android:layout_width="wrap_content"
           android:layout_height="wrap_content"
           app:buttonType="video"
           app:progressBarDuration="15"
           app:minRecordTime="3"/>
```

####属性说明
* `innerCircleColorPhoto`     (color)     -> default  #FFFFFF      拍照模式内圆颜色
* `outerCircleColorPhoto`     (color)     -> default  #AAFFFFFF    拍照模式外圆颜色
* `innerCircleColorVideo`     (color)     -> default  #FF4500      录像模式内圆颜色
* `outerCircleColorVideo`     (color)     -> default  #AAFFFFFF    录像模式外圆颜色
* `progressBarColor`          (color)     -> default  #90EE90      进度条颜色
* `timerTextColor`            (color)     -> default  #FFFFFF      计时器文本颜色
* `timerTextSize`             (dimension) -> default  14sp         计时器文本大小
* `progressBarDuration`       (integer)   -> default  15(s)        进度条总时间（即录像总时间）
* `minRecordTime`             (integer)   -> default  3(s)         最小录像时间

kotlin
-----

```
//录像结束监听
videoButton.setOnRecordListener(object :VideoButton.OnRecordListener{
            override fun onRecordFinished() {
                Toast.makeText(this@MainActivity,"录像结束",Toast.LENGTH_SHORT).show()
            }
        })

```





