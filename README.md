# DoubanMovieCard

自定义ViewGroup方式实现豆瓣电影推荐卡片层叠效果

# 产品形态
有三个叠在一起的卡片，卡片错落叠放从而有一定的层次感
开始只有一张卡片
当放上第二张卡片时，第一张卡片适度透明，并且放置卡片的过程中两张卡片会运动
当放上第三张卡片时，第一张以及第二张都适度透明，并且放置卡片的过程中三张卡片会运动
卡片放置的动画过程中，不允许打断。不能两个动画同时进行。但是可以通过多次点击滑动的方式干预某个动画

屏幕中最多可以放两组卡片，一组是层叠的卡片，一组是等待滑动到高亮区的卡片。
支持左右滑动。有阻尼效果，但是不支持抛动。

从左边看，卡片位于起始位置时，继续向右边滑动，则有过度拉伸效果
从右边看，卡片位于起终止位置时，继续向左边滑动，则有过度拉伸效果
滑动过程有监听，进而可以协调外部元素一起运动，具体效果同豆瓣

# ViewGroup实现该方式需要解决哪些问题？
接收手势事件
绘制层叠区域的三个卡片的层级关系，并且支持卡片的相对运动
绘制所有卡片的相对运动

卡片从层叠区域左右滑动时，有个复合动画，如果拆开来看的话，有缩放动画、alpha动画、位移动画
动画过程中有一些临界值，用来控制动画的落点

# 外部访问的接口
