package com.darwinreforged.servermodifications.objects;

import com.darwinreforged.servermodifications.util.FriendsStorageManager;

import java.util.ArrayList;
import java.util.UUID;

public class FriendsStorage {
    public boolean toggledTeleportsOff;

    public void toggle() {
        toggledTeleportsOff = !toggledTeleportsOff;
    }
	public FriendsStorage(UUID uuid) {
		setPlayer(uuid);	
	}
	
	//Never use this value but might be useful in the future
	private UUID player;

	public void setPlayer(UUID uuid) {
		this.player = uuid;
	}

	private ArrayList<UUID> friends = new ArrayList<UUID>();;

	public ArrayList<UUID> getFriends(){
		return friends;
	}

	private ArrayList<UUID> requests = new ArrayList<UUID>();

	public ArrayList<UUID> getRequests(){
		return requests;
	}

	public void addRequest(UUID uuid) {
		this.requests.add(uuid);
	}

	public void removeRequest(UUID uuid) {
		this.requests.remove(uuid);
	}


	//use this one for seeing if someone is a friend, if you cant figure this out....
	public boolean isFriend(UUID uuid) {
		return this.friends.contains(uuid);
	}

	public void addFriend(UUID uuid){
		requests.remove(uuid);
		friends.add(uuid);	
	}

	public void removeFriend(UUID uuid) {
		if (this.friends.contains(uuid)) {
			this.friends.remove(uuid);
		}
		else {
			removeRequest(uuid);
		}
		FriendsStorageManager.save(player, this);
	}
}
