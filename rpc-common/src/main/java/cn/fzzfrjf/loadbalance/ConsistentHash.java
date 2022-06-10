package cn.fzzfrjf.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.*;

public class ConsistentHash {
    private static TreeMap<Integer,String> Nodes = new TreeMap<>();
    private static int VIRTUAL_NODES = 160;
    private static List<Instance> instances = new ArrayList<>();
    public static final HashMap<String,Instance> map = new HashMap<>();
    public ConsistentHash(List<Instance> instances,int VIRTUAL_NODES){
        ConsistentHash.instances = instances;
        ConsistentHash.VIRTUAL_NODES = VIRTUAL_NODES;
        creatHashRound();
    }
//    static {
//        for(Instance instance : instances){
//            String ip = instance.getIp();
//            Nodes.put(getHash(ip),ip);
//            map.put(ip,instance);
//            for(int i = 0;i < VIRTUAL_NODES;i++){
//                int hash = getHash(ip + "#" + i);
//                Nodes.put(hash,ip);
//                map.put(ip,instance);
//            }
//        }
//    }
    private void creatHashRound(){
        for(Instance instance : instances){
            String ip = instance.getIp();
            Nodes.put(getHash(ip),ip);
            map.put(ip,instance);
            for(int i = 0;i < VIRTUAL_NODES;i++){
                String virtualNodesName = getVirtualNodeName(ip, i);
                int hash = getHash(virtualNodesName);
                Nodes.put(hash,virtualNodesName);
            }
        }
    }
    private String getVirtualNodeName(String ip,Integer i){
        return ip + "&&VN" + i;
    }

    private String getRealNodeName(String virtualNodeName){
        return virtualNodeName.split("&&")[0];
    }


    public String getServer(String clientInfo){
        int hash = getHash(clientInfo);
        SortedMap<Integer,String> subMap = Nodes.tailMap(hash);
        String virtualNodeName;
        if(subMap == null || subMap.isEmpty()){
            virtualNodeName = Nodes.get(Nodes.firstKey());
        }else{
            virtualNodeName = subMap.get(subMap.firstKey());
        }
        return getRealNodeName(virtualNodeName);
    }



    public static int getHash(String str){
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for(int i = 0; i < str.length();i++){
            hash = (hash^str.charAt(i)) * p;
            hash += hash << 13;
            hash ^= hash >> 7;
            hash += hash << 3;
            hash ^= hash >> 17;
            hash += hash << 5;
            if(hash < 0){
                hash = Math.abs(hash);
            }
        }
        return hash;
    }
}
