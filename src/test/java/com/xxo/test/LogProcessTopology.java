package com.xxo.test;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

public class LogProcessTopology {
	
	public static void main(String[] args) {
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		//其实是指定kafka集群的地址，在这指定zk的地址也可以动态获取kafka的地址
		BrokerHosts hosts = new ZkHosts("192.168.33.73:2181,192.168.33.74:2181,192.168.33.75:2181");
		//指定主题
		String topic = "spider1";
		//指定一个节点目录，zk会在根目录下创建这个节点,最终会在这个节点下面保存从kafka中消费数据的位置等信息
		String zkRoot = "/kafkaSpout1";//会在storm使用的zk集群中创建这个根节点
		//类似于kafka中的groupid
		String id = "234";
		SpoutConfig spoutConf = new SpoutConfig(hosts, topic, zkRoot, id);
		
		String SPOUT_ID = KafkaSpout.class.getSimpleName();
		String BOLT_1 = LogFilterBolt.class.getSimpleName();
		
		//优化：spout和bolt的并行度设置
		/**
		 * 主要需要分析出来爬虫每秒钟大致会产生多少条日志信息，以这个为依据来估算spout和bolt的并行度
		 * 注意：简单的提高spout的并行度并不能提高spout从kafka中消费数据的能力，必须对应的提高kafka中主题的分区数量
		 */
		topologyBuilder.setSpout(SPOUT_ID, new KafkaSpout(spoutConf));
		topologyBuilder.setBolt(BOLT_1, new LogFilterBolt()).shuffleGrouping(SPOUT_ID);
		
		String simpleName = LogProcessTopology.class.getSimpleName();
		Config config = new Config();
		//防止雪崩问题
		config.setMaxSpoutPending(1000);
		StormTopology createTopology = topologyBuilder.createTopology();
		
		if(args.length==0){
			LocalCluster localCluster = new LocalCluster();
			localCluster.submitTopology(simpleName, config, createTopology);
		}else{//在集群运行
			try {
				StormSubmitter.submitTopology(simpleName, config, createTopology);
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			}
		}
		
		
	}

}
