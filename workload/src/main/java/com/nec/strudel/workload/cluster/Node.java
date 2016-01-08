package com.nec.strudel.workload.cluster;

public class Node {

	public static Node empty() {
		return new Node(0, 0, "");
	}

	public static Node create(int nodeId, int nodeNum, String url) {
		return new Node(nodeId, nodeNum, url);
	}


	private int id;
	private int num;
	private String url;

	public Node(int id, int num, String url) {
		this.id = id;
		this.num = num;
		this.url = url;
	}
	public Node() {
		this.url = "";
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * The number of nodes in the cluster.
	 * @return 0 if there is no node (i.e.,
	 * local execution).
	 */
    public int getNum() {
    	return num;
    }
    public void setNum(int num) {
		this.num = num;
	}

    public String getUrl() {
    	return url;
    }
    public void setUrl(String url) {
		this.url = url;
	}

    public boolean isLocal() {
    	return url.isEmpty();
    }
}