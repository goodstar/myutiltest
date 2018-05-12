package org.myutiltest.biz;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class EsTest {
	
	public static void main(String[] args) throws Exception {
		Settings esSettings = Settings.builder().put("cluster.name", "elasticsearch") // 设置ES实例的名称
				.build();
		TransportClient client = new PreBuiltTransportClient(esSettings);// 初始化client较老版本发生了变化，此方法有几个重载方法，初始化插件等。
		// 此步骤添加IP，至少一个，其实一个就够了，因为添加了自动嗅探配置
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		Map<String, Object> infoMap = new HashMap<String, Object>();
		infoMap.put("name", "广告信息11");
		infoMap.put("title", "我的广告22");
		// infoMap.put("createTime", new Date());
		infoMap.put("count", 1022);
		//IndexResponse indexResponse = client.prepareIndex("test", "info", "200").setSource(infoMap).execute()
		//		.actionGet();
		//System.out.println("id:" + indexResponse.getId());

		//GetResponse response = client.prepareGet("test", "info", "200").execute().actionGet();
		//System.out.println(response.getId());
		//System.out.println(response.getSourceAsString());
		
		client.prepareDelete("test", "info", "200").execute().actionGet();
		QueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("count").gt(50);
		SearchResponse searchResponse = client.prepareSearch("test").setTypes("info").setQuery(rangeQueryBuilder)
				.addSort("count", SortOrder.DESC).setSize(20).execute().actionGet();
		SearchHits hits = searchResponse.getHits();
		System.out.println("查到记录数：" + hits.getTotalHits());
		SearchHit[] searchHists = hits.getHits();
		if (searchHists.length > 0) {
			for (SearchHit hit : searchHists) {
				System.out.println(hit.getId());
				String name = (String) hit.getSource().get("name");
				Integer count = (Integer) hit.getSource().get("count");
				System.out.format("name:%s ,count :%d \n", name, count);
			}
		}
	}
}
