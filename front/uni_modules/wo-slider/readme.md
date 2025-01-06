# wo-slider 滑块组件

> 采用touchstart、touchmove、touchend事件实现的滑块组件,支持H5、微信小程序（其他小程序未试过，可自行尝试）
> 参考文章：https://blog.csdn.net/qq_42278240/article/details/132423428
## props属性

### height

> 滑道高度（rpx单位）

~~~js
height: {
    type: Number,
    default: 80,
},
~~~

### sliderSize

> 滑块大小

~~~js
sliderSize: {
    type: Number,
    default: 80
},
~~~

### round

> 圆角滑道

~~~js
round: {
    type: Object,
    default: function () {
        return { show: false, style: '1rpx solid #C8C9CC' }
    }
},
~~~

### border

> 边框样式

~~~js
border: {
    type: Object,
    default: function () {
        return { show: true, style: '1rpx solid #C8C9CC' }
    }
}
~~~

## 插槽

### 起始滑块自定义

> v-slot:begin

~~~html
<wo-slider>
    <template v-slot:begin>
        <view style="background-color: #E5673B; height: 100%; display: flex; justify-content: center; align-items: center">
            <image style="height: 50rpx;width: 50rpx;" src="@/static/arrow-right-bold.svg"></image>
        </view>
    </template>
</wo-slider>
~~~

### 终点滑块自定义

> v-slot:end

~~~html
<wo-slider>
    <template v-slot:end>
        <view style="background-color: #1BA035; height: 100%; display: flex; justify-content: center; align-items: center">
            <image style="height: 50rpx;width: 50rpx;" src="@/static/select-bold.svg"></image>
        </view>
    </template>
</wo-slider>
~~~

### 滑道-初始态内容自定义

> v-slot:init

可在此自定义滑动结束前的背景、内容信息等

~~~html
<wo-slider>
    <template v-slot:init>
        <view style="background-color: #107BBA; color: #fff; height: 100%; display: flex; justify-content: center;align-items: center;">😀滑动解锁大礼包</view>
    </template>
</wo-slider>
~~~

### 滑道-成功态内容自定义

> v-slot:isFinished

可在此自定义滑动结束后的成功态背景色、以及内容信息等.

一般结合@slideFinish事件动态设置滑动结束后的内容信息，这样可以做到滑动结束后才显示内容信息

~~~html
<wo-slider @slideFinish="onFinish">
    <template v-slot:isFinished>
        <view style="height: 100%;background-color: #FF5F2F; color: #fff; display: flex; justify-content: center;align-items: center;">
            <view>
                {{content}}
            </view>
        </view>
    </template>
</wo-slider>
~~~

~~~js
methods: {
    onFinish(e) {
        console.log('滑动完成', e);
        this.content = '😊验证成功'
    }
}
~~~



## 事件

### @slideFinish

> 滑动结束后执行，

#### 参数

+ status属性: 滑动结束标志，值为：success
+ resetFunc函数：重置函数，滑块回到原点

