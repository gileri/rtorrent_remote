package com.github.fearhardcore.rtorrent;

public class Torrent {
	
	public static final int STATUS_STOPPED = 0;
	public static final int STATUS_STARTED = 1;
	
	public String getHash() {
		return hash;
	}

	String hash;
	String name;
	int status = 0;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public Torrent(String hash, String name) {
		this.hash = hash;
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
