package com.nec.strudel.workload.cluster;

/**
 * <pre>
 * {
 *   "size": N,
 *   "urls" : ["url"]
 * }
 * </pre>
 * @author tatemura
 *
 */
public class Cluster {
 
    private String[] urls;
    private int size;

    public Cluster() {
    	urls = new String[0];
    }

    public Cluster(int size, String[] urls) {
    	this.size = size;
    	this.urls = urls;
    }
    public void setSize(int size) {
		this.size = size;
	}
    public void setUrls(String[] urls) {
		this.urls = urls;
	}
    public int size() {
    	return Math.min(urls.length, size);
    }

    public boolean isLocal() {
		for (String u : urls) {
			if (!u.isEmpty()) {
				return false;
			}
		}
	    return true;
	}

    public Node[] nodes() {
        int size = size();
        Node[] nodes = new Node[size];
        for (int nodeId = 0; nodeId < size; nodeId++) {
            nodes[nodeId] = Node.create(nodeId, size, urls[nodeId]);
        }
    	return nodes;
	}

    public Cluster limit(int nodeNum) {
        return new Cluster(Math.min(nodeNum, this.size), urls);
	}

}
