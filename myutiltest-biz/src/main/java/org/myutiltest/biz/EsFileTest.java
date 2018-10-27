package org.myutiltest.biz;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

@SuppressWarnings("all")
public class EsFileTest {
	TransportClient client;

	String Index = "books";
	String Type = "article";

	//@Before
	public void setUp() throws Exception {
		Settings esSettings = Settings.builder().put("cluster.name", "elasticsearch") // 设置ES实例的名称
				.build();
		client = new PreBuiltTransportClient(esSettings);// 初始化client较老版本发生了变化，此方法有几个重载方法，初始化插件等。
		// 此步骤添加IP，至少一个，其实一个就够了，因为添加了自动嗅探配置
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
	}

	//@Test
	public void testCreateIndex() throws Exception {
		File parent = new File("/Users/lixing/books/");
		File[] arr = parent.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".txt");
			}
		});
		System.out.println(arr.length);
		for (File tmp : arr) {
			String content = IOUtils.toString(new FileInputStream(tmp));
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("path", tmp.getAbsolutePath());
			infoMap.put("content", content.substring(0, content.indexOf("START")));
			IndexResponse indexResponse = client.prepareIndex(Index, Type, tmp.getAbsolutePath()).setSource(infoMap)
					.execute().actionGet();
			System.out.println(indexResponse.getId());
		}
	}

	//@Test
	public void testQuery() {
		QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("content", "title");
		long start = System.currentTimeMillis();
		SearchResponse searchResponse = client.prepareSearch(Index).setTypes(Type).setQuery(queryBuilder)
				.setSize(10000).execute().actionGet();
		System.out.println("cost time:" + (System.currentTimeMillis() - start));
		SearchHits hits = searchResponse.getHits();
		System.out.println("查到记录数：" + hits.getTotalHits());
		SearchHit[] searchHists = hits.getHits();
		if (searchHists.length > 0) {
			for (SearchHit hit : searchHists) {
				System.out.println(hit.getId());
				System.out.println(hit.getSourceAsString());
				// client.prepareDelete(Index, Type, hit.getId()).execute();
			}
		}
	}

}