# ad_options
通过github实现广告的控制

   github提供个人主页的功能，因此可以从多个平台访问到我发布到github上的文件，从而可以通过xml文件控制安卓app的行为。目前正在做的是一个通过上传 xml文件控制安卓应用显示广告的工具。
   最近做了一个串口调试工具的程序，已提交市场正在审核中，想要加点广告来赚钱，但是此时是无法接入广告平台的，因为广告平台需要提交市场上架的证明才会给我一个id码用于调用广告SDK，而得到id之后还需要将id写入程序中，重新发布到市场进行审核，这个过程是比较耗时的。所以我决定编写一个广告参数获取工具，我将申请到的广告id码保存在个人主页的某个目录下的xml文件中，然后在安卓端通过网络访问获取到xml文件的数据流，然后通过sax工具读取xml中的配置信息。提取出boolean和String。获取是否启用广告和广告的id，其实只需要获取String就够了，因为如果不想显示广告，则可以直接将String设为“”。
   我查了一下关于个人主页的访问限制，发现并没有访问次数的限制，但是有每月100G的流量限制。这个不用担心，因为我感觉我的这个OTG最多每天有十来个人用，一个月顶多有30*10*100B = 30KB的流量。能给我一天带来五六毛钱我就已经很满足了。一天挣几毛钱的码农估计真正的农民都会鄙视我吧。
   
   没有正经学过xml，凭感觉写了个xml配置文件:
   
   <?xml version="1.0" encoding="utf-8"?>
    <root appid="xxxxxxxx">
      <splash en="true"  posid="xxxxxxxxxxxxxxxx" />
    </root>
    
   写完后通过git指令push到服务器。
   其中splash是开屏广告的配置，为了不影响用户体验所以并没有加banner和插屏。
   
   我将安卓端的代码封装为一个类 AdOptionFetcher.java
   主要的函数如下：
   
   public AdOptionFetcher(Context context,String url);
   开启线程从指定的url地址获取广告的配置文件进行解读，解读完成后线程关闭。
   
   public AdOptionFetcher(Context context,String url,OnOptionsFetchedListener listener); 
   相对于上一个函数，添加了设置 监听器，当配置获取过程结束后，会调用onOptionsFetched()函数，如果认为waitForResult函数会使界面卡顿，则可以在onOptionsFetched函数中初始化广告。这样就可以避免卡顿，但是同时会导致广告初始化的时刻不可预测。
   
   public void waitForResult();
   等待配置解析线程关闭，该方法在主线程中使用，用于实现同步，当网络状况差时会用到但是同时会造成界面卡顿。但在网络流畅的时候不会出现卡顿问题。
   总共就这三个函数，使用起来非常简单。
   
   在使用时在Activity中声明该工具类，然后在onCreate调用new上述函数实例化。然后即可通过访问类中的id和en信息。
   
   但是这里比较麻烦的一点是如果我的广告在不同的Acitivty中展示，splash广告在SplashActicity，banner广告在主界面Activity，这是我从SplashActivity进入MainActivity后需要重新实例化该类，然后重新获取xml配置数据。这样是很反人类的做法，为了避免这种脑残设计，我使用了SharedPreference。在应用打开后初次获取到数据后，会将数据记录到sharedPreference中。然后就可以在不同的Activity中获取一开始得到的数据，而不需要重新获取xml数据。
   
   
