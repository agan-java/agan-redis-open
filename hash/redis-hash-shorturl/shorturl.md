
##淘宝短链接如何设计？

### 体验淘宝短链接业务场景
#### 场景1：淘宝短信
你们应该收到淘宝的短信
```
【天猫】有优惠啦！黄皮金煌芒果（水仙芒）带箱10斤49.8元！
核薄无丝很甜喔！购买： c.tb.cn/c.ZzhFZ0 急鲜丰 退订回TD
```
打开IE，输入 c.tb.cn/c.ZzhFZ0 就转变为如下:
https://h5.m.taobao.com/ecrm/jump-to-app.html?scm=20140608.2928562577.LT_ITEM.1699166744&target_url=
http%3A%2F%2Fh5.m.taobao.com%2Fawp%2Fcore%2Fdetail.htm%3Fid%3D567221004504%26scm=20140607.2928562577.
LT_ITEM.1699166744&spm=a313p.5.1cfl9ch.947174560063&short_name=c.ZzhFZ0&app=chrome



#### 场景2：淘宝APP分享URL


```
【这个#聚划算团购#宝贝不错:【官方旗舰】步步高家教机S5英语小学初高中课本同步小天才平板
儿童点读学习机智能学生平板电脑护眼旗舰店(分享自@手机淘宝android客户端)】
https://m.tb.cn/h.eAE6vuE 
點￡擊☆鏈ㄣ接，再选择瀏覽→噐咑ぺ鐦；或椱ァ製这句话€eyuf1YeAXFf€后打开👉淘宀┡ē👈
```
打开IE，输入https://m.tb.cn/h.eAE6vuE  就转变为如下:
https://detail.tmall.com/item.htm?id=597254411409&price=3998-4398&sourceType=item&sourceType=item&suid=
4c8fc4d8-cb5e-40c0-b4b6-c4a06598781a&ut_sk=1.WmH11veugHoDAGWzSv+jAZg2_21646297_1574219840558.Copy.1&un
=ceed7d76bfbe7a3b4b68d5f77a161062&share_crt_v=1&spm=a2159r.13376460.0.0&sp_tk=4oKzaUU0SllFcWZuRjLigrM=
&cpp=1&shareurl=true&short_name=h.eF25Q3n&sm=505e90&app=chrome&sku_properties=1627207:28332

体验了以上2个场景，我们来总结：
1. 先说下什么是短链接？
就是把普通网址，转换成比较短的网址。  
2. 短链接有什么好处？
- 节省网址长度，便于社交化传播。
- 方便后台跟踪点击量、统计。



### 案例实战：SpringBoot+Redis高并发《短链接转换器》
《短链接转换器》的原理：
1. 长链接转换为短链接
实现原理：长链接转换为短链接加密串key,然后存储于redis的hash结构中。
2. 重定向到原始的url
实现原理：通过加密串key到redis找出原始url，然后重定向出去



