package com.lvvi.vividtv.model

class ChannelInfoModel {
    /**
     * code : 200
     * serverTime : 1563804551
     * EPG : [{"seqId":"ad2eb53f-12ba-4417-b85e-d892df14006f","programId":"EPGPSAbO683L5Ah3JBHLtVQq190722","title":"晚间新闻","channelId":"cctv1","startTime":"1563803980","endTime":"1563806205","eventType":"","eventId":"","bindVsetid":"C10420","bindVideoid":"","bindMid":"17u2U7Ff0815","playLength":2225,"firstPlayFlag":1},{"seqId":"260c3651-7bd0-4336-9366-fbec39f03cda","programId":"EPGP0eEufHI0ebFzthhn5EVm190721","title":"财经人物周刊-2019-28","channelId":"cctv2","startTime":"1563803280","endTime":"1563806310","eventType":"","eventId":"","bindVsetid":"VSET100220095839","bindVideoid":"","bindMid":"172myANn0815","playLength":3030,"firstPlayFlag":1},{"seqId":"8c0ae153-08ab-4502-85a5-24e281cf415a","programId":"EPGPXz9OrwcwogNkOwDSYAka190721","title":"非常6+1-2019-29","channelId":"cctv3","startTime":"1563800325","endTime":"1563805690","eventType":"","eventId":"","bindVsetid":"C10605","bindVideoid":"","bindMid":"17yeuE730815","playLength":5365,"firstPlayFlag":1}]
     */

    var code: Int = 0
    var serverTime: String? = null
    var EPG: List<EPGBean>? = null

    class EPGBean {
        /**
         * seqId : ad2eb53f-12ba-4417-b85e-d892df14006f
         * programId : EPGPSAbO683L5Ah3JBHLtVQq190722
         * title : 晚间新闻
         * channelId : cctv1
         * startTime : 1563803980
         * endTime : 1563806205
         * eventType :
         * eventId :
         * bindVsetid : C10420
         * bindVideoid :
         * bindMid : 17u2U7Ff0815
         * playLength : 2225
         * firstPlayFlag : 1
         */

        var seqId: String? = null
        var programId: String? = null
        var title: String? = null
        var channelId: String? = null
        var startTime: String? = null
        var endTime: String? = null
        var eventType: String? = null
        var eventId: String? = null
        var bindVsetid: String? = null
        var bindVideoid: String? = null
        var bindMid: String? = null
        var playLength: Int = 0
        var firstPlayFlag: Int = 0
    }
}
